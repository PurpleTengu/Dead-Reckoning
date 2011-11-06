package net.plaidypus.deadreckoning.actions;

import net.plaidypus.deadreckoning.Tile;
import net.plaidypus.deadreckoning.Utilities;
import net.plaidypus.deadreckoning.entities.Entity;
import net.plaidypus.deadreckoning.entities.LivingEntity;

public class AttackAction extends Action{
	
	int damage;
	
	public AttackAction( Tile source, Tile target, int damage) {
		super(source, target);
		this.damage = damage;
	}

	protected void apply(int delta) {
		try{ applyToEntity((LivingEntity)target.getEntity()); }
		catch(Exception e){ applyToEntity(target.getEntity()); }
	}
	
	private void applyToEntity(Entity entity){this.completed=true;}
	
	private void applyToEntity(LivingEntity e){
		
		e.setCurrentAnimation(LivingEntity.ANIMATION_ATTACK);
		e.HP-=damage;
		
		int xdiff = source.getX()-target.getX();
		int ydiff = source.getY()-target.getY();
		
		if(ydiff<xdiff*(Utilities.booleanPlusMin(e.getFacing()))){
			e.setCurrentAnimation(LivingEntity.ANIMATION_FLINCH_BACK);
		}
		else{
			e.setCurrentAnimation(LivingEntity.ANIMATION_FLINCH_FRONT);
		}

		this.completed=true;

	}
	
	public int calculateRange(LivingEntity source) {
		return 1;
	}

}