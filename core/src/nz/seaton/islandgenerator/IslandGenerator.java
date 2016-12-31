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

	public static int OCTAVES = 1;
	public static float FREQUENCY = 10.5f;
	public static float AMPLITUDE = 0.5f;
	public static int radius = 400;

	int[][] pixels;

	double[][] islandTemplate;

	Pixmap screen;
	Texture tex;
	SpriteBatch renderer;
	
	long last = 0;

	@Override
	public void create() {
		Gdx.graphics.setTitle("Island Generator");
		Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		Gdx.graphics.setResizable(false);

		reset();
		
		SimplexNoise.init((int)System.currentTimeMillis(), WINDOW_WIDTH, WINDOW_HEIGHT);
		
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
//		if(System.currentTimeMillis() - last > 1000){
			long t0 = System.currentTimeMillis();
			System.out.println("Generating new texture");

			OCTAVES = 10;
			FREQUENCY = 0.0005f;
			AMPLITUDE = 800f;
			radius = 400;

			reset();

			createIsland();
			GenerateNoiseMap();

			long tf = System.currentTimeMillis() - t0;
			System.out.println("Generated new texture in " + tf + "ms\n");
			last = System.currentTimeMillis();
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
		SimplexNoise.newSeed((int)System.currentTimeMillis());
		
		for (int x = 0; x < WINDOW_WIDTH; x++) {
			for (int y = 0; y < WINDOW_HEIGHT; y++) {
				double n = SimplexNoise.OctaveSimplex(x, y, OCTAVES, 0.7, FREQUENCY, AMPLITUDE);
				n += islandTemplate[x][y];
				pixels[x][y] = RGB((float) n, (float) n, (float) n);
			}
		}
	}

	public void reset() {
		screen = new Pixmap(WINDOW_WIDTH, WINDOW_HEIGHT, Pixmap.Format.RGB888);
		renderer = new SpriteBatch();
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
