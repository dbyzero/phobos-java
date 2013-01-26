package phobos.engine;
//: c01:Engine.java

//lib general

import java.util.LinkedList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import phobos.engine.views.game.Ingame;
import phobos.engine.views.game.TestViewVBO;
import phobos.engine.views.menu.MainMenu;
import phobos.engine.views.menu.OptionMenu;

/**
 * Main class of phobos project, manage state, window, input
 * Implement singleton pattern
 * @author Half
 */
public class Engine extends StateBasedGame {

	static private String project = "Phobos" ;
	static private String version = "v0.01" ;
	static private int minUpdateMs = 20 ;
	static private int maxUpdateMs = 20 ;
	static private boolean azerty = false;
	static private Engine instanceEngine = null ;
	
	private int FPS = 60 ;
	private boolean fullscreen = false ;
	private boolean vSync = true ;
	private LinkedList<int[]> listResolution = new LinkedList<int[]>() ;
	private AppGameContainer container ;
	
	static public void main(String[] argv) {
		try {
			for (String string : argv) {
				if(string.equals("-azerty")) Engine.azerty = true;
				Log.info("KEYBOARD TYPE: AZERTY");
			}
			Engine engine = Engine.getInstance() ;
			AppGameContainer container = new AppGameContainer(engine); 
			container.setUpdateOnlyWhenVisible(false) ;
			container.setAlwaysRender(true) ;
			engine.setContainer(container) ;
			Log.info("Start " + Engine.project + " " + Engine.version) ;
			container.setDisplayMode(800,600,engine.fullscreen); 
			container.setVSync(engine.vSync); 
			container.setTargetFrameRate(engine.FPS); 
			//GL.glRotatef(rotate++, 0.0f, 0.0f, 1.0f); 
			container.start(); 
		}
		catch (SlickException e)
		{
			Log.error("[error] SlickExeption when container and display start",e);
		} 
	}

	static public int getMinUpdateMs() { 
		return minUpdateMs ;
	} 

	static public int getMaxUpdateMs() { 
		return maxUpdateMs ;
	} 
		
	static public Engine getInstance() {
		if(Engine.instanceEngine == null){
			Engine.instanceEngine = new Engine();
		}
		return Engine.instanceEngine ;
	}

	static public boolean isAzerty() {
		return azerty ;	
	}
	
	private Engine() { 
	 	super(Engine.project + " " + Engine.version); 
		listResolution.add(new int[] {1920,1080}) ;
		listResolution.add(new int[] {800,600}) ;
		listResolution.add(new int[] {1024,768}) ;
		listResolution.add(new int[] {1366,768}) ;
		listResolution.add(new int[] {1280,800}) ;
		listResolution.add(new int[] {1280,960}) ;
		listResolution.add(new int[] {1280,1024}) ;
		listResolution.add(new int[] {1360,1024}) ;
		listResolution.add(new int[] {1600,900}) ;
		listResolution.add(new int[] {1600,1200}) ;
	} 

	public void setContainer(AppGameContainer container) {
		this.container = container ;
	}

	@Override
	public AppGameContainer getContainer() {
		return this.container ;
	}

	public void nextResolution() {
		for(int i=0;i<listResolution.size();i++) {
			int[] resolution = (int[]) listResolution.get(i) ;
			if((container.getWidth() == resolution[0]) && (container.getHeight() == resolution[1])) {
				if(++i == listResolution.size()) {
					changeResolution(0) ;
				} else {
					changeResolution(i) ;
				}
				break ;
			}
		}
	}

	public void changeResolution(int indexReso) {
		int[] resolution = (int[]) listResolution.get(indexReso) ;
		if(container.getScreenWidth() < resolution[0] || container.getScreenHeight() < resolution[1]) {
			//Log.info("width < "+resolution[0] +" height < "+resolution[1]) ;
			if(++indexReso == listResolution.size()) {
				changeResolution(0) ;
			} else {
				changeResolution(indexReso) ;
			}
			return;
		}
		try
		{
			container.setDisplayMode(resolution[0],resolution[1],container.isFullscreen()) ;
			Ingame IGstate ;
			IGstate = (Ingame) getState(States.INGAME.getCode()) ;
			IGstate.getCamera().setWidth(resolution[0]) ;
			IGstate.getCamera().setHeight(resolution[1]) ;
		} catch(SlickException e) {
			Log.error("Can't change resolution to "+resolution[0]+"x"+resolution[1],e) ;
		}
	}

	@Override
	public void initStatesList(GameContainer container) { 
		//addState(TestViewVBO.getInstance()); 
//		addState(MainMenu.getInstance()); 
		addState(Ingame.getInstance()); 
//		addState(OptionMenu.getInstance()); 
	}
	

}
