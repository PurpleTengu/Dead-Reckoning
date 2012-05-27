package net.plaidypus.deadreckoning.actions;

import net.plaidypus.deadreckoning.DeadReckoningGame;
import net.plaidypus.deadreckoning.board.Tile;
import net.plaidypus.deadreckoning.entities.Entity;

// TODO: Auto-generated Javadoc
/**
 * The Class Action.
 */
public abstract class Action {

	/** The source. */
	public Entity source;
	
	/** The target. */
	public Tile target;

	/** The takes turn. */
	public boolean completed, takesTurn;

	/**
	 * actions are the main method of changing things in the game's environment.
	 *
	 * @param source the tile the entity creating the action is standing on
	 * @param target the tile the action is going to target
	 */
	public Action(Entity source, Tile target) {
		this.source = source;
		this.target = target;
		completed = false;
		takesTurn = true;
	}

	/**
	 * applies the action. called repeatedley.
	 *
	 * @param message the message
	 * @return if the action is complete (will not continue to call apply once
	 * completed)
	 */
	public static void sendMessage(String message) {
		DeadReckoningGame.instance.getMessageElement().addMessage(message);
	}

	/**
	 * Checks if is noticed.
	 *
	 * @return true, if is noticed
	 */
	protected abstract boolean isNoticed();

	/**
	 * Apply action.
	 *
	 * @param delta the delta
	 */
	public void applyAction(int delta) {
		if (!completed) {
			completed = apply(delta);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String[] p = this.getClass().toString().split("actions.");

		return p[p.length - 1] + " " + this.source + " -> " + this.target;
	}

	/**
	 * Apply.
	 *
	 * @param delta the delta
	 * @return true, if successful
	 */
	protected abstract boolean apply(int delta);
}