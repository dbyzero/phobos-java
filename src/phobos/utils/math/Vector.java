package phobos.utils.math;

public class Vector {

	//////////////////////////////	
	/////  CONSUTRUCTEURS    /////
	//////////////////////////////
	public Vector(float x, float y, float z){
		this.coord = new float[3];
		this.coord[0] = x;
		this.coord[1] = y;
		this.coord[2] = z;
	}

	/////////////////////////////////	
	/////  GETTERS / SETTERS    /////
	/////////////////////////////////
	public float getX() {
		return this.coord[0];
	}

	public float[] getCoords() {
		return coord;
	}
	
	
	public void setX(float x) {
		this.coord[0] = x;
	}

	public float getY() {
		return coord[1];
	}

	public void setY(float y) {
		this.coord[1] = y;
	}

	public float getZ() {
		return coord[2];
	}

	public void setZ(float z) {
		this.coord[2] = z;
	}
	
	public void addX(float _x){
		this.coord[0] += _x;
	}
	
	public void addY(float _y){
		this.coord[1] += _y;
	}
	
	public void addZ(float _z){
		this.coord[2] += _z;
	}
	
	//////////////////////////////////	
	/////  METHODES STATIQUES    /////
	//////////////////////////////////
	/**
	 * 
	 * @param v1
	 * @param v2
	 * @return addition vectorielle
	 */
	public static Vector addition(Vector v1, Vector v2){
		return new Vector(v1.coord[0]+v2.coord[0],v1.coord[1]+v2.coord[1],v1.coord[2]+v2.coord[2]);
	}
	
	public static Vector minus(Vector v1, Vector v2){
		return new Vector(v1.coord[0]-v2.coord[0],v1.coord[1]-v2.coord[1],v1.coord[2]-v2.coord[2]);
	}
	
	public void minus(Vector vec){
		this.coord[0] -= vec.coord[0];
		this.coord[1] -= vec.coord[1];
		this.coord[2] -= vec.coord[2];
	}
	
	public void addition(Vector vec){
		this.coord[0] += vec.coord[0];
		this.coord[1] += vec.coord[1];
		this.coord[2] += vec.coord[2];
	}
	
	public float getIntensity(){
		return (float) Math.hypot3d(this.coord[0], this.coord[1], this.coord[2]);
	}
	
	@Override
	public String toString() {
		return "("+this.coord[0]+";"+this.coord[1]+";"+this.coord[2]+")";
	}
	
	@Override
	public Vector clone() {
		return new Vector(this.coord[0],this.coord[1],this.coord[2]);
	}
	
	public void set(float _x, float _y, float _z){
		this.coord[0] = _x;
		this.coord[1] = _y;
		this.coord[2] = _z;
	}
	
	public void set(float[] vec){
		if(vec.length > 2){
			this.coord[0] = vec[0];
			this.coord[1] = vec[1];
			this.coord[2] = vec[2];
		}
	}
	
	public void scale(float scale){
		this.coord[0] *= scale;
		this.coord[1] *= scale;
		this.coord[2] *= scale;
	}
	
	public Vector getScaledVector(float scale){
		return new Vector(this.coord[0]*scale,this.coord[1]*scale,this.coord[2]*scale);
	}
	/////////////////////////////////	
	/////  VARIABLES PRIVATE    /////
	/////////////////////////////////
	private float[] coord;
	
}
