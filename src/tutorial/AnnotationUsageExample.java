package tutorial;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import toolkit.annotation.Task;
import toolkit.annotation.TaskAnnotationParser;
import toolkit.annotation.TaskRepository;
import core.graph.Graph;
import core.schedule.latch.LatchScheduler;
import core.schedule.latch.LatchTask;

public class AnnotationUsageExample {

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
			@Task(id = "T1")
			private LatchTask<String, Double> task1;
			@Task(id = "T2")
			private LatchTask<String, Integer> task2;

			@Override
			public Double executeTask() throws Throwable {
				Double result1 = task1.getOutput();
				Integer result2 = task2.getOutput();
				return result1 + result2;
			}
		};

		TaskRepository<LatchTask<?, ?>> repository = new TaskRepository<>();
		repository.add(task1);
		repository.add(task2);
		repository.add(task3);
		TaskAnnotationParser<LatchTask<?, ?>> parser = new TaskAnnotationParser<LatchTask<?, ?>>() {
			@Override
			public void addDependency(LatchTask<?, ?> upstreamTask,
					LatchTask<?, ?> downstreamTask) {
				upstreamTask.addToDownstream(downstreamTask);
				downstreamTask.addToUpstream(upstreamTask);
			}
		};
		parser.parse(repository);

		Graph<LatchTask<?, ?>> graph = new Graph<>();
		graph.add(repository.getAll());

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
