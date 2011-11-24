package net.plaidypus.deadreckoning.state;

import java.util.ArrayList;

import net.plaidypus.deadreckoning.ItemGrid;
import net.plaidypus.deadreckoning.Tile;
import net.plaidypus.deadreckoning.Items.Item;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class LootState extends BasicGameState{
	
	static ItemGrid gridA, gridB;
	static Image background;
	static int selector;
	
	Input input;
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {	
		input = gc.getInput();
	}
	
	public static void makeFrom(Image background, ArrayList<Item> inventoryA, ArrayList<Item> inventoryB){
		LootState.background = background;
		LootState.gridA.setContents(inventoryA);
		LootState.gridB.setContents(inventoryB);
		selector = 0;
	}
	
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
		
	}
	
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
	}
	
	public int getID() {
		return 0;
	}

}