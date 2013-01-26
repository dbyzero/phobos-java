package phobos.engine.models.lights;

import org.newdawn.slick.Color;

import phobos.engine.models.entities.Entity;

/**
 * Simple pulsating light without using of advanced effect. <br>
 * The radius start to this maximum and decrease to this minimum before restart to increase to this maximum value on the duration of the period.
 * <br>
 * <br>
 * To modify the initial value of the radius, you can call <code>light.setRadius(byte newRadius)</code> just after the creation of the instance.
 * @author Leosky
 * @version r1
 *
 */
public class PulsatingLight extends Light{

	private byte minRadius;
	private byte maxRadius;
	private boolean increasing = true;
	private long last_update = 0;
	private int deltaT;

	public PulsatingLight(Color col, Entity entity, byte minRadius, byte maxRadius, int periode) {
		super(col, minRadius, entity);

		this.maxRadius = maxRadius;
		this.minRadius = minRadius;
		this.deltaT = periode/(2*(maxRadius - minRadius));
	}

	@Override
	public boolean update(int delta) {
		
		if(System.currentTimeMillis() >= last_update + deltaT){
			if(increasing){
				this.setRadius((byte) (this.getRadius()+1));
				if(this.getRadius() >= this.maxRadius) {
					increasing = false;
					
				}
			}else{
				this.setRadius((byte) (this.getRadius()-1));
				if(this.getRadius() <= this.minRadius) increasing = true;
			}
			this.last_update = System.currentTimeMillis();
		}
		return true;
	}

	public int compareTo(Light arg0) {
		return 0;
	}
	
}
