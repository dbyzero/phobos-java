package phobos.engine.controllers.motions;

import java.util.Queue;

import phobos.engine.models.entities.Entity;
import phobos.engine.models.entities.MobileEntity;
import phobos.engine.models.motions.Impulse;
import phobos.engine.views.game.Ingame;
import phobos.utils.math.Vector;

/**
 * 
 * @author Leosky, Half
 */
public class MotionController {
	
	/**
	 * 
	 * @param delta time in ms since last update
	 */
	public void update(int delta){
		for (Entity entity : Ingame.getInstance().entityList) {
			//test to know if we have a mobile that is not sleeping.
			if(entity instanceof MobileEntity && entity.isActive()){ 
				
				MobileEntity current = (MobileEntity)entity; // it's okay our dude is a mobile dude.
				Vector speed = current.getSpeed();
				Queue<Impulse> current_list = current.getImpulsesQueue();
				
				//get old position to go back if collision
				Vector oldLocation = new Vector(current.getLocation().getX(),current.getLocation().getY(),current.getLocation().getZ()) ;
				
				for (Impulse impulse : current_list) {
					//apply impulse to position
					if(!impulse.update(delta)){	// if impulse is died after his update ...
						current.removeImpulse(impulse); // ... we bury it :'(
					}
				}
				
				//apply speed to position
				current.getLocation().addition(speed.getScaledVector((float)(delta/1000.0)));
				
				//if Z collision go to old position else update tile owner
				if(current.getZ() -0.5f < Ingame.getInstance().getTile(current.getLocation().getX(),current.getLocation().getY()).getZ()) {
					current.setLocation(oldLocation) ;
				}
				
				//update tile owner
				current.updateTileOwner() ;				
			}
		}
	}
}