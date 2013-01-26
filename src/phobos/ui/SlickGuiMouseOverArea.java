package phobos.ui ;

import org.newdawn.slick.Image;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;

import phobos.engine.Engine;

public class SlickGuiMouseOverArea extends MouseOverArea {

	private boolean draggable = true ;
	private String name = "" ;

	public SlickGuiMouseOverArea(Image image,int X,int Y,int width,int height,ComponentListener listener,boolean draggable,String name) {
		super(Engine.getInstance().getContainer(),image,X,Y,width,height,listener) ;
		this.name = name ;
		this.draggable = draggable ;
	}

	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		if(draggable && isMouseOver()) {
			setX((float) (getX() + newx - oldx)) ;
			setY((float) (getY() + newy - oldy)) ;
		}
	}

	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount) ;
		//Log.info("Mouse Clicked : "+name) ;
		if(isMouseOver()) {
		}
	}

	public void notifyListeners() {
		super.notifyListeners() ;
	}	
}
