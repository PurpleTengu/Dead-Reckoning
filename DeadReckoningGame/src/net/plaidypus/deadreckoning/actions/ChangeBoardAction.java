package net.plaidypus.deadreckoning.actions;

import net.plaidypus.deadreckoning.board.GameBoard;
import net.plaidypus.deadreckoning.save.Save;

import org.newdawn.slick.SlickException;

// TODO: Auto-generated Javadoc
/**
 * The Class ChangeBoardAction.
 */
public class ChangeBoardAction extends Action {

	/** The target floor. */
	String targetFloor;

	/**
	 * Instantiates a new change board action.
	 * 
	 * @param source
	 *            the source
	 * @param targetFloor
	 *            the target floor
	 */
	public ChangeBoardAction(int sourceID, String targetFloor) {
		super(sourceID);
		this.targetFloor = targetFloor;
		this.takesTurn = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.plaidypus.deadreckoning.actions.Action#isNoticed()
	 */
	@Override
	protected boolean isNoticed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.plaidypus.deadreckoning.actions.Action#apply(int)
	 */
	@Override
	protected boolean apply(int delta) throws SlickException {
		Save.enterNewMap(
			GameBoard.getEntity(this.sourceID).getParent().getGame(),
			GameBoard.getEntity(this.sourceID).getParent().getSaveID(),
			targetFloor);
		return true;
	}

}
