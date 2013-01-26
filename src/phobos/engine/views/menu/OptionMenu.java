package phobos.engine.views.menu;
//: c01:MainMenu.java

//lib general

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import phobos.engine.Engine;
import phobos.engine.States;
import phobos.ui.SlickGUI;
import phobos.ui.SlickGuiCheckBox;
import phobos.ui.SlickGuiMouseOverArea;

/**
 * Option Menu state, implements singleton pattern
 * @author half
 *
 */
public class OptionMenu extends BasicGameState {
	
	static private OptionMenu instance ;
	
	private int testNbrUp = 0 ;

	private SlickGUI gui ;
	private UnicodeFont font ;
	private Color bgColor = new Color(0.1f,0.12f,0.16f,1.0f) ;


	static public OptionMenu getInstance() {
		if(OptionMenu.instance == null){
			OptionMenu.instance = new OptionMenu();
		}
		return OptionMenu.instance ;
	}
	
	private OptionMenu() {

	}
	
	public void init(final GameContainer container,final StateBasedGame engine) throws SlickException
	{
		String fontPath = "res/FreeSans.ttf";
		font = new UnicodeFont(fontPath , 36, false, false); 
		font.addAsciiGlyphs();   
		font.getEffects().add(new ColorEffect(java.awt.Color.WHITE)); 
		font.loadGlyphs();  
		
		this.gui = new SlickGUI() ;
		Engine.getInstance().getContainer().setMinimumLogicUpdateInterval(Engine.getMinUpdateMs()) ;
		Engine.getInstance().getContainer().setMaximumLogicUpdateInterval(Engine.getMaxUpdateMs()) ;

		//Resolution button
		final Engine eng = (Engine) engine ;

		/** START INIT GUI **/
		SlickGuiMouseOverArea resoBtn = new SlickGuiMouseOverArea(new Image("res/resoButton.png"),200,100,176,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					eng.nextResolution() ;
				}
			},false,"Option menu, resolution button"
		) ;

		//Quit button
		SlickGuiMouseOverArea quitBtn = new SlickGuiMouseOverArea(new Image("res/returnButton.png"),200,400,110,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					engine.enterState(States.MAIN_MENU.getCode()) ;
				}
			},false,"Option menu, return button"
		) ;

		//Show if fullscreen and can switch by clinking
		final SlickGuiCheckBox fsBtnStatus = new SlickGuiCheckBox(new Image("res/check_ok.png"),new Image("res/check_nok.png"),400,200,Engine.getInstance().getContainer().isFullscreen());
		fsBtnStatus.addListener(
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					try {
						Engine.getInstance().getContainer().setFullscreen(!Engine.getInstance().getContainer().isFullscreen()) ;
						fsBtnStatus.setActive(Engine.getInstance().getContainer().isFullscreen()) ;
					} catch (SlickException e) {
						Log.error("Error switch fullscrenn mode",e) ;
					}
				}
			}
		) ;

		//Fullscreen button
		SlickGuiMouseOverArea fsBtn = new SlickGuiMouseOverArea(new Image("res/fullscreenButton.png"),200,200,170,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					try {
						Engine.getInstance().getContainer().setFullscreen(!Engine.getInstance().getContainer().isFullscreen()) ;
						fsBtnStatus.setActive(Engine.getInstance().getContainer().isFullscreen()) ;
					} catch (SlickException e) {
						Log.error("Error switch fullscrenn mode",e) ;
					}
				}
			},false,"Option menu, fullscrenn btn"
		) ;
		//Show if vsync and can switch by clinking
		final SlickGuiCheckBox vsyncBtnStatus = new SlickGuiCheckBox(new Image("res/check_ok.png"),new Image("res/check_nok.png"),400,300,Engine.getInstance().getContainer().isFullscreen());
		vsyncBtnStatus.addListener(
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					vsyncBtnStatus.setActive(Engine.getInstance().getContainer().isVSyncRequested()) ;
					Engine.getInstance().getContainer().setVSync(!Engine.getInstance().getContainer().isVSyncRequested()) ;
				}
			}
		) ;
		//Fullscreen vsync
		SlickGuiMouseOverArea vsyncBtn = new SlickGuiMouseOverArea(new Image("res/vSyncButton.png"),200,300,106,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					vsyncBtnStatus.setActive(Engine.getInstance().getContainer().isVSyncRequested()) ;
					Engine.getInstance().getContainer().setVSync(!Engine.getInstance().getContainer().isVSyncRequested()) ;
				}
			},false,"Option menu, VSync btn"
		) ;
		quitBtn.setMouseOverImage(new Image("res/returnButtonActive.png")) ;
		quitBtn.setMouseDownImage(new Image("res/returnButtonActive.png")) ;
		fsBtn.setMouseOverImage(new Image("res/fullscreenButtonActive.png")) ;
		fsBtn.setMouseDownImage(new Image("res/fullscreenButtonActive.png")) ;
		vsyncBtn.setMouseOverImage(new Image("res/vSyncButtonActive.png")) ;
		vsyncBtn.setMouseDownImage(new Image("res/vSyncButtonActive.png")) ;
		resoBtn.setMouseOverImage(new Image("res/resoButtonActive.png")) ;
		resoBtn.setMouseDownImage(new Image("res/resoButtonActive.png")) ;
		gui.addMouseArea(quitBtn) ;
		gui.addMouseArea(resoBtn) ;
		gui.addMouseArea(fsBtn) ;
		gui.addCheckbox(fsBtnStatus) ;
		gui.addMouseArea(vsyncBtn) ;
		gui.addCheckbox(vsyncBtnStatus) ;
		/** END INIT GUI **/
	}

	public void render(GameContainer container,StateBasedGame engine, Graphics g)
	{
		g.setBackground(bgColor) ;
		g.drawString("Update : " + testNbrUp, 10, 30);
		g.setFont(font) ;
		g.drawString(""+Engine.getInstance().getContainer().getWidth()+"x"+Engine.getInstance().getContainer().getHeight(),400,102) ;
		g.resetFont() ;
		gui.render(g) ;
	}

	public void update(GameContainer container,StateBasedGame engine, int delta)
	{
		gui.update(delta);
		testNbrUp++;
	} 

	@Override
	public void enter(GameContainer container,StateBasedGame engine)
	{
	} 

	@Override
	public void leave(GameContainer container,StateBasedGame engine)
	{
	} 

	@Override
	public int getID()
	{
		return States.OPTION_MENU.getCode() ;
	} 
}

