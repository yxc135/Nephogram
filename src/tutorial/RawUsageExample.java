package tutorial;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import core.graph.Graph;
import core.schedule.latch.LatchScheduler;
import core.schedule.latch.LatchTask;

public class RawUsageExample {

	public static void main(String[] args) throws Exception {
		ExecutorService executor = Executors.newWorkStealingPool(10);
		LatchScheduler scheduler = new LatchScheduler(executor);

		LatchTask<String, Double> task1 = new LatchTask<String, Double>(
				scheduler, "T1", "1.1") {
			@Override
			public Double executeTask() throws Throwable {
				return Double.valueOf(input);
			}
		};

		LatchTask<String, Integer> task2 = new LatchTask<String, Integer>(
				scheduler, "T2", "2") {
			@Override
			public Integer executeTask() throws Throwable {
				return Integer.valueOf(input);
			}
		};

		LatchTask<Void, Double> task3 = new LatchTask<Void, Double>(scheduler,
				"T3", null) {
			@Override
			public Double executeTask() throws Throwable {
				Double result1 = null;
				Integer result2 = null;
				for (LatchTask<?, ?> task : upstreamTasks) {
					if (task.getId().equals("T1")) {
						result1 = (Double) task.getOutput();
					} else if (task.getId().equals("T2")) {
						result2 = (Integer) task.getOutput();
					}
				}
				return result1 + result2;
			}
		};

		task1.addToDownstream(Collections.singleton(task3));
		task2.addToDownstream(Collections.singleton(task3));
		task3.addToUpstream(Collections.singleton(task1));
		task3.addToUpstream(Collections.singleton(task2));

		Graph<LatchTask<?, ?>> graph = new Graph<>();
		graph.add(Collections.singleton(task1));
		graph.add(Collections.singleton(task2));
		graph.add(Collections.singleton(task3));

		scheduler.schedule(graph);

		scheduler.shutdown(10, TimeUnit.SECONDS);

		System.out.println(task1.getError());
		System.out.println(task2.getError());
		System.out.println(task3.getError());
		System.out.println(task1.getOutput());
		System.out.println(task2.getOutput());
		System.out.println(task3.getOutput());
	}

}
