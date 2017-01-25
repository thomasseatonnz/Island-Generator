package nz.seaton.islandgenerator;

import java.util.ArrayList;
import java.util.List;

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

import nz.seaton.islandgenerator.UI.UI;
import nz.seaton.islandgenerator.island.Island;

public class IslandGenerator extends ApplicationAdapter {

	public final static int WINDOW_WIDTH = 1280;
	public final static int WINDOW_HEIGHT = 720;

	public static int OCTAVES = 10;
	public static float FREQUENCY = 0.0005f;
	public static float AMPLITUDE = 800f;
	public static float PERSISTANCE = 0.7f;

	public static boolean DEBUG = true;

	public static RenderingMode renderMode = RenderingMode.CONTOURCOLOR;

	PerspectiveCamera cam;
	CameraInputController camController;

	Environment environment;
	DirectionalLight sun;

	ModelBatch mBatch;
	UI ui;

	ArrayList<Model> islandModels;
	ArrayList<ModelInstance> islandInstances;

	// DEBUG
	ArrayList<Model> arrowModels;
	ArrayList<ModelInstance> arrowInstances;

	Model oceanModel;
	ModelInstance oceanInstance;

	Island island;

	long lastFPS = System.currentTimeMillis();
	float time;

	long last = 0;

	@Override
	public void create() {
		Gdx.graphics.setTitle("Island Generator");
		Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		Gdx.graphics.setResizable(false);
		Gdx.graphics.setVSync(false);

		ui = new UI(WINDOW_WIDTH, WINDOW_HEIGHT);

		island = new Island(WINDOW_WIDTH, WINDOW_HEIGHT, System.currentTimeMillis(), ui);
		ui.setIsland(island);

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 300f, 100f);
		cam.lookAt(0f, 0f, 0f);
		cam.near = 10f;
		cam.far = 1000f;
		cam.update();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.0f));
		// sun = new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0, 1, 0);
		sun = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.8f, -1, 0);
		environment.add(sun);

		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		mBatch = new ModelBatch();

		islandModels = new ArrayList<Model>();
		islandInstances = new ArrayList<ModelInstance>();

		int res = 5;
		float scl = 1.5f;
		float factor = res * scl;
		int chunkSize = 20;

		int chunkCount = 0;

		float xoffset = (island.w * scl) / 2;
		float zoffset = (island.h * scl) / 2;

		ModelBuilder builder = new ModelBuilder();

		// for (int cx = 0; cx < island.w / res; cx += chunkSize) {
		for (int cy = 0; cy < island.h / res; cy += chunkSize) {
			chunkCount++;
			builder.begin();

			Material material = new Material();
			// material.set(IntAttribute.createCullFace(GL20.GL_NONE));
			MeshPartBuilder meshBuilder = builder.part("chunk" + chunkCount, GL20.GL_TRIANGLE_STRIP, Usage.Position | Usage.Normal | Usage.ColorPacked, material);

			Matrix4 transformMatrix = new Matrix4();
			transformMatrix.translate(-xoffset, 0, -zoffset);
			meshBuilder.setVertexTransform(transformMatrix);

			// for (int x = cx; (x < (island.w / res) - 1) && x < (cx + chunkSize); x++) {
			// for (int y = cy; (y < (island.h / res) - 1) && y < (cy + chunkSize); y++) {
			// // v1
			// float v1H = 100 * (float) island.heightmap[x * res][y * res];
			// Vector3 v1P = new Vector3(x * factor, v1H, y * factor);
			// VertexInfo v1 = new VertexInfo().setPos(v1P).setCol(island.colormap[x * res][y * res]);
			//
			// // v2
			// float v2H = 100 * (float) island.heightmap[(x + 1) * res][y * res];
			// Vector3 v2P = new Vector3((x + 1) * factor, v2H, y * factor);
			// VertexInfo v2 = new VertexInfo().setPos(v2P).setCol(island.colormap[(x + 1) * res][y * res]);
			//
			// // v3
			// float v3H = 100 * (float) island.heightmap[x * res][(y + 1) * res];
			// Vector3 v3P = new Vector3(x * factor, v3H, (y + 1) * factor);
			// VertexInfo v3 = new VertexInfo().setPos(v3P).setCol(island.colormap[x * res][(y + 1) * res]);
			//
			// // v4
			// float v4H = 100 * (float) island.heightmap[(x + 1) * res][(y + 1) * res];
			// Vector3 v4P = new Vector3((x + 1) * factor, v4H, (y + 1) * factor);
			// VertexInfo v4 = new VertexInfo().setPos(v4P).setCol(island.colormap[(x + 1) * res][(y + 1) * res]);
			//
			// v1.setNor(Util.normal(v3P, v2P, v1P));
			// v2.setNor(Util.normal(v3P, v2P, v1P));
			// v3.setNor(Util.normal(v3P, v2P, v1P));
			// meshBuilder.triangle(v3, v2, v1);
			//
			// v2.setNor(Util.normal(v2P, v3P, v4P));
			// v3.setNor(Util.normal(v2P, v3P, v4P));
			// v4.setNor(Util.normal(v2P, v3P, v4P));
			// meshBuilder.triangle(v2, v3, v4);
			// }
			// }
			// meshBuilder.setColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f));
			for (int y = cy; (y < (island.h / res) - 1) && y < (cy + chunkSize); y++) {
				// for (int x = cx; (x < (island.w / res)) && x < (cx + chunkSize); x++) {
				for (int x = 0; (x < (island.w / res)); x++) {
					float v1H = 100 * (float) island.heightmap[x * res][y * res];
					Vector3 v1P = new Vector3(x * factor, v1H, y * factor);
					Vector3 v1N = calcNormal(x, y, res);
					if (v1H > island.waterLevel * 100f)
						queueArrow(v1P, v1N);
					VertexInfo v1 = new VertexInfo().setPos(v1P).setNor(v1N).setCol(island.colormap[x * res][y * res]);

					float v2H = 100 * (float) island.heightmap[x * res][(y + 1) * res];
					Vector3 v2P = new Vector3(x * factor, v2H, (y + 1) * factor);
					Vector3 v2N = calcNormal(x, y + 1, res);
					if (v2H > island.waterLevel * 100f)
						queueArrow(v2P, v2N);
					VertexInfo v2 = new VertexInfo().setPos(v2P).setNor(v2N).setCol(island.colormap[x * res][(y + 1) * res]);

					meshBuilder.index(meshBuilder.vertex(v1));
					meshBuilder.index(meshBuilder.vertex(v2));
				}
			}

			islandModels.add(builder.end());
			islandInstances.add(new ModelInstance(islandModels.get(islandModels.size() - 1)));
		}
		// }
		System.out.println(chunkCount + " chunks!");

		// Arrow
		arrowModels = new ArrayList<Model>();
		arrowInstances = new ArrayList<ModelInstance>();

//		builder.begin();
//		int i = 0;
//		for (Arrow a : arrowQueue) {
//			MeshPartBuilder meshBuilder = builder.part("Arrow" + i, GL20.GL_LINES, Usage.Position | Usage.ColorPacked, new Material());
//			Matrix4 transformMatrix = new Matrix4();
//			transformMatrix.translate(-xoffset, 0, -zoffset);
//
//			meshBuilder.setVertexTransform(transformMatrix);
//			meshBuilder.setColor(Color.PURPLE);
//
//			meshBuilder.line(a.start, a.end);
//			i++;
//		}
//		arrowModels.add(builder.end());
//		arrowInstances.add(new ModelInstance(arrowModels.get(arrowModels.size() - 1)));

		// Ocean
		builder.begin();
		MeshPartBuilder meshBuilder = builder.part("ocean1", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.ColorPacked, new Material());
		float waterLevel = island.waterLevel * 100;

		meshBuilder.setColor(new Color(0x064273FF));
		VertexInfo o1 = new VertexInfo().setPos(-10000, waterLevel, -10000).setNor(0, 1, 0);
		VertexInfo o2 = new VertexInfo().setPos(10000, waterLevel, -10000).setNor(0, 1, 0);
		VertexInfo o3 = new VertexInfo().setPos(10000, waterLevel, 10000).setNor(0, 1, 0);
		VertexInfo o4 = new VertexInfo().setPos(-10000, waterLevel, 10000).setNor(0, 1, 0);

		meshBuilder.rect(o4, o3, o2, o1);
		oceanModel = builder.end();

		oceanInstance = new ModelInstance(oceanModel);

	}

	private class Arrow {
		public Vector3 start, end;

		Arrow(Vector3 pos, Vector3 normal) {
			this.start = pos;
			this.end = new Vector3((pos.x + normal.x * 3), (pos.y + normal.y * 3), (pos.z + normal.z * 3));
		}
	}

	List<Arrow> arrowQueue = new ArrayList<Arrow>();

	private void queueArrow(Vector3 pos, Vector3 normal) {
		arrowQueue.add(new Arrow(pos, normal));
	}

	public Vector3 calcNormal(int x, int y, int res) {
		x *= res;
		y *= res;

		if (x - res < 0)
			x = res;
		else if (x + res >= island.w)
			x = island.w - res - 1;

		if (y - res < 0)
			y = res;
		else if (y + res >= island.h)
			y = island.h - res - 1;

		float heightL = (float) island.heightmap[x - res][y];
		float heightR = (float) island.heightmap[x + res][y];
		float heightD = (float) island.heightmap[x][y - res];
		float heightU = (float) island.heightmap[x][y + res];
		
		Vector3 norm = new Vector3(heightL - heightR, 0.5f , heightD - heightU).nor();

		return norm;
	}

	@Override
	public void render() {
		camController.update();

		// Day Night Cycle
		time += 1;
		sun.setDirection((float) Math.sin(time / 200f), (float) Math.cos(time / 200f), 0f);
		float f = (float) ((-0.5 * Math.cos(time / 200f)) + 0.5);
		Gdx.gl.glClearColor(0.5294f * f, 0.8078f * f, 0.9215f * f, 1.0f);

		float tideTimeScale = 100f;
		float tideHeightScale = 0.6f;
		oceanInstance.transform.translate(new Vector3(0f, -(float) (tideHeightScale * (Math.sin((time - 1) / tideTimeScale))), 0f));
		oceanInstance.transform.translate(new Vector3(0f, (float) (tideHeightScale * (Math.sin(time / tideTimeScale))), 0f));
		oceanInstance.calculateTransforms();

		update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		mBatch.begin(cam);
		for (ModelInstance isi : islandInstances)
			mBatch.render(isi, environment);
		if (DEBUG)
			for (ModelInstance isi : arrowInstances)
				mBatch.render(isi);
		mBatch.render(oceanInstance, environment);
		mBatch.end();

		ui.render();
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

		if (Gdx.input.isKeyJustPressed(Input.Keys.P))
			Util.screenshot();

		island.update(1);
		ui.update();
	}

	@Override
	public void dispose() {
		if (island != null)
			island.dispose();
		if (ui != null)
			ui.dispose();
		for (Model i : islandModels)
			if (i != null)
				i.dispose();
		if (oceanModel != null)
			oceanModel.dispose();
	}
}