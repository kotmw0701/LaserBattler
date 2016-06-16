package jp.kotmw.lb.FileDatas;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StageFiles extends PluginFiles {

	public static List<String> getStageList() {
		return getFileList(stagedir);
	}

	public static void setStage(String stage, String world, int x1b, int y1b, int z1b, int x2b, int y2b, int z2b) {
		FileConfiguration file = new YamlConfiguration();
		int x1 = x1b, x2 = x2b;
		if(x1b < x2b)//大きいほうの値を最初のx座標にする
		{
			x1 = x2b;
			x2 = x1b;
		}
		int y1 = y1b, y2 = y2b;
		if(y1b < y2b)//大きいほうの値を最初のy座標にする
		{
			y1 = y2b;
			y2 = y1b;
		}
		int z1 = z1b, z2 = z2b;
		if(z1b < z2b)//大きいほうの値を最初のz座標にする
		{
			z1 = z2b;
			z2 = z1b;
		}
		file.set(stage +".World", world);
		file.set(stage +".Point1.x", x1);
		file.set(stage +".Point1.y", y1);
		file.set(stage +".Point1.z", z1);
		file.set(stage +".Point2.x", x2);
		file.set(stage +".Point2.y", y2);
		file.set(stage +".Point2.z", z2);
		SettingFiles(file, StageFile(stage), true);
	}

	public static void setStayRoom(Location l, String stage) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(PluginFiles.StageFile(stage));
		file.set(stage+".Room.world", l.getWorld().getName());
		file.set(stage+".Room.x", l.getX());
		file.set(stage+".Room.y", l.getY());
		file.set(stage+".Room.z", l.getZ());
		PluginFiles.SettingFiles(file, PluginFiles.StageFile(stage), true);
	}

	public static Location getStayRoom(String stage) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(StageFile(stage));
		String world = file.getString(stage+".Room.world");
		double x = file.getDouble(stage+".Room.x");
		double y = file.getDouble(stage+".Room.y");
		double z = file.getDouble(stage+".Room.z");
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public static void setRespawn(Location l, String stage, int team, int nom) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(StageFile(stage));
		if(!file.contains(stage+".Team"+team))
			file.set(stage+".TotalTeamNum", file.getInt(stage+".TotamTeamNum")+1);
		file.set(stage+".Team"+team+".RespawnPoint", file.getInt(stage+".Team"+team+".RespawnPoint")+1);
		file.set(stage+".Team"+team+".loc"+nom+".world", l.getWorld().getName());
		file.set(stage+".Team"+team+".loc"+nom+".x", l.getX());
		file.set(stage+".Team"+team+".loc"+nom+".y", l.getY());
		file.set(stage+".Team"+team+".loc"+nom+".z", l.getZ());
		PluginFiles.SettingFiles(file, PluginFiles.StageFile(stage), true);
	}

	public static Location getRespawn(int team, String stage) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(StageFile(stage));
		Random random = new Random();
		int loc = random.nextInt(file.getInt(stage+".Team"+team+".RespawnPoint")) + 1;
		String world = file.getString(stage+".Team"+team+".loc"+loc+".world");
		double x= file.getDouble(stage+".Team"+team+".loc"+loc+".x");
		double y= file.getDouble(stage+".Team"+team+".loc"+loc+".y");
		double z= file.getDouble(stage+".Team"+team+".loc"+loc+".z");
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public static int getTotalTeamNum(String stage) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(StageFile(stage));
		return file.getInt(stage+".TotalTeamNum");
	}
}
