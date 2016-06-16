package jp.kotmw.lb;

import java.io.File;

import jp.kotmw.lb.FileDatas.PluginFiles;
import jp.kotmw.lb.FileDatas.StageFiles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Main extends JavaPlugin {
	public static Main instance;
	public static String pPrefix = ChatColor.WHITE + "["+ChatColor.GREEN+"LaserBattle"+ChatColor.WHITE+"] ";
	//public String team1meta = "Team1Meta", team2meta = "Team2Meta";
	//public String stagemeta = "StageMeta";
	public String filepath = getDataFolder() + File.separator;


	@Override
	public void onEnable() {
		instance = this;
		getServer().getPluginManager().registerEvents(new LaserGun(), this);
		getServer().getPluginManager().registerEvents(new MainBattle(), this);
		ScoreBoard.createScoreBoard();
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.reloadConfig();

		if(!PluginFiles.stagedir.exists())
			PluginFiles.stagedir.mkdir();
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
				} else if((args.length == 3) && ("getlaser".equalsIgnoreCase(args[0]))) {
					p.getInventory().addItem(LaserGun.getLaserGun(Integer.valueOf(args[1]),Integer.valueOf(args[2])));
				} else if((args.length == 1) && ("setEnergyball".equalsIgnoreCase(args[0]))) {
					final ArmorStand armor = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(10, 0, 0), EntityType.ARMOR_STAND);
					armor.setVisible(false);
					Item item = p.getWorld().dropItem(p.getLocation(), LaserGun.getEnergyBall());
					armor.setPassenger(item);
					armor.setCustomName(LaserGun.eball);
					armor.setCustomNameVisible(true);
					Bukkit.getScheduler().runTaskLater(this, new Runnable() {
						@Override
						public void run() {
							armor.remove();
						}
					}, 20*10);
				} else if((args.length == 1) && ("getPlayerData".equalsIgnoreCase(args[0]))) {
					if(!MainBattle.pdata.containsKey(p.getName()))
						return false;
					PlayerData data = MainBattle.pdata.get(p.getName());
					p.sendMessage("Stage: " +data.getStage());
					p.sendMessage("Output: " +data.getoutput());
					p.sendMessage("Energy: " +data.getenergy());
					p.sendMessage("TeamId: " +data.getTeamId());
				}
			}
		}
		return false;
	}

	public Location LocConversion(Location l) {
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	/**
	 * パケットを送信
	 *
	 * @param player 対象
	 * @param packet パケット
	 */
	@SuppressWarnings("rawtypes")
	public static void sendPlayer(Player player, net.minecraft.server.v1_8_R3.Packet packet)
	{
		((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}
}
