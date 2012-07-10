package net.plaidypus.deadreckoning.actions;

import net.plaidypus.deadreckoning.board.GameBoard;
import net.plaidypus.deadreckoning.board.Tile;
import net.plaidypus.deadreckoning.entities.Entity;
import net.plaidypus.deadreckoning.entities.InteractiveEntity;
import net.plaidypus.deadreckoning.entities.LivingEntity;

// TODO: Auto-generated Javadoc
/**
 * The Class EntityTypeAction.
 */
public abstract class EntityTypeAction extends Action {

	/** The layer. */
	int layer;

	/**
	 * Instantiates a new entity type action.
	 * 
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param targetLayer
	 *            the target layer
	 */
	public EntityTypeAction(int sourceID, Tile target, int targetLayer) {
		super(sourceID, target);
		layer = targetLayer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.plaidypus.deadreckoning.actions.Action#apply(int)
	 */
	protected boolean apply(int delta) {
		if (target.getX() > GameBoard.getEntity(this.sourceID).getX()) {
			GameBoard.getEntity(this.sourceID).setFacing(true);
		} else if (target.getX() < GameBoard.getEntity(this.sourceID).getX()) {
			GameBoard.getEntity(this.sourceID).setFacing(false);
		}
		
		if(!target.isOpen(layer)){
			if (LivingEntity.class.isAssignableFrom(target.getEntity(layer).getClass())) {
				return applyToEntity((LivingEntity) (target.getEntity(layer)),delta);
			} else if (InteractiveEntity.class.isAssignableFrom(target.getEntity(layer).getClass())) {
				return applyToEntity((InteractiveEntity) (target.getEntity(layer)),delta);
			} else {
				return applyToEntity((Entity) (target.getEntity(layer)),delta);
			} 
		}
		return true;
	}

	/**
	 * Apply to entity.
	 * 
	 * @param entity
	 *            the entity
	 * @return true, if successful
	 */
	protected abstract boolean applyToEntity(Entity entity, int delta);

	/**
	 * Apply to entity.
	 * 
	 * @param e
	 *            the e
	 * @return true, if successful
	 */
	protected abstract boolean applyToEntity(LivingEntity e, int delta);

	/**
	 * Apply to entity.
	 * 
	 * @param e
	 *            the e
	 * @return true, if successful
	 */
	protected abstract boolean applyToEntity(InteractiveEntity e, int delta);

}
