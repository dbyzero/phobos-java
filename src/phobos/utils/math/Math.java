package phobos.utils.math;


/**
 * 
 * @author Leosky
 * @version r2
 *
 */
public class Math {
	/**
	 * 
	 * @param a >= 0
	 * @param b 
	 * @return power a^b
	 */
	static public double pow(final double a, final double b) {
		//`final int x = (int) (Double.doubleToLongBits(a) >> 32);
		//10% faster ?
		final int x = (int) (Double.doubleToRawLongBits(a) >> 32);
		final int y = (int) (b * (x - 1072632447) + 1072632447);
		return Double.longBitsToDouble(((long) y) << 32);
	} 
	
	static public double hypot2d(double a, double b) {
		return sqrt(
				  pow(java.lang.Math.abs(a), 2) 
				+ pow(java.lang.Math.abs(b), 2)
			);
	}
	
	static public double hypot3d(double a, double b, double c){
		return sqrt(
				  pow(java.lang.Math.abs(a), 2) 
				+ pow(java.lang.Math.abs(b), 2)
				+ pow(java.lang.Math.abs(c), 2)
			);
	}
	
	static public double sqrt(final double a) {
		final long x = Double.doubleToRawLongBits(a) >> 32;
		double y = Double.longBitsToDouble((x + 1072632448) << 31);

		// repeat the following line for more precision
		//y = (y + a / y) * 0.5;
		return y;
	}
	
	/**
	 * 
	 * @param x - between -PI and PI
	 * @return sinus
	 */
	static public double sinLow(double x){
		double sin;
		if(x < 0){
			sin = 1.27323954 * x + 0.405284735 * x * x;
		}else{
			sin = 1.27323954 * x - 0.405284735 * x * x;
		}
		return sin;
	}
	
	
	/**
	 * 
	 * @param x
	 * @return angulus x put between -Pi and Pi 
	 */
	public static double warpToPi(final double x){
		double ret = x;
		if(ret < -3.14159265){
			while(ret < -3.14159265) ret += 6.28318531;
		}else if(x > 3.14159265){
			while(ret > 3.14159265) ret -= 6.28318531;
		}
		return ret;
	}
	
	/**
	 * 
	 * @param x - between -PI and PI
	 * @return sinus
	 */
	static public double sinHigh(double x){
		double sin;
		if(x < 0){
			sin = 1.27323954 * x + 0.405284735 * x * x;
			
			if(sin < 0){
				sin = 0.225 * (sin *-sin - sin) + sin;
			}else{
				sin = 0.225 * (sin * sin - sin) + sin;
			}
		}else{
			sin = 1.27323954 * x - 0.405284735 * x * x;
			
			if(sin < 0){
				sin = 0.225 * (sin * -sin - sin) + sin;
			}else{
				sin = 0.225 * (sin * sin - sin) + sin;
			}
		}
		return sin;
	}
	
	/**
	 * 
	 * @param x - between -PI and PI
	 * @return cosinus
	 */
	static public double cosLow(double x){
		double cos;
		x += 1.57079632;
		if (x > 3.14159265) x -= 6.28318531;

		if(x < 0){
			cos = 1.27323954 * x + 0.405284735 * x * x;
		}else{
			cos = 1.27323954 * x - 0.405284735 * x * x;
		}
		return cos;
	}
	
	/**
	 * 
	 * @param x - between -PI and PI
	 * @return cosinus
	 */
	static public double cosHigh(double x){
		double cos;
		
		x += 1.57079632;
		if (x > 3.14159265) x -= 6.28318531;
		
		if(x < 0){
			cos = 1.27323954 * x + 0.405284735 * x * x;
			
			if(cos < 0){
				cos = 0.225 * (cos * -cos - cos) + cos;
			}else{
				cos = 0.225 * (cos * cos - cos) + cos;
			}
		}else{
			cos = 1.27323954 * x - 0.405284735 * x * x;
			
			if(cos < 0){
				cos = 0.225 * (cos * -cos - cos) + cos;
			}else{
				cos = 0.225 * (cos * cos - cos) + cos;
			}
		}
		return cos;
	}
	
	/**
	 * 
	 * @param x - between -PI and PI
	 * @return cosinus
	 */
	static public double cosJava(double x){
		return java.lang.Math.cos(x);
	}
}
