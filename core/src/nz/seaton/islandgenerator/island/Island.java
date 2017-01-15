package nz.seaton.islandgenerator.island;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import nz.seaton.islandgenerator.IslandGenerator;
import nz.seaton.islandgenerator.PlaceName;
import nz.seaton.islandgenerator.RenderingMode;
import nz.seaton.islandgenerator.SimplexNoise;
import nz.seaton.islandgenerator.Util;
import nz.seaton.islandgenerator.island.IslandTemplate.Type;

public class Island {

	IslandTemplate template;

	public double[][] heightmap;
	public Color[][] colormap;
	public int w, h;
	private long seed;
	public String name;
	
	// private ArrayList<Spawner> birdSpawners;

	// ----------
	public final float waterLevel = 0.05f;
	final int res = 150;
	final float thickness = 0.023f;
	final float beachBiomeSize = 0.035f;
	// ----------
	
	private double tide = waterLevel;

	public Island(int ww, int hh, long s) {
		w = ww;
		h = hh;
		seed = s;

		generate();

		createTexture();
	}

	public void generate() {
		long last = System.currentTimeMillis();

		heightmap = new double[w][h];
		colormap = new Color[w][h];
		template = new MountainTemplate(w, h, seed, 1);

		SimplexNoise.init(seed, w, h);

		double f = IslandGenerator.FREQUENCY;
		double a = IslandGenerator.AMPLITUDE;
		int o = IslandGenerator.OCTAVES;
		double p = IslandGenerator.PERSISTANCE;

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				heightmap[x][y] = SimplexNoise.OctaveSimplex(x, y, o, p, f, a);
				heightmap[x][y] += template.getTemplate(x, y);
			}
		}

		name = PlaceName.generatePlaceName(seed);
		System.out.println("New heightmap generated in " + (System.currentTimeMillis() - last) + "ms");
	}

	public void createTexture() {
		long last = System.currentTimeMillis();

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				float i = (float) heightmap[x][y];
				Color c = new Color();

				if (IslandGenerator.renderMode == RenderingMode.TOPOLINES) {
					if (i > tide) {
						int ii = (int) (i * 1000d);
						int iii = ii % res;

						int cc = (int) ((i + thickness) * 1000d);
						int ccc = cc % res;

						if ((cc - ccc) != (ii - iii)) // Brown Topo Lines
							c = new Color(0x856537FF);
						else { // Land
							final float a = (float) ((1.0f - 0.0f) / (1.0f - tide)); // Constants
																					// for
																					// normalising
																					// equation
							final float b = 1.0f - a * 1.0f;

							float ic = ((float) ((ii - iii) / 1000f)); // current
																		// level
							float nic = a * ic + b; // Normalised value of ic

							final float scale = 0.7f;

							float nr = Util.lerp((0.7882f * scale), 1.0f, nic);
							float nb = Util.lerp((0.9137f * scale), 1.0f, nic);
							float ng = Util.lerp((0.6157f * scale), 1.0f, nic);

							c = new Color(nr, nb, ng, 1.0f);
						}
					} else if (i > tide - thickness) { // Topolines
						c = new Color(0x856537FF);
					} else { // water
						c = new Color(0xe6f6fcFF);
					}

				} else if (IslandGenerator.renderMode == RenderingMode.CONTOURCOLOR) {
					if (i < tide + beachBiomeSize) {
						c = new Color(0xffeb96FF); // Beach
					} else if (i > tide) {
						int ii = (int) (i * 1000d);
						int iii = ii % res;

						float ic = ((float) ((ii - iii) / 1000f)); // current
																	// level

						final float a = (float) ((1.0f - 0.0f) / (1.0f - tide)); // Constants
																				// for
																				// normalising
																				// equation
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

				} else if (IslandGenerator.renderMode == RenderingMode.GRAYSCALE)// Simplex
																					// Noise
																					// Map
					c = new Color(i, i, i, 1.0f);
				else if (IslandGenerator.renderMode == RenderingMode.ISLAND_TEMPLATE) {
					i = (float) template.getTemplate(x, y);
					if (i > 0)
						c = new Color(i, i, i, 1.0f);
					else if (i == 0)
						c = new Color(0.3f, 0.0f, 0.0f, 1.0f);
					else
						c = new Color(0.0f, Math.abs(i) * 0.3f, Math.abs(i) * 0.3f, 1.0f);
				}

				colormap[x][y] = c;
			}
		}

		System.out.println("New texture generated in " + (System.currentTimeMillis() - last) + "ms\n");
	}

	public void update(long step) {
	}

	public void render(SpriteBatch r, BitmapFont font, ShapeRenderer shape) {
		font.setColor(Color.BLACK);
		font.draw(r, name + " Island", 50, 50);

		shape.setColor(Color.RED);

		if (template.getType() == Type.MOUNTAIN && IslandGenerator.DEBUG) {
			int peaks = ((MountainTemplate)template).peaks;
			int div = peaks + 2;
			for (int i = 1; i < peaks + 2; i++) {
				shape.line((i * this.w) / div, 0, (i * this.w) / div, this.h);
				shape.line(0, (i * this.h) / div, this.w, (i * this.h) / div);
			}
		}
	}

	public void dispose() {
	}
}
