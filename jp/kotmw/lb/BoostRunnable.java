package jp.kotmw.lb;

import jp.kotmw.lb.GameItems.GameItemType;
import jp.kotmw.lb.datas.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BoostRunnable extends BukkitRunnable {

	GameItemType boosttype;
	PlayerData data;
	String type;
	int second;

	public BoostRunnable(PlayerData data ,GameItemType boosttype ,int second ) {
		this.boosttype = boosttype;
		this.data = data;
		this.second = second;
		this.type = boosttype.getName();
	}

	@Override
	public void run() {
		if(!MainBattle.hasinGame(data.getName())) {
			this.cancel();
			return;
		}
		if(60 >= second) {
			Bukkit.getPlayer(data.getName()).sendMessage(Main.pPrefix + type + " の効果時間終了まであと1分です");
		} else if(0 >= second) {
			Bukkit.getPlayer(data.getName()).sendMessage(Main.pPrefix + type + " の効果時間が終了しました");
			switch(boosttype) {
			case EnergyBoost:
				data.setEnergyBoost(false);
				break;
			case PowerUP:
				data.setPowerUP(false);
				break;
			case OpponentGrowing:
				break;
			}
			this.cancel();
		}
		second--;
	}

}
