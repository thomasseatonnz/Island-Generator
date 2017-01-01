package nz.seaton.islandgenerator;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nz.seaton.islandgenerator.entity.Bird;
import nz.seaton.islandgenerator.entity.Entity;

public class Spawner extends Entity {
	public ArrayList<Entity> entities;

	// debug code
	Texture debugTex;

	public Spawner(SpawnType type, int xx, int yy) {
		super(xx, yy);

		//debug code
		Pixmap map = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
		map.setColor(Color.RED);
		map.drawRectangle(0, 0, 10, 10);
		debugTex = new Texture(map);
		map.dispose();
		//-----
		
		if (type == SpawnType.BIRD) {
			entities = new ArrayList<Entity>();

			int birds = ((int) (Math.random() * 3) + 1);
			for (int i = 0; i < birds; i++) {
				final int spread = 20;
				int randX = (int) (x + (((Math.random() * 2.0f) - 1.0f) * spread));
				int randY = (int) (y + (((Math.random() * 2.0f) - 1.0f) * spread));

				entities.add(new Bird(randX, randY));
			}
		}
	}

	public void render(SpriteBatch renderer) {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).render(renderer);
		}
		if (IslandGenerator.DEBUG) {
			renderer.draw(debugTex, (int)x-5, (int)y-5);
		}

	}

	public void update(long step) {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).update(step);
		}
	}

	public void dispose() {
		if (debugTex != null)
			debugTex.dispose();
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).dispose();
		}
	}
}
