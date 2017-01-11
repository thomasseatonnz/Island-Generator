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
				
				double cRad = Vector2.dst(x, y, w/2, h/2);
				
				int islandStartRad = radius - thickness;
				int islandEndRad = radius + thickness;
				double distFromIslandStart = (cRad - (double)islandStartRad);
				
				if(cRad > islandStartRad && cRad < islandEndRad){
					template[x][y] += ((-0.5)*Math.cos((distFromIslandStart/(thickness/3)))) + 0.5;
					template[x][y] *= 0.2;
				}
			}
		}
	}

}
