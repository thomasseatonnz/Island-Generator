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

	public void generate() {
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
				if (heightmap[x][y] < 0)
					heightmap[x][y] = 0;
			}
		}
	}

	public void createTexture() {
		if (tex != null)
			tex.dispose();

		Pixmap pixels = new Pixmap(w, h, Pixmap.Format.RGBA8888);

		for (int x = 0; x < pixels.getWidth(); x++) {
			for (int y = 0; y < pixels.getHeight(); y++) {
				float i = (float) heightmap[x][y];
				Color c = new Color();

				final float waterLevel = 0.05f;
				final int res = 150;
				final float thickness = 0.023f;

				if (IslandGenerator.renderMode == RenderingMode.TOPOLINES) {
					if (i > waterLevel) {
						int ii = (int) (i * 1000d);
						int iii = ii % res;

						int cc = (int) ((i + thickness) * 1000d);
						int ccc = cc % res;

						if ((cc - ccc) != (ii - iii))
							c = new Color(0x856537FF); 	// Brown Topo Lines
						else
							c = new Color(0xc9e99dFF); 	// Green Land
					} else if (i > waterLevel - thickness) {
						c = new Color(0x856537FF); 		// Brown Topo Lines at Water level
					} else {
						c = new Color(0xe6f6fcFF); // Water
					}

				} else if (IslandGenerator.renderMode == RenderingMode.CONTOURCOLOR) {
					if (i > waterLevel) {
						int ii = (int) (i * 1000d);
						int iii = ii % res;
						
						float ic = ((float) ((ii - iii) / 1000f)); //current level
						
						final float a = (1.0f-0.0f)/(1.0f-waterLevel); //Constants for normalising equation
						final float b = 1.0f - a * 1.0f;
								
						float nic =  a * ic + b; //Normalised value of ic
						
						//c = new Color(ic * 0.788f, ic * 0.9137f, ic * 0.6157f, 1.0f);
						
						final float scale = 0.8f;
						
						float nr = lerp((0.7882f*scale), 1.0f, nic);
						float nb = lerp((0.9137f*scale), 1.0f, nic);
						float ng = lerp((0.6157f*scale), 1.0f, nic);
						
						c = new Color(nr, nb, ng, 1.0f);
					} else {
						c = new Color(0xe6f6fcFF); // Water
					}
				} else {
					c = new Color(i, i, i, 1.0f);
				}

				pixels.setColor(c);
				pixels.drawPixel(x, y);
			}
		}

		tex = new Texture(pixels);
		pixels.dispose();

	}
	
	private float lerp(float start, float end, float x){
		return start*(1-x)+end*x;
	}

	public Texture getTex() {
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
