package nz.seaton.islandgenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

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

	PerspectiveCamera cam;
	CameraInputController camController;
	Environment environment;
	
	ModelBatch mBatch;
	Model box;
	ModelInstance boxInstance;
	
	Island island;
	
	long last = 0;

	@Override
	public void create() {
		Gdx.graphics.setTitle("Island Generator");
		Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		Gdx.graphics.setResizable(false);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.graphics.setVSync(true);
		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0.0f, 10f, 10f);
		cam.lookAt(0f, 0f, 0f);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.0f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, -1f, -0.2f));
		
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);
		
		//------ DEBUG
		mBatch = new ModelBatch();
		ModelBuilder builder = new ModelBuilder();
		box = builder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(com.badlogic.gdx.graphics.Color.WHITE)), Usage.Normal | Usage.Position);
		boxInstance = new ModelInstance(box);
		
		island = new Island(WINDOW_WIDTH, WINDOW_HEIGHT, "seed".hashCode());
	}

	@Override
	public void render() {
		camController.update();
		update();
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		mBatch.begin(cam);
		mBatch.render(boxInstance, environment);
		mBatch.end();
		
	}
	
	public void changeRenderMode(RenderingMode m){
		renderMode = m;
		island.createTexture();
	}

	public void update() {

		// Generate new map
//		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
//			
//			if(renderMode == RenderingMode.ISLAND_TEMPLATE)
//				changeRenderMode(RenderingMode.TOPOLINES);
//			else if (renderMode == RenderingMode.TOPOLINES)
//				changeRenderMode(RenderingMode.CONTOURCOLOR);
//			else if(renderMode == RenderingMode.CONTOURCOLOR)
//				changeRenderMode(RenderingMode.GRAYSCALE);
//			else
//				changeRenderMode(RenderingMode.ISLAND_TEMPLATE);
//		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			System.out.println("asd");
//		if(System.currentTimeMillis() - last > 1000){ //Reloads map every 1 second
			
			//These are here for the convenience of testing new variations
			OCTAVES = 10;
			FREQUENCY = 0.0005f;
			AMPLITUDE = 800f;
			PERSISTANCE = 0.7f;
			
			island.dispose();
			island = new Island(WINDOW_WIDTH, WINDOW_HEIGHT, System.currentTimeMillis());
			
			last = System.currentTimeMillis();
		}
		
		//REMOVED FOR DEBUG
//		island.update(1);
	}

	@Override
	public void dispose() {
		island.dispose();
		box.dispose();
	}
}
