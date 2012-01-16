package net.plaidypus.deadreckoning;


import java.util.ArrayList;

import net.plaidypus.deadreckoning.entities.Entity;
import net.plaidypus.deadreckoning.entities.LivingEntity;
import net.plaidypus.deadreckoning.grideffects.GridEffect;
import net.plaidypus.deadreckoning.hudelements.GameplayElement;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


public class GameBoard {

	public ArrayList<Entity> ingameEntities;

	Tile[][] board;
	Tile primaryHighlight;
	int width, height;

	ArrayList<GridEffect> overEffects, underEffects;
	
	GameplayElement GameplayLayer;
	
	static final Color primaryHighlightColor = new Color(255, 75, 23);

	public GameBoard(GameplayElement g, int width, int height) {
		board = new Tile[width][height];
		this.width = width;
		this.height = height;
		this.GameplayLayer=g;
	}

	public void placeEntity(Tile t, Entity e) {
		placeEntity(t.getX(), t.getY(), e);
	}

	public void placeEntity(int x, int y, Entity e) {
		board[x][y].setEntity(e);
		ingameEntities.add(e);
	}

	public void clearTile(int x, int y) {
		ingameEntities.remove(board[x][y].getEntity());
		board[x][y].disconnectEntity();
	}

	public void moveEntity(Tile source, Tile target) {
		target.setEntity(source.getEntity());
		source.disconnectEntity();
	}

	public void init() throws SlickException {
		ingameEntities = new ArrayList<Entity>(0);
		overEffects = new ArrayList<GridEffect>(0);
		underEffects = new ArrayList<GridEffect>(0);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				board[x][y] = new Tile(this, x, y);
			}
		}
	}

	public Tile getTileAt(int x, int y) {
		return board[x][y];
	}

	public void render(Graphics g, float xoff, float yoff) {
		for (int x = 0; x < 25; x++) {
			for (int y = 0; y < 25; y++) {
					board[x][y].render(g,
							x*DeadReckoningGame.tileSize + xoff,
							y*DeadReckoningGame.tileSize + yoff);
			}
		}

		if (primaryHighlight != null) {
			g.setColor(primaryHighlightColor);
			g.drawRect(primaryHighlight.getX() * DeadReckoningGame.tileSize
					+ (int) xoff, primaryHighlight.getY()
					* DeadReckoningGame.tileSize + (int) yoff,
					DeadReckoningGame.tileSize, DeadReckoningGame.tileSize);
		}

		for (int i = 0; i < underEffects.size(); i++) {
			underEffects.get(i).render(g, xoff, yoff);
		}

		for (int x = 0; x < 25; x++) {
			for (int y = 0; y < 25; y++) {
				if (!board[x][y].isOpen() && board[x][y].lightLevel >= 1 && board[x][y].isVisible()) {
					board[x][y].getEntity().render(g,
							x*DeadReckoningGame.tileSize + xoff,
							y*DeadReckoningGame.tileSize + yoff);
				}
			}
		}

		for (int i = 0; i < overEffects.size(); i++) {
			overEffects.get(i).render(g, xoff, yoff);
		}
	}

	public void updateSelctor(Input i, float xOff, float yOff) {

		if (i.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			primaryHighlight = board[Utilities.limitTo(
					(i.getMouseX() - (int) xOff) / DeadReckoningGame.tileSize,
					0, this.getWidth())][Utilities.limitTo(
					(i.getMouseY() - (int) yOff) / DeadReckoningGame.tileSize,
					0, this.getHeight())];
		}

		if (primaryHighlight != null) {
			if (i.isKeyPressed(Input.KEY_LEFT)) {
				primaryHighlight = primaryHighlight.getToLeft();
			}
			if (i.isKeyPressed(Input.KEY_RIGHT)) {
				primaryHighlight = primaryHighlight.getToRight();
			}
			if (i.isKeyPressed(Input.KEY_UP)) {
				primaryHighlight = primaryHighlight.getToUp();
			}
			if (i.isKeyPressed(Input.KEY_DOWN)) {
				primaryHighlight = primaryHighlight.getToDown();
			}
		}
	}

	public void updateAllTiles(GameContainer gc, int delta) {

		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				if (!board[x][y].isOpen()) {
					board[x][y].getEntity().update(gc, delta);
					if(board[x][y].getEntity().toKill){
						board[x][y].clearTile();
					}
				}
			}
		}

		for (int i = 0; i < underEffects.size(); i++) {
			underEffects.get(i).update(delta);
			if (underEffects.get(i).isComplete()) {
				underEffects.remove(i);
				i -= 1;
			}
		}

		for (int i = 0; i < overEffects.size(); i++) {
			overEffects.get(i).update(delta);
			if (overEffects.get(i).isComplete()) {
				overEffects.remove(i);
				i -= 1;
			}
		}
	}
	
	public void updateBoardEffects(GameContainer gc, int delta) {
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				if (!board[x][y].isOpen()) {
					board[x][y].getEntity().updateBoardEffects(gc, delta);
				}
			}
		}
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void addEffectUnder(GridEffect g) {
		if(g!=null){this.underEffects.add(g);}
	}

	public void addEffectOver(GridEffect g) {
		if(g!=null){this.overEffects.add(g);}
	}
	
	public void addEffectUnder(Tile t,GridEffect g) {
		if(g!=null){
			g.setLocation(t);
			this.underEffects.add(g);
		}
	}

	public void addEffectOver(Tile t,GridEffect g) {
		if(g!=null){
			g.setLocation(t);
			this.overEffects.add(g);
		}
	}

	public void highlightSquare(int x, int y) {
		board[x][y].setHighlighted(1);
	}

	public boolean isTileHighlighted(int x, int y) {
		return board[x][y].getHighlighted() == 1;
	}

	public void setPrimairyHighlight(int x, int y) {
		this.primaryHighlight = board[x][y];
	}

	public void setPrimairyHighlight(Tile t) {
		this.primaryHighlight = t;
	}

	public Tile getPrimairyHighlight() {
		return primaryHighlight;
	}

	public void clearPrimaryHighlight() {
		this.primaryHighlight = null;
	}

	public void clearHighlightedSquares() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				board[x][y].setHighlighted(0);
			}
		}
	}

	//TODO block light based on transparency / vision
	public void lightInRadius(Tile location, float VIS) {
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				int dist = (int) Utilities.getDistance(location, board[x][y]);
				if (dist <= VIS) {
					float level = Utilities.limitTo(VIS + 1 - dist, 1,
							Tile.numLightLevels);
					if (level > (int)board[x][y].lightLevel && isLineofSight(location,board[x][y])) {
						board[x][y].lightLevel = level;
					}
				}
			}
		}
	}

	public void revealFromEntity(LivingEntity e) {
		for(int i=0; i<width; i++){	revealAlongVector(e.getLocation(),this.getTileAt(i,0)); }
		for(int i=0; i<width; i++){	revealAlongVector(e.getLocation(),this.getTileAt(i,height-1)); }
		for(int i=0; i<height-2; i++){	revealAlongVector(e.getLocation(),this.getTileAt(0,i+1)); }
		for(int i=0; i<height-2; i++){	revealAlongVector(e.getLocation(),this.getTileAt(width-1,i+1)); }
	}
	
	/**
	 * returns if the vision was blocked
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean revealAlongVector(Tile a, Tile b){//TODO it's a work in progress
		if(a.getX()!=b.getX()){
			double slope = (double)(a.getY()-b.getY())/(a.getX()-b.getX());
			int iteration = (b.getX()-a.getX())/Math.abs(b.getX()-a.getX());
			int lasty = a.getY();
			for(int i=1; i<Math.abs(a.getX()-b.getX())+1;i++){
					int x = a.getX()+(i*iteration);
					int newy = (int) Math.round(a.getY()+i*iteration*slope);
					if (revealAlongVector(getTileAt(x-iteration,lasty),getTileAt(x-iteration,newy)) || revealAlongVector(getTileAt(x,newy),getTileAt(x,newy))){
						return true;
					}
					lasty=newy;
			}
		}
		else if (a!=b){
			int iteration = (b.getY()-a.getY())/Math.abs(b.getY()-a.getY());
			for(int i=0; i<Math.abs(a.getY()-b.getY())+1; i++){
				Tile target = a.getParent().getTileAt(a.getX(), a.getY()+i*iteration);
				target.visibility=true;
				if(target.lightLevel>0){
					target.explored=true;
				}
				if(!target.isTransparent()){
					return true;
				}
			}
		}
		else{
			a.visibility=true;
			if(a.lightLevel>0){
				a.explored=true;
			}
			return false;
		}
		return false;
	}

	public boolean isLineofSight(Tile a, Tile b){
		if(a.getX()!=b.getX()){
			double slope = (double)(a.getY()-b.getY())/(a.getX()-b.getX());
			int iteration = (b.getX()-a.getX())/Math.abs(b.getX()-a.getX());
			int lasty = a.getY();
			for(int i=1; i<Math.abs(a.getX()-b.getX())+1;i++){
					int x = a.getX()+(i*iteration);
					int newy = (int) Math.round(a.getY()+i*iteration*slope);
					if (!isLineofSight(getTileAt(x-iteration,lasty),getTileAt(x-iteration,newy)) || !isLineofSight(getTileAt(x,newy),getTileAt(x,newy))){
						return false;
					}
					lasty=newy;
			}
		}
		else if (a!=b){
			int iteration = (b.getY()-a.getY())/Math.abs(b.getY()-a.getY());
			for(int i=0; i<Math.abs(a.getY()-b.getY()); i++){
				Tile target = a.getParent().getTileAt(a.getX(), a.getY()+i*iteration);
				if(!target.isTransparent()){
					return false;
				}
			}
		}
		else{
			return a.isTransparent();
		}
		return true;
	}
	
	public ArrayList<Tile> findAvailablePaths(Tile source, int wanderDist){//TODO limit generated array to dimensions of board
		ArrayList<Tile> toRet = new ArrayList<Tile> (0);
		
		double[][] tiles = new double[wanderDist*2+1][wanderDist*2+1];
		for(int i=0; i< wanderDist*2+1; i++){
			for(int t=0; t< wanderDist*2+1; t++){
				tiles[i][t]=wanderDist+1;
			}
		}
		tiles[wanderDist][wanderDist]=0;
		
		
		for(int i=0; i<wanderDist; i++){
			for(int x=0; x< wanderDist*2+1; x++){
				for(int y=0; y< wanderDist*2+1; y++){
					for(int a=-1; a<2; a++){
						for(int b=-1; b<2; b++){
							if(Math.abs(b)!=Math.abs(a)){
								double tot=tiles[Utilities.limitTo(x+a,0,tiles.length)] [Utilities.limitTo(y+b,0,tiles[x].length)];								
								if(tiles[x][y]>=tot+1
										&& coordInGrid(source.getX()-wanderDist+x,source.getY()-wanderDist+y)
										&& getTileAt(source.getX()-wanderDist+x,source.getY()-wanderDist+y).isOpen()){
									tiles[x][y]=tot+1;
								}
							}
						}
					}
				}
			}
		}
		
		for(int y=0; y<tiles.length; y++){
			for(int x=0; x<tiles[y].length; x++){
				int ax = source.getX()-wanderDist+x;
				int ay = source.getY()-wanderDist+y;
				if(tiles[x][y]<=wanderDist && coordInGrid(ax,ay)){
					toRet.add(getTileAt(ax,ay));
				}
			}
		}
		
		toRet.remove(source);
		return toRet;
	}
	
	public void highLightAvailablePaths(Tile source, int wanderDist){
		ArrayList<Tile> targetableTiles = findAvailablePaths(source,wanderDist);
		System.out.println(targetableTiles);
		for(int i=0; i<targetableTiles.size() ;i++){
			targetableTiles.get(i).highlighted=Tile.HIGHLIGHT_CONFIRM;
		}
	}
	
	/**
	 * makes every tile on the board have a low light level, and makes it invisible
	 */
	public void HideAll() {
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				board[x][y].lightLevel = 0;
				board[x][y].visibility=false; //TODO visibility
			}
		}
	}
	
	public boolean coordInGrid(int x, int y){
		return x>=0 && y>=0 && x<width && y<width;
	}

	public GameplayElement getGame() {
		return GameplayLayer;
	}

	public boolean isIdle() {
		for(int i=0; i<this.ingameEntities.size() ;i++){
			if(!ingameEntities.get(i).isIdle()){
				return false;
			}
		}
		return true;
	}

}
