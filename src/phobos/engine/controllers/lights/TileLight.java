package phobos.engine.controllers.lights;

import java.util.LinkedList;

import phobos.engine.models.entities.Orientation;
import phobos.exception.UnmanagedSwitchCaseException;

/**
 * 
 * TileLights are used by light controller to calculate raycasting from a light source, it's a list of coord between a Tile and a Light
 * @author Half
 *
 */
public class TileLight {
	
	/**
	 *  Position of tile in X from center of Light
	 */
	private float tileX ;
	/** Position of tile in Y from center of Light
	 */
	private float tileY ;
	/**
	 *  Distance from light center on the same plan
	 */
	private float decal2D ;
	/**
	 *  List of coords between TileLight and light center
	 */
	private LinkedList<float[]> coll = new LinkedList<float[]>() ;

	public TileLight(float tileX, float tileY) throws UnmanagedSwitchCaseException{
		
		this.tileX = tileX ;	
		this.tileY = tileY ;

		this.decal2D = (float) (Math.hypot(tileX,tileY)) ;
		
		Orientation orent = null ;

		//Bottom
		if(tileX >= 0 && tileY >= 0) {
			orent = Orientation.SOUTH ;
			for(int i = 0;i < tileX + tileY;i++) {
				for(int j = 0; j <= i;j++) {
					if(LineIntersectsSquare(j, i - j)) {
						float[] coord = {j, i - j,(float)Math.hypot(j, i - j)} ;
						coll.add(coord) ;
					}
				}
			}
		}
		//Right
		if(tileX >= 0 && tileY < 0) {
			orent = Orientation.EAST ;
			for(int i = 0;i < tileX - tileY;i++) {
				for(int j = 0; j <= i;j++) {
					if(LineIntersectsSquare(j, -1*(i - j))) {
						float[] coord = {j, -1*(i - j),(float)Math.hypot(j, -(i - j))} ;
						coll.add(coord) ;
					}
				}
			}
		}
		//Left
		if(tileX < 0 && tileY >= 0) {
			orent = Orientation.WEST ;
			for(int i = 0;i < -tileX + tileY;i++) {
				for(int j = 0; j <= i;j++) {
					if(LineIntersectsSquare(-j, (i - j))) {
						float[] coord = {-j, (i - j),(float)Math.hypot(-j, i - j)} ;
						coll.add(coord) ;
					}
				}
			}
		}
		//Top
		if(tileX < 0 && tileY < 0) {
			orent = Orientation.NORTH ;
			for(int i = 0;i < -tileX - tileY;i++) {
				for(int j = 0; j <= i;j++) {
					if(LineIntersectsSquare(-j, -1*(i - j))) {
						float[] coord = {-j, -1*(i - j),(float)Math.hypot(-j, -(i - j))} ;
						coll.add(coord) ;
					}
				}
			}
		}
		
		//For each coord check if the last one is juxtaposed (no diagonal) else add a Tile in collision
		float coeff = tileY / tileX ;
		float lastx = 0 ;
		float lasty = 0 ;
		LinkedList <float[]>fixCoord = new LinkedList<float[]>() ;
		
		for (float[] collCoord : coll) {
			if(collCoord[0] != lastx && collCoord[1] != lasty) {
				switch(orent) {
					case NORTH :
						if(coeff > 1) {
							float[] coordN = {collCoord[0], collCoord[1] + 1,(float)Math.hypot(collCoord[0], collCoord[1] + 1)} ;
							fixCoord.add(coordN) ;
						} else {
							float[] coordN = {collCoord[0] + 1, collCoord[1],(float)Math.hypot(collCoord[0] + 1, collCoord[1])} ;
							fixCoord.add(coordN) ;
						}
						break ;
					case SOUTH :
						if(coeff > 1) {
							float[] coordS = {collCoord[0], collCoord[1] - 1,(float)Math.hypot(collCoord[0], collCoord[1] - 1)} ;
							fixCoord.add(coordS) ;
						} else {
							float[] coordS = {collCoord[0] - 1, collCoord[1],(float)Math.hypot(collCoord[0] - 1, collCoord[1])} ;
							fixCoord.add(coordS) ;
						}
						break ;
					case EAST :
						if(coeff < -1) {
							float[] coordE = {collCoord[0], collCoord[1] + 1,(float)Math.hypot(collCoord[0], collCoord[1] + 1)} ;
							fixCoord.add(coordE) ;
						} else {
							float[] coordE = {collCoord[0] - 1, collCoord[1],(float)Math.hypot(collCoord[0] - 1, collCoord[1])} ;
							fixCoord.add(coordE) ;
						}
						break ;
					case WEST :
						if(coeff < -1) {
							float[] coordW = {collCoord[0], collCoord[1] - 1,(float)Math.hypot(collCoord[0], collCoord[1] - 1)} ;
							fixCoord.add(coordW) ;
						} else {
							float[] coordW = {collCoord[0] + 1, collCoord[1],(float)Math.hypot(collCoord[0] + 1, collCoord[1])} ;
							fixCoord.add(coordW) ;
						}
						break ;
				default:
					throw new UnmanagedSwitchCaseException("Orientation "+orent+" not managed") ;
				}				
			}
			lastx = collCoord[0] ;
			lasty = collCoord[1] ;
			fixCoord.add(collCoord) ;
		}
		//Check if TileLight is juxtaposed with the last tile in collision (no diagonal) else add a Tile in collision
		if(tileX != lastx && tileY != lasty) {
			switch(orent) {
			case NORTH :
				if(coeff > 1) {
					float[] coordN = {tileX, tileY + 1,(float)Math.hypot(tileX, tileY + 1)} ;
					fixCoord.add(coordN) ;
				} else {
					float[] coordN = {tileX + 1, tileY,(float)Math.hypot(tileX + 1, tileY)} ;
					fixCoord.add(coordN) ;
				}
				break ;
			case SOUTH :
				if(coeff > 1) {
					float[] coordS = {tileX, tileY - 1,(float)Math.hypot(tileX, tileY - 1)} ;
					fixCoord.add(coordS) ;
				} else {
					float[] coordS = {tileX - 1, tileY,(float)Math.hypot(tileX - 1, tileY)} ;
					fixCoord.add(coordS) ;
				}
				break ;
			case EAST :
				if(coeff < -1) {
					float[] coordE = {tileX, tileY + 1,(float)Math.hypot(tileX, tileY + 1)} ;
					fixCoord.add(coordE) ;
				} else {
					float[] coordE = {tileX - 1, tileY,(float)Math.hypot(tileX - 1, tileY)} ;
					fixCoord.add(coordE) ;
				}
				break ;
			case WEST :
				if(coeff < -1) {
					float[] coordW = {tileX, tileY - 1,(float)Math.hypot(tileX, tileY - 1)} ;
					fixCoord.add(coordW) ;
				} else {
					float[] coordW = {tileX + 1, tileY,(float)Math.hypot(tileX + 1, tileY)} ;
					fixCoord.add(coordW) ;
				}
				break ;
			default:
				break;
			}				
		}
		coll = fixCoord ;
	}

	/**
	 * @param tileCheckX X to check (from center in square)
	 * @param tileCheckY Y to check (from center in square)
	 * @return true if the cords are on the line center -> <b>Tile</b>
	 */
	public boolean LineIntersectsSquare(int tileCheckX, int tileCheckY) {

		//if(tileCheckX == 0 && tileCheckY == 0) return false ;

		//calc in Y by X
		float coeff = 0 ;
		if(getTileXFromCenter() == 0) {
			if(0 == tileCheckX) {
				return true ;
			} else {
				return false ;
			}
		} else {
			coeff = ((float)getTileYFromCenter() / (float)getTileXFromCenter()) ;
		}

		if(tileCheckY == (int)((float)coeff*(float)tileCheckX)) {
			return true ;
		}

		//calc on X by Y
		coeff = 0 ;
		if(getTileYFromCenter() == 0) {
			if(0 == tileCheckY) {
				return true ;
			} else {
				return false ;
			}
		} else {
			coeff = ((float)getTileXFromCenter() / (float)getTileYFromCenter()) ;
		}

		if(tileCheckX == (int)((float)coeff*(float)tileCheckY)) {
			return true ;
		}

		return false ;
	}
	
	/**
	 * 
	 * @return Delta X from center in tile (so can't be > radius)
	 */
	public float getTileXFromCenter() {
		return tileX ;
	}
	
	/**
	 * 
	 * @return Delta Y from center in tile (so can't be > radius)
	 */
	public float getTileYFromCenter() {
		return tileY ;
	}
	
	/**
	 * 
	 * @return Return range from center in tile (so can't be > radius)
	 */
	public float getTileDecal2D() {
		return decal2D ;
	}	
	
	/**
	 * 
	 * @return List of cords which can obstruct the light
	 */
	public LinkedList<float[]> getColl() {
		return coll ;
	}
}
