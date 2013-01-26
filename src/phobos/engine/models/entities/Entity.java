package phobos.engine.models.entities;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;


import phobos.engine.AnimationSpritesheet;
import phobos.engine.Spritesheet;
import phobos.engine.models.lights.Light;
import phobos.engine.views.game.Ingame;
import phobos.exception.UnmanagedSwitchCaseException;
import phobos.utils.math.Vector;

/**
 * Entity describe a static element in the world. It just contain the location of the element, it's orientation and the lights it emits.<br>
 * <br>
 * For performance, location and orientation aren't synchronized (as only the MotionController access to them) but lights are (they may be influenced
 * by outside effect of the LightController).
 * 
 * @author Leosky,Half
 * @version r1
 */
public class Entity implements Comparable<Entity>{
	
	public Entity(float x, float y, float z,  Orientation orientation) {
		this.loc = new Vector(x,y,z);
		this.orientation = orientation;
		this.lights = new ConcurrentLinkedQueue<Light>();
		animSouthWest = new AnimationSpritesheet(Ingame.getInstance().getSpritsheet(),8) ;
		animSouthEast = new AnimationSpritesheet(Ingame.getInstance().getSpritsheet(),8) ;
		animNorthWest = new AnimationSpritesheet(Ingame.getInstance().getSpritsheet(),8) ;
		animNorthEast = new AnimationSpritesheet(Ingame.getInstance().getSpritsheet(),8) ;
	}

	public float getX() {
		return this.loc.getX();
	}
	
	public float getY() {
		return this.loc.getY();
	}
	
	public float getZ() {
		return this.loc.getZ();
	}

	public void setX(float x) {
		this.loc.setX(x);
	}
	
	public void setY(float y) {
		this.loc.setY(y);
	}
	
	public void setZ(float z) {
		this.loc.setZ(z);
	}
	
	public void setLocation(Vector vec){
		this.loc = vec;
	}
	
	public Vector getLocation(){
		return this.loc;
	}
	
		
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	/**
	 * This method is used to access to the Lights associated to an Entity. <br> <br>.
	 * 
	 * @return {@link Queue} of all Entity lights.
	 */
	public Queue<Light> getLightsList(){
		return this.lights;
	}
	
	/**
	 * Add a Light to the Entity.<br>
	 * Thread safe.
	 * @return true if this list changed as a result of the call 
	 */
	public boolean addLight(Light light){
		return this.lights.add(light);
	}
	
	/**
	 * Add a Collection of Light to the Entity.<br>
	 * Thread safe.
	 * @return true if this list changed as a result of the call 
	 */
	public boolean addLights(Collection<Light> c){
		return this.lights.addAll(c);
	}
	
	public boolean isActive(){
		return this.active;
	}
	
	public void setActive(boolean active){
		this.active = active;
	}
	
	public int compareTo(Entity o) {
		return 0;
	}
	
	/**
	 * Set sprite for an orientation<br>
	 * Size of selection on the spritesheet depends of the width and height properties of the instance
	 * @param or Orientation of the sprite set
	 * @param tx X position on the spritesheet
	 * @param ty Y position on the spritesheet
	 * @return true
	 * @throws UnmanagedSwitchCaseException
	 */
	public boolean setSprite(Orientation or,float tx, float ty) throws UnmanagedSwitchCaseException {
		switch(or) {
			case NORTHWEST :
				imageNorthWest = new Rectangle(tx/Spritesheet.getSSWidth(),
						   ty/Spritesheet.getSSHeight(),
						   this.width/Spritesheet.getSSWidth(),
						   this.height/Spritesheet.getSSHeight()) ;
				break ;
			case NORTHEAST :
				imageNorthEast = new Rectangle(tx/Spritesheet.getSSWidth(),
						   ty/Spritesheet.getSSHeight(),
						   this.width/Spritesheet.getSSWidth(),
						   this.height/Spritesheet.getSSHeight()) ;
				break ;
			case SOUTHWEST :
				imageSouthWest = new Rectangle(tx/Spritesheet.getSSWidth(),
						   ty/Spritesheet.getSSHeight(),
						   this.width/Spritesheet.getSSWidth(),
						   this.height/Spritesheet.getSSHeight()) ;
				break ;
			case SOUTHEAST :
				imageSouthEast = new Rectangle(tx/Spritesheet.getSSWidth(),
						   ty/Spritesheet.getSSHeight(),
						   this.width/Spritesheet.getSSWidth(),
						   this.height/Spritesheet.getSSHeight()) ;
				break ;
			default:
				throw new UnmanagedSwitchCaseException("Orientation "+or+" not managed") ;
			}
		return true ;
	}

	public int render(Graphics g) {

		switch(getOrientation()) {
			case NORTHWEST :
				currentAnim = animNorthWest ;
				currentSprite = imageNorthWest ;
				trueIfLastMoveE = false ;
				trueIfLastMoveN = true ;
				break ;
			case NORTHEAST :
				currentAnim = animNorthEast ;
				currentSprite = imageNorthEast ;
				trueIfLastMoveE = true ;
				trueIfLastMoveN = true ;
				break ;
			case SOUTHWEST :
				currentAnim = animSouthWest ;
				currentSprite = imageSouthWest ;
				trueIfLastMoveE = false ;
				trueIfLastMoveN = false ;
				break ;
			case SOUTHEAST :
				currentAnim = animSouthEast ;
				currentSprite = imageSouthEast ;
				trueIfLastMoveE = true ;
				trueIfLastMoveN = false ;
				break ;
			case NORTH :
				if(trueIfLastMoveE) {
					currentAnim = animNorthEast ;
					currentSprite = imageNorthEast ;
				} else {
					currentAnim = animNorthWest ;
					currentSprite = imageNorthWest ;
				}
				trueIfLastMoveN = true ;
				break ;
			case EAST :
				if(trueIfLastMoveN) {
					currentAnim = animNorthEast ;
					currentSprite = imageNorthEast ;
				} else {
					currentAnim = animSouthEast ;
					currentSprite = imageSouthEast ;
				}
				trueIfLastMoveE = true ;
				break ;
			case SOUTH :
				if(trueIfLastMoveE) {
					currentAnim = animSouthEast ;
					currentSprite = imageSouthEast ;
				} else {
					currentAnim = animSouthWest ;
					currentSprite = imageSouthWest ;
				}
				trueIfLastMoveN = false ;
				break ;
			case WEST :
				if(trueIfLastMoveN) {
					currentAnim = animNorthWest ;
					currentSprite = imageNorthWest ;
				} else {
					currentAnim = animSouthWest ;
					currentSprite = imageSouthWest ;
				}
				trueIfLastMoveE = false ;
				break ;
		}
		
		if(!isMove()) {
			Ingame.getInstance().getSpritsheet().drawEmbedded(
				getRelativeToCameraX(), 
				getRelativeToCameraY() ,
				getRelativeToCameraWidth(), 
				getRelativeToCameraHeight(),
				currentSprite
			);
		} else {
			currentAnim.update() ;
			currentAnim.draw(
				getRelativeToCameraX(), 
				getRelativeToCameraY() ,
				getRelativeToCameraWidth(), 
				getRelativeToCameraHeight()
			);
		}
		return 1 ;
	}

	/**
	 * Set params use to display it, size and centers
	 * @param h Height in the spritesheet
	 * @param w Width in the spritesheet
	 * @param dX X to shift to show sprite on tile center.
	 * @param dY Y to shift to show sprite on tile center.
	 */
	public void setParamDisplay(float w,float h,float dX, float dY) {
		setWidth(w) ;
		setHeight(h) ;
		setDecalX(dX) ;
		setDecalY(dY) ;
	}
	
	public void setWidth(float w) {
		this.width = w ;
	}
	
	public void setHeight(float h) {
		this.height = h ;
	}
	
	public float getHeight() {
		return height ;
	}

	public float getWidth() {
		return width ;
	}
	
	public void setDecalX(float dX) {
		this.decalX = dX ;
	}
	
	public void setDecalY(float dY) {
		this.decalY = dY ;
	}

	public float getDecalX() {
		return decalX ;
	}

	public float getDecalY() {
		return decalY ;
	}
	
	public boolean isMove() {
		return move ;
	}
	
	public void setMove(boolean b) {
		move = b ;
	}
	
	/**
	 * 
	 * @return Return width in pixel corresponding to the actual camera zoom
	 */
	public float getRelativeToCameraWidth() {
		return (Ingame.getInstance().getCamera().getCoeffZoom() * (getWidth()));
	}
	
	/**
	 * 
	 * @return Return height in pixel corresponding to the actual camera zoom
	 */
	public float getRelativeToCameraHeight() {
		return (Ingame.getInstance().getCamera().getCoeffZoom() * (getHeight()));
	}

	/**
	 * 
	 * @return Return x position in pixel corresponding to screen coords
	 */
	public float getInScreenCoordX() {
		return (getX() * 16 - getY() * 16  + getDecalX()) ;
	}

	/**
	 * 
	 * @return Return y position in pixel corresponding to screen coords
	 */
	public float getInScreenCoordY() {
		return (getX() * 8 + getY() * 8 - getZ() * 16 - getHeight() + getDecalY()) ;
	}
	/**
	 * 
	 * @return Return x position in pixel corresponding to the actual camera zoom and position
	 */
	public float getRelativeToCameraX() {
		return (Ingame.getInstance().getCamera().getCoeffZoom() * (getInScreenCoordX() - Ingame.getInstance().getCamera().getX())) ;
	}

	/**
	 * 
	 * @return Return y position in pixel corresponding to the actual camera zoom and position
	 */
	public float getRelativeToCameraY() {
		return (Ingame.getInstance().getCamera().getCoeffZoom() * (getInScreenCoordY() - Ingame.getInstance().getCamera().getY())) ;
	}
	
	private boolean active = true ;
	private Vector loc;
	private Orientation orientation;
	private Queue<Light> lights;
	private float decalX,decalY,width,height ;
	private boolean trueIfLastMoveN,trueIfLastMoveE,move ; 
	
	/*private AnimationSpritesheet animNorth ;
	private AnimationSpritesheet animSouth ;
	private AnimationSpritesheet animEst ;
	private AnimationSpritesheet animWest ;*/
	private AnimationSpritesheet animNorthEast ;
	private AnimationSpritesheet animNorthWest ;
	private AnimationSpritesheet animSouthEast ;
	private AnimationSpritesheet animSouthWest ;
	private AnimationSpritesheet currentAnim ;
	/*private Rectangle imageNorth ;
	private Rectangle imageSouth ;
	private Rectangle imageEst ;
	private Rectangle imageWest ;*/
	private Rectangle imageNorthEast ;
	private Rectangle imageNorthWest ;
	private Rectangle imageSouthEast ;
	private Rectangle imageSouthWest ;
	private Rectangle currentSprite ;

}
