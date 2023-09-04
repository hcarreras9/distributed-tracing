package com.ibm.domain.model.graph;

import org.apache.commons.lang3.StringUtils;

public class Microservice {
	private String name;
	public Microservice(String name) {
		if(StringUtils.isBlank(name)) {
			throw new RuntimeException("'name' cannot be null or empty");
		}
		this.name = name;
	}
	
	String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		Microservice other = (Microservice) obj;
		return name.equals(other.name);
	}

	@Override
	public String toString() {
		return "Microservice [name=" + name + "]";
	}
}
