package phobos.engine.models.lights;
import org.newdawn.slick.Color;

import phobos.engine.models.entities.Entity;

/**
 *	Direct inherited for Light abstract class
 *	@author Half
 */

public class SimpleLight extends Light {

	/**
	  * @param col color of light
	  * @param radius radius of light \o\
	  * @param ent Entity linked to the light
	  */
	public SimpleLight (Color col,byte radius,Entity ent ) {
		super(col,radius,ent);
	}
	
	/**
	 * @param delta Time since last update in ms 
	 */
	public boolean update(int delta) {
		return true ;
	}

	public int compareTo(Light o) {
		return 0;
	}

}
