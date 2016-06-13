package jp.kotmw.lb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Main instance;
	public String team1meta = "Team1Meta", team2meta = "Team2Meta";
	public String stagemeta = "StageMeta";
	public String filepath = getDataFolder() + File.separator;
	public File stagedir = new File(filepath + "Stage");


	@Override
	public void onEnable() {
		instance = this;
		getServer().getPluginManager().registerEvents(new LaserGun(), this);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.reloadConfig();

		if(!stagedir.exists())
			stagedir.mkdir();
	}

	@Override
	public void onDisable() {}

	public boolean onCommand(CommandSender s, Command cmd, String lav, String[] args) {
		if(args.length >= 1) {
			if(s instanceof Player) {
				Player p = (Player)s;
				if((args.length == 4) && ("setup".equalsIgnoreCase(args[0]))) {
					String stagename = args[1];
					if("setspawn".equalsIgnoreCase(args[2])) {
						setRespawn(p.getLocation(), stagename, Integer.valueOf(args[3]), 1);
					}
				}
				if((args.length == 3) && ("join".equalsIgnoreCase(args[0]))) {
					String stagename = args[1];
					if(!getArenaList().contains(stagename)) {
						p.sendMessage(ChatColor.RED + "そのステージは存在しませぬよ(´・ω・｀ )");
						return false;
					}
					p.setMetadata(stagemeta, new FixedMetadataValue(this, stagename));
					if("team1".equalsIgnoreCase(args[2])) {
						if(p.hasMetadata(team2meta))
							p.removeMetadata(team2meta, this);
						p.setMetadata(team1meta, new FixedMetadataValue(this, p.getName()));
					} else if ("team2".equalsIgnoreCase(args[2])) {
						if(p.hasMetadata(team1meta))
							p.removeMetadata(team1meta, this);
						p.setMetadata(team2meta, new FixedMetadataValue(this, stagename));
					}
				}
			}
		}
		return false;
	}

	public Location LocConversion(Location l) {
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public void setRespawn(Location l, String name, int team, int nom) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(StageDirFiles(name));
		file.set("Laser.Respawn.Team."+team+".l"+nom+".world", l.getWorld().getName());
		file.set("Laser.Respawn.Team."+team+".l"+nom+".x", l.getX());
		file.set("Laser.Respawn.Team."+team+".l"+nom+".y", l.getY());
		file.set("Laser.Respawn.Team."+team+".l"+nom+".z", l.getZ());
		SettingFiles(file, StageDirFiles(name), true);
	}

	public Location getRespawn(int team, String name) {
		FileConfiguration file = new YamlConfiguration();
		file = YamlConfiguration.loadConfiguration(StageDirFiles(name));
		String world = file.getString("Laser.Respawn.Team."+team+".l1.world");
		double x= file.getDouble("Laser.Respawn.Team."+team+".l1.x");
		double y= file.getDouble("Laser.Respawn.Team."+team+".l1.y");
		double z= file.getDouble("Laser.Respawn.Team."+team+".l1.z");
		return new Location(Bukkit.getWorld(world), x, y, z);
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

	/**
	 * ファイルの保存
	 *
	 * @param fileconfiguration ファイルコンフィグを指定
	 * @param file ファイル指定
	 * @param save 上書きをするかリセットするか
	 */
	public void SettingFiles(FileConfiguration fileconfiguration, File file, boolean save)
	{
		if(!file.exists() || save)
		{
			try {
				fileconfiguration.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public File StageDirFiles(String name)
	{
		File file = new File(filepath + "Stage" + File.separator + name +".yml");
		return file;
	}

	public String getName(String name)
	{
		if (name == null)
			return null;
		int point = name.lastIndexOf(".");
		if (point != -1)
			return name.substring(0, point);
		return name;
	}

	public List<String> getArenaList()
	{
		List<String> names = new ArrayList<>();
		for(File file : Arrays.asList(stagedir.listFiles()))
		{
			if(file.isDirectory())
				continue;
			names.add(getName(file.getName()));
		}
		return names;
	}
}
