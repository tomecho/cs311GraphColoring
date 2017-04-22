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
		int amountvertex = readFromFile(args[0], edges);
		ArrayList<Integer> reds = new ArrayList<Integer>();
		ArrayList<Integer> blues = new ArrayList<Integer>();
		
		//int to represent if a vertex is colored (index is vertex id - 1)
		//-1 uncolored, 0 red, 1 blue
		int[] colored = new int[amountvertex]; 
		for(int i=0; i<colored.length;i++) colored[i] = -1; //nothing is colored yet
		
		reds.add(edges.get(0)[0]); //first vertex is red add to coloring stack
		colored[edges.get(0)[0]-1] = 0; //first is colored red
		int colorCount = 1; //we have colored 1 so far
		
		Queue<Integer> vertexQ = new LinkedList<Integer>(); //will track which element should be colored next
		vertexQ.add(edges.get(0)[0]);
		
		while(vertexQ.peek() != null || colorCount < amountvertex) {
			//got to add some more stuff, these must be disconnected
			if(vertexQ.peek() == null) {
				int i=0;
				for(;i<colored.length;i++){
					if(colored[i] == -1) //this one isn't colored 
						break;
				}
				colored[i] = 0; //color it red
				colorCount++;
				reds.add(i+1); //remember i+1 is the vertex
				vertexQ.add(i+1); //make it the next one to be colored
			}
			
			int vertex = vertexQ.remove();
			int color = colored[vertex-1]; //-1 uncolored, 0 red, 1 blue
			
			//should already be colored
			/*if(color == -1 ) { //we have not colored this thing yet
				//color it
				coloring[++colorIndex] = vertex;
				colored[vertex-1] = true;
			}
			color = getIndexInColored(vertex,coloring); //whatever we colored this vertex in the end*/
			
			//find associated edges and add to queue
			for(int v : connectedVertexs(vertex, edges)){
				if(v == vertex) continue; //don't run on self
				int connectedVertexColor = colored[v-1]; //-1 uncolored, 0 red, 1 blue
				if(connectedVertexColor == -1) {
					//color it the opposite of ourselves
					if(color == -1) { //this one isnt even colored, this shouldnt be reachable
						System.out.println("error detected");
						System.exit(0);
					} else if(color == 0) { //current vertex is red, next should be blue
						blues.add(v);
						colored[v-1] = 1;
					} else if(color == 1) { //current vertex is blue, next should be red
						reds.add(v);
						colored[v-1] = 0;
					}
					colorCount++;
					vertexQ.add(v); // we should get to coloring, its neighbors too
				}
				else if(connectedVertexColor == color) {
					//same color as the thing its connected to
					System.out.println("not bipartete");
					System.exit(0);
				}
				//else they are a different color, which is fine
			}
		}
		System.out.println("graph is probably fine");
	}
	
	public static Integer[] connectedVertexs(int vertexId, ArrayList<int[]> edges) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int[] e : edges) {
			if(e[0] == vertexId) {
				temp.add(e[1]);
			} else if(e[1] == vertexId) {
				temp.add(e[0]);
			}
		}
		return temp.toArray(new Integer[temp.size()]);
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
		
		
		System.out.println("Successfully loaded " + edges.size() + " edges");
		return vertexs;
	}
}
