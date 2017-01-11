package nz.seaton.islandgenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

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

	Model islandModel;
	ModelInstance islandInstance;
	Model oceanModel;
	ModelInstance oceanInstance;

	Island island;

	long last = 0;

	@Override
	public void create() {
		Gdx.graphics.setTitle("Island Generator");
		Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		Gdx.graphics.setResizable(false);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.graphics.setVSync(true);

		island = new Island(WINDOW_HEIGHT, WINDOW_HEIGHT, System.currentTimeMillis());

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 300f, 100f);
		cam.lookAt(0f, 0f, 0f);
		cam.near = 1f;
		cam.far = 1000f;
		cam.update();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.0f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1, -1, 0));

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		mBatch = new ModelBatch();

		ModelBuilder builder = new ModelBuilder();
		builder.begin();

		MeshPartBuilder meshBuilder = builder.part("part1", GL20.GL_TRIANGLES,
				Usage.Position | Usage.Normal | Usage.ColorPacked, new Material());

		int res = 10;
		float scl = 0.5f;
		float factor = res * scl;

		float xoffset = (WINDOW_HEIGHT*scl) / 2;
		float zoffset = (WINDOW_HEIGHT*scl) / 2;

		Matrix4 transformMatrix = new Matrix4();
		transformMatrix.translate(-xoffset, 0, -zoffset);
		meshBuilder.setVertexTransform(transformMatrix);

		for (int x = 0; x < (WINDOW_HEIGHT / res) - 1; x++) {
			for (int y = 0; y < (WINDOW_HEIGHT / res) - 1; y++) {
				//v1
				float v1H = 100 * (float) island.heightmap[x * res][y * res];
				Vector3 v1P = new Vector3(x * factor, v1H, y * factor);
				VertexInfo v1 = new VertexInfo().setPos(v1P).setCol(island.colormap[x * res][y * res]);

				//v2
				float v2H = 100 * (float) island.heightmap[(x + 1) * res][y * res];
				Vector3 v2P = new Vector3((x + 1) * factor, v2H, y * factor);
				VertexInfo v2 = new VertexInfo().setPos(v2P).setCol(island.colormap[(x + 1) * res][y * res]);

				//v3
				float v3H = 100 * (float) island.heightmap[x * res][(y + 1) * res];
				Vector3 v3P = new Vector3(x * factor, v3H, (y + 1) * factor);
				VertexInfo v3 = new VertexInfo().setPos(v3P).setCol(island.colormap[x * res][(y + 1) * res]);

				//v4
				float v4H = 100 * (float) island.heightmap[(x + 1) * res][(y + 1) * res];
				Vector3 v4P = new Vector3((x + 1) * factor, v4H, (y + 1) * factor);
				VertexInfo v4 = new VertexInfo().setPos(v4P).setCol(island.colormap[(x + 1) * res][(y + 1) * res]);
				
				v1.setNor(normal(v3P, v2P, v1P));
				v2.setNor(normal(v3P, v2P, v1P));
				v3.setNor(normal(v3P, v2P, v1P));
				meshBuilder.triangle(v3, v2, v1);
				
				v2.setNor(normal(v2P, v3P, v4P));
				v3.setNor(normal(v2P, v3P, v4P));
				v4.setNor(normal(v2P, v3P, v4P));
				meshBuilder.triangle(v2, v3, v4);
			}
			
		}
		islandModel = builder.end();
		
		builder.begin();
		meshBuilder = builder.part("part2", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.ColorPacked, new Material());
		float waterLevel = island.waterLevel * 100;
		
		VertexInfo o1 = new VertexInfo().setPos(-10000, waterLevel, -10000).setNor(0, 1, 0).setCol(new Color(0x064273FF));
		VertexInfo o2 = new VertexInfo().setPos(10000, waterLevel, -10000).setNor(0, 1, 0).setCol(new Color(0x064273FF));
		VertexInfo o3 = new VertexInfo().setPos(10000, waterLevel, 10000).setNor(0, 1, 0).setCol(new Color(0x064273FF));
		VertexInfo o4 = new VertexInfo().setPos(-10000, waterLevel, 10000).setNor(0, 1, 0).setCol(new Color(0x064273FF));
		
		meshBuilder.rect(o4, o3, o2, o1);
		oceanModel = builder.end();

		islandInstance = new ModelInstance(islandModel);
		oceanInstance = new ModelInstance(oceanModel);

		System.out.println("done");

	}
	
	public Vector3 normal(Vector3 a, Vector3 b, Vector3 c){
		Vector3 ab = new Vector3(a);
		ab.sub(b);
		Vector3 ac = new Vector3(c);
		ac.sub(a);
		
		Vector3 normal = new Vector3(ac);
		normal.crs(ab);
		
		return normal;
	}

	@Override
	public void render() {
		camController.update();
		update();

		Gdx.gl.glClearColor(0.5294f, 0.8078f, 0.9215f, 1.0f);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		mBatch.begin(cam);
		mBatch.render(islandInstance, environment);
		mBatch.render(oceanInstance, environment);
		mBatch.end();

	}

	public void changeRenderMode(RenderingMode m) {
		renderMode = m;
		island.createTexture();
	}

	public void update() {
		if (Gdx.input.isKeyPressed(Input.Keys.W))
			cam.translate(0f, 0f, 0.1f);
		if (Gdx.input.isKeyPressed(Input.Keys.A))
			cam.translate(-0.1f, 0f, 0f);
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			cam.translate(0f, 0f, -0.1f);
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			cam.translate(0.1f, 0f, 0f);
		cam.update();

		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			System.out.println("asd");
			// if(System.currentTimeMillis() - last > 1000){ //Reloads map every 1 second

			// These are here for the convenience of testing new variations
			OCTAVES = 10;
			FREQUENCY = 0.0005f;
			AMPLITUDE = 800f;
			PERSISTANCE = 0.7f;

			island.dispose();
			island = new Island(WINDOW_WIDTH, WINDOW_HEIGHT, System.currentTimeMillis());

			last = System.currentTimeMillis();
		}

		// REMOVED FOR DEBUG
		// island.update(1);
	}

	@Override
	public void dispose() {
		island.dispose();
		islandModel.dispose();
		oceanModel.dispose();
	}
}
