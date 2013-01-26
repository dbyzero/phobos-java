package phobos.engine.views.menu;
//: c01:MainMenu.java

//lib general

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.BlobbyTransition;
import org.newdawn.slick.state.transition.EmptyTransition;

import phobos.engine.Engine;
import phobos.engine.States;
import phobos.ui.SlickGUI;
import phobos.ui.SlickGuiMouseOverArea;

/**
 * In Menu state, implements singleton pattern
 * @author half
 */
public class MainMenu extends BasicGameState {
	
	static private MainMenu instance ;
	
	private int testNbrUp = 0 ;

	private boolean quit ;
	private SlickGUI gui ;
	//private Color bgColor = new Color(0.8f,0.82f,0.86f,1.0f) ;
	private Color bgColor = new Color(0.1f,0.12f,0.16f,1.0f) ;

	static public MainMenu getInstance() {
		if(MainMenu.instance == null){
			MainMenu.instance = new MainMenu();
		}
		return MainMenu.instance ;
	}
	
	public void init(GameContainer container,final StateBasedGame engine) throws SlickException
	{
		Engine.getInstance().getContainer().setMouseCursor("res/cursor.png",0,0) ;
		this.gui = new SlickGUI() ;
		Engine.getInstance().getContainer().setMinimumLogicUpdateInterval(Engine.getMinUpdateMs()) ;
		Engine.getInstance().getContainer().setMaximumLogicUpdateInterval(Engine.getMaxUpdateMs()) ;
		this.quit = false ;

		SlickGuiMouseOverArea startBtn = new SlickGuiMouseOverArea(new Image("res/startButton.png"),200,200,200,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					engine.enterState(States.INGAME.getCode(),new EmptyTransition(),new BlobbyTransition()) ;
				}
			},false,"Main menu, start button"
		
		) ;
		SlickGuiMouseOverArea optionBtn = new SlickGuiMouseOverArea(new Image("res/optionButton.png"),200,300,112,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					engine.enterState(States.OPTION_MENU.getCode()) ;
				}
			},false,"Main menu, option btn"
		
		) ;
		SlickGuiMouseOverArea quitBtn = new SlickGuiMouseOverArea(new Image("res/quitButton.png"),200,400,71,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					quit = true ;
				}
			},false,"Main menu, quit button"
		
		) ;
		startBtn.setMouseOverImage(new Image("res/startButtonActive.png")) ;
		startBtn.setMouseDownImage(new Image("res/startButtonActive.png")) ;
		optionBtn.setMouseOverImage(new Image("res/optionButtonActive.png")) ;
		optionBtn.setMouseDownImage(new Image("res/optionButtonActive.png")) ;
		quitBtn.setMouseOverImage(new Image("res/quitButtonActive.png")) ;
		quitBtn.setMouseDownImage(new Image("res/quitButtonActive.png")) ;
		gui.addMouseArea(startBtn) ;
		gui.addMouseArea(optionBtn) ;
		gui.addMouseArea(quitBtn) ;
	}

	public void render(GameContainer container,StateBasedGame engine, Graphics g)
	{
		g.setBackground(bgColor) ;
		g.drawString("Update : " + testNbrUp, 10, 30);
		gui.render(g) ;
	}

	public void update(GameContainer container,StateBasedGame engine, int delta)
	{
		gui.update(delta);
		testNbrUp++;
		if(quit) Engine.getInstance().getContainer().exit() ;
	} 

	@Override
	public void enter(GameContainer container,StateBasedGame engine)
	{
		quit = false ;
	} 

	@Override
	public void leave(GameContainer container,StateBasedGame engine)
	{
	} 

	@Override
	public int getID()
	{
		return States.MAIN_MENU.getCode() ;
	} 
}
