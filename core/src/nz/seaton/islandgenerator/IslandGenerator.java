package nz.seaton.islandgenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IslandGenerator extends ApplicationAdapter {
	
	public final static int WINDOW_WIDTH = 1280;
	public final static int WINDOW_HEIGHT = 720;
	
	int[][] pixels;
	Pixmap screen;
	Texture tex;
	SpriteBatch renderer;
	
	@Override
	public void create () {
		Gdx.graphics.setTitle("Island Generator");
		Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		Gdx.graphics.setResizable(false);
		
		screen = new Pixmap(WINDOW_WIDTH, WINDOW_HEIGHT, Pixmap.Format.RGB888);
		renderer = new SpriteBatch();
		
		pixels = new int[WINDOW_WIDTH][WINDOW_HEIGHT];
		for(int x = 0; x < WINDOW_WIDTH; x++){
			for(int y = 0; y < WINDOW_HEIGHT; y++){
				pixels[x][y] = RGB((float)Math.random(), 0.0f, 1.0f);
			}
		}
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		for(int x = 0; x < screen.getWidth(); x++){
			for(int y = 0; y < screen.getHeight(); y++){
				screen.setColor(pixels[x][y]);
				screen.drawPixel(x, y);
			}
		}
		
		tex = new Texture(screen);
		
		renderer.begin();
		renderer.draw(tex, 0, 0);
		renderer.end();
		
	}
	
	@Override
	public void dispose () {
		screen.dispose();
		tex.dispose();
		renderer.dispose();
	}
	
	public static int RGB(float r, float g, float b){
		return Color.rgb888(new Color(r, g, b, 1.0f));
	}
}
