package phobos.engine.models.world;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.Log;

import phobos.engine.Spritesheet;
import phobos.engine.models.entities.Entity;
import phobos.engine.models.entities.Orientation;
import phobos.engine.views.game.Ingame;
import phobos.generators.Generator;
import phobos.utils.math.Vector;

/**
 * 
 * A Tile of the heightmap
 * 
 * @author half
 * @version r2
 *
 */

public class Tile {
	/**
	 * 
	 * @param tiletype Type of tile
	 * @param X	Coord X in the world
	 * @param Y Coord Y in the world
	 * @param Z Coord Z in the world
	 */
	public Tile(byte tiletype, float X, float Y, float Z) {
		colFilterFinal = new Color(255,255,255) ;
		currentColFilter = new Color(255,255,255) ;
		colFilterLights = new Color(0,0,0) ;
		position = new Vector(X,Y,Z) ;
		area = new Rectangle(getX() * 16 + getY() * -16,getX() * 8 + getY() * 8 - getZ() * 16,32,32) ;
		setDeltaZPerlinNoise() ;
		
		/* TEMP CODE */
		if(Math.random() > 0.999) {
			Entity ent = new Entity(getX(), getY(), getZ(), Orientation.SOUTHWEST) ;
			ent.setParamDisplay(32, 64, 1, 12) ;
			try {
				ent.setSprite(Orientation.SOUTHWEST, 224, 0) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
			addEntity(ent) ;
		}
	}
	
	/**
	 * Calcul the new perlin noise for this tile with new variable of Generator
	 */
	public void setDeltaZPerlinNoise() {
		float oldZ = getZ() ;
		setZ(Generator.perlinNoiseHeight(getX(),getY(),Generator.getZoom(),Generator.getMaxHeight(),Generator.getOctave(),Generator.getP())) ;
		
		//update entities Z
		for (Entity ent : this.listEntity) {
			ent.setZ(ent.getZ() + (this.getZ() - oldZ)) ;
		}
	}
	
	/**
	 * @return The X coord on the Chunk where is the Tile
	 */
	public int getPosChunkX() {
		return Chunk.getNbrTiles() % (int)position.getX() ;
	}

	/**
	 * @return The Y coord on the Chunk where is the Tile
	 */
	public int getPosChunkY() {
		return Chunk.getNbrTiles() % (int)position.getY()  ;
	}

	/**
	 * Calculate delta Z for near tiles and save it
	 */
	public void calcCliffHeight() {
		
		//Bot Right
		try {
			setCliffBR(getZ() - Ingame.getInstance().getTile(getX() + 1, getY()).getZ()) ;
		} catch (ArrayIndexOutOfBoundsException e) {
			setCliffBR(0) ;
		}

		//Bot Left
		try {
			setCliffBL(getZ() - Ingame.getInstance().getTile(getX(), getY() + 1).getZ()) ;
		} catch (ArrayIndexOutOfBoundsException e) {
			setCliffBL(0) ;
		}
		
		if(getCliffBL() > getCliffBR()) {
			setCliffHeight(getCliffBL()) ;
		} else {
			setCliffHeight(getCliffBR()) ;
		}
		
		area.setY(getX() * 8 + getY() * 8 - getZ() * 16) ;
		
		//reserve space in array for color
		Color[] old_clights  ;
		Color[] old_cfinal ;
		Color[] old_ccurrent ;
		try {
			old_clights = colFilterLightsCliff.clone() ;
			old_cfinal = colFilterFinalCliff.clone() ;
			old_ccurrent = currentColFilterCliff.clone() ;
		} catch(NullPointerException e) {
			old_clights = new Color[0] ;
			old_cfinal = new Color[0] ;
			old_ccurrent = new Color[0] ;
		}
		
		if(cliffHeight > 0.0f) {
			colFilterFinalCliff = new Color[(int) (cliffHeight) + 1] ;
			colFilterLightsCliff = new Color[(int) (cliffHeight) + 1] ;
			currentColFilterCliff = new Color[(int) (cliffHeight) + 1] ;
		} else {
			colFilterFinalCliff = new Color[0] ;
			colFilterLightsCliff = new Color[0] ;
			currentColFilterCliff = new Color[0] ;
		}
		
		//fill array for color
		for(int i = 0; i < colFilterLightsCliff.length;i++) {
			try {
				colFilterLightsCliff[i] = old_clights[i] ;
				colFilterFinalCliff[i] = old_cfinal[i] ;
				currentColFilterCliff[i] = old_ccurrent[i] ;
			} catch (ArrayIndexOutOfBoundsException e ) {
				colFilterLightsCliff[i] = new Color(Ingame.getInstance().getColorAmbiance().r,Ingame.getInstance().getColorAmbiance().g,Ingame.getInstance().getColorAmbiance().b) ;
				colFilterFinalCliff[i] = new Color(Ingame.getInstance().getColorAmbiance().r,Ingame.getInstance().getColorAmbiance().g,Ingame.getInstance().getColorAmbiance().b) ;
				currentColFilterCliff[i] = new Color(Ingame.getInstance().getColorAmbiance().r,Ingame.getInstance().getColorAmbiance().g,Ingame.getInstance().getColorAmbiance().b) ;
			}
		}
					
		//calcul the new area of the cliff
		areaCliff = new Rectangle(area.getX(),area.getY()+16,area.getWidth(),(cliffHeight)*16) ;
	}

	/**
	 * 
	 * @return Return the number of square representing the cliff
	 */
	public float getCliffHeight() {
		return cliffHeight ;
	}

	/**
	 * 
	 * @return Return true if on the screen
	 */
	public boolean onScreen() {
		if((getRelativeToCameraX() + getRelativeToCameraWidth()) < 0) return false ;
		if(getRelativeToCameraX() > Ingame.getInstance().getCamera().getWidth()) return false ;
		//if bottom cliff (y + h + cliffH) is above screen return false
		if(((getRelativeToCameraY() + (getRelativeToCameraHeightCliff()) + getRelativeToCameraHeight()) ) < 0) return false ;
		//if lower Y of tile is beyond screen return false
		calcLowerYOnScreen() ;
		if(getLowerYOnScreen() > Ingame.getInstance().getCamera().getHeight()) return false ;
		return true ;
	}
	
	/**
	 * 
	 * @param foc A boolean to set the focus
	 */
	public void setFocus(boolean foc) {
		this.focus = foc ;
	}
	
	/**
	 * 
	 * @return Return true if Tile is focused by the mouse
	 */
	public boolean getFocus() {
		return this.focus ;
	}

	/**
	 * 
	 * @param g <b>Graphic</b> instance, the renderer
	 * @return The number of sprite rendered 
	 */
	public int render(Graphics g) {
				
		int nbrQuad = 1 ;

		//application ambiance color
		multiplyFinalColor(Ingame.getInstance().getColorAmbiance()) ;

		//if first render => currentColor = color filter targeted
		if(firstRender) {
			currentColFilter.r = Ingame.getInstance().getColorAmbiance().r ;
			currentColFilter.g = Ingame.getInstance().getColorAmbiance().g ;
			currentColFilter.b = Ingame.getInstance().getColorAmbiance().b ;
			for(int i = (int) cliffHeight; i > 0;i--) {
				currentColFilterCliff[i].r = Ingame.getInstance().getColorAmbiance().r ;
				currentColFilterCliff[i].g = Ingame.getInstance().getColorAmbiance().g ;
				currentColFilterCliff[i].b = Ingame.getInstance().getColorAmbiance().b ;
			}
			firstRender = false ;
		}


		//draw cliff if there is, from lower to higher
		if(cliffHeight > 0) {
			for(int i = (int) cliffHeight; i >= 0;i--) {
				//add 1 polygone to the return value
				nbrQuad++;
			
				//add the unique color to the color targeted
				colFilterFinalCliff[i].add(colFilterLightsCliff[i]) ;
				float delta_r = colFilterFinalCliff[i].r - currentColFilterCliff[i].r ;
				float delta_g = colFilterFinalCliff[i].g - currentColFilterCliff[i].g ;
				float delta_b = colFilterFinalCliff[i].b - currentColFilterCliff[i].b ;
				//Simple calcul to smooth the red component of current color (currentColFilterCliff) to the target filter (colFilterFinalCliff)
				if(Math.abs(delta_r) < 0.01f) {
					currentColFilterCliff[i].r = colFilterFinalCliff[i].r ;
				} else if(delta_r != 0.0f) {
					currentColFilterCliff[i].r += delta_r/10 ;
				}
	
				//Simple calcul to smooth the green component of current color (currentColFilterCliff) to the target filter (colFilterFinalCliff)
				if(Math.abs(delta_g) < 0.01f) {
					currentColFilterCliff[i].g = colFilterFinalCliff[i].g ;
				} else if(delta_g != 0.0f) {
					currentColFilterCliff[i].g += delta_g/10 ;
				}
	
				//Simple calcul to smooth the blue component of current color (currentColFilterCliff) to the target filter (colFilterFinalCliff)
				if(Math.abs(delta_b) < 0.01f) {
					currentColFilterCliff[i].b = colFilterFinalCliff[i].b ;
				} else if(delta_b != 0.0f) {
					currentColFilterCliff[i].b += delta_b/10 ;
				}
	
				//bind the current color in filter
				currentColFilterCliff[i].bind() ;
	
				Ingame.getInstance().getSpritsheet().drawEmbedded(
					getRelativeToCameraX(),
					getRelativeToCameraY() + 16*(i) * Ingame.getInstance().getCamera().getCoeffZoom(),
					getRelativeToCameraWidth(), 
					getRelativeToCameraHeight(),
					spriteCliff
				);
			}
		}

		//DRAW TOP TILE
		///add lights color to the color targeted
		addColorToFinalColor(colFilterLights) ;
		//Simple calcul to smooth the red component of current color (currentFiltreCliff) to the target filter (colorCliff)
		float delta_r = colFilterFinal.r - currentColFilter.r ;
		float delta_g = colFilterFinal.g - currentColFilter.g ;
		float delta_b = colFilterFinal.b - currentColFilter.b ;
		if(Math.abs(delta_r) < 0.01f) {
			currentColFilter.r = colFilterFinal.r ;
		} else if(delta_r != 0.0f) {
			currentColFilter.r += delta_r/10 ;
		}

		//Simple calcul to smooth the green component of current color (currentFiltreCliff) to the target filter (colorCliff)
		if(Math.abs(delta_g) < 0.01f) {
			currentColFilter.g = colFilterFinal.g ;
		} else if(delta_g != 0.0f) {
			currentColFilter.g += delta_g/10 ;
		}

		//Simple calcul to smooth the blue component of current color (currentFiltreCliff) to the target filter (colorCliff)
		if(Math.abs(delta_b) < 0.01f) {
			currentColFilter.b = colFilterFinal.b ;
		} else if(delta_b != 0.0f) {
			currentColFilter.b += delta_b/10 ;
		}

		//bind the current filter
		currentColFilter.bind() ;
		Ingame.getInstance().getSpritsheet().drawEmbedded(
			getRelativeToCameraX(), 
			getRelativeToCameraY() ,
			getRelativeToCameraWidth(), 
			getRelativeToCameraHeight(),
			spriteSurface
		);
		
		//render entities

		for (Entity ent : this.listEntity) {
				//if ent above tile
				if(ent.getZ() >= this.getZ()) {
					nbrQuad += ent.render(g) ;	
				}
		}
			
		//raz colFilter (Unique and Targeted)
		setColorFilterToDefaultValues() ;
		return nbrQuad ;
	}

	/**
	 * Get the area where is the tile
	 * @return Return a <b>Rectangle</b> defining where is the tile in cartesian coords 
	 */
	public Rectangle getArea() {
		return (Rectangle) this.area ;
	}

	/**
	 * get delta cliff with tile bottom right
	 * @return cliff bottom right
	 */
	public float getCliffBR() {
		return cliffBR ;
	}
	
	/**
	 * get delta cliff with tile bottom left
	 * @return cliff bottom left
	 */
	public float getCliffBL() {
		return cliffBL ;
	}
	
	/**
	 * set delta cliff with tile bottom left
	 * @param d delta value in float
	 */
	public void setCliffBR(float d) {
		cliffBR = d ;
	}
	
	/**
	 * set delta cliff with tile bottom left
	 * @param d delta value in float
	 */
	public void setCliffBL(float d) {
		cliffBL = d;
	}

	/**
	 * set cliff height
	 * @param Z cliff height in float
	 */
	public void setCliffHeight(float Z) {
		cliffHeight = Z ;
	}

	public boolean setSpriteSurface(float tx, float ty,float w, float h) {
		spriteSurface = new Rectangle(tx/Spritesheet.getSSWidth(),
				   ty/Spritesheet.getSSHeight(),
				   w/Spritesheet.getSSWidth(),
				   h/Spritesheet.getSSHeight()) ;
		return true ;
	}
	
	public boolean setSpriteCliff(float tx, float ty,float w, float h) {
		spriteCliff = new Rectangle(tx/Spritesheet.getSSWidth(),
				   ty/Spritesheet.getSSHeight(),
				   w/Spritesheet.getSSWidth(),
				   h/Spritesheet.getSSHeight()) ;
		return true ;
	}

	public String toString() {
		return "Tile X:"+getX()+" Y:"+getY()+" Z:"+getZ() + " cliff:"+cliffHeight+" arraCliff:"+
				currentColFilterCliff.length+ " Nbr Entity:"+listEntity.size()+ " lowerYEntityOnScreen:" + lowerYOnScreen;
	}

	/**
	 * Add color to <b>colFilterLights</b>
	 * @param col the color to add
	 */
	public void addColorToSurfaceLightColor(Color col) {
		colFilterLights.add(col) ;
	}

	/**
	 * Add color to a <b>colFilterLightsCliff</b>
	 * @param col the color to add
	 * @param cliff the cliff to add light color
	 */
	public void addColorToCliffLightColor(Color col,int cliff) {
			colFilterLightsCliff[cliff].add(col) ;
	}
	
	/**
	 * Multiply <b>colFilterFinal</b> and all colFilterFinalCliff
	 * @param col Color filter
	 */
	public void multiplyFinalColor(Color col) {
		colFilterFinal = colFilterFinal.multiply(col) ;
		for(int i = 0; i < getCliffHeight();i++) {
			colFilterFinalCliff[i] = colFilterFinalCliff[i].multiply(col) ;
		}
	}
	
	/**
	 * Scale <b>finalColor</b> and all colFilterFinalCliff
	 * @param scale Scale color to scale
	 */
	public void scaleFinalColor(float scale) {
		colFilterFinal.scale(scale) ;
		for(int i = 0; i < getCliffHeight();i++) {
			colFilterFinalCliff[i].scale(scale) ;
		}
	}
	
	/**
	 * Add color to <b>colFilterFinal</b> and all colFilterFinalCliff
	 * @param col color to add
	 */
	public void addColorToFinalColor(Color col) {
		colFilterFinal.add(col) ;
		for(int i = 0; i < getCliffHeight();i++) {
			colFilterFinalCliff[i].add(col) ;
		}
	}
	
	/**
	 * Set filter color to default value to make new calcul on them
	 */
	public void setColorFilterToDefaultValues() {
		colFilterFinal.r = 1.0f ;
		colFilterFinal.g = 1.0f ;
		colFilterFinal.b = 1.0f ;
		colFilterFinal.a = 1.0f ;
		colFilterLights.r = 0.0f ;
		colFilterLights.g = 0.0f ;
		colFilterLights.b = 0.0f ;
		colFilterLights.a = 1.0f ;
		for(int i = 0; i < getCliffHeight();i++) {
			colFilterFinalCliff[i].r = 1.0f ;
			colFilterFinalCliff[i].g = 1.0f ;
			colFilterFinalCliff[i].b = 1.0f ;
			colFilterFinalCliff[i].a = 1.0f ;
			colFilterLightsCliff[i].r = 0.0f ;
			colFilterLightsCliff[i].g = 0.0f ;
			colFilterLightsCliff[i].b = 0.0f ;
			colFilterLightsCliff[i].a = 1.0f ;
		}
	}


	/**
	 * invoked when the mouse is pushed
	 * @param button button pushed
	 */
	public void mouseClick(int button) {
		if(getFocus() && Ingame.getInstance().getActive() && onScreen()) {
			try {
				// if left button
				if(button == 0) {
					setZ(getZ() + 1) ;
					//render entities
					for (Entity ent : this.listEntity) {
						ent.setZ(ent.getZ() + 1) ;
						
					}
				//if right button
				} else if(button == 1) {
					setZ(getZ() - 1) ;
					for (Entity ent : this.listEntity) {
						ent.setZ(ent.getZ() - 1)  ;
					}
				}
				calcCliffHeight() ;
				
				//recalc DZ for X-1 / Y
				Ingame.getInstance().getTile(getX() - 1, getY()).calcCliffHeight() ;
				
				//recalc DZ for X / Y-1
				Ingame.getInstance().getTile(getX(), getY() - 1).calcCliffHeight() ;
				
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.error("OUT OF ARRAY IN TILE MOUSECLICK!!!") ;
			} catch (NullPointerException e) {
				Log.error("NullException in Tile.mouseClicked",e) ;
			}
		}
	}

	/**
	 * 
	 * @param newx
	 * @param newy
	 * @return Return true if tile is under <b>newx</b>/<b>newy</b> coords
	 */
	public boolean isMouseOver(int newx, int newy) {
		if(getRelativeToCameraX() > newx) return false ;
		if(getRelativeToCameraX() + getRelativeToCameraWidth() < newx) return false ;
		if(getRelativeToCameraY() > newy) return false ;
		if(getRelativeToCameraY() + getRelativeToCameraHeight() + getRelativeToCameraHeightCliff() < newy) return false ;
		return true ;
	}


	/**
	 * 
	 * @return Return cliff height in pixel corresponding to the actual camera zoom
	 */
	public int getRelativeToCameraHeightCliff() {
		return (int) (Ingame.getInstance().getCamera().getCoeffZoom() * areaCliff.getHeight());
	}
	
	/**
	 * 
	 * @return Return width in pixel corresponding to the actual camera zoom
	 */
	public float getRelativeToCameraWidth() {
		return (Ingame.getInstance().getCamera().getCoeffZoom() * (area.getWidth()));
	}
	
	/**
	 * 
	 * @return Return height in pixel corresponding to the actual camera zoom
	 */
	public float getRelativeToCameraHeight() {
		return (Ingame.getInstance().getCamera().getCoeffZoom() * (area.getHeight()));
	}

	/**
	 * 
	 * @return Return x position in pixel corresponding to the actual camera zoom and position
	 */
	public float getRelativeToCameraX() {
		return (Ingame.getInstance().getCamera().getCoeffZoom() * (area.getX() - Ingame.getInstance().getCamera().getX())) ;
	}

	/**
	 * 
	 * @return Return y position in pixel corresponding to the actual camera zoom and position
	 */
	public float getRelativeToCameraY() {
		return (Ingame.getInstance().getCamera().getCoeffZoom() * (area.getY() - Ingame.getInstance().getCamera().getY())) ;
	}


	public float getLowerYOnScreen() {
		return lowerYOnScreen ;
	}                     
           
	public void setLowerYOnScreen(float lY) {
		this.lowerYOnScreen = lY ; ;
	}              
	
	public void calcLowerYOnScreen() {
		setLowerYOnScreen(getRelativeToCameraY()) ;
		for (Entity ent : listEntity) {
			if(getLowerYOnScreen() > ent.getRelativeToCameraY()) {
				setLowerYOnScreen(ent.getRelativeToCameraY()) ;
			}
		}
	}  
	
	/**
	 * 
	 * @return Return x tile position in the world
	 */
	public float getX() {
		return position.getX() ;
	}                     
                              
	/**
	 * 
	 * @return Return y tile position in the world
	 */
	public float getY() {
		return position.getY() ;
	}    
    
	/**
	* 
	* @return Return z tile position in the world
	*/
	public float getZ() {
		return position.getZ() ;
	} 
	
	public void setX(float x) {
		this.position.setX(x) ;
	}

	public void setY(float y) {
		this.position.setY(y) ;
	}
	
	public void setZ(float z) {
		this.position.setZ(z) ;
	}	
	
	public boolean addEntity(Entity ent) {
		if(getLowerYOnScreen() > ent.getRelativeToCameraY()) {
			//setLowerYOnScreen(ent.getRelativeToCameraY()) ;
		}
		return listEntity.add(ent) ;
	}	
	
	public boolean removeEntity(Entity ent) {
		return listEntity.remove(ent) ;
	}
	
	//MEMBERS
	
	protected static SGL GL = Renderer.get();

	private Vector position ;
	private float cliffBR = 0 ;
	private float cliffBL = 0 ;
	private float cliffHeight ;;
	private float lowerYOnScreen ;
	
	/**
	 * Filter color targeted
	 */
	private Color colFilterFinal ;	
	/**
	 * Color to add in last
	 */
	private Color colFilterLights ;
	/**
	 * Current filter color;
	 */
	private Color currentColFilter ;
	/**
	 * Filter color targeted for each cliff blocks
	 */
	private Color[] colFilterFinalCliff ;
	/**
	 * Color to add in last for each cliff blocks
	 */
	private Color[] colFilterLightsCliff ;	
	/**
	 * Current filter color for each cliff blocks
	 */
	private Color[] currentColFilterCliff ;

	private Rectangle spriteSurface,spriteCliff ;

	private boolean firstRender = true ;
	/**
	 * True if mouse focus this tile
	 */
	private boolean focus = false ;

	/**  
	 * A <b>Shape</b> defining where is the tile in cartesian coords 
	 */
	private Shape area;	
	/**  
	 * A <b>Rectangle</b> defining where is the tile cliff in cartesian coords 
	 */
	private Rectangle areaCliff ;
	
	private ArrayList<Entity> listEntity = new ArrayList<Entity>() ;

}
