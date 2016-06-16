package jp.kotmw.lb.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BattleStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private static String stage;

	public BattleStartEvent(String stage) {
		BattleStartEvent.stage = stage;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public String getStage() {
		return stage;
	}


}
