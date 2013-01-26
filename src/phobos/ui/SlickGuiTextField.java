package phobos.ui ;

import org.newdawn.slick.gui.*; 
import java.util.*; 
import org.newdawn.slick.AppGameContainer; 
import org.newdawn.slick.GameContainer; 
import org.newdawn.slick.Font ; 
import org.newdawn.slick.util.Log; 

import phobos.engine.Engine;

public class SlickGuiTextField extends TextField {

	//private float X,Y ;
	private String name ;

	public SlickGuiTextField(Font font,int X,int Y,int width,int height,ComponentListener listener,String name) {
		super(Engine.getInstance().getContainer(),font,X,Y,width,height,listener) ;
		this.name = name ;
	}

	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount) ;
		Log.info("Mouse Clicked : "+name) ;
	}
}
