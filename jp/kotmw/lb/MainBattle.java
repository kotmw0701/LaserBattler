package jp.kotmw.lb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.kotmw.lb.FileDatas.StageFiles;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainBattle implements Listener{

	public static Map<String, PlayerData> pdata = new HashMap<>();
	public static Map<String, Location> bloc = new HashMap<>();

	public static void TeleportWaintRoom(Player p, String stage) {
		PlayerData data = new PlayerData(p.getName());
		data.setStage(stage);
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

	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack is = p.getItemInHand();
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
				ScoreBoard.setTeam(stage, data, i);
				p.sendMessage(Main.pPrefix + ScoreBoard.getTeamChatColor(i)+"Team "+i+ChatColor.WHITE+" を選択しました");
				break;
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
				+ScoreBoard.getTeamChatColor(killer.getTeamId())+p.getName()+ChatColor.WHITE+" に倒された！");
		p.setHealth(p.getMaxHealth());
		p.setRemainingAir(p.getMaximumAir());
		p.setFoodLevel(20);
		p.setGameMode(GameMode.ADVENTURE);
		p.teleport(StageFiles.getRespawn(team, stagename));
	}

	public List<String> getPlayerCount(String stage) {
		List<String> players = new ArrayList<>();
		for(String name : pdata.keySet()) {
			if(pdata.get(name).getStage().equalsIgnoreCase(stage))
				players.add(name);
		}
		return players;
	}
}
