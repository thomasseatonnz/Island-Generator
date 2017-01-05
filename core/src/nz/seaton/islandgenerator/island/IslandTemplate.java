package nz.seaton.islandgenerator.island;

public abstract class IslandTemplate {
	protected double[][] template;
	public int w, h;
	
	private Type type;
	protected long seed;
	
	static enum Type {
		MOUNTAIN, ACHIPELAGO
	}
	
	public abstract void regenerate();
	
	public IslandTemplate(int ww, int hh, long s,Type t){
		template = new double[ww][hh];
		w = ww;
		h = hh;
		
		type = t;
		seed = s;
	}
	
	public double[][] getTemplate(){
		return template;
	}
	
	public double getTemplate(int x, int y){
		return template[x][y];
	}

	public Type getType() {
		return type;
	}
}
