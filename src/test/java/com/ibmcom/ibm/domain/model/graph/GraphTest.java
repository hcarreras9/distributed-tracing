package com.ibmcom.ibm.domain.model.graph;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.ibm.domain.model.graph.Graph;


public class GraphTest {
	
	@Test 
    public void graph() {
		RuntimeException thrown = assertThrows(
           RuntimeException.class, () -> new Graph(Collections.emptyList()),
           "Exception not thrown");
		assertTrue(thrown.getMessage().contains("'connections' cannot be empty"));
    }

	
}
