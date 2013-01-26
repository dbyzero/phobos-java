package phobos.engine;

import phobos.engine.models.entities.Entity;

/**
 * Camera of the game
 * Scroll and can zoom
 * @author Half
 */

public class Camera {
	private int X,Y,width,height ;
	private float coeffZoom = 1.0f ;

	public Camera(int x,int y,int w,int h) {
		this.X = x ;
		this.Y = y ;
		this.width = w ;
		this.height = h ;
	}

	public void setX(int x) {
		this.X = x ;
	}

	public void setY(int y) {
		this.Y = y ;
	}

	public void setWidth(int w) {
		this.width = w ;
	}

	public void setHeight(int h) {
		this.height = h ;
	}

	public void setCoeff(float c) {
		this.coeffZoom = c ;
	}

	public int getX() {
		return this.X ;
	}

	public int getY() {
		return this.Y ;
	}

	public int getWidth() {
		return this.width ;
	}

	public int getHeight() {
		return this.height ;
	}

	public float getCoeffZoom() {
		return this.coeffZoom ;
	}
	
	public void centerTo(Entity e) {
		setX((int)(e.getInScreenCoordX() + e.getWidth()/2 - getWidth()/(2*coeffZoom))) ;
		setY((int)(e.getInScreenCoordY() + e.getHeight()/2 - getHeight()/(2*coeffZoom))) ;
	}
}
