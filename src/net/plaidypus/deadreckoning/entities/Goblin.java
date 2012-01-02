package net.plaidypus.deadreckoning.entities;

import java.util.ArrayList;

import net.plaidypus.deadreckoning.Tile;
import net.plaidypus.deadreckoning.Utilities;
import net.plaidypus.deadreckoning.actions.Action;
import net.plaidypus.deadreckoning.actions.MoveAction;
import net.plaidypus.deadreckoning.actions.WaitAction;
import net.plaidypus.deadreckoning.skills.Movement;
import net.plaidypus.deadreckoning.skills.Skill;

import org.newdawn.slick.GameContainer;

public class Goblin extends LivingEntity {

	public Skill movement;

	public int direction;

	/**
	 * a testing monster class
	 */
	public Goblin(Tile targetTile) {
		super("res/player.entity", targetTile);
		movement = new Movement(this);
		direction = 0;
	}

	/**
	 * if something is blocking its path, it will turn left
	 */
	public Action chooseAction(GameContainer gc, int delta) {
		Tile dest = this.getParent().getTileAt(
				Utilities.limitTo(this.getX()+Utilities.randInt(-2, 2),0,getParent().getWidth()),
				Utilities.limitTo(this.getY()+Utilities.randInt(-2, 2),0,getParent().getHeight()));
		if(dest.isOpen() && !dest.equals(this.getLocation())){
			return new MoveAction(this.getLocation(),dest);
		}
		return new WaitAction(this.getLocation());
	}

	public void updateBoardEffects(GameContainer gc, int delta) {}
	
}
