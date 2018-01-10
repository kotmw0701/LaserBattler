package jp.kotmw.lb;

import java.util.HashMap;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WindowText {


	final static HashMap<String, Integer> Count = new HashMap<String,Integer>();

	/**
	 * アクションバーの表示
	 *
	 * @param player 対象プレイヤー
	 * @param test テキスト
	 */
	public static void sendActionBar(Player player, String text)
	{
		final String text_ = text.replace("_", " ");
		String s = ChatColor.translateAlternateColorCodes('&', text_);
		IChatBaseComponent icbc = ChatSerializer.a("{\"text\": \"" + s +"\"}");
		PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte)2);
		Main.sendPlayer(player, bar);
	}

	/**
	 * タイトルの表示
	 *
	 * @param player 対象プレイヤー
	 * @param fadein フェードイン
	 * @param stay 表示時間
	 * @param fadeout フェードアウト
	 * @param MainTitle メインタイトル
	 * @param Subtitle サブタイトル
	 *
	 */
	public static void sendFullTitle(Player player, int fadein, int stay, int fadeout, String MainTitle, String SubTitle)
	{
		sendTitle(player, fadein, stay, fadeout, MainTitle, SubTitle);
	}

	/**
	 * タイトルの表示
	 *
	 * @param player 対象プレイヤー
	 * @param stay 表示時間
	 * @param MainTitle メインタイトル
	 * @param Subtitle サブタイトル
	 *
	 */
	public static void sendFullTitle(Player player, int stay, String MainTitle, String SubTitle)
	{
		sendTitle(player, 0, stay, 0, MainTitle, SubTitle);
	}

	/**
	 * タイトルの表示
	 *
	 * @param player 対象プレイヤー
	 * @param fadein フェードイン
	 * @param stay 表示時間
	 * @param fadeout フェードアウト
	 * @param MainTitle メインタイトル
	 * @param Subtitle サブタイトル
	 *
	 */
	private static void sendTitle(Player player, int fadein, int stay, int fadeout, String MainTitle, String SubTitle)
	{
		PacketPlayOutTitle titletime = new PacketPlayOutTitle(
				PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadein*20, stay*20, fadeout*20);
		Main.sendPlayer(player, titletime);

		if(SubTitle != null)
		{
			SubTitle = ChatColor.translateAlternateColorCodes('&', SubTitle);
			IChatBaseComponent subtitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + SubTitle + "\"}");
			PacketPlayOutTitle sendsubtitle = new PacketPlayOutTitle(
					PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle);
			Main.sendPlayer(player, sendsubtitle);
		}

		if(MainTitle != null)
		{
			MainTitle = ChatColor.translateAlternateColorCodes('&', MainTitle);
			IChatBaseComponent maintitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + MainTitle + "\"}");
			PacketPlayOutTitle sendmaintitle = new PacketPlayOutTitle(
					PacketPlayOutTitle.EnumTitleAction.TITLE, maintitle);
			Main.sendPlayer(player, sendmaintitle);
		}
	}
}