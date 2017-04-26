package cs311GraphColoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class cs311GraphColoring {
	public static void main(String ... args) {
		ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<ArrayList<Integer>>();
		
		//not very intuitive but readFromFile returns an int of vertex's and loads the edges with edges
		int amountvertex = readFromFile(args[0], adjacencyList);
		ArrayList<Integer> reds = new ArrayList<Integer>();
		ArrayList<Integer> blues = new ArrayList<Integer>();
		
		//int to represent if a vertex is colored (index is vertex id - 1)
		//-1 uncolored, 0 red, 1 blue
		int[] colored = new int[amountvertex]; 
		for(int i=0; i<colored.length;i++) colored[i] = -1; //nothing is colored yet
		
		reds.add(adjacencyList.get(0).get(0)); //first vertex is red add to coloring stack
		colored[adjacencyList.get(0).get(0)-1] = 0; //first is colored red
		int colorCount = 1; //we have colored 1 so far
		
		Queue<Integer> vertexQ = new LinkedList<Integer>(); //will track which element should be colored next
		vertexQ.add(adjacencyList.get(0).get(0));
		
		ArrayList<Integer> currentSubgraph = new ArrayList<Integer>();
		
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
				
				//reset subgraph, this is disconnected and must be a different graph
				currentSubgraph = new ArrayList<Integer>();
			}
			
			int vertex = vertexQ.remove();
			int color = colored[vertex-1]; //-1 uncolored, 0 red, 1 blue
			currentSubgraph.add(vertex); //this vertex is part of this subgraph
			
			//find associated edges and add to queue
			for(int v : connectedVertexs(vertex, adjacencyList)){
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
					//which means that we have run into an odd cycle
					writeOutputFile(false, colored, currentSubgraph);
				}
				//else they are a different color, which is fine
			}
		}
		writeOutputFile(true, colored, currentSubgraph);
	}
	
	public static void writeOutputFile(boolean bipartate, int[] colored, ArrayList<Integer> subgraph) {
		if(bipartate) { //print yes and the color of each node
			System.out.println("yes"); //it is two colourable
			for(int i=0;i<colored.length;i++) 
				System.out.println("Vertex " + (i+1) + " is " + (colored[i] == -1 ? "uncolored" : (colored[i] > 0 ? "Blue" : "Red")));
		} else { //print no and the odd cycle
			System.out.println("no"); //it isnt too colorable
			for(Integer i : subgraph) System.out.println(i);
		}
		System.exit(0); //quit after this
	}
	
	/***
	 * Extract integer array of connected vertex's to vertexId
	 * @param vertexId
	 * @param adjacencyList
	 * @return 
	 */
	public static Integer[] connectedVertexs(int vertexId, ArrayList<ArrayList<Integer>> adjacencyList) {
		ArrayList<Integer> temp = adjacencyList.get(vertexId-1);
		return temp.toArray(new Integer[temp.size()]);
	}
	
	/***
	 * Load our edges into an adjacency list ArrayList<ArrayList<Integer>> outside list is for each vertex, inside list of each vertex it connects to
	 * @param filePath
	 * @param adjacencyList
	 * @return
	 */
	public static int readFromFile(String filePath, ArrayList<ArrayList<Integer>> adjacencyList) {
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
				vertexs = Integer.parseInt(line);
				//define adjacencyList, one index for each vertex being an ArrayList of directly connected vertex's
				for(int i=0; i< vertexs; i++) {
					//construct adjacencyList so that first element is always the node itself
					adjacencyList.add(new ArrayList<Integer>(Arrays.asList(i+1)));
				}
			}
			
			while ((line = bufferedReader.readLine()) != null) {
				if(!line.isEmpty()) {
					String[] temp = line.split("\\s"); //split by space
					
					//two notable vertex's
					int a = Integer.parseInt(temp[0]);
					int b = Integer.parseInt(temp[1]);
					
					//add b to a
					ArrayList<Integer> adjacentNodesToA = adjacencyList.get(a-1);
					adjacentNodesToA.add(b);
					adjacencyList.set(a-1, adjacentNodesToA);
					
					//add a to b
					ArrayList<Integer> adjacentNodesToB = adjacencyList.get(b-1);
					adjacentNodesToB.add(a);
					adjacencyList.set(b-1, adjacentNodesToB);
				}
			}
			fileReader.close();
		} catch (Exception e) {
			System.out.println("failed to read file");
			System.exit(0);
		}
		
		System.out.println("Successfully loaded edges into Adjacency List");
		return vertexs;
	}
}
