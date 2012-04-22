package net.plaidypus.deadreckoning;

import java.io.File;
import java.util.ArrayList;

import net.plaidypus.deadreckoning.entities.Player;
import net.plaidypus.deadreckoning.generator.Biome;
import net.plaidypus.deadreckoning.hudelements.*;
import net.plaidypus.deadreckoning.hudelements.game.GameplayElement;
import net.plaidypus.deadreckoning.hudelements.game.PlayerHudElement;
import net.plaidypus.deadreckoning.hudelements.game.StatusTrackerElement;
import net.plaidypus.deadreckoning.hudelements.game.substates.ItemGridElement;
import net.plaidypus.deadreckoning.hudelements.game.substates.ItemGridInteractionElement;
import net.plaidypus.deadreckoning.hudelements.game.substates.MiniMap;
import net.plaidypus.deadreckoning.hudelements.game.substates.ReturnToGameElement;
import net.plaidypus.deadreckoning.hudelements.menuItems.FairyLights;
import net.plaidypus.deadreckoning.hudelements.simple.ColorFiller;
import net.plaidypus.deadreckoning.hudelements.simple.StillImageElement;
import net.plaidypus.deadreckoning.hudelements.simple.StringPutter;
import net.plaidypus.deadreckoning.hudelements.simple.TextElement;
import net.plaidypus.deadreckoning.state.ExclusiveHudLayersState;
import net.plaidypus.deadreckoning.state.HudLayersState;
import net.plaidypus.deadreckoning.state.MainMenuState;
import net.plaidypus.deadreckoning.state.NewGameState;
import net.plaidypus.deadreckoning.state.PlayerViewerState;
import net.plaidypus.deadreckoning.state.SaveSelectorState;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.StateBasedGame;


public class DeadReckoningGame extends StateBasedGame
{
	public static final int LOOTSTATE		= 0,
							INVENTORYSTATE 	= 1,
							GAMEPLAYSTATE	= 2,
							MAINMENUSTATE	= 3,
							SAVESELECTSTATE	= 4,
							MAPSTATE 		= 5,
							SKILLSTATE 		= 6,
							NEWGAMESTATE	= 7,
							ERRORSTATE 		= 8;
	
	public static final int tileSize = 32;
	
	public static DeadReckoningGame instance;
	public static final Color
	menuColor = new Color(60,40,50,255),
	menuBackgroundColor = new Color(20,40,60),
	menuTextColor = new Color(255,255,255),
	menuTextBackgroundColor = new Color(0,0,0),
	mouseoverBoxColor = new Color(50,30,50,200),
	mouseoverTextColor = new Color(255,255,255,200),
	menuHighlightColor = new Color(210,210,0);
	
	protected ArrayList<HudElement> menuBackground;
	protected StringPutter messages;
	protected GameplayElement game;
	
	public static UnicodeFont menuFont, menuSmallFont;
	
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
		
		this.menuBackground = new ArrayList<HudElement>(0);
		SpriteSheet particles = new SpriteSheet(new Image("res/menu/particles.png"),50,50);
		menuBackground.add( new StillImageElement(0,0,HudElement.TOP_LEFT,new Image("res/menu/background.png")));
		menuBackground.add(new FairyLights(-50,-300,HudElement.BOTTOM_LEFT,850,250,80,particles));
		menuBackground.add(new FairyLights(-50,-200,HudElement.BOTTOM_LEFT,850,150,100,particles));
		menuBackground.add(new FairyLights(-50,-100,HudElement.BOTTOM_LEFT,850,100,120,particles));
		
		menuFont = new UnicodeFont("/res/visitor.ttf", 20,true,false);
		menuFont.addNeheGlyphs();
		menuFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE)); 
		menuFont.loadGlyphs();
		menuSmallFont = new UnicodeFont("/res/visitor.ttf", 18,true,false);
		menuSmallFont.addNeheGlyphs();
		menuSmallFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE)); 
		menuSmallFont.loadGlyphs();
		
		
		HudElement.calculateOffsets(container);
		Biome.init();
		new Player().init();
		
		this.addState(new MainMenuState(MAINMENUSTATE,this.menuBackground));
		
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
		
		this.addState(new HudLayersState(ERRORSTATE, new HudElement[] {
				new ColorFiller(menuBackgroundColor),
				new TextElement(0,0,HudElement.TOP_LEFT,"",menuTextColor,menuFont)
		}));
		
		this.addState(new SaveSelectorState(SAVESELECTSTATE,menuBackground));
		this.addState(new PlayerViewerState(SKILLSTATE));
		this.addState(new NewGameState(NEWGAMESTATE,menuBackground));
		
		this.enterState(MAINMENUSTATE);
	}
	
	public void flashException(Exception e) {
		HudLayersState s = (HudLayersState) this.getState(ERRORSTATE);
		s.makeFrom(new Object[] {null,e.getStackTrace().toString()});
		this.enterState(ERRORSTATE);
	}
	
}
