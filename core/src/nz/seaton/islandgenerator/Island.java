package nz.seaton.islandgenerator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Island {

	public static double[][] islandTemplate;

	private double[][] heightmap;
	private int w, h;
	private long seed;
	
	private Texture tex;
	
	static {
		System.out.println("Creating Island Template");
		createIslandTemplate();
	}

	public Island(int ww, int hh, long s) {
		w = ww;
		h = hh;
		seed = s;
		
		generate();
		
		createTexture();
	}
	
	public void generate(){
		heightmap = new double[w][h];
		SimplexNoise.init(seed, w, h);

		double f = IslandGenerator.FREQUENCY;
		double a = IslandGenerator.AMPLITUDE;
		int o = IslandGenerator.OCTAVES;
		double p = IslandGenerator.PERSISTANCE;
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				heightmap[x][y] = SimplexNoise.OctaveSimplex(x, y, o, p, f, a);
				heightmap[x][y] += islandTemplate[x][y];
			}
		}
	}

	public void createTexture() {
		if(tex != null)
			tex.dispose();
		
		Pixmap pixels = new Pixmap(w, h, Pixmap.Format.RGBA8888);
		
		for (int x = 0; x < pixels.getWidth(); x++) {
			for (int y = 0; y < pixels.getHeight(); y++) {
				float i = (float)heightmap[x][y];
				pixels.setColor(new Color(i, i, i, 1.0f));
				pixels.drawPixel(x, y);
			}
		}

		tex = new Texture(pixels);
		pixels.dispose();

	}
	
	public Texture getTex(){
		return tex;
	}

	public void dispose() {
		tex.dispose();
	}

	private static void createIslandTemplate() {
		islandTemplate = new double[IslandGenerator.WINDOW_WIDTH][IslandGenerator.WINDOW_HEIGHT];

		int mx = (int) IslandGenerator.WINDOW_WIDTH / 2;
		int my = (int) IslandGenerator.WINDOW_HEIGHT / 2;
		for (int x = 0; x < IslandGenerator.WINDOW_WIDTH; x++) {
			for (int y = 0; y < IslandGenerator.WINDOW_HEIGHT; y++) {
				double d = Math.sqrt(Math.pow(x - mx, 2) + Math.pow(y - my, 2));
				islandTemplate[x][y] = (-Math.log10(d / 10)) + 1.5;
			}
		}
	}
}
