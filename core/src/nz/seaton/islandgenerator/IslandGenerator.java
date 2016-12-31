package nz.seaton.islandgenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IslandGenerator extends ApplicationAdapter {

	public final static int WINDOW_WIDTH = 1280;
	public final static int WINDOW_HEIGHT = 720;

	public static int OCTAVES = 10;
	public static float FREQUENCY = 0.0005f;
	public static float AMPLITUDE = 800f;
	public static float PERSISTANCE = 0.7f;
	
	public static RenderingMode renderMode = RenderingMode.TOPOLINES;

	double[][] islandTemplate;

	SpriteBatch renderer;
	
	Island island;
	
	long last = 0;

	@Override
	public void create() {
		Gdx.graphics.setTitle("Island Generator");
		Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		Gdx.graphics.setResizable(false);
		
		renderer = new SpriteBatch();
		
		island = new Island(WINDOW_WIDTH, WINDOW_HEIGHT, "seed".hashCode());
	}

	@Override
	public void render() {
		update();

		Gdx.graphics.setVSync(true);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		renderer.begin();
		renderer.draw(island.getTex(), 0, 0);
		renderer.end();
	}
	
	public void changeRenderMode(RenderingMode m){
		renderMode = m;
		island.createTexture();
	}

	public void update() {

		// Generate new map
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
			
			if(renderMode == RenderingMode.GRAYSCALE)
				changeRenderMode(RenderingMode.TOPOLINES);
			else if (renderMode == RenderingMode.TOPOLINES)
				changeRenderMode(RenderingMode.CONTOURCOLOR);
			else 
				changeRenderMode(RenderingMode.GRAYSCALE);
		}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//		if(System.currentTimeMillis() - last > 1000){ //Reloads map every 1 second
			long t0 = System.currentTimeMillis();
			System.out.println("Generating new texture");
			
			//These are here for the convenience of testing new variations
			OCTAVES = 10;
			FREQUENCY = 0.0005f;
			AMPLITUDE = 800f;
			PERSISTANCE = 0.7f;
			
			island.dispose();
			island = new Island(WINDOW_WIDTH, WINDOW_HEIGHT, System.currentTimeMillis());
			
			long tf = System.currentTimeMillis() - t0;
			System.out.println("Generated new texture in " + tf + "ms\n");
			last = System.currentTimeMillis();
		}
	}

	@Override
	public void dispose() {
		renderer.dispose();
		island.dispose();
	}

	public static int RGB(float r, float g, float b) {
		return Color.rgba8888(new Color(r, g, b, 1.0f));
	}
}
