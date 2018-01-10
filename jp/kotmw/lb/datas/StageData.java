package jp.kotmw.lb.datas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.kotmw.lb.FileDatas.StageFiles;

import org.bukkit.Location;

public class StageData {
	private int TotalTeamNum;
	private Location loc1, loc2;
	private Location stayloc;
	private List<Integer> RespawnPoint = new ArrayList<>();
	private Map<Integer,List<Location>> teamlocs = new HashMap<>();

	public StageData(String stage) {
		this.TotalTeamNum = StageFiles.getTotalTeamNum(stage);
		this.loc1 = StageFiles.getStageLoc(stage, 1);
		this.loc2 = StageFiles.getStageLoc(stage, 2);
		this.stayloc = StageFiles.getStayRoom(stage);
		for(int i = 1; i <= TotalTeamNum; i++) {
			RespawnPoint.add(StageFiles.getRespawnPoint(stage, i));
			teamlocs.put(i, null);
		}
	}

	public int getTotalTeamNum() {
		return TotalTeamNum;
	}

	public Location getStageLoc1() {
		return loc1;
	}

	public Location getStageLoc2() {
		return loc2;
	}

	public Location getStayLoc() {
		return stayloc;
	}
}
