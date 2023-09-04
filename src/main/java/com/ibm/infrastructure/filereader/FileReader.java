package com.ibm.infrastructure.filereader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.ibm.domain.model.graph.Connection;
import com.ibm.domain.model.graph.Microservice;

public class FileReader {
	
	public static List<Connection> readFile(String path) {
		File file = new File(path);
		if(!file.exists()) {
			throw new RuntimeException("File does not exist: " + path);
		}
		
		try(Scanner scanner = new Scanner(file)) {
			String line = scanner.nextLine();
			return parseLine(line);
		} 
		catch (FileNotFoundException e) {
			throw new RuntimeException("There was an error reading file at: " + path, e);
		}
	}
	
	private static List<Connection> parseLine(String line) {
		List<Connection> traces = new ArrayList<>(); 
		String[] connections = line.split(",");
		for(int i = 0; i < connections.length; i++) {
			String conn = connections[i];
			List<String> connList = conn.trim()
					.chars()
					.mapToObj(c -> String.valueOf((char)c))
					.collect(Collectors.toList());
			Connection trace = new Connection(
					new Microservice(connList.get(0)), 
					new Microservice(connList.get(1)), 
					Integer.parseInt(connList.get(2)));
			traces.add(trace); 
		}
		return traces;
	}
}
