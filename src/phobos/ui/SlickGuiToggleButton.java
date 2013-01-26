package phobos.ui ;

import org.newdawn.slick.Image;
import org.newdawn.slick.gui.MouseOverArea;

import phobos.engine.Engine;

public class SlickGuiToggleButton extends MouseOverArea {

	private SlickGuiWindow window ;
	private String name = "" ;

	public SlickGuiToggleButton(Image image,int X,int Y,int width,int height,SlickGuiWindow window,String name) {
		super(Engine.getInstance().getContainer(),image,X,Y,width,height) ;
		this.window = window ;
		this.name = name ;
	}

	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount) ;
		//Log.info("Mouse Clicked : "+name) ;
		if(isMouseOver()) {
			window.toggleWindow() ;
			consumeEvent() ;
		}
	}

	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		if(isMouseOver()) {
			consumeEvent() ;
		}
	}

}
