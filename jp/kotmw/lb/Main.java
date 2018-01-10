package jp.kotmw.lb;

import java.io.File;

import jp.kotmw.lb.GameItems.DropItem;
import jp.kotmw.lb.FileDatas.StageFiles;
import jp.kotmw.lb.datas.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Main extends JavaPlugin {

	public static Main main;
	public static String pPrefix = ChatColor.WHITE + "["+ChatColor.GREEN+"LaserBattle"+ChatColor.WHITE+"] ";
	public String filepath = getDataFolder() + File.separator;

	@Override
	public void onEnable() {
		main = this;
		getServer().getPluginManager().registerEvents(new LaserGun(), this);
		getServer().getPluginManager().registerEvents(new MainBattle(), this);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.reloadConfig();

		if(!StageFiles.stagedir.exists())
			StageFiles.stagedir.mkdir();
		ScoreBoard.createScoreBoard();
	}

	@Override
	public void onDisable() {}

	public boolean onCommand(CommandSender s, Command cmd, String lav, String[] args) {
		if(args.length >= 1) {
			if(s instanceof Player) {
				Player p = (Player)s;
				if((args.length >= 3) && ("setup".equalsIgnoreCase(args[0]))) {
					String stagename = args[1];
					if((args.length == 3) && ("create".equalsIgnoreCase(args[2]))) {
						WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
						Selection selection = worldEdit.getSelection(p);
						if(selection != null) {
							World w = selection.getWorld();
							String worldName = w.getName();
							int x1 = selection.getMinimumPoint().getBlockX();
							int y1 = selection.getMinimumPoint().getBlockY();
							int z1 = selection.getMinimumPoint().getBlockZ();
							int x2 = selection.getMaximumPoint().getBlockX();
							int y2 = selection.getMaximumPoint().getBlockY();
							int z2 = selection.getMaximumPoint().getBlockZ();
							StageFiles.setStage(stagename, worldName, x1, y1, z1, x2, y2, z2);
							p.sendMessage(pPrefix + "ステージを設定しますた(/・ω・)/");
						}
					} else if((args.length == 5) && ("setspawn".equalsIgnoreCase(args[2]))) {
						if(!StageFiles.getStageList().contains(stagename)) {
							p.sendMessage(pPrefix + ChatColor.RED + "そのステージは存在しませぬよ(´・ω・｀ )");
							return false;
						}
						StageFiles.setRespawn(p.getLocation(), stagename, Integer.valueOf(args[3]), Integer.valueOf(args[4]));
						p.sendMessage(pPrefix + "スポーンポイントを設定しますた");
					} else if("setstayroom".equalsIgnoreCase(args[2])) {
						if(!StageFiles.getStageList().contains(stagename)) {
							p.sendMessage(pPrefix + ChatColor.RED + "そのステージは存在しませぬよ(´・ω・｀ )");
							return false;
						}
						StageFiles.setStayRoom(p.getLocation(), stagename);
					}
				} else if((args.length == 2) && ("join".equalsIgnoreCase(args[0]))) {
					String stagename = args[1];
					if(!StageFiles.getStageList().contains(stagename)) {
						p.sendMessage(ChatColor.RED + "そのステージは存在しませぬよ(´・ω・｀ )");
						return false;
					}
					MainBattle.TeleportWaintRoom(p, stagename);
				} else if((args.length == 1) && ("leave".equalsIgnoreCase(args[0]))) {
					if(!MainBattle.pdata.containsKey(p.getName()))
						return false;
					MainBattle.ExitTransfer(p);
				} else if((args.length == 1) && ("start".equalsIgnoreCase(args[0]))) {
					if(!MainBattle.pdata.containsKey(p.getName()))
						return false;
					MainBattle.GameStart(MainBattle.pdata.get(p.getName()).getStage());
					return true;
				} else if((args.length == 1) && ("end".equalsIgnoreCase(args[0]))) {
					if(!MainBattle.pdata.containsKey(p.getName()))
						return false;
					MainBattle.GameEnd(MainBattle.pdata.get(p.getName()).getStage());
					return true;
				} else if((args.length == 1) && ("clear".equalsIgnoreCase(args[0]))) {
					StandClear();
				} else if((args.length == 1) && ("setdata".equalsIgnoreCase(args[0]))) {
					PlayerData data = new PlayerData(p.getName());
					data.setStage("test");
					data.setTeamId(1);
					data.setInfinity(true);
					MainBattle.pdata.put(p.getName(), data);
				} else if((args.length == 1) && ("removedata".equalsIgnoreCase(args[0]))) {
					if(MainBattle.pdata.containsKey(p.getName()))
						MainBattle.pdata.remove(p.getName());
				} else if((args.length == 1) && ("getGun".equalsIgnoreCase(args[0]))) {
					p.sendMessage("Gun");
					p.getInventory().addItem(GameItems.getLaserGun(20, 40));
				} else if((args.length == 1) && ("setenergy".equalsIgnoreCase(args[0]))) {
					BattleRunnable.setEnergyBall(p.getLocation().clone().add(10, 0, 0));
				}
			}
		}
		return false;
	}

	public Location LocConversion(Location l) {
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public void StandClear() {
		for(String stage : StageFiles.getStageList()) {
			for(Entity entity : StageFiles.getStageLoc(stage, 1).getWorld().getEntities()) {
				if(entity.getType() == EntityType.ARMOR_STAND
						&& (entity.getCustomName().equalsIgnoreCase(DropItem.EnergyBall.getItemName())
								|| entity.getCustomName().equalsIgnoreCase(DropItem.BonusPoint.getItemName()))) {
					entity.remove();
					Entity pas = entity.getPassenger();
					if(pas != null)
						pas.remove();
				}
			}
		}
	}

	/**
	 * パケットを送信
	 *
	 * @param player 対象
	 * @param packet パケット
	 */
	@SuppressWarnings("rawtypes")
	public static void sendPlayer(Player player, net.minecraft.server.v1_10_R1.Packet packet)
	{
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}
}
