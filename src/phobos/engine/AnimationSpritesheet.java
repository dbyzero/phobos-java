package phobos.engine;

import java.util.LinkedList;
import org.newdawn.slick.geom.Rectangle;

/**
 * This class is use with {@link Spritesheet} to make an animated sprite
 * @author Half
 */


public class AnimationSpritesheet {
	private LinkedList<Rectangle> frames = new LinkedList<Rectangle>();
	private int currentFrame = 0;
	private int updateByFrame;
	private int updateLeft;
	private Spritesheet spriteSheet = null;

	/**
	 * @param ss The {@link Spritesheet} where animation is extracted
	 * @param updateByFrame How many frame before next sprite
	 */
	public AnimationSpritesheet(Spritesheet ss, int updateByFrame){
		spriteSheet = ss;
		this.updateByFrame = updateByFrame ;
	}

	public void addFrame(Rectangle rect){
		frames.add(rect) ;
	} 

	/**
	 * Decrease frame counter before next frame, if 0 go to next frame
	 */
	public void update() {
		if(updateLeft == 0) {
			if(++currentFrame == frames.size()) currentFrame = 0 ;
			updateLeft = updateByFrame ;
		} else {
			updateLeft-- ;
		}
	}

	/**
	 * 
	 * @param x Position x on screen
	 * @param y Position y on screen
	 * @param width Width of animation (sprite)
	 * @param height Height og animation (sprite)
	 */
	public void draw(float x,float y,float width,float height) {
		Rectangle rect = frames.get(currentFrame) ;
		spriteSheet.drawEmbedded(x,y,width,height,rect) ;
	}
}
