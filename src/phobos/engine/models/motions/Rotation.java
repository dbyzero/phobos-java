package phobos.engine.models.motions;

import phobos.engine.models.entities.Entity;
import phobos.engine.models.entities.MobileEntity;
import phobos.utils.math.Vector;
import phobos.utils.math.Math;

/**
 * 
 * @author Leosky
 *
 */
public class Rotation extends Impulse {

	private double angular;
	private double teta;
	private Entity center;
	private MobileEntity mobile;
	private float radius;
	private boolean first_update;
	private float t;
	private int periode;
	private float last_x;
	private float last_y;
	private float last_z;

	public Rotation(Entity centralEntity, MobileEntity rotatingEntity, int periode) {
		this.center = centralEntity;
		this.mobile = rotatingEntity;
		Vector radius = Vector.minus(rotatingEntity.getLocation(), centralEntity.getLocation());
		this.radius = radius.getIntensity();
		this.angular = 6.28318531 / periode;
		this.periode = periode;
		this.teta = java.lang.Math.acos(radius.getX() / radius.getIntensity());
	}

	@Override
	public boolean update(int delta) {
		if(this.active) {
			if(first_update) {
				t = 0;
			}else{
				t += delta;
				if(t >= periode){
					t -= periode;
				}
			}
			
			last_x = mobile.getX();
			last_y = mobile.getY();
			last_z = mobile.getZ();
			
			float next_x = (float) (center.getX() + Math.cosLow(Math.warpToPi((float) (teta + angular * t)))*radius);
			float next_y = (float) (center.getY() + Math.sinLow(Math.warpToPi((float) (teta + angular * t)))*radius);
			float next_z = (float) center.getZ();
			
			if(center instanceof MobileEntity){
				next_x += ((MobileEntity)center).getSpeed().getX();
				next_y += ((MobileEntity)center).getSpeed().getY();
				next_z += ((MobileEntity)center).getSpeed().getZ();
			}
			
			mobile.setSpeed(new Vector((next_x - last_x),(next_y - last_y), (next_z - last_z)));
		}
		
		return true;
	}
	
	public void setPeriode(int periode){
		angular = 6.28318531 / periode;
	}

}
