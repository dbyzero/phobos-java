package phobos.engine.controllers.lights;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.util.Log;

import phobos.engine.models.entities.Entity;
import phobos.engine.models.lights.Light;
import phobos.engine.models.world.Tile;
import phobos.engine.views.game.Ingame;
import phobos.exception.UnmanagedSwitchCaseException;
import phobos.utils.math.Math;

/**
 *	@author Half
 */


public class LightController {

	public LightController () {
		this.radiusTile = new HashMap<Byte, List<TileLight>>();
	}

	/**
	 *	If not set, calculates the collection of {@link TileLight} for a given radius and put it in radiusTile {@link HashMap}
	 *
	 *	@param radius radius of the light
	 * @throws UnmanagedSwitchCaseException 
	*/
	
	public boolean calcRadiusTile(byte radius) throws UnmanagedSwitchCaseException {
		
		if(radiusTile.containsKey(radius) ) {
			return false ;
		} else {
			List<TileLight> tilelight = new LinkedList<TileLight>() ;
	
			//4*radius + 4 axes + center
			short nbr_tl = (short)((radius*radius)*4 + radius*4 + 1) ;
			
			//making tileLight
			for(short tileModified = 0;tileModified < nbr_tl;tileModified++) {
				short TileLightX = (short)(tileModified%(radius * 2 + 1) - radius) ;
				short TileLightY = (short)(tileModified/(radius * 2 + 1) - radius) ;
				if(Math.hypot2d(TileLightX,TileLightY) > radius) continue ;
				tilelight.add(new TileLight(TileLightX,TileLightY)) ;
			}		
			radiusTile.put(radius, tilelight) ;
			return true;
		}
	}
	
	/**
	 * Calculate projections of all lights by raycasting
	 * TODO calculate lights only on screen
	 * @param delta time in ms since laste update
	 * @return true
	 */
	
	public boolean update(int delta) {

		// Temp Var
		//true if light is stopped
		boolean lightCollision = false ;
		//max cliff Height where light can through		
		float maxCliffHeight ;
		//cliff Height where the light through
		float cliffHeight ;
		//delta between maxCliffHeight and cliffHeight
		float cliffDelta = 1.0f;
		//intensity of light
		float coeffDecr = 1.0f;
				
		//ref to the Tile corresponding to the TL		
		Tile tileToCheck = null ;
		//ref to a tile which can collision
		Tile tlCollision  = null ;
		//tile where is the light
		Tile tileWhereIsTheLight = null ;
		

		//For each Light in game
		for (Entity entity : Ingame.getInstance().entityList) {
			if(entity.isActive()){
				for (Light light : entity.getLightsList()) {
					if(!light.isActive()) continue ;
					try {
						this.calcRadiusTile(light.getRadius());
					} catch (UnmanagedSwitchCaseException e1) {
						e1.printStackTrace();
					}
					//if light under surface
					//Log.info(light.getEntity().getX()+" "+light.getEntity().getY()) ;
					try {
						tileWhereIsTheLight = Ingame.getInstance().getTile(light.getEntity().getX(),light.getEntity().getY()) ;
					} catch (ArrayIndexOutOfBoundsException e) {
						Log.error("Light out of Chucks !") ;
						continue ;
					}
					
						
					if(tileWhereIsTheLight.getZ() > light.getEntity().getZ()) continue ;
					
					//For each tileLight corresponding to the radius in this.radiusTile
					for (TileLight tilelight : radiusTile.get(light.getRadius())) {

						//if tile if out of radios => stop
						if(light.getRadius() < tilelight.getTileDecal2D()) continue ;
						
						int TileX = (int) (light.getEntity().getX() + tilelight.getTileXFromCenter()) ;
						int TileY = (int) (light.getEntity().getY() + tilelight.getTileYFromCenter()) ;
						
						//get the Tile object where is the TL (need for : onScreen(), getDeltaZ() and apply light)
						try {
							tileToCheck = Ingame.getInstance().getTile(TileX,TileY) ;
							if(!tileToCheck.onScreen()) continue ;
						} catch (ArrayIndexOutOfBoundsException e) {
							continue ;
						}
						
						
						// decal is the distance in 3D between TL and center
						float decal = (float) Math.hypot2d(tilelight.getTileDecal2D(),((light.getEntity().getZ() - tileToCheck.getZ()))) ;

						//SURFACE TILE
						//if tile in range
						if(decal <= light.getRadius()) {

							//decrease light by 25% for each Z above the light
							if(tileToCheck.getZ() > light.getEntity().getZ()) {
								coeffDecr -= (tileToCheck.getZ() - (light.getEntity().getZ())) * 0.50f ;
							}
												
							for(float[] tlCollisionArray : tilelight.getColl()) {
								//if light have intensity
								if(coeffDecr <= 0.0f) {
									lightCollision = true; 
									break ;
								}
								//0 = x 1 = y 2 = z
								try {
									tlCollision = Ingame.getInstance().getTile(light.getEntity().getX() +  + tlCollisionArray[0],light.getEntity().getY() +  + tlCollisionArray[1]) ;

									/*if(tileToCheck.getFocus()) {
										tlCollision.filtreAdd(new Color(255,255,255)) ;
									}*/
									
									//using Thales to know the max Height
									maxCliffHeight = (float)(tlCollisionArray[2]*(tileToCheck.getZ() - light.getEntity().getZ()))/(tilelight.getTileDecal2D()) ;
									cliffHeight = (float)(tlCollision.getZ() - light.getEntity().getZ()) ;
									
									//if tile height < maximum height authorized to be traversed by the light => OK ! else decrease intensity by 25% for each Z above
									if(maxCliffHeight < cliffHeight) {
										cliffDelta = (cliffHeight - maxCliffHeight) * 0.50f ;
										coeffDecr -= cliffDelta ;
									}
								} catch (ArrayIndexOutOfBoundsException e) {
									Log.error("ERROR OUT OF ARRAY IN LIGHT SHADOW CALCUL (Ingame.render)") ;
									continue ;
								} catch (ArithmeticException e) {
									Log.error("ERROR DIVISION BY ZERO IN LIGHT SHADOW CALCUL (Ingame.render)") ;
									continue ;
								}
							}
							//apply light color if no collision (intensity is mitigate by distance)
							if(!lightCollision) {
								//get intensity
								Color intensity = new Color(
									light.getColor().r - light.getColor().r * ((float)decal/(float)light.getRadius()),
									light.getColor().g - light.getColor().g * ((float)decal/(float)light.getRadius()),
									light.getColor().b - light.getColor().b * ((float)decal/(float)light.getRadius()),
									1.0f);
								
								//scale color by radius
								intensity.scale(coeffDecr) ;
								if( intensity.r < 0.0f ) intensity.r = 0.0f ;
								if( intensity.g < 0.0f ) intensity.g = 0.0f ;
								if( intensity.b < 0.0f ) intensity.b = 0.0f ;
								tileToCheck.addColorToSurfaceLightColor(intensity) ;
							}
							
							//reset temp variable for cliff calcul
							coeffDecr = 1.0f ;
							lightCollision = false; 
						}

						//CLIFF TILE CALCULS
						//if have cliff and light is under the cliff/wall
						if(tileToCheck.getCliffHeight() > 0) {
							//CLIFF
							//if light is directly above the cliff
							if(0 == tilelight.getTileXFromCenter() && 0 == tilelight.getTileYFromCenter() ) continue ;
							//for each square cliff
							for(int j = 0; j < tileToCheck.getCliffHeight();j++) {

								if (tileWhereIsTheLight.getRelativeToCameraY() <= tileToCheck.getRelativeToCameraY() + tileToCheck.getCliffHeight() * 16) { break;} 
								
								//var to cache the distance between the cliff square and the center of the light
								float decalCliff  ; 
																
								//var to cache the delta between light and cliff square // j+1 : because 0 is top // 
								float heightDiff = (light.getEntity().getZ() - (tileToCheck.getZ() - (j+1))) ;

								//if light and cliff on the same plan => don't need to use pythagore to get the distance
								if(heightDiff == 0) {
									decalCliff = tilelight.getTileDecal2D() ;
								//if cliff out of radius => stop
								} else if (heightDiff > light.getRadius()){
									continue ;
								//else use pythagore to get the distance between light and cliff
								} else {
									decalCliff = (float) Math.hypot2d(tilelight.getTileDecal2D(),((heightDiff))) ;
								}
								
								//if cliff out of range => continue
								if(decalCliff > light.getRadius()) continue ;
																
								//for each tile can stop light between center and TL
								for(float[] tlCollisionArray : tilelight.getColl()) {
									//0 = x 1 = y 2 = z
									try {
										tlCollision = Ingame.getInstance().getTile(light.getEntity().getX() +  + tlCollisionArray[0],light.getEntity().getY() +  + tlCollisionArray[1]) ;
										
										//using Thales to know the max Height // j+1 : because 0 is top
										maxCliffHeight = tlCollisionArray[2]*((tileToCheck.getZ() - (j+1)) - light.getEntity().getZ())/(tilelight).getTileDecal2D() ;
										cliffHeight = tlCollision.getZ() - light.getEntity().getZ() ;
										
										//if tile height < maximum height authorized to be traversed by the light => OK ! else decrease intensity by 25% for each Z above
										if(maxCliffHeight < cliffHeight) {
											cliffDelta = cliffHeight - maxCliffHeight ;
											coeffDecr -= cliffDelta ;
											if(coeffDecr <= 0.0f) {

												lightCollision = true; 
												break ;
											}
										}
									} catch (ArrayIndexOutOfBoundsException e) {
										Log.error("ERROR OUT OF ARRAY IN LIGHT SHADOW CALCUL") ;
										continue ;
									} catch (ArithmeticException e) {
										//delta2D = 0
										Log.error("ERROR DIVISION BY ZERO IN LIGHT SHADOW CALCUL") ;
										continue ;
									}
								}
								//apply light color if no collision (intensity is mitigate by distance)
								if(!lightCollision) {
									Color intensity = new Color(
										light.getColor().r - light.getColor().r * ((float)decalCliff/(float)light.getRadius()),
										light.getColor().g - light.getColor().g * ((float)decalCliff/(float)light.getRadius()),
										light.getColor().b - light.getColor().b * ((float)decalCliff/(float)light.getRadius()),
										1.0f) ;
									intensity.scale(coeffDecr) ;
									if( intensity.r < 0.0f ) intensity.r = 0.0f ;
									if( intensity.g < 0.0f ) intensity.g = 0.0f ;
									if( intensity.b < 0.0f ) intensity.b = 0.0f ;
									tileToCheck.addColorToCliffLightColor(intensity,j) ;									
								}
								//reset for the next cliff
								coeffDecr = 1.0f ;
								lightCollision = false; 
							}
						}
						//reset for the next TileLight
						coeffDecr = 1.0f ;
						lightCollision = false; 
					}
					light.update(delta);
				}
			}
		}

		return true ;
	}
	
	private HashMap<Byte,List<TileLight>> radiusTile;

}