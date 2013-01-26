package phobos.ui ;

import java.util.LinkedList;

import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.util.Log;

import phobos.engine.Engine;

public class SlickGuiWindow extends MouseOverArea {

	private LinkedList<Object> listComponent = new LinkedList<Object>() ;
	private SlickGuiMouseOverArea btnClose ;
	private boolean hasFocus = false  ;
	private boolean show = true  ;
	private boolean top = false  ;
	private boolean showCross = true  ;
	private boolean clicked = false  ;
	private boolean sleep = false  ;
	private String name = "" ;

	public SlickGuiWindow(Image img,int X,int Y,int width,int height,String name) {
		super(Engine.getInstance().getContainer(),img,X,Y,width,height) ;
		this.name = name ;
		try {
			listComponent.add(new SlickGuiMouseOverArea(new Image("res/close.png"),(X + width-16),Y,16,16,
				new ComponentListener() {
					public void componentActivated(AbstractComponent source) { 
						show = false ;
					}
				}
			,false,name+" close btn")) ;
		} catch (NullPointerException e) {
			Log.error("Null Pointer when adding MouseArea to GUI Window",e) ;
		} catch (SlickException e) {
			Log.error("Can't add button to GUI Window",e) ;
		}
		//setInput(new Input(600)) ;
	}

	public Object getLastAdded() {
		return listComponent.getLast() ;
	}

	public Object getComponent(int c) {
		return listComponent.get(c) ;
	}

	public void mouseClicked(int button, int x, int y, int clickCount) {
		//Log.info("Mouse Clicked : "+name) ;
		if(isMouseOver() && show && !sleep) {
			for(int i = 0;i < listComponent.size();i++) {
				String classComponent = listComponent.get(i).getClass().getName() ;
				if(classComponent == "phobos.ui.SlickGuiMouseOverArea") {
					SlickGuiMouseOverArea moa = (SlickGuiMouseOverArea)listComponent.get(i);
					if(moa.isMouseOver()) moa.notifyListeners() ;
				}
				if(classComponent == "phobos.ui.SlickGuiTextField") {
					SlickGuiTextField tf = (SlickGuiTextField)listComponent.get(i);
					tf.mouseClicked(button, x, y, clickCount) ;
					tf.mouseReleased(button, x, y) ;
				}
			}
			consumeEvent() ;
		}
	}

	public void mousePressed(int button, int mx, int my) {
	        if(isMouseOver() && show && !sleep) {
			setClicked(true) ;
			consumeEvent() ;
		}
	}

	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		if(isMouseOver() && show && !sleep) {
			setX((float) (getX() + newx - oldx)) ;
			setY((float) (getY() + newy - oldy)) ;
			for(int i = 0;i < listComponent.size();i++) {
				String classComponent = listComponent.get(i).getClass().getName() ;
				if(classComponent == "phobos.ui.SlickGuiMouseOverArea") {
					SlickGuiMouseOverArea moa = (SlickGuiMouseOverArea)listComponent.get(i);
					moa.setX((float) (moa.getX() + newx - oldx)) ;
					moa.setY((float) (moa.getY() + newy - oldy)) ;
				}
				if(classComponent == "phobos.ui.SlickGuiTextField") {
					SlickGuiTextField tf = (SlickGuiTextField)listComponent.get(i);
					tf.setLocation((tf.getX() + newx - oldx),(tf.getY() + newy - oldy)) ;
				}
				if(classComponent == "org.newdawn.slick.particles.ParticleSystem") {
					ParticleSystem ps = (ParticleSystem)listComponent.get(i);
					ps.setPosition((ps.getPositionX() + newx - oldx),(ps.getPositionY() + newy - oldy)) ;
				}
			}
			consumeEvent() ;
		}
	}

	public void addMouseArea(String img,int X,int Y,int width,int height,ComponentListener listener,String name) {
		try {
			listComponent.add(new SlickGuiMouseOverArea(new Image(img),getX()+X,getY()+Y,width,height,listener,false,name)) ;
		} catch (NullPointerException e) {
			Log.error("Null Pointer when adding MouseArea to GUI Window",e) ;
		} catch (SlickException e) {
			Log.error("Can't add button to GUI Window",e) ;
		}
	}

	public void addTextField(Font font,int X,int Y,int width,int height,ComponentListener listener,String name) {
		try {
			listComponent.add(new SlickGuiTextField(font,getX()+X,getY()+Y,width,height,listener,name)) ;
		} catch (NullPointerException e) {
			Log.error("Null Pointer when adding TextField to GUI Window",e) ;
		}
	}

	public void addParticleSystem(ParticleSystem ps) {
		try {
			listComponent.add(ps) ;
		} catch (NullPointerException e) {
			Log.error("Null Pointer when adding ParticleSystem to GUI Window",e) ;
		}
	}

	public void render(GameContainer container, Graphics g) {
		if(show) {
			super.render(container,g) ;
			
			setFocus(false) ;
			for(int i = 0;i < listComponent.size();i++) {
				String classComponent = listComponent.get(i).getClass().getName() ;
				if(classComponent == "phobos.ui.SlickGuiMouseOverArea") {
					SlickGuiMouseOverArea moa = (SlickGuiMouseOverArea)listComponent.get(i);
					moa.render(container,g) ;
					if(moa.hasFocus()) hasFocus = true ;
				}
				if(classComponent == "phobos.ui.SlickGuiTextField") {
					SlickGuiTextField tf = (SlickGuiTextField)listComponent.get(i);
					tf.render(container,g) ;
					if(tf.hasFocus()) { 
						setFocus(true) ;
					}
				}
				if(classComponent == "org.newdawn.slick.particles.ParticleSystem") {
					ParticleSystem ps = (ParticleSystem)listComponent.get(i);
					ps.render() ;
				}
			}
		}
	}

	public void update(int delta) {
		for(int i = 0;i < listComponent.size();i++) {
			String classComponent = listComponent.get(i).getClass().getName() ;
			if(classComponent == "org.newdawn.slick.particles.ParticleSystem") {
				ParticleSystem ps = (ParticleSystem)listComponent.get(i);
				ps.update(delta) ;
			}
		}
	}

	public void wakeup() {
		sleep = false ;
	}

	public void sleep() {
		sleep = true ;
	}

	public void showWindow() {
		show = true ;
	}

	public void hideWindow() {
		show = false ;
	}

	public void removeCross() {
		listComponent.removeFirst() ;
	}

	public void setFocus(boolean focus) {
		hasFocus = focus ;
	}

	public void setClicked(boolean focus) {
		clicked = focus ;
	}

	public boolean hasFocus() {
		return hasFocus ;
	}

	public boolean isClicked() {
		return clicked ;
	}

	public void toggleWindow() {
		show = !show ;
	}

	public boolean isShow() {
		return show ;
	}
}
