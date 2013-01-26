package phobos.engine;

/**
 * Simple enumeration to literate the state of the game.
 * @author Half
 */
public enum States {
	MAIN_MENU(1), OPTION_MENU(2), INGAME(3), TestViewVBO(4) ;
	private int s;
	 
	private States(int a) {
	  s = a;
	}
	 
	public int getCode() {
	  return s;
	}
}
