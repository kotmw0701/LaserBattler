package jp.kotmw.lb;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

public class BattleRunnable extends BukkitRunnable {

	static String stage;
	static boolean midst;

	public BattleRunnable(String stage) {
		BattleRunnable.stage = stage;
	}

	@Override
	public void run() {
		if(midst) {

		} else {
			this.cancel();
		}
	}

	public boolean setEnergyBall(Location l) {
		ArmorStand armor = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		armor.setVisible(false);
		Item item = l.getWorld().dropItem(l, LaserGun.getEnergyBall());
		armor.setPassenger(item);
		armor.setCustomName(LaserGun.eball);
		armor.setCustomNameVisible(true);
		return false;
	}

}
