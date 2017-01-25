package nz.seaton.islandgenerator.island;

import com.badlogic.gdx.graphics.Color;

import nz.seaton.islandgenerator.IslandGenerator;
import nz.seaton.islandgenerator.PlaceName;
import nz.seaton.islandgenerator.SimplexNoise;
import nz.seaton.islandgenerator.Util;
import nz.seaton.islandgenerator.UI.UI;

public class Island {

	IslandTemplate template;
	UI ui;

	public double[][] heightmap;
	public Color[][] colormap;
	public int w, h;
	public String name;

	// private ArrayList<Spawner> birdSpawners;

	// ---------- RENDERING DETAILS
	final int res = 150;
	final float thickness = 0.023f;

	// ---------- ISLAND GENERATOR VARIABLES
	public long seed;
	public float waterLevel = 0.05f;
	public float beachBiomeSize = 0.035f;
	public int peaks = 1;
	public float overx = 10;
	public float plusx = 1.5f;

	public Island(int ww, int hh, long s, UI ui) {
		w = ww;
		h = hh;
		seed = s;
		this.ui = ui;

		// recreate();
		generate();
		createTexture();
	}

	// public void recreate() {
	// (new Thread() {
	// public void run() {
	// this.setName("Map Generator");
	// System.out.println("Starting Thread");
	// ui.startLoading(w * h + (w * h * IslandGenerator.OCTAVES));
	// generate();
	// tempPixels = createTexture();
	// ui.setIslandName(name);
	// System.out.println("Ending Thread\n");
	// }
	// }).start();
	// }

	public void generate() {
		long last = System.currentTimeMillis();

		// ui.updateLoadingStatus("Creating Template");
		heightmap = new double[w][h];
		colormap = new Color[w][h];

		template = new MountainTemplate(w, h, seed, peaks, overx, plusx);

		// ui.updateLoadingStatus("initialising simplex noise");
		SimplexNoise.init(seed, w, h);

		double f = IslandGenerator.FREQUENCY;
		double a = IslandGenerator.AMPLITUDE;
		int o = IslandGenerator.OCTAVES;
		double p = IslandGenerator.PERSISTANCE;

		// ui.updateLoadingStatus("Creating map");
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				heightmap[x][y] = SimplexNoise.OctaveSimplex(x, y, o, p, f, a);
				heightmap[x][y] += template.getTemplate(x, y);
				// ui.addLoadCycle(IslandGenerator.OCTAVES);
			}
		}

		name = PlaceName.generatePlaceName(seed) + " Island";
		System.out.println("New heightmap generated in " + (System.currentTimeMillis() - last) + "ms");
	}

	public void createTexture() {
		// ui.updateLoadingStatus("Creating texture");
		long last = System.currentTimeMillis();

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				float i = (float) heightmap[x][y];
				Color c = new Color();

				// if (IslandGenerator.renderMode == RenderingMode.TOPOLINES) {
				// if (i > waterLevel) {
				// int ii = (int) (i * 1000d);
				// int iii = ii % res;
				//
				// int cc = (int) ((i + thickness) * 1000d);
				// int ccc = cc % res;
				//
				// if ((cc - ccc) != (ii - iii)) // Brown Topo Lines
				// c = new Color(0x856537FF);
				// else { // Land
				// final float a = (float) ((1.0f - 0.0f) / (1.0f - waterLevel)); // Constants for normalising equation
				// final float b = 1.0f - a * 1.0f;
				//
				// float ic = ((float) ((ii - iii) / 1000f)); // current level
				// float nic = a * ic + b; // Normalised value of ic
				//
				// final float scale = 0.7f;
				//
				// float nr = Util.lerp((0.7882f * scale), 1.0f, nic);
				// float nb = Util.lerp((0.9137f * scale), 1.0f, nic);
				// float ng = Util.lerp((0.6157f * scale), 1.0f, nic);
				//
				// c = new Color(nr, nb, ng, 1.0f);
				// }
				// } else if (i > waterLevel - thickness) { // Topolines
				// c = new Color(0x856537FF);
				// } else { // water
				// c = new Color(0xe6f6fcFF);
				// }

				// } else if (IslandGenerator.renderMode == RenderingMode.CONTOURCOLOR) {
				if (i < waterLevel + beachBiomeSize) {
					c = new Color(0xffeb96FF); // Beach
				} else if (i > waterLevel) {
					int ii = (int) (i * 1000d);
					int iii = ii % res;

					float ic = ((float) ((ii - iii) / 1000f)); // current level

					final float a = (float) ((1.0f - 0.0f) / (1.0f - waterLevel)); // Constants for normalising equation
					final float b = 1.0f - a * 1.0f;

					float nic = a * ic + b; // Normalised value of ic

					// c = new Color(ic * 0.788f, ic * 0.9137f, ic *
					// 0.6157f, 1.0f);

					final float scale = 0.8f;

					float nr = Util.lerp((0.7882f * scale), 1.0f, nic);
					float nb = Util.lerp((0.9137f * scale), 1.0f, nic);
					float ng = Util.lerp((0.6157f * scale), 1.0f, nic);

					c = new Color(nr, nb, ng, 1.0f);
				} else {
					c = new Color(0xe6f6fcFF); // Water
				}

				// } else if (IslandGenerator.renderMode == RenderingMode.GRAYSCALE)// Simplex Noise Map
				// c = new Color(i, i, i, 1.0f);
				// else if (IslandGenerator.renderMode == RenderingMode.ISLAND_TEMPLATE) {
				// i = (float) template.getTemplate(x, y);
				// if (i > 0)
				// c = new Color(i, i, i, 1.0f);
				// else if (i == 0)
				// c = new Color(0.3f, 0.0f, 0.0f, 1.0f);
				// else
				// c = new Color(0.0f, Math.abs(i) * 0.3f, Math.abs(i) * 0.3f, 1.0f);
				// }
				colormap[x][y] = c;
			}
		}

		System.out.println("New texture generated in " + (System.currentTimeMillis() - last) + "ms\n");
	}

	public float lerp(float start, float end, float x) {
		return start * (1 - x) + end * x;
	}

	public void update(long step) {

	}

	public void render() {

	}

	public void dispose() {

	}
}