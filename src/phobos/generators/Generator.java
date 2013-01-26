package phobos.generators;

//import org.newdawn.slick.util.Log;
import phobos.utils.math.Math ;

public class Generator {

	//zoom = 100
	static int zoom = 200 ;
	static int maxHeight = 100 ;
	static int octave = 7;
	static double p = 0.6f ;

	static public void setZoom(int zoom) {
		Generator.zoom = zoom ;
	}

	static public void setMaxHeight(int maxH) {
		Generator.maxHeight = maxH ;
	}

	static public void setOctave(int octave) {
		Generator.octave = octave;
	}

	static public void setP(double p) {
		Generator.p = p ;
	}

	static public int getZoom() {
		return zoom ;
	}
	static public int getMaxHeight() {
		return maxHeight ;

	}
	static public int getOctave() {
		return octave ;

	}
	static public double getP() {
		return p ;
	}
	
	static public float perlinNoiseHeight(float x, float y, int zoom, int maxHeight, int octaves, double p) {
	    double getnoise = 0;
	    double maxAmplitude = 0 ;
	    for(int a=0;a<octaves-1;a++)//This loops trough the octaves.
	    {
			//This uses our perlin noise functions. It calculates all our zoom and frequency and amplitude
			double frequency = Math.pow(2,a);//This increases the frequency with every loop of the octave.
			double amplitude = Math.pow(p,a);//This decreases the amplitude with every loop of the octave.
			maxAmplitude += amplitude ;
			getnoise += noise(((double)(x))*frequency/zoom,((double)(y))/zoom*frequency)*amplitude;
	    }
	    //Log.info(""+(maxHeight * (getnoise+maxAmplitude)/(maxAmplitude*2))) ;
	    return (float)(maxHeight * (getnoise+maxAmplitude)/(maxAmplitude*2));//Convert to 0-maxHeight values.
	}

	static public double noise(double x,double y)
	{
	    double floorx=(double)((int)x);//This is kinda a cheap way to floor a double integer.
	    double floory=(double)((int)y);
	    double s,t,u,v;//Integer declaration
	    s=findnoise2(floorx,floory);
	    t=findnoise2(floorx+1,floory);
	    u=findnoise2(floorx,floory+1);//Get the surrounding pixels to calculate the transition.
	    v=findnoise2(floorx+1,floory+1);
	    double int1=interpolate(s,t,x-floorx);//Interpolate between the values.
	    double int2=interpolate(u,v,x-floorx);//Here we use x-floorx, to get 1st dimension. Don't mind the x-floorx thingie, it's part of the cosine formula.
	    return interpolate(int1,int2,y-floory);//Here we use y-floory, to get the 2nd dimension.

	}

	static public double findnoise2(double x,double y)
	{
	    int n=(int)x+(int)y*57;
	    n=(n<<13)^n;
	    int nn=(n*(n*n*60493+19990303)+1376312589)&0x7fffffff;
	    return 1.0-((double)nn/1073741824.0);

	}

	static public double interpolate(double a,double b,double x)
	{
	    double ft=x * 3.1415927;
	    double f=(1.0-Math.cosJava(ft))* 0.5;
	    return a*(1.0-f)+b*f;

	}
}

