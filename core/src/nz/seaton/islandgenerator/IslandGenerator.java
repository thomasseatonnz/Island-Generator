package nz.seaton.islandgenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IslandGenerator extends ApplicationAdapter {

	public final static int WINDOW_WIDTH = 1280;
	public final static int WINDOW_HEIGHT = 720;

	public static int OCTAVES = 5;
	public static float FREQUENCY = 10.5f;
	public static float AMPLITUDE = 0.5f;
	public static int radius = 400;

	int[][] pixels;

	double[][] oct;
	double[][] islandTemplate;

	Pixmap screen;
	Texture tex;
	SpriteBatch renderer;

	@Override
	public void create() {
		Gdx.graphics.setTitle("Island Generator");
		Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		Gdx.graphics.setResizable(false);

		reset();

		createIsland();
		GenerateNoiseMap();

	}

	@Override
	public void render() {
		update();

		Gdx.graphics.setVSync(true);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		for (int x = 0; x < screen.getWidth(); x++) {
			for (int y = 0; y < screen.getHeight(); y++) {
				screen.setColor(pixels[x][y]);
				screen.drawPixel(x, y);
			}
		}

		tex = new Texture(screen);

		renderer.begin();
		renderer.draw(tex, 0, 0);
		renderer.end();

		tex.dispose();

	}

	public void update() {

		// Generate new map
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {

			long t0 = System.currentTimeMillis();
			System.out.println("Generating new texture");

			OCTAVES = 9;
			FREQUENCY = 7f;
			AMPLITUDE = 0.9f;
			radius = 400;

			reset();

			createIsland();
			GenerateNoiseMap();

			long tf = System.currentTimeMillis() - t0;
			System.out.println("Generated new texture in " + tf + "ms\n");
		}
	}

	public void createIsland() {
		islandTemplate = new double[WINDOW_WIDTH][WINDOW_HEIGHT];
		int mx = (int) WINDOW_WIDTH / 2;
		int my = (int) WINDOW_HEIGHT / 2;
		for (int x = 0; x < WINDOW_WIDTH; x++) {
			for (int y = 0; y < WINDOW_HEIGHT; y++) {
				double d = Math.sqrt(Math.pow(x - mx, 2) + Math.pow(y - my, 2));
				islandTemplate[x][y] = (-Math.log10(d / 10)) + 1.5;
			}
		}
	}

	public void GenerateNoiseMap() {
		for (int x = 0; x < WINDOW_WIDTH; x++) {
			for (int y = 0; y < WINDOW_HEIGHT; y++) {
				for (int o = 0; o < OCTAVES; o++) {
					oct[x][y] += SimplexNoise.noise(x * (1.0f / (o * FREQUENCY)), y * (1.0f / (o * FREQUENCY)))
							* (1 - (AMPLITUDE / ((float) o + 1)));
					oct[x][y] += (islandTemplate[x][y]);
				}
				pixels[x][y] = RGB((float) oct[x][y], (float) oct[x][y], (float) oct[x][y]);
			}
		}
	}

	public void reset() {
		screen = new Pixmap(WINDOW_WIDTH, WINDOW_HEIGHT, Pixmap.Format.RGB888);
		renderer = new SpriteBatch();
		oct = new double[WINDOW_WIDTH][WINDOW_HEIGHT];
		pixels = new int[WINDOW_WIDTH][WINDOW_HEIGHT];
	}

	@Override
	public void dispose() {
		screen.dispose();
		tex.dispose();
		renderer.dispose();
	}

	public static int RGB(float r, float g, float b) {
		return Color.rgba8888(new Color(r, g, b, 1.0f));
	}
}
