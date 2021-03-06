package nz.seaton.islandgenerator.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import nz.seaton.islandgenerator.IslandGenerator;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = IslandGenerator.WINDOW_WIDTH;
		config.height = IslandGenerator.WINDOW_HEIGHT;
		//config.samples = 4;
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.backgroundFPS = 0;
		
		new LwjglApplication(new IslandGenerator(), config);
	}
}
