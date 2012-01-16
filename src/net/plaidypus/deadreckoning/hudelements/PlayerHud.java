package net.plaidypus.deadreckoning.hudelements;

import net.plaidypus.deadreckoning.entities.Player;
import net.plaidypus.deadreckoning.hudelements.GameplayElement;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class PlayerHud extends HudElement{
	
	Player target;
	Image img;
	int hookElement;
	
	public PlayerHud(int x, int y, int bindMethod, int hookElement) {
		super(x, y, bindMethod, false);
		this.hookElement=hookElement;
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
	}

	@Override
	public void makeFrom(Object o) {}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		img = new Image("res/HUD/PlayerBox.png");
		GameplayElement p = (GameplayElement)(this.getParent().getElement(hookElement));
		this.target = p.player;
	}

	public int getWidth() {
		return img.getWidth();
	}

	public int getHeight() {
		return img.getHeight();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) {
		g.drawImage(img,getX(),getY());
		g.drawImage(target.getProfession().getPortriat(),getX()+19,getY()+23);
		g.setColor(new Color(200,70,70));
		g.fillRect(getX()+126, getY()+25, 75*target.HP/target.maxHP, 9);
		g.setColor(new Color(70,70,200));
		g.fillRect(getX()+126, getY()+49, 75*target.MP/target.maxMP, 9);
		g.setColor(new Color(200,200,70));
		g.fillRect(getX()+126, getY()+74, 75*target.EXP/target.getEXPforLevel(), 9);
	}

}
