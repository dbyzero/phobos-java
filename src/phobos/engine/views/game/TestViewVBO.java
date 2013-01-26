package phobos.engine.views.game;
//: c01:Ingame.java

//lib general
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import phobos.engine.States;
import phobos.engine.models.render.Vbo_Render;


public class TestViewVBO extends BasicGameState {
	
	static private TestViewVBO instance = null ;
	static private Vbo_Render vbo = null ;

	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		vbo = new Vbo_Render() ;
		vbo.init() ;
	}

	public void render(GameContainer arg0, StateBasedGame arg1, Graphics g)
			throws SlickException {
		// TODO Auto-generated method stub
		vbo.render(g) ;
	}

	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
	}

	@Override
	public int getID() {
		return States.TestViewVBO.getCode() ;
	}

	static public TestViewVBO getInstance() {
		if(TestViewVBO.instance == null){
			TestViewVBO.instance = new TestViewVBO();
		}
		return TestViewVBO.instance ;
	}
}