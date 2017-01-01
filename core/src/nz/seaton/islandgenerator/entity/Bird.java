package nz.seaton.islandgenerator.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bird extends Entity{
	static Texture tex;
	
	double angle = 0.0;
	boolean clockwise;
	double speed;
	double angspeed;
	
	static {
		Pixmap pixels = new Pixmap(5, 3, Pixmap.Format.RGBA8888);
		pixels.setColor(0.2f,  0.2f,  0.2f, 1.0f);
		pixels.drawLine(0, 0, 2, 3);
		pixels.drawLine(5, 0, 2, 3);
		tex = new Texture(pixels);
		
		pixels.dispose();
	}
	
	public Bird(double xx, double yy) {
		super(xx, yy);
		
		clockwise = (Math.random() > 0.5) ? true : false;
		speed = Math.random();
		angspeed = Math.random() * 0.15;
	}

	public void update(long step) {
		double xVel = Math.cos(angle) * speed;
		double yVel = Math.sin(angle) * speed;
		
		if(clockwise){
			x += (double)xVel;
			y += (double)yVel;
		}else{
			x -= (double)xVel;
			y -= (double)yVel;
		}
		
		angle += angspeed;
		if(angle > (2*Math.PI))
			angle = 0.0;
	}

	public void render(SpriteBatch renderer) {
		renderer.draw(tex, (int)x, (int)y);
	}

	public void dispose() {
		tex.dispose();
	}

}
