package nz.seaton.islandgenerator.island;

import com.badlogic.gdx.math.Vector2;

public class ArchipelagoTemplate extends IslandTemplate {
	private int radius, thickness;

	public ArchipelagoTemplate(int ww, int hh, long s, int rad, int thick) {
		super(ww, hh, s, Type.ACHIPELAGO);
		radius = rad;
		thickness = thick;

		regenerate();
	}

	public void regenerate() {
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				template[x][y] = -0.3;

				double cRad = Vector2.dst(x, y, w / 2, h / 2);

				float amplitude = 0.5f;
				float offset = 1.0f;

				template[x][y] -= (smoothstep(radius, radius + thickness, (float) cRad) * amplitude) - offset;
				template[x][y] += (smoothstep(radius - thickness, radius, (float) cRad) * amplitude) - offset;
				template[x][y] -= (smoothstep(radius + thickness, 3 * radius, (float) cRad));
			}
		}
	}

	private static float smoothstep(float edge0, float edge1, float x) {
		x = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
		return x * x * (3 - 2 * x);
	}

	private static float clamp(float value, float min, float max) {
		return (value < min) ? min : (value > max) ? max : value;
	}

}
