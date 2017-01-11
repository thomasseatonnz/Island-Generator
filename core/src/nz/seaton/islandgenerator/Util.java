package nz.seaton.islandgenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public class Util {
	public static Vector3 normal(Vector3 a, Vector3 b, Vector3 c) {
		Vector3 ab = new Vector3(a);
		ab.sub(b);
		Vector3 ac = new Vector3(c);
		ac.sub(a);

		Vector3 normal = new Vector3(ac);
		normal.crs(ab);

		return normal;
	}
	
	public static float lerp(float start, float end, float x) {
		return start * (1 - x) + end * x;
	}
	
	public static void screenshot(){
		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
		PixmapIO.writePNG(Gdx.files.local("Screenshot-"+ System.currentTimeMillis() + ".png"), pixmap);
		pixmap.dispose();
		System.out.println("Screenshot '" + "Screenshot-"+ System.currentTimeMillis() + ".png" + "' created!");
	}
}
