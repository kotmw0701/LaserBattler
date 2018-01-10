package jp.kotmw.lb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.kotmw.lb.FileDatas.StageFiles;
import jp.kotmw.lb.datas.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainBattle implements Listener{

	public static Map<String, PlayerData> pdata = new HashMap<>();
	public static Map<String, Location> bloc = new HashMap<>();
	public static Map<String, BattleRunnable> srun = new HashMap<>();

	public static void TeleportWaintRoom(Player p, String stage) {
		PlayerData data = new PlayerData(p.getName());
		data.setStage(stage);
		data.setTeamId(1);
		ScoreBoard.setTeam(stage, data, 1);
		MainBattle.pdata.put(p.getName(), data);
		MainBattle.bloc.put(p.getName(), p.getLocation());
		p.teleport(StageFiles.getStayRoom(stage));
		SetTeamSelectItem(p, stage);
	}

	public static void SetTeamSelectItem(Player p, String stage) {
		for(int i = 1; i <= StageFiles.getTotalTeamNum(stage); i++) {
			ItemStack item = new ItemStack(Material.WOOL, 1, ScoreBoard.getTeamWoolColor(i));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ScoreBoard.getTeamChatColor(i)+"Join team "+i);
			item.setItemMeta(meta);
			p.getInventory().setItem(i-1, item);
		}
	}

	public static void ExitTransfer(Player p) {
		if(!pdata.containsKey(p.getName()))
			return;
		ScoreBoard.removeTeam(pdata.get(p.getName()));
		p.getInventory().clear();
		p.teleport(MainBattle.bloc.remove(p.getName()));
		MainBattle.pdata.remove(p.getName());
	}

	public static void GameStart(String stage) {
		for(PlayerData data : getPlayerCount(stage)) {
			Player p = Bukkit.getPlayer(data.getName());
			p.teleport(StageFiles.getRespawn(data.getTeamId(), stage));
			p.getInventory().clear();
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			p.getInventory().addItem(GameItems.getLaserGun(data.getoutput(), data.getenergy()));
			p.getInventory().addItem(GameItems.getItemPack());
			p.setScoreboard(ScoreBoard.getScoreboard(data));
			WindowText.sendFullTitle(p, 0, 3, 1, ChatColor.AQUA + "ゲームスタート", "");
		}
		BattleRunnable run = new BattleRunnable(stage);
		run.runTaskTimer(Main.main, 10*20, 3*20);
		srun.put(stage, run);
	}

	public static void GameEnd(String stage) {
		if(!srun.containsKey(stage))
			return;
		for(PlayerData data : getPlayerCount(stage)) {
			Player p = Bukkit.getPlayer(data.getName());
			p.teleport(bloc.get(data.getName()));
			p.getInventory().clear();
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			pdata.remove(data.getName());
		}
		Main.main.StandClear();
		srun.remove(stage).cancel();
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack is = p.getInventory().getItemInMainHand();
		Action a = e.getAction();
		if(!pdata.containsKey(p.getName()))
			return;
		PlayerData data = pdata.get(p.getName());
		if((a == Action.LEFT_CLICK_AIR)
				|| (a == Action.LEFT_CLICK_BLOCK)
				|| (a == Action.PHYSICAL))
			return;
		if((is == null)
				|| (is.getType() == Material.AIR)
				|| (!is.getItemMeta().hasDisplayName()))
			return;
		String stage = data.getStage();
		for(int i = 1; i <= StageFiles.getTotalTeamNum(stage); i++) {
			if(is.getItemMeta().getDisplayName().equals(ScoreBoard.getTeamChatColor(i)+"Join team "+i)) {
				if(data.getTeamId() == i) {
					p.sendMessage(Main.pPrefix + ScoreBoard.getTeamChatColor(i)+"Team "+i+ChatColor.WHITE+" にはすでに所属しています");
					return;
				}
				ScoreBoard.setTeam(stage, data, i);
				p.sendMessage(Main.pPrefix + ScoreBoard.getTeamChatColor(i)+"Team "+i+ChatColor.WHITE+" を選択しました");
				return;
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(MainBattle.pdata.containsKey(p.getName()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(MainBattle.pdata.containsKey(p.getName()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onChangeFoodLevel(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if(MainBattle.pdata.containsKey(p.getName()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		pdata.remove(e.getPlayer().getName());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(!MainBattle.pdata.containsKey(p.getName()))
			return;
		PlayerData data = MainBattle.pdata.get(p.getName());
		PlayerData killer = data.getLatestKiller();
		int team = data.getTeamId();
		String stagename = data.getStage();
		e.setDeathMessage(Main.pPrefix
				+ScoreBoard.getTeamChatColor(team)+p.getName()+ChatColor.WHITE+" が "
				+ScoreBoard.getTeamChatColor(killer.getTeamId())+killer.getName()+ChatColor.WHITE+" に倒された！");
		p.setHealth(p.getMaxHealth());
		p.setRemainingAir(p.getMaximumAir());
		p.setFoodLevel(20);
		p.setGameMode(GameMode.ADVENTURE);
		p.teleport(StageFiles.getRespawn(team, stagename));
	}

	public static List<PlayerData> getPlayerCount(String stage) {
		List<PlayerData> players = new ArrayList<>();
		for(String name : pdata.keySet()) {
			if(pdata.get(name).getStage().equalsIgnoreCase(stage))
				players.add(pdata.get(name));
		}
		return players;
	}

	public static boolean hasinGame(String name) {
		return pdata.containsKey(name);
	}
}
