package nz.seaton.islandgenerator;

import com.badlogic.gdx.math.Vector3;

public class Util {
	public static Vector3 normal(Vector3 a, Vector3 b, Vector3 c) {
		Vector3 ab = new Vector3(a);
		ab.sub(b);
		Vector3 ac = new Vector3(c);
		ac.sub(a);

		Vector3 normal = new Vector3(ac);
		normal.crs(ab);

		return normal;
	}
	
	public static float lerp(float start, float end, float x) {
		return start * (1 - x) + end * x;
	}
}
