package Castles.util;

/**
 * A generic n-dimension vector class, whose values are of type double. 
 */
public class VectorND {
	// The values associated with this vector
	public double[] values;
	
	/**
	 * Create the zero vector of the given dimension. 
	 */
	public VectorND(int dim) {
		values = new double[ Math.max(1, dim) ];
	}
	
	/**
	 * Create a vector with the given values. 
	 */
	public VectorND(double... vals) {
		values = new double[vals.length];
		
		for (int idx = 0; idx < vals.length; ++idx) {
			values[idx] = vals[idx];
		}
	}
	
	/**
	 * Set v[dimIdx] = newVal. 
	 */
	public void set(int dimIdx, double newVal) throws IndexOutOfBoundsException {
		values[dimIdx] = newVal;
	}
	
	/**
	 * Return v[dimIdx]. 
	 */
	public double get(int dimIdx) throws IndexOutOfBoundsException {
		return values[dimIdx];
	}
	
	/**
	 * Return the dimension of this. 
	 */
	public int getDim() { return values.length; }
	
	/**
	 * Return the magnitude of this.
	 * 
	 *  mag_v = v[0] ^ 2 + v[1] ^ 2 + v[3] ^ 2 + ... + v[n - 1] ^ 2,
	 *  where n is the dimension of the vector
	 */
	public double magnitude() {
		double mag = 0.0;
		
		for (double val : values) {
			mag += Math.pow(val, 2.0);
		}
		return Math.sqrt(mag);
	}
	
	/**
	 * Multiply all values of this by the given scalar value. 
	 */
	public void scalarMult(double scalar) {
		for (int idx = 0; idx < values.length; ++idx) {
			values[idx] *= scalar;
		}
	}
	
	/**
	 * Returned v scaled by scalar without modifying v. 
	 */
	public static VectorND scalarMult(double scalar, VectorND v) {
		VectorND v_scaled = v.cloneInClass();
		v_scaled.scalarMult(scalar);
		return v_scaled;
	}
	
	/**
	 * Normalize this.
	 * 
	 * v_norm = v / v_mag 
	 */
	public void normalize() {
		double mag = magnitude();

		for (int idx = 0; idx < values.length; ++idx) {
			values[idx] /= mag;
		}
	}
	
	/**
	 * Return the normalized form of v without modifying v. 
	 */
	public static VectorND normalize(VectorND v) {
		VectorND u = v.cloneInClass();
		u.normalize();
		return u;
	}
	
	/**
	 * Add each respective value of v to this [vector's]
	 * respective values. 
	 */
	public void add(VectorND v) {
		for (int idx = 0; idx < v.values.length; ++idx) {
			values[idx] += v.values[idx];
		}
	}
	
	/**
	 * Returns the sum of all the vectors given without
	 * modifying any of the given vectors.
	 */
	public static VectorND add(VectorND... vectors) {
		VectorND sum = vectors[0].cloneInClass();
		
		// Sum up all the given vectors
		for (int idx = 1; idx < vectors.length; ++idx) {
			sum.add(vectors[idx]);
		}
		
		return sum;
	}
	
	/**
	 * Create an independent replica of this. 
	 */
	public VectorND cloneInClass() {
		return new VectorND(values);
	}
	
	@Override
	public Object clone() {
		return cloneInClass();
	}
	
	@Override
	public boolean equals(Object obj) {
		// Check class type
		if (obj instanceof VectorND) {
			VectorND v = (VectorND)obj;
			int dim = v.getDim();
			// Check vector dimensions
			if (values.length == dim) {
				// Compare each respective value of both vectors
				for (int idx = 0; idx < values.length; ++idx) {
					if (values[idx] != v.values[idx]) {
						return false;
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String vectorStr = "[ ";
		
		for (int idx = 0; idx < values.length; ++idx) {
			vectorStr += String.format("%4.3f", values[idx]);
			
			if (idx < (values.length - 1)) {
				vectorStr += ",";
			}
			
			vectorStr += " ";
		}
		
		return vectorStr + "]";
	}
}
