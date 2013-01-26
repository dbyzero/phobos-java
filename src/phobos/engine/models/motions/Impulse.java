package phobos.engine.models.motions;


/**
 * The interface Impuse define the condition of an object to be used by a MotionController to alter the
 * motion state of a MobileEntity. <br>
 * 
 * Impulse is the object that describe acceleration in Newtonian physic. It's used to get the acceleration
 * vector that alter the motion vector of MobileEntity.
 * 
 * @author Leosky
 * @version r2
 */
public abstract class Impulse{
	protected boolean active = true;
	/**
	 * A call on this method iterate the life-cycle of the Impulse.
	 * @return <b>true</b> if the life-cycle is not finished.
	 */
	public abstract boolean update(int delta);
	
	public boolean isActive() {
		return active ;
	}

	public void setActive(boolean b) {
		this.active = b ;
	}

}
