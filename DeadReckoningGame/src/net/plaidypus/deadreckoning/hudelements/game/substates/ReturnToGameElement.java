package net.plaidypus.deadreckoning.hudelements.game.substates;

import net.plaidypus.deadreckoning.DeadReckoningGame;
import net.plaidypus.deadreckoning.hudelements.HudElement;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class ReturnToGameElement extends HudElement {
	
	int returnKey;
	
	public ReturnToGameElement(int key) {
		super(0, 0, 0, false);
		this.returnKey=key;
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE) || gc.getInput().isKeyPressed(Input.KEY_DELETE) || gc.getInput().isKeyPressed(returnKey)) {
			DeadReckoningGame.instance
					.enterState(DeadReckoningGame.GAMEPLAYSTATE);
			gc.getInput().clearKeyPressedRecord();
		}
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
	}

	@Override
	public void makeFrom(Object o) {
	}

}
