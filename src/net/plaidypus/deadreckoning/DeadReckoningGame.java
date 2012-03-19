package net.plaidypus.deadreckoning;

import java.io.File;

import net.plaidypus.deadreckoning.entities.Chest;
import net.plaidypus.deadreckoning.generator.Biome;
import net.plaidypus.deadreckoning.hudelements.*;
import net.plaidypus.deadreckoning.skills.Fireball;
import net.plaidypus.deadreckoning.state.ExclusiveHudLayersState;
import net.plaidypus.deadreckoning.state.HudLayersState;
import net.plaidypus.deadreckoning.state.MainMenuState;
import net.plaidypus.deadreckoning.state.NewGameState;
import net.plaidypus.deadreckoning.state.PlayerViewerState;
import net.plaidypus.deadreckoning.state.SaveSelectorState;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.StateBasedGame;


public class DeadReckoningGame extends StateBasedGame
{
	
	public static final int LOOTSTATE		= 0;
	public static final int INVENTORYSTATE 	= 1;
	public static final int GAMEPLAYSTATE	= 2;
	public static final int MAINMENUSTATE	= 3;
	public static final int SAVESELECTSTATE	= 4;
	public static final int MAPSTATE 		= 5;
	public static final int SKILLSTATE 		= 6;
	public static final int NEWGAMESTATE	= 7;
	
	public static final int tileSize = 32;
	
	public static DeadReckoningGame instance;
	public static final Color
	menuColor = new Color(60,40,50,255),
	menuBackgroundColor = new Color(20,40,60),
	menuTextColor = new Color(255,255,255),
	menuTextBackgroundColor = new Color(0,0,0);
	
	protected StringPutter messages;
	protected GameplayElement game;
	
	public static UnicodeFont menuFont;
	
	DeadReckoningGame() throws SlickException
	{
		super("Dead Reckoning");
		
		this.messages = new StringPutter(0,0,HudElement.BOTTOM_LEFT,0,80);
		this.game = new GameplayElement(0);
		
		DeadReckoningGame.instance=this;
		
		String [] s = {"classes/" , "saves/"};
		for(int i=0; i<s.length; i++){
			File f = new File(s[i]);
			try{
				if(!f.exists()) f.mkdir();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	public GameplayElement getGameElement(){return game;}
	
	public StringPutter getMessageElement(){return messages;}
	
	public static void main(String[] args) throws SlickException
	{
		
		try{
			AppGameContainer app = new AppGameContainer(new DeadReckoningGame());
				app.setDisplayMode(800, 600, false);
				app.start();
				app.getInput().enableKeyRepeat();
		} catch(SlickException e) {
			e.printStackTrace();
		}
		
	}

	public HudLayersState getHudState(int id){
		return (HudLayersState)(getState(id));
	}
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		
		menuFont = new UnicodeFont("/res/visitor.ttf", 20,true,false);
			
		HudElement.calculateOffsets(container);
		Biome.init();
		Fireball.init();
		
		this.addState(new MainMenuState(MAINMENUSTATE));
		
		this.addState(new HudLayersState(GAMEPLAYSTATE,new HudElement[] {
				game,
				new PlayerHudElement(10,10,HudElement.TOP_LEFT,0),
				new StatusTrackerElement(10,120,HudElement.TOP_LEFT,0),
				messages
				} ));
		
		this.addState(new ExclusiveHudLayersState(LOOTSTATE, new HudElement[] { //TODO create custom state for this, instead of "interaction" element
				new StillImageElement(0,0,HudElement.TOP_LEFT),
				messages,
				new ItemGridElement(-241,-132,HudElement.CENTER_CENTER),
				new ItemGridElement(50,-132,HudElement.CENTER_CENTER),
				new ItemGridInteractionElement(2,3),
				new ReturnToGameElement()} ));
		
		this.addState(new HudLayersState(INVENTORYSTATE, new HudElement[] {
				new StillImageElement(0,0,HudElement.TOP_LEFT),
				messages,
				new ItemGridElement(0,0, HudElement.CENTER_CENTER),
				new ReturnToGameElement()}));
		
		this.addState(new HudLayersState(MAPSTATE, new HudElement[] {
				new StillImageElement(0,0,HudElement.TOP_LEFT),
				messages,
				new MiniMap(0,0,HudElement.CENTER_CENTER,DeadReckoningGame.GAMEPLAYSTATE),
				new ReturnToGameElement()}));
		
		this.addState(new SaveSelectorState(SAVESELECTSTATE));
		this.addState(new PlayerViewerState(SKILLSTATE));
		this.addState(new NewGameState(NEWGAMESTATE));
		
		this.enterState(MAINMENUSTATE);
	}
	
}
