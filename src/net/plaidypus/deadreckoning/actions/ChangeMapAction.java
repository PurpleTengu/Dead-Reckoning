package net.plaidypus.deadreckoning.actions;

import net.plaidypus.deadreckoning.board.Tile;
import net.plaidypus.deadreckoning.entities.Entity;

public class ChangeMapAction extends Action{
	
	Entity toWrite;
	
	public ChangeMapAction(Tile source, Tile target, Entity toOverWrite) {
		super(source, target);
		toWrite = toOverWrite;
		
		takesTurn=false;
	}

	protected boolean apply(int delta) {
		source.getParent().placeEntity(target, toWrite);
		return true;
	}

	@Override
	public String getMessage() {
		return null;
	}
}
