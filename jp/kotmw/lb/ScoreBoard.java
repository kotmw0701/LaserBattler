package jp.kotmw.lb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.kotmw.lb.FileDatas.StageFiles;
import jp.kotmw.lb.datas.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class ScoreBoard {

	public static Map<String, Scoreboard> scoreboard = new HashMap<>();
	public static Map<String, List<String>> teams = new HashMap<>();

	public static void createScoreBoard() {
		for(String stages : StageFiles.getStageList()) {
			List<String> team = new ArrayList<>();
			Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

			Objective obj = sb.registerNewObjective("LBBoard", "dummy");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			obj.setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "LaserBattle");

			for(int i = 1; i <= StageFiles.getTotalTeamNum(stages); i++) {
				team(sb, "LBTeam"+i, getTeamChatColor(i).toString());
				team.add("LBTeam"+i);
			}

			scoreboard.put(stages, sb);
			teams.put(stages, team);
		}
	}

	private static Team team(Scoreboard sb, String teamname, String prefix) {
		Team team = sb.registerNewTeam(teamname);
		team.setPrefix(prefix);
		team.setSuffix(ChatColor.RESET.toString());
		team.setAllowFriendlyFire(false);
		team.setCanSeeFriendlyInvisibles(false);
		team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
		return team;
	}

	public static Team setTeam(String stage, PlayerData data, int teamid) {
		Scoreboard sb = scoreboard.get(stage);
		Team bteam = sb.getEntryTeam(data.getName());
		Team team = sb.getTeam("LBTeam"+teamid);
		if((bteam != null)
				&& (!team.getName().equalsIgnoreCase(bteam.getName()))) {
			bteam.removeEntry(data.getName());
			team.addEntry(data.getName());
		} else if(bteam == null)
			team.addEntry(data.getName());
		data.setTeamId(teamid);
		return team;
	}

	public static Scoreboard getScoreboard(PlayerData data) {
		return scoreboard.get(data.getStage());
	}

	public static Team removeTeam(PlayerData data) {
		Scoreboard sb = scoreboard.get(data.getStage());
		Team team = sb.getTeam("LBTeam"+data.getTeamId());
		team.removeEntry(data.getName());
		return team;
	}

	public static List<String> mainScoreBoard(String stage) {
		List<String> board = new ArrayList<>();
		for(int i = 1; i <= StageFiles.getTotalTeamNum(stage); i++) {
			board.add("Team"+i+": " );
		}
		return board;
	}

	public static List<String> waitScoreBoard(String stage) {
		List<String> board = new ArrayList<>();
		board.add("Stage: "+stage);
		board.add("Players: ");
		board.add(ChatColor.RESET.toString());
		return board;
	}

	public static ChatColor getTeamChatColor(int i) {
		ChatColor color = ChatColor.RED;
		if(i == 1) color=ChatColor.RED;
		else if(i == 2) color=ChatColor.BLUE;
		else if(i == 3) color=ChatColor.DARK_GREEN;
		else if(i == 4) color=ChatColor.LIGHT_PURPLE;
		else if(i == 5) color=ChatColor.DARK_AQUA;
		else if(i == 6) color=ChatColor.DARK_GRAY;
		return color;
	}

	public static short getTeamWoolColor(int i) {
		short color=0;
		if(i == 1) color=14;
		else if(i == 2) color=11;
		else if(i == 3) color=13;
		else if(i == 4) color=6;
		else if(i == 5) color=9;
		else if(i == 6) color=7;
		return color;
	}

	public static WoolColorEnum getTeamLaserColor(int i) {
		WoolColorEnum color = WoolColorEnum.RED;
		if(i == 1) color=WoolColorEnum.RED;
		else if(i == 2) color=WoolColorEnum.BLUE;
		else if(i == 3) color=WoolColorEnum.TEAL;
		else if(i == 4) color=WoolColorEnum.PINK;
		else if(i == 5) color=WoolColorEnum.PURPLE;
		else if(i == 6) color=WoolColorEnum.GRAY;
		return color;
	}
}
