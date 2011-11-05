package net.plaidypus.deadreckoning.actions;

import net.plaidypus.deadreckoning.GameBoard;
import net.plaidypus.deadreckoning.Tile;
import net.plaidypus.deadreckoning.entities.LivingEntity;

public class MoveAction extends Action{
	
	int destinationX,destinationY;
	
	public MoveAction(LivingEntity source, int destinationX, int destinationY){
		super(source,source);
		this.destinationX=destinationX;
		this.destinationY=destinationY;
	}
	
	public void apply() {
		GameBoard board = target.getLocation().getParent();
		if(board.getTileAt(destinationX, destinationY).isOpen()){
			
			target.setCurrentAnimation(LivingEntity.ANIMATION_WALK);
			board.getTileAt(destinationX, destinationY).placeEntity(target);
			target.getLocation().removeEntity();
			
		}
		
	}
	
	public int calculateRange(LivingEntity source){
		return source.getMovementSpeed();
	}
	
}
