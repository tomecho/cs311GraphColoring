package cs311GraphColoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class cs311GraphColoring {
	public static void main(String ... args) {
		//array list of int arrays each element being a vertex
		ArrayList<int[]> edges = new ArrayList<int[]>();
		
		//not very intuitive but readfromfile returns an int of vertex's and loads the edges with edges
		//coloring is a int array, even index red odd index blue
		int[] coloring = new int[readFromFile(args[0], edges)];
		
		int colorIndex = 0;
		coloring[colorIndex] = edges.get(0)[0]; //first vertex is red add to coloring stack
		
		Queue<Integer> vertexQ = new LinkedList<Integer>(); //will track which element should be colored next
		vertexQ.add(edges.get(0)[0]);
		
		while(vertexQ.peek() != null) {
			int vertex = vertexQ.remove();
			int color = getIndexInColored(vertex, coloring);
			if(color == -1 ) { //we have not colored this thing yet
				//color it
				coloring[++colorIndex] = vertex;
			}
			color = getIndexInColored(vertex,coloring); //whatever we colored this vertex in the end
			
			//find associated edges and add to queue
			for(int v : connectedVertexs(vertex, edges)){
				if(v == vertex) continue; //dont run on self
				int connectedVertexColor = getIndexInColored(v,coloring);
				if(connectedVertexColor == -1)
					vertexQ.add(v); // we should get to coloring that
				else if(connectedVertexColor % 2 == color % 2) {
					//same color as the thing its connected to
					System.out.println("not bipartete");
					System.exit(0);
				}
			}
		}
		System.out.println("graph is probably fine");
	}
	
	public static Integer[] connectedVertexs(int vertexId, ArrayList<int[]> edges) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int[] e : edges) {
			if(e[0] == vertexId || e[1] == vertexId) temp.add(e[0]);
		}
		return temp.toArray(new Integer[temp.size()]);
	}
	
	public static boolean isColored(int vertexId, int[] coloring) {
		for(int v : coloring) if(v == vertexId) return true;
		return false;
	}
	
	public static int getIndexInColored(int vertexId, int[] coloring){
		for(int i=0;i<coloring.length;i++){
			if(coloring[i] == vertexId) return i;
		}
		return -1;
	}
	
	/***
	 * Reads in the edges from file
	 * @param filePath
	 * @param edges
	 * @return vertex's
	 */
	public static int readFromFile(String filePath, ArrayList<int[]> edges) {
		System.out.println("Reading file");
		int vertexs = -1;
		try { 
			//load edges from file to edges list
			File file = new File(filePath);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			//but first line is actual number of vertex's
			String line = bufferedReader.readLine();
			if(line != null) {
				System.out.println("Loading in " + line + " vertex's");
				//define array as number of edges, with each entry containing two parts, one for each vertex
				vertexs = Integer.parseInt(line);
			}
			
			while ((line = bufferedReader.readLine()) != null) {
				if(!line.isEmpty()) {
					String[] temp = line.split("\\s"); //split by space
					edges.add(new int[] { Integer.parseInt(temp[0]), Integer.parseInt(temp[1]) });
				}
			}
			fileReader.close();
		} catch (Exception e) {
			System.out.println("failed to read file");
			System.exit(0);
		}
		
		
		System.out.println("Successfully loaded " + edges.size() + "edges");
		return vertexs;
	}
}
