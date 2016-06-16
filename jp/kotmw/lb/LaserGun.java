package jp.kotmw.lb;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LaserGun implements Listener{

	static String gun = ChatColor.GREEN + "LaserGun";
	static String eball = ChatColor.GOLD + ChatColor.BOLD.toString() + " EnergyBall" + ChatColor.RESET;


	@EventHandler
	public void onLazerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location l = p.getLocation().clone();
		ItemStack is = p.getItemInHand();
		if(!MainBattle.pdata.containsKey(p.getName()))
			return;
		PlayerData data = MainBattle.pdata.get(p.getName());
		if((is == null)
				|| (is.getType() == Material.AIR)
				|| (!is.getItemMeta().hasDisplayName())
				|| (!is.getItemMeta().getDisplayName().equals(gun))
				|| (!is.getItemMeta().hasLore()))
			return;
		if(!p.isSneaking())
			return;
		l.add(0, 1.5, 0);
		Laser(p, null, l, 100, 0.5, ScoreBoard.getTeamLaserColor(data.getTeamId()), 1, 0, false, false);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Location l = p.getLocation().clone();
		ItemStack is = p.getItemInHand();
		Action a = e.getAction();
		if(!MainBattle.pdata.containsKey(p.getName()))
			return;
		PlayerData data = MainBattle.pdata.get(p.getName());
		if((is == null)
				|| (is.getType() == Material.AIR)
				|| (!is.getItemMeta().hasDisplayName())
				|| (!is.getItemMeta().getDisplayName().equals(gun))
				|| (!is.getItemMeta().hasLore()))
			return;
		if((a == Action.LEFT_CLICK_AIR)
				|| (a == Action.LEFT_CLICK_BLOCK)
				|| (a == Action.PHYSICAL))
			return;
		if(!p.isSneaking()) {
			int output = data.getoutput();
			if(output >= 2)
				data.setoutput(output-1);
			else if(output <= 1)
				data.setoutput(20);
			WindowText.sendActionBar(p, ChatColor.GREEN+"出力を変更しました"+ChatColor.WHITE +"["+meter(data.getoutput())+"]");
			p.getInventory().remove(Material.STICK);
			p.getInventory().addItem(getLaserGun(data.getoutput(), data.getenergy()));
			return;
		}
		if(data.getenergy() < data.getoutput()*2) {
			WindowText.sendFullTitle(p, 2, ChatColor.RED+"エネルギーがありません！", "");
			return;
		}
		l.add(0, 1.5, 0);
		Laser(p, data, l, 200, 0.2, Color.LIME, 1, data.getoutput(), true, true);
	}

	public void Laser(Player p, PlayerData data, Location l, double max, double pInterval, Color color, int Reflection, int output, boolean damage, boolean view) {
		float yaw = -l.getYaw(),pitch = -l.getPitch();
		double xo = l.getX(), yo = l.getY(), zo = l.getZ();
		int Refcount = 0, ii = 0;
		if(data != null) {
		int energy = data.getenergy();
		data.setenergy(energy - output*2);
		p.getInventory().remove(Material.STICK);
		p.getInventory().addItem(getLaserGun(data.getoutput(), data.getenergy()));
		}
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
			AttachCheck(p, data, x, y, z);
		}
	}

	@EventHandler
	public void onPick(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if(!MainBattle.pdata.containsKey(p.getName()))
			return;
		PlayerData data = MainBattle.pdata.get(p.getName());
		ItemStack is = e.getItem().getItemStack();
		if((is == null)
				|| (is.getType() == Material.AIR)
				|| (!is.getItemMeta().hasDisplayName())
				|| (!is.getItemMeta().getDisplayName().equals(eball))
				|| (!is.getItemMeta().hasLore()))
			return;
		e.setCancelled(true);
		e.getItem().remove();
		Entity entity = e.getItem().getVehicle();
		if(entity != null)
			entity.remove();
		data.setenergy(40);
		WindowText.sendFullTitle(p, 0, 2, 1, "", ChatColor.GREEN + "エネルギーを補充しました");
		p.getInventory().remove(Material.STICK);
		p.getInventory().addItem(getLaserGun(data.getoutput(), data.getenergy()));
	}

	public void AttachCheck(Player p, PlayerData data, double x, double y, double z) {
		int llx = (int) x, lly = (int) y, llz = (int) z;
		for(World world : Bukkit.getWorlds()) {
			for(Entity entity : world.getEntities()) {
				if(!(entity instanceof Player))
					continue;
				Player target = (Player)entity;
				if(target != p) {
					if(!MainBattle.pdata.containsKey(target.getName()))
						continue;
					PlayerData targetdata = MainBattle.pdata.get(p.getName());
					int pteam = data.getTeamId();
					int tteam = targetdata.getTeamId();
					if(pteam == tteam)
						continue;
					int tlx = (int)target.getLocation().getX();
					int tly = (int)target.getLocation().getY();
					int tlz = (int)target.getLocation().getZ();
					double life = target.getHealth();
					double damage = 10;
					if((llx==tlx)&&(lly==tly)&&(llz==tlz)) {
						targetdata.setDamager(data);
						if(0 <= life-damage)
							targetdata.setKiller(data);
						Damager(p, target, damage);
					}
					if((llx==tlx)&&(lly==tly+1)&&(llz==tlz)) {
						targetdata.setDamager(data);
						if(0 <= life-damage*2)
							targetdata.setKiller(data);
						Damager(p, target, damage*2);
					}
				}
			}
		}
	}

	public void Damager(Player d, Player t, double damage) {
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

	public static ItemStack getLaserGun(int i, int ii) {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RESET.toString() + ChatColor.GREEN
				+"----------"+ChatColor.BLUE+ChatColor.BOLD.toString()
				+"Status"+ChatColor.GREEN+"----------");
		lore.add(ChatColor.RESET + "出力 ["+meter(i)+ChatColor.RESET+"]");
		lore.add(ChatColor.RESET + "耐久 ["+emeter(ii)+ChatColor.RESET+"]");
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
		meta.setDisplayName(eball);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static String meter(int i) {
		String meter = "||||||||||||||||||||";
		return ChatColor.DARK_PURPLE+meter.substring(0, i)+ChatColor.WHITE+meter.substring(i, 20);
	}

	public static String emeter(int i) {
		String meter = "||||||||||||||||||||||||||||||||||||||||";
		return ChatColor.DARK_PURPLE+meter.substring(0, i)+ChatColor.WHITE+meter.substring(i, 40);
	}
}
