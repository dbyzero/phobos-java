package phobos.engine;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

/**
 * This class represent the spritesheet
 * @author Half
 */

public class Spritesheet extends Image {

	static private final int width = 2048 ;
	static private final int height = 2048 ;

	public Spritesheet(String str) throws SlickException {
		super(str) ;
	}

	static public float getSSWidth() {
		return Spritesheet.width ;
	}

	static public float getSSHeight() {
		return Spritesheet.height ;
	}

	/**
	 * Draw a sprite of the spritesheet on the screen, "Embedded" mean "do not call glBegin / glEnd in opengl" 
	 * @param x X position of sprite on screen
	 * @param y Y position of sprite on screen
	 * @param width width of sprite on screen
	 * @param height height of sprite on screen
	 * @param rect {@link Rectangle} on spritesheet where sprite is located
	 */
	public void drawEmbedded(float x,float y,float width,float height,Rectangle rect) {
		drawEmbedded(x,y,width,height,rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight()) ;
	}
	
	/**
	 * Draw a sprite of the spritesheet on the screen, "Embedded" mean "do not call glBegin / glEnd in opengl" 
	 * @param x X position of sprite on screen
	 * @param y Y position of sprite on screen
	 * @param width width of sprite on screen
	 * @param height height of sprite on screen
	 * @param textureOffsetX x position of sprite on spritesheet
	 * @param textureOffsetY x position of sprite on spritesheet
	 * @param textureWidth width of sprite on spritesheet
	 * @param textureHeight height of sprite on spritesheet
	 */
	public void drawEmbedded(float x,float y,float width,float height,float textureOffsetX,float textureOffsetY,float textureWidth,float textureHeight) {
		super.init();

		GL.glTexCoord2f(textureOffsetX, textureOffsetY);
		GL.glVertex3f(x, y, 0);
		GL.glTexCoord2f(textureOffsetX, (textureOffsetY + textureHeight));
		GL.glVertex3f(x, y + height, 0);
		GL.glTexCoord2f((textureOffsetX + textureWidth), (textureOffsetY
		  	+ textureHeight));
		GL.glVertex3f(x + width, y + height, 0);
		GL.glTexCoord2f((textureOffsetX + textureWidth), textureOffsetY);
		GL.glVertex3f(x + width, y, 0);
	}
}
