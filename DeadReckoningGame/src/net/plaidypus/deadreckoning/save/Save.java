/*
 * 
 */
package net.plaidypus.deadreckoning.save;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.plaidypus.deadreckoning.board.GameBoard;
import net.plaidypus.deadreckoning.board.Tile;
import net.plaidypus.deadreckoning.entities.Entity;
import net.plaidypus.deadreckoning.entities.InteractiveEntity;
import net.plaidypus.deadreckoning.entities.LandingPad;
import net.plaidypus.deadreckoning.entities.Player;
import net.plaidypus.deadreckoning.generator.Biome;
import net.plaidypus.deadreckoning.generator.DungeonMap;
import net.plaidypus.deadreckoning.hudelements.game.GameplayElement;
import net.plaidypus.deadreckoning.items.Equip;
import net.plaidypus.deadreckoning.items.Item;
import net.plaidypus.deadreckoning.modloader.ModLoader;
import net.plaidypus.deadreckoning.statmaster.Profession;
import net.plaidypus.deadreckoning.statmaster.SkillProgression;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

/**
 * The Class Save. This class administers most of the basic file I/O for game
 * storage to disk
 */
public class Save {

	/** The location of the save. */
	String saveLocation;

	/** The name of the save. */
	String name;

	/** The current setPlayerPosition, I.E. the level where the player last left off. */
	String currentMap;

	/**
	 * Instantiates a new save.
	 * 
	 * @param saveLocation
	 *            the location to create the save at
	 */
	public Save(String saveLocation) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(saveLocation
					+ "/saveInformation.txt"));
			name = r.readLine();
			currentMap = r.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.saveLocation = saveLocation;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the location.
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return saveLocation;
	}

	/**
	 * Loads the game. self explanatory. loads player from player file loads
	 * board, entities from current map file (loadBoard and LoadEntities are
	 * passed the same BufferedReader, because the required data is stored on
	 * different lines of the same file)'
	 * 
	 * for initial startup
	 * 
	 * @param state
	 *            the GameplayElement into which the game will be loaded
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SlickException
	 *             the slick exception
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	public void loadGame(GameplayElement state, GameContainer c) throws SlickException {
		try {
			BufferedReader r = new BufferedReader(new FileReader(saveLocation + "/" + currentMap));
		GameBoard g = loadBoard(state, saveLocation, currentMap, r);

		BufferedReader playerReader = new BufferedReader(new FileReader(
				saveLocation + "/player.txt"));
		Player play = loadPlayerStatus(playerReader, c, g,
				loadPlayerProfession(playerReader));
		setPlayerPosition(play, g, state.lastMap);
		state.setPlayer(play);
		state.saveLocation = this.saveLocation;

		loadEntities(g, r);
		state.setBoard(g);
		
		} catch (FileNotFoundException e) {
			throw new SlickException("fuck",e);
		} catch (IOException e) {
			throw new SlickException("fuck",e);
		}
	}

	/**
	 * Load game.
	 * 
	 * static, meant to provide an easy way to get a GameBoard object without
	 * needing to deal with instanciation of saves.
	 * 
	 * requires that the player already be placed on the GameplayElement's GameBoard
	 * 
	 * @param game
	 *            the GameplayElement into which the game will be loaded
	 * @param saveLocation
	 *            the save location
	 * @param targetFloor
	 *            the target floor
	 * @return the game board
	 */
	public static void enterNewMap(GameplayElement game, String saveLocation,
		String targetFloor) throws SlickException{
		try {
			BufferedReader r;
			r = new BufferedReader(new FileReader(saveLocation
					+ "/" + targetFloor));
			
			GameBoard b = loadBoard(game, saveLocation, targetFloor, r);
			game.setBoard(b);
			loadEntities(b, r);
			setPlayerPosition(game.player, b, game.lastMap);
			
		} catch (FileNotFoundException e) {
			throw new SlickException("fuck",e);
		}
		game.resetCamera();
	}

	private static void setPlayerPosition(Player player, GameBoard b, String lastMap) {
		//find the landing pad if it's coming from the old map. Otherwise, use the player's coords
		if (!lastMap.equals("")) {
			Tile target = null;
			for (int x = 0; x < b.getWidth(); x++) {
				for (int y = 0; y < b.getHeight(); y++) {
					if (!b.getTileAt(x, y).isOpen(Tile.LAYER_PASSIVE_MAP) ){
						if( LandingPad.class.isAssignableFrom(
								b.getTileAt(x, y).
								getEntity(Tile.LAYER_PASSIVE_MAP).getClass()
								)){
							LandingPad pad = LandingPad.class.cast(b.getTileAt(x, y)
									.getEntity(Tile.LAYER_PASSIVE_MAP));
							if (pad.fromFloor.equals(lastMap)) {
								target = b.getTileAt(x, y);
								break;
							}
						}
					}
				}
			}
			b.insertEntity(0,target, player, Tile.LAYER_ACTIVE);
		} else {
			b.insertEntity(0,b.getTileAt(player.getX(), player.getY()), player, Tile.LAYER_ACTIVE);
		}
	}

	/**
	 * Loads the board from a BufferedReader. the board is stored as a series of
	 * char's, who's char# refers to the integer value, followed by entities and
	 * their locations
	 * 
	 * the order is as follows: 0-depth, 1-width, 2-height, 3 onward-contents
	 * 
	 * @param g
	 *            the GameplayElement into which to load
	 * @param saveLocation
	 *            the save location from which to read
	 * @param mapID
	 *            the ID of the map in the save
	 * @param r
	 *            the Buffered Reader from which to read the Board Data
	 * @return the game board
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SlickException
	 *             the slick exception
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public static GameBoard loadBoard(GameplayElement g, String saveLocation,
			String mapID, BufferedReader r) throws SlickException{
		try{
			GameBoard b = new GameBoard(g, saveLocation, mapID);
			b.depth = r.read();
			r.readLine();
			b.width = r.read();
			r.readLine();
			b.height = r.read();
			r.readLine();
			b.biome = Biome.getBiome(r.readLine());
			b.board = new Tile[b.width][b.height];
	
			for (int y = 0; y < b.height; y++) {
				for (int x = 0; x < b.width; x++) {
					int q = r.read();
					b.board[x][y] = new Tile(b, x, y, q);
				}
				r.readLine();
			}
	
			for (int y = 0; y < b.height; y++) {
				for (int x = 0; x < b.width; x++) {
					int q = r.read();
					b.board[x][y].blocking = (q == 1);
				}
				r.readLine();
			}
	
			for (int y = 0; y < b.height; y++) {
				for (int x = 0; x < b.width; x++) {
					int q = r.read();
					b.board[x][y].explored = (q == 1);
				}
				r.readLine();
			}
	
			return b;
		} catch (IOException e){
			throw new SlickException("Fuck",e);
		}
	}

	/**
	 * Loads entities from a BufferedReader. Each entity is on it's own line and
	 * has it's own custom method of parsing an Entity out of a String However,
	 * the first item in the declaration (items are separated by colons) is the
	 * classpath of the Entity this has the potential for malware in
	 * mods...maybe
	 * 
	 * at any rate, the general form is (Entity type, x, y, layer,....)
	 * 
	 * @param target
	 *            the GameBoard on to which the Entities should be loaded
	 * @param r
	 *            the BufferedReader from which to load
	 * @return an ArrayList of all the Loaded Entities. Not really used at all,
	 *         just there for the sake of convenience and potential extension
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	public static void loadEntities(GameBoard target,
			BufferedReader r) throws SlickException{
		String definition;
		try {
			definition = r.readLine();
		while (definition != null) {
			String[] defInfo = definition.split(":");
			Class<? extends Entity> clas;
			clas = ModLoader.loadClass(defInfo[0]).asSubclass(Entity.class);
			Entity e = clas.newInstance().makeFromString(target, defInfo);
			target.placeEntity(e.getLocation(),e,e.getLayer());
			definition = r.readLine();
		}
		} catch (IOException e) {
			throw new SlickException("unable to load files for save", e);
		} catch (InstantiationException e) {
			throw new SlickException("unable to Instanciate class", e);
		} catch (IllegalAccessException e) {
			throw new SlickException("Dafuq is you doing?", e);
		}
	}

	/**
	 * Saves a GameBoard. Self Explanatory
	 * 
	 * @param b
	 *            the b
	 * @param r
	 *            the r
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see Save.loadBoard
	 */
	public static void saveBoard(GameBoard b, BufferedWriter r)
			throws IOException {
		r.write(b.depth);
		r.newLine();
		r.write(b.width);
		r.newLine();
		r.write(b.height);
		r.newLine();
		r.write(b.getBiome().getClass().getCanonicalName());
		r.newLine();
		

		for (int y = 0; y < b.height; y++) {
			for (int x = 0; x < b.width; x++) {
				r.write(b.getTileAt(x, y).getTileFace());
			}
			r.newLine();
		}

		for (int y = 0; y < b.height; y++) {
			for (int x = 0; x < b.width; x++) {
				if (b.getTileAt(x, y).blocking) {
					r.write(1);
				} else {
					r.write(0);
				}
			}
			r.newLine();
		}

		for (int y = 0; y < b.height; y++) {
			for (int x = 0; x < b.width; x++) {
				if (b.getTileAt(x, y).explored) {
					r.write(1);
				} else {
					r.write(0);
				}
			}
			r.newLine();
		}

	}

	/**
	 * Save entities
	 * 
	 * @param b
	 *            the Board from which the Entities come
	 * @param r
	 *            the writer to which the entities should be written
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void saveEntities(GameBoard b, BufferedWriter r)
			throws IOException {
		Iterator<Entity> e = b.getIngameEntities().iterator();
		while(e.hasNext()){
			Entity en =  e.next();
			if(!Player.class.isAssignableFrom(en.getClass())){
				r.write(en.saveToString());
				r.newLine();
			}
		}
	}

	/**
	 * Saves the player to a file.
	 * 
	 * @param w
	 *            the w
	 * @param p
	 *            the p
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void savePlayerProfession(BufferedWriter w, Profession p)
			throws IOException {
		w.write(p.getParentMod() + "\n");
		w.write(p.getBaseClass());

		for (int i = 0; i < 3; i++) {
			w.newLine();
			w.write(p.getTrees()[i].sourceMod);
			w.newLine();
			w.write(p.getTrees()[i].sourceClass);
			w.write(p.getTrees()[i].sourceTree);
			w.write(p.getTrees()[i].getSkills()[0].getLevel());
			w.write(p.getTrees()[i].getSkills()[1].getLevel());
			w.write(p.getTrees()[i].getSkills()[2].getLevel());
			w.write(p.getTrees()[i].getSkills()[3].getLevel());
		}

		w.newLine();
		w.write(p.getLevel());

	}

	/**
	 * Make a new save from scratch. generates a whole dungeon, level by level,
	 * and saves it to the disk
	 * 
	 * @param fileLocation
	 *            the file location for the save
	 * @param nameofSave
	 *            the name of the save
	 * @param p
	 *            the profession generated by the class generator
	 * @return the save generated
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SlickException
	 *             the slick exception
	 */
	public static Save makeNewSave(String fileLocation, String nameofSave,
			Profession p) throws SlickException {
		try{
			new File(fileLocation).mkdir();
			File director = new File(fileLocation + "/saveInformation.txt");
			BufferedWriter r = new BufferedWriter(new FileWriter(director));
			r.write(nameofSave);
			r.newLine();
			r.write("floor0.map");
			r.close();
	
			DungeonMap map = new DungeonMap(16);
	
			Tile spawnTile = null;
	
			for (int i = 0; i < map.getDepth(); i++) {
				File floorFile = new File(fileLocation + map.getFloorName(i));
				floorFile.createNewFile();
				r = new BufferedWriter(new FileWriter(floorFile));
	
				GameBoard gameBoard = map.makeBoard(i);
	
				if (i == 0) {
					spawnTile = gameBoard.getValidSpawnTile();
				}
	
				Save.saveBoard(gameBoard, r);
				Save.saveEntities(gameBoard, r);
				r.close();
			}
	
			File playerFile = new File(fileLocation + "/player.txt");
			playerFile.createNewFile();
			BufferedWriter w = new BufferedWriter(new FileWriter(playerFile));
			savePlayerProfession(w, p);
			Player pl = new Player(p,null);
			pl.setLocation(spawnTile, Tile.LAYER_ACTIVE);
			savePlayerStatus(w, pl);
			w.close();
	
			Save s = new Save(fileLocation);
			return s;
		} catch (IOException e){
			throw new SlickException("unable to create save "+nameofSave,e);
		}
	}

	public static void updateSave(String saveLocation, Player p,
			GameBoard currentMap) throws IOException {
		File playerFile = new File(saveLocation + "/player.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(playerFile));

		Save.savePlayerProfession(w, p.getProfession());
		Save.savePlayerStatus(w, p);
		w.close();

		BufferedWriter r = new BufferedWriter(new FileWriter(saveLocation + "/"
				+ currentMap.mapID));
		Save.saveBoard(currentMap, r);
		Save.saveEntities(currentMap, r);
		r.close();

		File director = new File(saveLocation + "/saveInformation.txt");
		BufferedReader a = new BufferedReader(new FileReader(director));
		String name = a.readLine();
		a.close();

		BufferedWriter b = new BufferedWriter(new FileWriter(director));
		b.write(name);
		b.newLine();
		b.write(currentMap.mapID);
		b.close();
	}

	/**
	 * Loads the player.
	 * 
	 * @param r
	 *            the BufferedReader of the player file
	 * @return the player
	 * @throws SlickException
	 *             the slick exception
	 */
	private Profession loadPlayerProfession(BufferedReader r)
			throws SlickException {

		Profession p = Profession.loadProfession("core", 0);// either pull from
															// catalog of
															// parents or load
															// out of mod
		try {
			String parentMod = r.readLine();
			int baseClass = r.read();
			r.readLine();
			SkillProgression a = SkillProgression.loadTree(r.readLine(),
					r.read(), r.read());// TODO errors are happen here
			a.setLevels(r.read(), r.read(), r.read(), r.read());
			r.readLine();
			SkillProgression b = SkillProgression.loadTree(r.readLine(),
					r.read(), r.read());
			b.setLevels(r.read(), r.read(), r.read(), r.read());
			r.readLine();
			SkillProgression c = SkillProgression.loadTree(r.readLine(),
					r.read(), r.read());
			c.setLevels(r.read(), r.read(), r.read(), r.read());
			p = new Profession(parentMod, baseClass, a, b, c, 1);// TODO
																	// read/write
																	// player
			// level to savefile
			r.readLine();
			p.setLevel(r.read());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	private Player loadPlayerStatus(BufferedReader r, GameContainer c,
			GameBoard b, Profession p) throws SlickException, IOException {
		// TODO stuff
		r.readLine();
		int tileX = Integer.parseInt(r.readLine());
		int tileY = Integer.parseInt(r.readLine());
		int currentEXP = Integer.parseInt(r.readLine());
		int currentHP = Integer.parseInt(r.readLine());
		int currentMP = Integer.parseInt(r.readLine());
		ArrayList<Item> inventory = InteractiveEntity.loadItems(r.readLine().split(","));
		ArrayList<Item> equips = InteractiveEntity.loadItems(r.readLine().split(","));

		Player player = new Player(p, c.getInput());
		
		player.setLocation(b.getTileAt(tileX, tileY), Tile.LAYER_ACTIVE);
		player.EXP = currentEXP;
		player.HP=currentHP;
		player.MP=currentMP;
		player.getInventory().addAll(inventory);
		for(int i=0; i<equips.size(); i++){	
			if(equips.get(i)!=null){
				player.equipItem((Equip)equips.get(i));
			}
		}

		return player;
	}

	private static void savePlayerStatus(BufferedWriter w, Player p)
			throws IOException {
		w.newLine();
		w.write(Integer.toString(p.getX()));
		w.newLine();
		w.write(Integer.toString(p.getY()));
		w.newLine();
		w.write(Integer.toString(p.EXP));
		w.newLine();
		w.write(Integer.toString(p.HP));
		w.newLine();
		w.write(Integer.toString(p.MP));
		w.newLine();
		w.write(InteractiveEntity.getInventoryAsString( p.getInventory(), p.inventorySize ));
		w.newLine();
		w.write(InteractiveEntity.getInventoryAsString( p.getEquips(), p.inventorySize ));
	}

	/**
	 * Enumerates the saves in the default save location. saves must conform to
	 * the format "saves/SAVE [NUMBER]" to be counted.
	 * 
	 * @return the number of saves counted
	 */
	public static int enumerateSaves() {
		int numSaves = 0;
		File f = new File("saves/SAVE " + numSaves + "/");
		while (f.exists()) {
			numSaves++;
			f = new File("saves/SAVE " + numSaves + "/");
		}
		return numSaves;
	}

}
