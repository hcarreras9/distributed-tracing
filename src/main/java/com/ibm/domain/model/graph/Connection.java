package com.ibm.domain.model.graph;

public class Connection {
	private Microservice origin;
	private Microservice end;
	private int latency;

	public Connection(Microservice origin, Microservice end, Integer latency) {
		if(origin == null || end == null || latency == null || latency <= 0) {
			throw new RuntimeException("Connection parameters are wrong");
		}
		this.origin = origin;
		this.end = end;
		this.latency = latency;
	}

	Microservice getOrigin() {
		return origin;
	}

	Microservice getEnd() {
		return end;
	}

	int getLatency() {
		return latency;
	}
}
