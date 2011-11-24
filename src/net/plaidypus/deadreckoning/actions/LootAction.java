package net.plaidypus.deadreckoning.actions;

import net.plaidypus.deadreckoning.Tile;
import net.plaidypus.deadreckoning.entities.Entity;
import net.plaidypus.deadreckoning.entities.InteractiveEntity;
import net.plaidypus.deadreckoning.entities.LivingEntity;

public class LootAction extends EntityTypeAction{

	public LootAction(Tile source, Tile target) {
		super(source, target);
	}
	
	protected boolean applyToEntity(Entity entity){
		return true;
	}
	
	protected boolean applyToEntity(InteractiveEntity e){
		gotoLootScreen();
		return true;
	}
	
	protected boolean applyToEntity(LivingEntity e){
		if(e.isAlive()){
			//TODO damage the entityofShutup
		}
		else{
			gotoLootScreen();
		}
		return true;
	}
	
	private void gotoLootScreen(){
		System.out.println("I'll goto the thing with the thing later.");
	}

}
