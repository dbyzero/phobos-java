package phobos.engine.models.lights;

import org.newdawn.slick.Color;

import phobos.engine.models.entities.Entity;

/**
 * 
 * Light is describing the method used by the LightController to determine the enlightenment on each title. 
 * 
 * The radius of the Light is corresponding to the maximum distance (in tiles) of enlightenment. The color, is the color in RGB and the intensity of the Light.
 * 
 * @author Leosky, Half
 * @version r1
 */
public abstract class Light implements Comparable<Light> {
	
	protected Light(Color col, byte radius, Entity entity) {
		this.color = col;
		this.radius = radius;
		this.entity = entity;
	}

	/**
	 * 
	 * @return <b>byte</b> corresponding to the radius of the Light.
	 */
	public byte getRadius() {
		return radius;
	}
		
	public void setRadius(byte radius) {
		this.radius = radius ;
	}
	
	/**
	 * 
	 * @return <b>org.newdawn.slick.Color</b> the Color of the Light.
	 */
	public Color getColor(){
		return this.color;
	}
	
	
	public void setColor(Color col) {
		this.color = col ;
	}
	
	/**
	 * Called to iterate the life-cycle of a Light.
	 * @param delta time in ms since last update
	 * @return <b>true</b> if the life-cycle is not finished.
	 */
	public abstract boolean update(int delta);
	
	/**
	 * Called to know the associated Entity.
	 * @return <b>Entity</b>
	 */
	public Entity getEntity() {
		return entity;
	}
	
	/**
	 * Called to know if the light is active.
	 * @return <b>boolean</b> true if active
	 */
	public boolean isActive() {
		return active ;
	}

	public void setActive(boolean b) {
		this.active = b ;
	}
	
	
	private Color color ;
	private boolean active = true ;
	private byte radius ;
	private Entity entity ;
}
