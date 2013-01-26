package phobos.engine.models.entities;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import phobos.engine.models.motions.Impulse;
import phobos.engine.views.game.Ingame;
import phobos.utils.math.Vector;

/**
 * Entity describe a mobile element in the world. It extend Entity to add it the notion of speed and indirectly the notion of acceleration through the MotionController.
 * 
 * As the speed may be influenced by the MotionController and the Effects.
 * @author Leosky
 * @version r1
 */
public class MobileEntity extends Entity {
	
	public MobileEntity(float x, float y, float z, Orientation o) {
		super(x, y, z, o);
		tilePositionX = (int) x ;
		tilePositionY = (int) y ;
		this.speed = new Vector(0,0,0);
		this.impulses = new ConcurrentLinkedQueue<Impulse>();
	}
	
	public Queue<Impulse> getImpulsesQueue(){
		return this.impulses;
	}
	
	public boolean addImpulse(Impulse i){
		return this.impulses.add(i);
	}
	
	public boolean addImpulses(Collection<Impulse> c){
		return this.impulses.addAll(c);
	}
	
	public boolean removeImpulse(Impulse i){
		return this.impulses.remove(i);		
	}
	
	public Vector getSpeed() {
		return speed;
	}
	
	public void setSpeed(Vector vec) {
		this.speed = vec;
	}
	
	public void addSpeed(Vector vec){
		this.speed.addition(vec);
	}

	public void updateTileOwner() {
		if(!onHisTile()) {
			Ingame.getInstance().getTile(tilePositionX,tilePositionY).removeEntity(this) ;
			Ingame.getInstance().getTile(getX(),getY()).addEntity(this) ;
			tilePositionX = (int) getX() ;
			tilePositionY = (int) getY() ;
		}
	}
	
	public boolean onHisTile() {
		if((tilePositionX == (int) getX()) && (tilePositionY == (int) getY())) {
			return true ;
		} else {
			return false ;
		}
	}
	
	private int tilePositionX,tilePositionY ;
	private Vector speed;
	private Queue<Impulse> impulses;
}