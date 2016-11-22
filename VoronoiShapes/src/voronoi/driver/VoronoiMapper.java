package voronoi.driver;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import voronoi.map.Node;

public class VoronoiMapper {
	
	public Map<Integer, Color> colorMap = new HashMap<>();
	public static Map<String, Node> pathMap;
	
	public void createVoronoiMap(String directory) throws IOException{
		
		pathMap = new HashMap<>();
		
		Random random = new Random(10);
		File folder = new File(directory);
		File[] files = folder.listFiles();
		
		File dimsFile = files[0];
		BufferedReader dimsReader = new BufferedReader(new FileReader(dimsFile));
		String[] dimsStr = dimsReader.readLine().split(",");
		int height = Integer.parseInt(dimsStr[0]);
		int width = Integer.parseInt(dimsStr[1]);
		dimsReader.close();
		
		SimpleImage image = new SimpleImage(height, width, colorMap);
		List<List<Pixel>> shapes = new ArrayList<>();
		
		for(int i = 1; i < files.length; i++){
			List<Pixel> shape = new ArrayList<>();
			BufferedReader reader = new BufferedReader(new FileReader(files[i]));
			String line;
			while((line = reader.readLine()) != null){
				String[] pixStr = line.split(",");
				int x = Integer.parseInt(pixStr[0]);
				int y = Integer.parseInt(pixStr[1]);
				int r = Integer.parseInt(pixStr[2]);
				int g = Integer.parseInt(pixStr[3]);
				int b = Integer.parseInt(pixStr[4]);
				Pixel p = image.getPixel(y-1, x-1);
				p.setVal(i);
				p.setShape(true);
				p.setRGB(r, g, b);
				shape.add(p);
			}
			shapes.add(shape);
			// Assign a random color to the shapes tile
			float rv = random.nextFloat();// / 2f + 0.5f;
			float gv = random.nextFloat();// / 2f + 0.5f;
			float bv = random.nextFloat();// / 2f + 0.5f;
			colorMap.put(i, new Color(rv, gv, bv));
			reader.close();
		}
		
		int[] indexes = new int[shapes.size()];
		for(int i = 0; i < shapes.size(); i++){
			indexes[i] = i;
		}
			
		image.showImg();
		int numStages = 0;
		Date tic = new Date();
		int numPix = 0;
		while(!shapesEmpty(shapes)){
			shuffleArray(indexes);
			for(int i : indexes){
				List<Pixel> shape = shapes.get(i);
				List<Pixel> newShape = new ArrayList<>();
				for(Pixel p : shape){
					numPix ++;
					newShape.addAll(p.grow());
				}
				shapes.set(i, newShape);
 			}
			image.updateImg();
			numStages++;
		}
		
		System.out.println("num pix: " + numPix);
		Date toc = new Date();
		image.updateImg();
		System.out.println("It took " + numStages + " steps to create the diagram");
		System.out.println("It took " + (toc.getTime() - tic.getTime()) + "ms generate it");
	}
	
	public boolean shapesEmpty(List<List<Pixel>> shapes){
		for(List<Pixel> shape : shapes){
			if(shape.size() != 0){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Implementing Fisher�Yates shuffle
	 */
	public void shuffleArray(int[] ar){
		// If running on Java 6 or older, use `new Random()` on RHS here
	    Random rnd = new Random(1337);
	    for (int i = ar.length - 1; i > 0; i--){
	    	int index = rnd.nextInt(i + 1);
	    	// Simple swap
	    	int a = ar[index];
	    	ar[index] = ar[i];
	    	ar[i] = a;
	    }
	}
}
