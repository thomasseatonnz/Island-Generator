package nz.seaton.islandgenerator;

import java.util.ArrayList;
import java.util.Random;

public class PlaceName {
	public static ArrayList<String> vowels;
	public static ArrayList<String> cons;
	
	static {
		vowels = new ArrayList<String>();
		cons = new ArrayList<String>();
		
		vowels.add("a");
		vowels.add("e");
		vowels.add("i");
		vowels.add("o");
		vowels.add("u");
		
		cons.add("b");
		cons.add("c");
		cons.add("d");
		cons.add("f");
		cons.add("g");
		cons.add("h");
		cons.add("j");
		cons.add("k");
		cons.add("l");
		cons.add("m");
		cons.add("n");
		cons.add("p");
		cons.add("q");
		cons.add("r");
		cons.add("s");
		cons.add("t");
		cons.add("v");
		cons.add("w");
		cons.add("x");
		cons.add("y");
		cons.add("z");
	}
	
	public static String generatePlaceName(long seed){
		String finalName = "";
		Random rand = new Random(seed);
		
		int sounds = (int)(rand.nextDouble() * 4) + 2;
		for(int i = 0; i < sounds; i++){
			int c = (int)(rand.nextDouble() * 21);
			if(i == 0)
				finalName += cons.get(c).toUpperCase();
			else
				finalName += cons.get(c);
			
			
			int vnum = (int)(rand.nextDouble() * 2) + 1;
			for(int ii = 0; ii < vnum; ii++){
				int cc = (int)(rand.nextDouble() * 5);
				finalName += vowels.get(cc);
			}
		}
		return finalName;
	}
}