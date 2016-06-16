package jp.kotmw.lb.FileDatas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.kotmw.lb.Main;

import org.bukkit.configuration.file.FileConfiguration;

public class PluginFiles {

	public static String filepath = Main.instance.filepath;
	public static File stagedir = new File(filepath + "Stage");
	/**
	 * ファイルの保存
	 *
	 * @param fileconfiguration ファイルコンフィグを指定
	 * @param file ファイル指定
	 * @param save 上書きをするかリセットするか
	 */
	public static void SettingFiles(FileConfiguration fileconfiguration, File file, boolean save) {
		if(!file.exists() || save) {
			try {
				fileconfiguration.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static File StageFile(String name) {
		File file = new File(filepath + "Stage" + File.separator + name +".yml");
		return file;
	}

	private static String getName(String name) {
		if (name == null)
			return null;
		int point = name.lastIndexOf(".");
		if (point != -1)
			return name.substring(0, point);
		return name;
	}

	public static List<String> getFileList(File dir) {
		List<String> names = new ArrayList<>();
		for(File file : Arrays.asList(dir.listFiles())) {
			if(file.isDirectory())
				continue;
			names.add(getName(file.getName()));
		}
		return names;
	}
}
