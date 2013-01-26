package phobos.ui ;

import java.util.LinkedList;

import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.util.Log;

import phobos.engine.Engine;

public class SlickGUI {

	private LinkedList<AbstractComponent> listComponent = new LinkedList<AbstractComponent>() ;
	private boolean hasFocus = false  ;

	public boolean hasFocus() {
		return hasFocus ;
	}

	public LinkedList<AbstractComponent> getComponent() {
		return listComponent ;
	}

	public void addMouseArea(String img,int X,int Y,int width,int height,ComponentListener listener,String name) {
		try {
			listComponent.add(new SlickGuiMouseOverArea(new Image(img),X,Y,width,height,listener,true,name)) ;
		} catch (NullPointerException e) {
			Log.error("Null Pointer when adding MouseArea to GUI",e) ;
		} catch (SlickException e) {
			Log.error("Can't add button to GUI",e) ;
		}
	}

	public void addMouseArea(SlickGuiMouseOverArea ma) {
		listComponent.add(ma) ;
	}

	public void addCheckbox(SlickGuiCheckBox cb) {
		listComponent.add(cb) ;
	}

	public void addToggleButton(String img,int X,int Y,int width,int height,SlickGuiWindow window,String name) {
		try {
			listComponent.add(new SlickGuiToggleButton(new Image(img),X,Y,width,height,window,name)) ;
		} catch (NullPointerException e) {
			Log.error("Null Pointer when adding MouseArea to GUI",e) ;
		} catch (SlickException e) {
			Log.error("Can't add button to GUI",e) ;
		}
	}

	public void addWindow(SlickGuiWindow window) {
		try {
			//listWindow.add(window) ;
			listComponent.add(window) ;
		} catch (NullPointerException e) {
			Log.error("Null Pointer when adding SlickGuiWindow to GUI",e) ;
		}
	}

	public void addTextField(Font font,int X,int Y,int width,int height,ComponentListener listener,String name) {
		try {
			//listTextField.add(new SlickGuiTextField(font,X,Y,width,height,listener)) ;
			listComponent.add(new SlickGuiTextField(font,X,Y,width,height,listener,name)) ;
		} catch (NullPointerException e) {
			Log.error("Null Pointer when adding TextField to GUI",e) ;
		}
	}

	public void render(Graphics g) {
		hasFocus = false ;
		for(int i = 0;i < listComponent.size();i++) {
			String classComponent = listComponent.get(i).getClass().getName() ;
			if(classComponent == "phobos.ui.SlickGuiToggleButton") {
				SlickGuiToggleButton tb = (SlickGuiToggleButton)listComponent.get(i);
				tb.render(Engine.getInstance().getContainer(),g) ;
			}
			if(classComponent == "phobos.ui.SlickGuiMouseOverArea") {
				SlickGuiMouseOverArea moa = (SlickGuiMouseOverArea)listComponent.get(i);
				moa.render(Engine.getInstance().getContainer(),g) ;
			}
			if(classComponent == "phobos.ui.SlickGuiCheckBox") {
				SlickGuiCheckBox cb = (SlickGuiCheckBox)listComponent.get(i);
				cb.render(Engine.getInstance().getContainer(),g) ;
			}
			if(classComponent == "phobos.ui.SlickGuiTextField") {
				SlickGuiTextField tf = (SlickGuiTextField)listComponent.get(i);
				tf.render(Engine.getInstance().getContainer(),g) ;
				if(tf.hasFocus()) hasFocus = true ;
			}
			if(classComponent == "phobos.ui.SlickGuiWindow") {
				SlickGuiWindow w = (SlickGuiWindow)listComponent.get(i);
				if(w.isShow()) {
					if(w.hasFocus()) hasFocus = true ;
					if(w.isClicked()) {
						w.setClicked(false) ;
						listComponent.offerLast(w);
						listComponent.remove(i);
						Engine.getInstance().getContainer().getInput().removeListener(w) ;
						Engine.getInstance().getContainer().getInput().addPrimaryListener(w) ;
						i--;
						continue;
					}
					w.render(Engine.getInstance().getContainer(),g) ;
				}
			}
		}
	}

	public void update(int delta) {
		for(int i = 0;i < listComponent.size();i++) {
			String classComponent = listComponent.get(i).getClass().getName() ;
			if(classComponent == "phobos.ui.SlickGuiWindow") {
				SlickGuiWindow w = (SlickGuiWindow)listComponent.get(i);
				w.update(delta) ;
			}
		}
	}

	public void sleep() {
		for(int i = 0;i < listComponent.size();i++) {
			String classComponent = listComponent.get(i).getClass().getName() ;
			if(classComponent == "phobos.ui.SlickGuiToggleButton") {
			}
			if(classComponent == "phobos.ui.SlickGuiMouseOverArea") {
			}
			if(classComponent == "phobos.ui.SlickGuiCheckBox") {
			}
			if(classComponent == "phobos.ui.SlickGuiTextField") {
			}
			if(classComponent == "phobos.ui.SlickGuiWindow") {
				SlickGuiWindow w = (SlickGuiWindow)listComponent.get(i);
				w.sleep() ;
			}
		}
	}

	public void wakeup() {
		for(int i = 0;i < listComponent.size();i++) {
			String classComponent = listComponent.get(i).getClass().getName() ;
			if(classComponent == "phobos.ui.SlickGuiToggleButton") {
			}
			if(classComponent == "phobos.ui.SlickGuiMouseOverArea") {
			}
			if(classComponent == "phobos.ui.SlickGuiCheckBox") {
			}
			if(classComponent == "phobos.ui.SlickGuiTextField") {
			}
			if(classComponent == "phobos.ui.SlickGuiWindow") {
				SlickGuiWindow w = (SlickGuiWindow)listComponent.get(i);
				w.wakeup() ;
			}
		}
	}
}
