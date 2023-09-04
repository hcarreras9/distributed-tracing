package com.ibm.domain.service;

import com.ibm.domain.model.graph.Graph;
import com.ibm.domain.model.graph.Microservice;

public class TestRunner {
	public static final Microservice A = new Microservice("A");
	public static final Microservice B = new Microservice("B");
	public static final Microservice C = new Microservice("C");
	public static final Microservice D = new Microservice("D");
	public static final Microservice E = new Microservice("E");

	public int[] run(Graph graph) {
	    int one = graph.getAvgLatency(A, B, C);
	    int two = graph.getAvgLatency(A, D);
	    int three = graph.getAvgLatency(A, D, C);
	    int four = graph.getAvgLatency(A, E, B, C, D);
	    int five = graph.getAvgLatency(A, E, D);
	    int six = graph.getTracesWithMaxHops(C, C, 3);
	    int seven = graph.getTracesWithHopEquals(A, C, 4);
	    int eigth = graph.getShortestBetween(A, C);
	    int nine = graph.getShortestBetween(B, B);
	    int ten = graph.getAllTracesFromToWithMaxLatency(C, C, 30);
	    
	    return new int[] {one, two, three, four, five, six, seven, eigth, nine, ten};
	}
}
