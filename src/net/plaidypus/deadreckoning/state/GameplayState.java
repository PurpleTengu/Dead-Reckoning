package net.plaidypus.deadreckoning.state;

import java.util.ArrayList;

import net.plaidypus.deadreckoning.GameBoard;
import net.plaidypus.deadreckoning.entities.Entity;
import net.plaidypus.deadreckoning.entities.Goblin;
import net.plaidypus.deadreckoning.entities.Player;
import net.plaidypus.deadreckoning.particles.DamageParticle;
import net.plaidypus.deadreckoning.particles.Particle;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class GameplayState extends BasicGameState
{
	
	int stateID = -1;
	int currentEntity;
	
	public static float cameraX, cameraY;
	public static float cameraDestX, cameraDestY;
	
	static final float cameraRate = (float) 0.2;
	
	Input input;
	Player player;
	
	GameBoard gb;
	
	public static ArrayList<Particle> particles;
	
	public GameplayState(int stateID) throws SlickException
	{
		this.stateID = stateID;
		currentEntity=0;
	}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		input=gc.getInput();
		
		DamageParticle.init();
		particles=new ArrayList<Particle>(0);
		
		gc.setTargetFrameRate(60);
		gc.setVSync(true);
		gb = new GameBoard(25,25);
		gb.init();
		player = new Player(gc.getInput());
		gb.placeEntity(4, 4, player);
		gb.placeEntity(24, 1, new Goblin());
		cameraX=0;
		cameraY=0;
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		gb.render(g,-cameraX,-cameraY);
		for(int i=0; i<particles.size();i++){
			particles.get(i).render(g,-cameraX,-cameraY);
		}
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		
		cameraX = cameraX+(cameraDestX-cameraX)*cameraRate;
		cameraY = cameraY+(cameraDestY-cameraY)*cameraRate;
		
		gb.HideAll();
		gb.revealInRadius(player.getLocation(),player.VIS);
		
		for(int i=0; i<particles.size();i++){
			particles.get(i).update(gc, sbg, delta);
			if(particles.get(i).toKill){
				particles.remove(i);
				i--;
			}
		}
		
		Entity current = gb.ingameEntities.get(currentEntity);
		if(current.isIdle()){
			if(player.canSee(current)){
				cameraDestX=current.getAbsoluteX()-gc.getWidth()/2;
				cameraDestY=current.getAbsoluteY()-gc.getHeight()/2;
			}
			if(Math.abs(cameraX-cameraDestX)<0.01 && Math.abs(cameraY-cameraDestY)<=0.01){
				current.setAction(current.chooseAction(gc,delta));
			}
			
		}
		if(!current.isIdle()){
			current.applyAction(gc, delta);
			if(current.isIdle()){
				currentEntity = (currentEntity+1)%gb.ingameEntities.size();
			}
		}
		
		gb.updateSelctor(input,-cameraX,-cameraY);
		gb.updateAllTiles(gc, delta);
		
	}

	public int getID() {
		return stateID;
	}
	
	public static void spawnParticle(Particle p){
		particles.add(p);
	}
	
}
