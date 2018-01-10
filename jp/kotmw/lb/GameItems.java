package jp.kotmw.lb;

import java.util.ArrayList;
import java.util.List;

import jp.kotmw.lb.datas.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class GameItems {

	static String gun = ChatColor.GREEN + "LaserGun";
	static String itempack = ChatColor.LIGHT_PURPLE + "ItemPack";

	public static ItemStack getLaserGun(int i, int ii) {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET.toString() + ChatColor.GREEN
				+"----------"+ChatColor.BLUE+ChatColor.BOLD.toString()
				+"Status"+ChatColor.GREEN+"----------");
		lore.add(ChatColor.RESET + "出力 ["+LaserGun.meter(i)+ChatColor.RESET+"]");
		lore.add(ChatColor.RESET + "耐久 ["+LaserGun.emeter(ii)+ChatColor.RESET+"]");
		lore.add(ChatColor.GREEN +"---------------------------");
		meta.setDisplayName(gun);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getEnergyBall() {
		ItemStack item = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add("LaserGunEnergyBall");
		meta.setDisplayName(DropItem.EnergyBall.getItemName());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getEnergySkull() {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName("EnergyBall");
		meta.setOwner("kotmw0701");
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getBonusPoint() {
		ItemStack item = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add("BonusPoint!");
		meta.setDisplayName(DropItem.BonusPoint.getItemName());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getItemPack() {
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(itempack);
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET.toString() + ChatColor.YELLOW + "右クリックでアイテムメニュー展開");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public enum DropItem {
		EnergyBall(ChatColor.GOLD + ChatColor.BOLD.toString() + " EnergyBall" + ChatColor.RESET),
		BonusPoint(ChatColor.YELLOW + ChatColor.BOLD.toString() + " BonusPoint +5" + ChatColor.RESET);

		private String name;

		private DropItem(String name) {
			this.name = name;
		}

		public String getItemName() {
			return name;
		}
	}

	//////////これ以下はアイテムパックの内容//////////


	private static String energyboost = ChatColor.GREEN + "エネルギーブースト";
	private static String powerup = ChatColor.RED + "パワーアップ";
	private static String opponentgrowing = ChatColor.YELLOW + "マーキング";


	public static ItemStack getItem(GameItemType type, PlayerData data) {
		ItemStack item = type.getItem();
		int cost = type.getCost();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(meta.getLore().get(0));
		if(cost <= data.getPoint())
			lore.add(ChatColor.RESET.toString()+ChatColor.GREEN + "使用可能");
		else if(cost > data.getPoint())
			lore.add(ChatColor.RESET.toString()+ChatColor.RED + "使用不可能");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private static ItemStack getEnergyBoost() {
		ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET.toString()+ ChatColor.WHITE +"一定時間耐久の減少が遅くなる");
		lore.add(ChatColor.RESET.toString()+ ChatColor.GREEN +"使用コスト:" + ChatColor.WHITE + "5");
		meta.setDisplayName(energyboost);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private static ItemStack getPowerUP() {
		ItemStack item = new ItemStack(Material.FIREBALL);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET.toString()+ ChatColor.WHITE +"出力が上昇する");
		lore.add(ChatColor.RESET.toString()+ ChatColor.GREEN +"使用コスト:" + ChatColor.WHITE + "10");
		meta.setDisplayName(powerup);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private static ItemStack getOpponentGrowing() {
		ItemStack item = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET.toString()+ ChatColor.WHITE +"相手の姿が壁越しでも見えるようになる");
		lore.add(ChatColor.RESET.toString()+ ChatColor.GREEN +"使用コスト:" + ChatColor.WHITE + "20");
		meta.setDisplayName(opponentgrowing);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public enum GameItemType {
		EnergyBoost(getEnergyBoost(), energyboost, 5),
		PowerUP(getPowerUP(), powerup, 10),
		OpponentGrowing(getOpponentGrowing(), opponentgrowing, 20);

		private ItemStack item;
		private String name;
		private int cost;

		private GameItemType(ItemStack item, String name, int cost) {
			this.item = item;
			this.name = name;
			this.cost = cost;
		}

		public ItemStack getItem() {
			return this.item;
		}

		public String getName() {
			return this.name;
		}

		public int getCost() {
			return this.cost;
		}
	}

	public static GameItemType getType(String name) {
		for(GameItemType type : GameItemType.values()) {
			if(type.getName().equalsIgnoreCase(name))
				return type;
		}
		return null;
	}
}
