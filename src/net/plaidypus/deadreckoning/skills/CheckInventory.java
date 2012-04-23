package net.plaidypus.deadreckoning.skills;

import net.plaidypus.deadreckoning.DeadReckoningGame;
import net.plaidypus.deadreckoning.actions.Action;
import net.plaidypus.deadreckoning.actions.ChangeStateAction;
import net.plaidypus.deadreckoning.board.GameBoard;
import net.plaidypus.deadreckoning.board.Tile;
import net.plaidypus.deadreckoning.entities.LivingEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class CheckInventory.
 */
public class CheckInventory extends Skill {

	/**
	 * Instantiates a new check inventory.
	 */
	public CheckInventory() {
		super();
	}

	/**
	 * Instantiates a new check inventory.
	 *
	 * @param source the source
	 */
	public CheckInventory(LivingEntity source) {
		super(source);
	}

	/* (non-Javadoc)
	 * @see net.plaidypus.deadreckoning.skills.Skill#makeAction(net.plaidypus.deadreckoning.board.Tile)
	 */
	@Override
	public Action makeAction(Tile target) {
		return new ChangeStateAction(source, target,
				DeadReckoningGame.INVENTORYSTATE);
	}

	/* (non-Javadoc)
	 * @see net.plaidypus.deadreckoning.skills.Skill#canTargetTile(net.plaidypus.deadreckoning.board.Tile)
	 */
	@Override
	public boolean canTargetTile(Tile t) {
		return t == source.getLocation();
	}

	/* (non-Javadoc)
	 * @see net.plaidypus.deadreckoning.skills.Skill#isInstant()
	 */
	public boolean isInstant() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.plaidypus.deadreckoning.skills.Skill#highlightRange(net.plaidypus.deadreckoning.board.GameBoard)
	 */
	@Override
	public void highlightRange(GameBoard board) {
		highlightRadial(board, 0);
	}

}
