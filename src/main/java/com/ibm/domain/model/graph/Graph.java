package com.ibm.domain.model.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph {
	private Map<Microservice, List<Connection>> microservices = new HashMap<>();

	public Graph(List<Connection> connections) {
		if(connections == null || connections.isEmpty()) {
			throw new RuntimeException("'connections' cannot be empty");
		}
		init(connections);
	}
	
	private void init(List<Connection> connections) {
		for(Connection connection : connections) {
			List<Connection> originTraces = microservices.get(connection.getOrigin());
			if(originTraces == null) {
				originTraces = new ArrayList<Connection>();
				microservices.put(connection.getOrigin(), originTraces);
			}
			originTraces.add(connection);
		}
	}
	
	public int getAvgLatency(Microservice... micros) {
		Condition condition = new FollowPathCondition(micros);
		List<TracePath> result = findTraces(micros[0], condition);
		if(result.isEmpty()) {
			return -1;
		}
		else {
			return result.get(0).latency;
		}
	}
	
	public int getTracesWithMaxHops(Microservice origin, Microservice end, int maxHops) {
		Condition condition = new CompositeCondition()
				.add(new MaxHopCondition(maxHops))
				.add(new EndCondition(end));
		List<TracePath> result = findTraces(origin, condition);
		return result.size();
	}
	
	public int getTracesWithHopEquals(Microservice origin, Microservice end, int hops) {
		Condition condition = new CompositeCondition()
				.add(new HopEqualsCondition(hops))
				.add(new EndCondition(end));
		List<TracePath> result = findTraces(origin, condition);
		return result.size();
	}
	
	public int getShortestBetween(Microservice origin, Microservice end) {
		Condition condition = new CompositeCondition()
				.add(new NoRepetitionCondition(origin.equals(end)))
				.add(new EndCondition(end));
		List<TracePath> result = findTraces(origin, condition);
		return result.stream()
				.mapToInt(tp -> tp.latency).min()
				.orElse(0);
	}
	
	public int getAllTracesFromToWithMaxLatency(Microservice origin, Microservice end, int latency) {
		Condition condition = new CompositeCondition()
				.add(new MaxLatencyCondition(latency))
				.add(new EndCondition(end));
		List<TracePath> result = findTraces(origin, condition);
		return result.size();
	}
	
	
	
	/**
	 * Interface representing a condition that must be satisfied 
	 * by a {@link TracePath} to be accepted as valid trace
	 */
	private interface Condition {
		/**
		 * Evaluates where a {@link TracePath} satisfies the implemented
		 * condition or not. The result of this evaluation could be:
		 * <ul>
		 * <li>A number under zero which means that the condition is not satisfied but it
		 * could be on done when more {@link Connection}s are added to this {@link TracePath}.
		 * </br>
		 * Example: We need a trace with 4 hops but the current TracePath contains 2.</li>
		 * <li>Zero: this means that the condition is met by the TracePath</li>
		 * <li>A number bigger than zero: this means that the condition is not satisfied and 
		 * it won't be so on next hops. 
		 * </br>
		 * Example: We need a trace with a latency of at most 30 but the current TracePath contains 41.
		 * </li>
		 * </ul> 
		 * @return 
		 */
		int satisfy(TracePath tracepath);
	}
	
	private class CompositeCondition implements Condition {
		private List<Condition> conditions = new ArrayList<>();
		
		@Override
		public int satisfy(TracePath tracepath) {
			boolean allSucceed = true;
			for(Condition cond : conditions) {
				int condResult = cond.satisfy(tracepath);
				if(condResult > 0) {
					return condResult;
				}
				else if(condResult < 0) {
					allSucceed = false;
				}
			}
			
			return allSucceed ? 0 : -1;
		}
		
		public CompositeCondition add(Condition condition) {
			this.conditions.add(condition);
			return this;
		}
	}
	
	private class FollowPathCondition implements Condition {
		private String pathToFollow;
		
		public FollowPathCondition(Microservice... micros) {
			this.pathToFollow = Stream.of(micros)
					.map(m -> m.getName())
					.collect(Collectors.joining("-"));
		}
		
		@Override
		public int satisfy(TracePath tp) {
			if(!this.pathToFollow.startsWith(tp.toStringVisited())) {
				return 1;
			}
			if(this.pathToFollow.length() == tp.toStringVisited().length()) {
				return 0;
			}
			return -1;
		}
	}
	
	private class MaxLatencyCondition implements Condition {
		private int maxLatency;
		
		public MaxLatencyCondition(int maxLatency) {
			this.maxLatency = maxLatency;
		}
		
		@Override
		public int satisfy(TracePath tp) {
			return tp.latency < maxLatency ? 0 : 1;
		}
	}
	
	/**
	 * Maximum hopes means: less or equals
	 */
	private class MaxHopCondition implements Condition {
		private final int maxHops;
		
		MaxHopCondition(int maxHops) {
			this.maxHops = maxHops;
		}
		
		@Override
		public int satisfy(TracePath tp) {
			return tp.hops <= maxHops ? 0 : 1;
		}
	}

	/**
	 * Specific amount of hops
	 */
	private class HopEqualsCondition implements Condition {
		private final int hops;
		
		HopEqualsCondition(int hops) {
			this.hops = hops;
		}
		
		@Override
		public int satisfy(TracePath tp) {
			return tp.hops - hops;
		}
	}

	private class NoRepetitionCondition implements Condition {
		private boolean allowsEndRep;
		NoRepetitionCondition(boolean allowsEndRep) {
			this.allowsEndRep = allowsEndRep;
		}
		
		@Override
		public int satisfy(TracePath tracepath) {
			int setSize = new HashSet<Microservice>(tracepath.visited).size();
			int traceSize = tracepath.visited.size();
			if(setSize == traceSize) {
				return 0;
			}
			else if(allowsEndRep && 
					tracepath.visited.getFirst().equals(tracepath.visited.getLast())) {
				return 0;
			}
			return 1;
		}
	}
	
	private class EndCondition implements Condition {
		private Microservice end;

		EndCondition(Microservice end) {
			this.end = end;
		}
		
		@Override
		public int satisfy(TracePath tp) {
			return this.end.equals(tp.end) ? 0 : -1;
		}
	}
	
	/**
	 * Represents a path of a trace between two or more {@link Microservice}s
	 */
	private class TracePath {
		private Microservice origin;
		private Microservice end;
		private int latency;
		private int hops;
		private Deque<Microservice> visited;
		
		TracePath() {}
		
		TracePath(Connection connection) {
			this.origin = connection.getOrigin();
			this.end = connection.getEnd();
			this.latency = connection.getLatency();
			this.hops = 1;
			this.visited = new ArrayDeque<>();
			this.visited.addLast(connection.getOrigin());
			this.visited.addLast(connection.getEnd());
		}
		
		TracePath addHop(Connection connection) {
			TracePath newTp = new TracePath();
			newTp.origin = this.origin;
			newTp.end = connection.getEnd();
			newTp.latency = this.latency + connection.getLatency();
			newTp.hops = this.hops + 1;
			newTp.visited = new ArrayDeque<>(this.visited);
			newTp.visited.addLast(connection.getEnd());
			return newTp;
		}

		@Override
		public String toString() {
			return "TracePath [latency=" + latency + ", " + "visited=" + toStringVisited() + "]";
		}
		
		private String toStringVisited() {
			return visited.stream().map(m -> m.getName()).collect(Collectors.joining("-"));
		}
	}
	
	/**
	 * Initiates the {@link TracePath} based on origin {@link Microservice}. </br> 
	 * All the connections starting at origin will generate a TracePath.
	 */
	private List<TracePath> findTraces(Microservice origin, Condition condition) {
		List<TracePath> tps = microservices.get(origin).stream()
				.map(t -> new TracePath(t))
				.collect(Collectors.toList());
		List<TracePath> result = findTraces(tps, condition);
		
		// result.forEach(mt -> System.out.println("Hops visited: " + mt.toStringVisited()));
		
		return result;
	}
	
	/**
	 * Validates that {@link TracePath}s on parameter satisfy the {@link Condition}, 
	 * saving those that passed the validation, skipping the ones that don't, and 
	 * iterating over those {@link TracePath}s that can be satisfy the Condition 
	 * when a new hop is added to them.   
	 */
	private List<TracePath> findTraces(List<TracePath> mts, Condition condition) {
		List<TracePath> succeed = new ArrayList<>();
		List<TracePath> addHop = new ArrayList<>();
		
		for(TracePath mt : mts) {
			int result = condition.satisfy(mt);
			if(result == 0) {
				succeed.add(mt);
				addHop.add(mt);
			}
			else if(result < 0) {
				addHop.add(mt);
			}
		}
		
		if(addHop.isEmpty()) {
			return succeed;
		}
		
		List<TracePath> newMT = addHop.stream()
				.flatMap(mt -> microservices.get(mt.end).stream().map(tr -> mt.addHop(tr)))
				.collect(Collectors.toList());

		List<TracePath> succeedNewMT = findTraces(newMT, condition);
		succeed.addAll(succeedNewMT);
		return succeed;
	}
}
