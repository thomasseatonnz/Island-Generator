package nz.seaton.islandgenerator.island;

import java.util.Random;

public class MountainTemplate extends IslandTemplate{
	public int peaks = 1;
	
	public MountainTemplate(int w, int h, long s, int numpeaks) {
		super(w, h, s, Type.MOUNTAIN);
		peaks = numpeaks;
		regenerate();
	}
	
	public void regenerate() {
		Random rand = new Random(seed);
		int leftBorder = (int) w / (peaks + 2);
		int lowBorder = (int) h / (peaks + 2);

		for (int i = 0; i < peaks; i++) {
			int peakX = (int) (rand.nextDouble() * (w / (2 * peaks))) + (leftBorder * (i + 1));
			int peakY = (int) (rand.nextDouble() * ((peaks * h) / (peaks + 2))) + lowBorder;

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					double d = Math.sqrt(Math.pow(x - peakX, 2) + Math.pow(y - peakY, 2));
					template[x][y] += (-Math.log10(d / 10) + (1.5));
					if (template[x][y] < 0)
						template[x][y] *= 2;
				}
			}
		}
	}
}
