package com.ibm;

import static com.ibm.domain.service.TestRunner.A;
import static com.ibm.domain.service.TestRunner.B;
import static com.ibm.domain.service.TestRunner.C;
import static com.ibm.domain.service.TestRunner.D;
import static com.ibm.domain.service.TestRunner.E;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ibm.domain.model.graph.Connection;
import com.ibm.domain.model.graph.Graph;
import com.ibm.domain.service.TestRunner;
import com.ibm.infrastructure.filereader.FileReader;

public class ProvidedTest {
	
	static Graph graph;
	
	@BeforeAll
	public static void init() {
		String path = new File("src/test/resources/graph-test.txt").getAbsolutePath();
		List<Connection> connections = FileReader.readFile(path);
		graph = new Graph(connections);
	}
	
	@Test 
    public void test1() {
		int result = graph.getAvgLatency(TestRunner.A, B, C);
		assertEquals(result, 9);
    }
	
	@Test 
    public void test2() {
		int result = graph.getAvgLatency(A, D);
		assertEquals(result, 5);
    }
	
	@Test 
    public void test3() {
		int result = graph.getAvgLatency(A, D, C);
		assertEquals(result, 13);
    }
	
	@Test 
    public void test4() {
		int result = graph.getAvgLatency(A, E, B, C, D);
		assertEquals(result, 22);
    }
	
	@Test 
    public void test5() {
		int result = graph.getAvgLatency(A, E, D);
		assertEquals(result, -1);
    }
	
	@Test 
    public void test6() {
		int result = graph.getTracesWithMaxHops(C, C, 3);
		assertEquals(result, 2);
    }
	
	@Test 
    public void test7() {
		int result = graph.getTracesWithHopEquals(A, C, 4);
		assertEquals(result, 3);
    }
	
	@Test 
    public void test8() {
		int result = graph.getShortestBetween(A, C);
		assertEquals(result, 9);
    }
	
	@Test 
    public void test9() {
		int result = graph.getShortestBetween(B, B);
		assertEquals(result, 9);
    }
	
	@Test 
    public void test10() {
		int result = graph.getAllTracesFromToWithMaxLatency(C, C, 30);
		assertEquals(result, 7);
    }
}
