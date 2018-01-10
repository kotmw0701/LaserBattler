package jp.kotmw.lb;

import java.util.Random;

import jp.kotmw.lb.GameItems.DropItem;
import jp.kotmw.lb.FileDatas.StageFiles;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

public class BattleRunnable extends BukkitRunnable {

	private static Random random;
	public String stage;

	public BattleRunnable(String stage) {
		this.stage = stage;
	}

	@Override
	public void run() {
		random = new Random();
		int i = random.nextInt(10);
		if(i == 1 || i == 6)
			setEnergyBall(randomLocation(stage));
		if(i == 2)
			setBonusPoint(randomLocation(stage));
	}

	public static void setEnergyBall(Location l) {
		System.out.println("EnergyBall:  "+ "x: " +l.getX()+" y: "+l.getY()+" z: "+l.getZ());
		ArmorStand armor = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		armor.setVisible(false);
		armor.setGravity(false);
		armor.setCustomName(DropItem.EnergyBall.getItemName());
		armor.setCustomNameVisible(true);
		Item item = l.getWorld().dropItem(l, GameItems.getEnergyBall());
		item.setGravity(false);
		armor.setPassenger(item);
	}

	public static void setBonusPoint(Location l) {
		System.out.println("BonusPoint:  "+ "x: " +l.getX()+" y: "+l.getY()+" z: "+l.getZ());
		ArmorStand armor = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		armor.setVisible(false);
		armor.setGravity(false);
		armor.setCustomName(DropItem.BonusPoint.getItemName());
		armor.setCustomNameVisible(true);
		Item item = l.getWorld().dropItem(l, GameItems.getBonusPoint());
		item.setGravity(false);
		armor.setPassenger(item);
	}

	public static Location randomLocation(String stage) {
		random = new Random();
		Location maxloc = StageFiles.getStageLoc(stage, 1);
		Location minloc = StageFiles.getStageLoc(stage, 2);
		World world = maxloc.getWorld();
		int x = random.nextInt(maxloc.getBlockX()-minloc.getBlockX())+minloc.getBlockX();
		int y = minloc.getBlockY()+1;
		int z = random.nextInt(maxloc.getBlockZ()-minloc.getBlockZ())+minloc.getBlockZ();
		while(world.getBlockAt(x, y-1, z).getType() == Material.AIR
				|| (world.getBlockAt(x, y, z).getType() != Material.AIR
				|| world.getBlockAt(x, y+1, z).getType() != Material.AIR)) {
			x = random.nextInt(maxloc.getBlockX()-minloc.getBlockX())+minloc.getBlockX();
			y = random.nextInt(maxloc.getBlockY()-minloc.getBlockY())+minloc.getBlockY();
			z = random.nextInt(maxloc.getBlockZ()-minloc.getBlockZ())+minloc.getBlockZ();
		}
		return new Location(world, x+0.5, y, z+0.5);
	}

}
