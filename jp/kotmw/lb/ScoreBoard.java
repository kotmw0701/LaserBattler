package jp.kotmw.lb;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreBoard {

	public static Map<String, Scoreboard> scoreboard = new HashMap<>();

	public void createScoreBoard() {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

		Objective obj = sb.registerNewObjective("SplatScoreboard", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Splatoon");

		Team team1 = sb.registerNewTeam("LaserBattleTeam1");
		team1.setPrefix(ChatColor.RED.toString());
		team1.setSuffix(ChatColor.RESET.toString());
		team1.setAllowFriendlyFire(false);
		team1.setCanSeeFriendlyInvisibles(false);
		team1.setNameTagVisibility(NameTagVisibility.ALWAYS);

		Team team2 = sb.registerNewTeam("SplatTeam2");
		team2.setPrefix(ChatColor.BLUE.toString());
		team2.setSuffix(ChatColor.RESET.toString());
		team2.setAllowFriendlyFire(false);
		team2.setCanSeeFriendlyInvisibles(false);
		team2.setNameTagVisibility(NameTagVisibility.ALWAYS);
		scoreboard.put("test", sb);
	}
}
