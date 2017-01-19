package nz.seaton.islandgenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import nz.seaton.islandgenerator.UI.UI;
import nz.seaton.islandgenerator.island.Island;

public class IslandGenerator extends ApplicationAdapter {

	public final static int WINDOW_WIDTH = 1280;
	public final static int WINDOW_HEIGHT = 720;

	public static int OCTAVES = 10;
	public static float FREQUENCY = 0.0005f;
	public static float AMPLITUDE = 800f;
	public static float PERSISTANCE = 0.7f;

	public static boolean DEBUG = false;

	public static RenderingMode renderMode = RenderingMode.CONTOURCOLOR;

	SpriteBatch renderer;
	ShapeRenderer shape;
	UI ui;

	Island island;

	long last = 0;

	@Override
	public void create() {
		Gdx.graphics.setTitle("Island Generator");
		Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		Gdx.graphics.setResizable(false);

		renderer = new SpriteBatch();
		shape = new ShapeRenderer();
		ui = new UI(WINDOW_WIDTH, WINDOW_HEIGHT);

		island = new Island(WINDOW_WIDTH, WINDOW_HEIGHT, "seed".hashCode());
	}

	long lastFPS = System.currentTimeMillis();

	@Override
	public void render() {
		update();

		Gdx.graphics.setVSync(true);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (!ui.loading) {
			renderer.begin();
			shape.begin(ShapeRenderer.ShapeType.Line);
			island.render(renderer, shape);

			shape.end();
			renderer.end();
		}
		ui.render();

		if (System.currentTimeMillis() - last >= 2500) {
			System.out.println(Gdx.graphics.getFramesPerSecond());
			last = System.currentTimeMillis();
		}
	}

	public void changeRenderMode(RenderingMode m) {
		renderMode = m;
		island.createTexture();
	}

	public void update() {
		// Generate new map
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

			if (renderMode == RenderingMode.ISLAND_TEMPLATE)
				changeRenderMode(RenderingMode.TOPOLINES);
			else if (renderMode == RenderingMode.TOPOLINES)
				changeRenderMode(RenderingMode.CONTOURCOLOR);
			else if (renderMode == RenderingMode.CONTOURCOLOR)
				changeRenderMode(RenderingMode.GRAYSCALE);
			else
				changeRenderMode(RenderingMode.ISLAND_TEMPLATE);
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {

			// These are here for the convenience of testing new variations
			OCTAVES = 10;
			FREQUENCY = 0.0005f;
			AMPLITUDE = 800f;
			PERSISTANCE = 0.7f;

			island.dispose();
			island = new Island(WINDOW_WIDTH, WINDOW_HEIGHT, System.currentTimeMillis());

			last = System.currentTimeMillis();
		}

		island.update(1);
		ui.update();
	}

	@Override
	public void dispose() {
		renderer.dispose();
		island.dispose();
		shape.dispose();
		ui.dispose();
	}
}
