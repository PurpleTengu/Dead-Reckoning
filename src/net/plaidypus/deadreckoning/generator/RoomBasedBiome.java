package net.plaidypus.deadreckoning.generator;

import java.util.ArrayList;

import net.plaidypus.deadreckoning.Utilities;
import net.plaidypus.deadreckoning.board.GameBoard;
import net.plaidypus.deadreckoning.board.Tile;
import net.plaidypus.deadreckoning.entities.Stair;

import org.newdawn.slick.SlickException;

public abstract class RoomBasedBiome extends Biome{
	
	int numRooms, roomSizeMin=5, roomSizeMax=10;
	
	public RoomBasedBiome(int numRooms){
		this.numRooms = numRooms;
	}
	
	public GameBoard populateBoard(GameBoard target, ArrayList<int[]> rooms, ArrayList<Stair> linkedLevels){
		
		for(int i=0; i<rooms.size()-1; i++){
			drawCooridor(target,rooms.get(i), rooms.get(i+1));
		}
		

		for(int i=0; i<rooms.size(); i++){
			for(int x=1; x<rooms.get(i)[2]-1; x++){
				target.getTileAt(rooms.get(i)[0]+x,rooms.get(i)[1]).setTileFace(Tile.TILE_WALL_UP);
				target.getTileAt(rooms.get(i)[0]+x,rooms.get(i)[1]+rooms.get(i)[3]-1).setTileFace(Tile.TILE_WALL_DOWN);
			}
			for(int y=1; y<rooms.get(i)[3]-1; y++){
				target.getTileAt(rooms.get(i)[0],rooms.get(i)[1]+y).setTileFace(Tile.TILE_WALL_LEFT);
				target.getTileAt(rooms.get(i)[0]+rooms.get(i)[2]-1,rooms.get(i)[1]+y).setTileFace(Tile.TILE_WALL_RIGHT);
			}
			
			target.getTileAt(rooms.get(i)[0],rooms.get(i)[1]).setTileFace(Tile.TILE_WALL_UP_LEFT);
			target.getTileAt(rooms.get(i)[0],rooms.get(i)[1]+rooms.get(i)[3]-1).setTileFace(Tile.TILE_WALL_DOWN_LEFT);
			target.getTileAt(rooms.get(i)[0]+rooms.get(i)[2]-1,rooms.get(i)[1]).setTileFace(Tile.TILE_WALL_UP_RIGHT);
			target.getTileAt(rooms.get(i)[0]+rooms.get(i)[2]-1,rooms.get(i)[1]+rooms.get(i)[3]-1).setTileFace(Tile.TILE_WALL_DOWN_RIGHT);
			
			for(int x=1; x<rooms.get(i)[2]-1;x++){
				for(int y=1; y<rooms.get(i)[3]-1;y++){
						target.getTileAt(rooms.get(i)[0]+x,rooms.get(i)[1]+y).setTileFace(Tile.TILE_EMPTY);
				}
			}
		}
		
		return target;
	}
	
	public GameBoard drawCooridor(GameBoard target, int[] roomA, int[] roomB){
		int x = roomA[0]+roomA[2]/2;
		int y = roomA[1]+roomA[3]/2;
		int xDiff =  (roomB[0]+roomB[2]/2)-x;
		int yDiff =  (roomB[1]+roomB[3]/2)-y;
		
		if(Math.abs(xDiff)>=Math.abs(yDiff)&&
			drawLine(target,x,y,xDiff/2,true)&&
			drawLine(target,x+xDiff/2,y,yDiff,false)&&
			drawLine(target,x+xDiff/2,y+yDiff,xDiff-xDiff/2,true)){}
		else if (
			drawLine(target,x,y,yDiff/2,false) &&
			drawLine(target,x,y+yDiff/2,xDiff,true) &&
			drawLine(target,x+xDiff,y+yDiff/2,yDiff-yDiff/2,false)){}
			
		return target;
		
		
	}
	
	public boolean drawLine(GameBoard target, int ax, int ay, int length, boolean horizontal){
		boolean willdr=true;
		Tile previous = null;
		Tile t = null;
		for(int i=0; i!=length; i+=length/Math.abs(length)){
			previous = t;
			if(horizontal){
				t  = target.getTileAt( ax+i ,  ay );
			}
			else{
				t = target.getTileAt(ax, ay + i);
			}
			
			int draw = 0;
			if( t.getToDown().getTileFace()==Tile.TILE_SPECIAL ) {draw++;}
			if( t.getToUp().getTileFace()==Tile.TILE_SPECIAL ) {draw++;}
			if( t.getToLeft().getTileFace()==Tile.TILE_SPECIAL ) {draw++;}
			if( t.getToRight().getTileFace()==Tile.TILE_SPECIAL ) {draw++;}
			
			
			
			//if(t.getTileFace()==Tile.TILE_SPECIAL){ return false; }
			if(draw == 0 && !willdr){
				willdr=true;
				previous.setTileFace(Tile.TILE_SPECIAL);
			}
			if(willdr){
				t.setTileFace(Tile.TILE_SPECIAL);
			}
			if(draw>1){
				willdr=false;
			}
		}
		return true;
	}
	
	
	public GameBoard makeBoard(int depth, ArrayList<Stair> floorLinks){
		ArrayList<int[]> rooms = new ArrayList<int[]>(0);
		
		int roomWidth = 1, roomHeight = 1;
		
		while( rooms.size()<numRooms ){
			int[] newRoom = { Utilities.randInt(0,roomWidth+roomSizeMax), Utilities.randInt(0,roomHeight+roomSizeMax), Utilities.randInt(roomSizeMin, roomSizeMax) , Utilities.randInt(roomSizeMin, roomSizeMax) };
			if(!checkforCollisions(newRoom,rooms)){
				rooms.add(newRoom);
				if( roomWidth<newRoom[0]+newRoom[2] ){
					roomWidth = newRoom[0]+newRoom[2];
				}
				if( roomHeight<newRoom[1]+newRoom[3] ){
					roomHeight = newRoom[1]+newRoom[3];
				}
			}
		}
		
		GameBoard gb = this.populateBoard(new GameBoard(roomWidth, roomHeight),rooms, floorLinks);
		gb.depth=depth;
		
		return gb;
		
	}

	public boolean checkforCollisions(int[] room, ArrayList<int[]> rooms){ //check for collisions with other rooms;
		for(int i=0; i<rooms.size(); i++){
			if( !( room[0]+room[2]<rooms.get(i)[0] ||
				room[0]>rooms.get(i)[0]+rooms.get(i)[2] ||
				room[1]+room[3]<rooms.get(i)[1] ||
				room[1]>rooms.get(i)[1]+rooms.get(i)[3]
					) ){
				return true;
			}
		}
		return false;
	}
	
}