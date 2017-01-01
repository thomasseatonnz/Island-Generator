package nz.seaton.islandgenerator.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity {
	protected double x, y;
	
	public Entity(double xx, double yy){
		x = xx;
		y = yy;
	}
	
	public abstract void update(long step);
	
	public abstract void render(SpriteBatch renderer);
	
	public abstract void dispose();
}
