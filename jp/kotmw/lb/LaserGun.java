package jp.kotmw.lb;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class LaserGun implements Listener{

	String gun = "LaserGun";


	@EventHandler
	public void onLazerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location l = p.getLocation().clone();
		ItemStack is = p.getItemInHand();
		Color color = p.hasMetadata(Main.instance.team1meta) ? Color.RED : Color.BLUE;
		if((is == null)
				|| (is.getType() == Material.AIR)
				|| (!is.getItemMeta().hasDisplayName())
				|| (!is.getItemMeta().getDisplayName().equals(gun)))
			return;
		if(!p.isSneaking())
			return;
		l.add(0, 1.5, 0);
		Laser(p, l, 100, 0.5, color, 1, false, false);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Location l = p.getLocation().clone();
		ItemStack is = p.getItemInHand();
		Action a = e.getAction();
		if((is == null)
				|| (is.getType() == Material.AIR)
				|| (!is.getItemMeta().hasDisplayName())
				|| (!is.getItemMeta().getDisplayName().equals(gun)))
			return;
		if((a == Action.LEFT_CLICK_AIR)
				|| (a == Action.LEFT_CLICK_BLOCK)
				|| (a == Action.PHYSICAL))
			return;
		if(!p.isSneaking())
			return;
		l.add(0, 1.5, 0);
		Laser(p, l, 200, 0.2, Color.LIME, 1, true, true);
	}

	public void Laser(Player p, Location l, double max, double pInterval, Color color, int Reflection, boolean damage, boolean view) {
		float yaw = -l.getYaw(),pitch = -l.getPitch();
		double xo = l.getX(), yo = l.getY(), zo = l.getZ();
		int Refcount = 0, ii = 0;
		if(color.equals(Color.GREEN) || color.equals(Color.BLUE))
			ii = -1;
		for(double i = pInterval; i <= max; i = i + pInterval) {
			double x = xo+(i*Math.sin(Math.toRadians(yaw)));
			double y = yo+(i*Math.sin(Math.toRadians(pitch)));
			double z = zo+(i*Math.cos(Math.toRadians(yaw)));
			Location ll = Main.instance.LocConversion(new Location(l.getWorld(), x, y, z));
			if(ll.getBlock().getType() == Material.IRON_BLOCK) {
				if(Refcount == Reflection)
					break;
				int bi = checkBeforeLoc(l, ll, i-pInterval);
				max = max - i;
				i = pInterval;
				xo = x;
				yo = y;
				zo = z;
				if(bi == 1 || bi == 3)
					yaw = -yaw;
				else if(bi == 2 || bi == 4)
					yaw = -yaw + 180;
				else if(bi == 5 || bi == 6)
					pitch = -pitch;
				Refcount++;
			}
			if(ll.getBlock().getType() != Material.AIR
					&& ll.getBlock().getType() != Material.GLASS
					&& ll.getBlock().getType() != Material.STAINED_GLASS
					&& ll.getBlock().getType() != Material.IRON_BLOCK
					&& ll.getBlock().getType() != Material.THIN_GLASS
					&& ll.getBlock().getType() != Material.STAINED_GLASS_PANE
					&& ll.getBlock().getType() != Material.DOUBLE_PLANT
					&& ll.getBlock().getType() != Material.LONG_GRASS
					&& ll.getBlock().getType() != Material.CARPET
					&& ll.getBlock().getType() != Material.LADDER)
				break;
			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
					EnumParticle.REDSTONE
					, true
					, (float)x
					, (float)y
					, (float)z
					, (color.getRed() / 255) + ii
					, (color.getGreen() / 255)
					, (color.getBlue() / 255)
					, 10
					, 0
					, 0);
			for(Player online : Bukkit.getOnlinePlayers())
				if(view || p.getName() != online.getName())
					Main.sendPlayer(online, packet);
			if(!damage)
				continue;
			AttachCheck(p, x, y, z);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(p.hasMetadata(Main.instance.stagemeta)) {
			int team = p.hasMetadata(Main.instance.team1meta) ? 1 : 2;
			String stagename = p.getMetadata(Main.instance.stagemeta).get(0).asString();
			e.setDeathMessage("");
			p.setHealth(p.getMaxHealth());
			p.setRemainingAir(p.getMaximumAir());
			p.setFoodLevel(20);
			p.setGameMode(GameMode.ADVENTURE);
			p.teleport(Main.instance.getRespawn(team, stagename));
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata(Main.instance.team1meta)
				|| p.hasMetadata(Main.instance.team2meta)) {
			e.setCancelled(true);
		}
	}

	public void AttachCheck(Player p, double x, double y, double z) {
		int llx = (int) x, lly = (int) y, llz = (int) z;
		for(World world : Bukkit.getWorlds()) {
			for(Entity entity : world.getEntities()) {
				if(!(entity instanceof Player))
					continue;
				Player target = (Player)entity;
				if(target != p) {
					boolean playerteam = p.hasMetadata(Main.instance.team1meta) ? true : false;
					boolean otherteam = target.hasMetadata(Main.instance.team1meta) ? true : false;
					if((playerteam && otherteam) || (!playerteam && !otherteam))
						continue;
					int tlx = (int)target.getLocation().getX();
					int tly = (int)target.getLocation().getY();
					int tlz = (int)target.getLocation().getZ();
					if((llx==tlx)&&(lly==tly)&&(llz==tlz)) {
						Damager(p, target, 10);
					}
					if((llx==tlx)&&(lly==tly+1)&&(llz==tlz)) {
						Damager(p, target, 20);
					}
				}
			}
		}
	}

	public void Damager(Player d, Player t, int damage) {
		t.damage(damage);
		d.playSound(d.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
	}

	public int checkBeforeLoc(Location l, Location ll, double i) {
		double x = ll.getBlockX(), y = ll.getBlockY(), z = ll.getBlockZ();
		Location bl = new Location(l.getWorld(),
				l.getX()+(i*Math.sin(Math.toRadians(-l.getYaw()))),
				l.getY()+(i*Math.sin(Math.toRadians(-l.getPitch()))),
				l.getZ()+(i*Math.cos(Math.toRadians(-l.getYaw()))));
		double bx = bl.getBlockX(),by = bl.getBlockY(), bz = bl.getBlockZ();
		if(x+1 == bx && y == by && z == bz)
			return 1;
		if(x == bx && y == by && z+1 == bz)
			return 2;
		if(x-1 == bx && y == by && z == bz)
			return 3;
		if(x == bx && y == by && z-1 == bz)
			return 4;
		if(x == bx && y+1 == by && z == bz)
			return 5;
		if(x == bx && y-1 == by && z == bz)
			return 6;
		return 0;
	}
}
