package nz.seaton.islandgenerator.island;

public class MountainTemplate extends IslandTemplate{
	public int peaks = 1;
	
	public MountainTemplate(int w, int h, long s, int numpeaks) {
		super(w, h, s, Type.MOUNTAIN);
		peaks = numpeaks;
		regenerate();
	}
	
	public void regenerate() {
		for (int i = 0; i < peaks; i++) {
			int peakX = w/2;
			int peakY = h/2;
			
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					double d = Math.sqrt(Math.pow(x - peakX, 2) + Math.pow(y - peakY, 2));
					template[x][y] += (-Math.log10((d+20) / 10) + (1.39));
					if (template[x][y] < 0)
						template[x][y] *= 2;
				}
			}
		}
	}

}
