package jp.kotmw.lb.datas;

public class PlayerData {
	private String name;
	private int output;
	private int energy;
	private PlayerData LatestDamager;
	private PlayerData LatestKiller;
	private String stage;
	private int teamid;
	private int killcount;
	private int deathcount;
	private int point;
	private boolean energyboost;
	private boolean powerup;
	private boolean infinity;

	public PlayerData(String p) {
		name = p;
		output = 20;
		energy = 40;
	}

	public void setStage(String s) {stage = s;}

	public void setoutput(int i) {output = i;}

	public void setenergy(int i) {energy = i;}

	public void setDamager(PlayerData p) {LatestDamager = p;}

	public void setKiller(PlayerData p) {LatestKiller = p;}

	public void setTeamId(int t) {teamid = t;}

	public void addKillcount() {killcount++;}

	public void addDeathcount() {deathcount++;}

	public void setPoint(int p) {point = p;}

	public void setEnergyBoost(boolean b) {energyboost = b;}

	public void setPowerUP(boolean b) {powerup = b;}

	public void setInfinity(boolean b) {infinity = b;}

	//*********************************************************//

	public String getName() {return name;}

	public String getStage() {return stage;}

	public int getoutput() {return output;}

	public int getenergy() {return energy;}

	public PlayerData getLatestDamager() {return LatestDamager;}

	public PlayerData getLatestKiller() {return LatestKiller;}

	public int getTeamId() {return teamid;}

	public int getKillCount() {return killcount;}

	public int getDeathCount() {return deathcount;}

	public int getPoint() {return point;}

	public boolean getEnergyBoost() {return energyboost;}

	public boolean getPowerUP() {return powerup;}

	public boolean getInfinity() {return infinity;}
}
