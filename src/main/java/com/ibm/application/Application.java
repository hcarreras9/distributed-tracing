package com.ibm.application;

import java.util.List;

import com.ibm.domain.model.graph.Connection;
import com.ibm.domain.model.graph.Graph;
import com.ibm.domain.service.TestRunner;
import com.ibm.infrastructure.filereader.FileReader;

public class Application {
	
	public void runTests(String filePath) {
		List<Connection> connections = FileReader.readFile(filePath);
		Graph graph = new Graph(connections);
		int[] results = new TestRunner().run(graph);
		for(int i = 0; i < results.length; i++) {
			System.out.println((i+1) + ". " + checkNoSuchTrace(results[i]));
		}
	}
	
	private String checkNoSuchTrace(int result) {
		if(result < 0) {
			return "NO SUCH TRACE";
		}
		else {
			return ((Integer)result).toString();
		}
	}
	
	public static void main(String[] args) {
		if(args.length == 0) {
			throw new RuntimeException("Please provide a text file parameter like: ./gradlew run --args='/path/to/file.txt'");	
		}
		
		new Application().runTests(args[0]);
	}
}
