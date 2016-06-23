package core.schedule.latch;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import core.graph.Graph;
import core.schedule.Scheduler;
import core.schedule.exception.CircularGraphException;
import core.schedule.exception.ScheduleFailedException;
import core.schedule.exception.ScheduleRejectedException;

public class LatchScheduler implements Scheduler<LatchTask<?, ?>> {

	private final ExecutorService executor;
	private AtomicBoolean shutdownRequested;

	private final Phaser phaser;

	public LatchScheduler(ExecutorService executor) {
		this.executor = executor;
		shutdownRequested = new AtomicBoolean(false);
		phaser = new Phaser();
	}

	@Override
	public void schedule(Graph<LatchTask<?, ?>> graph)
			throws ScheduleFailedException {
		if (shutdownRequested.get()) {
			throw new ScheduleRejectedException();
		}
		phaser.bulkRegister(graph.size());
		Collection<LatchTask<?, ?>> bootstrapTasks = graph.getBootstrapTasks();
		if (bootstrapTasks.isEmpty()) {
			throw new CircularGraphException();
		}
		for (LatchTask<?, ?> task : bootstrapTasks) {
			schedule(task);
		}
	}

	// package access
	protected void schedule(LatchTask<?, ?> task) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				task.execute();
			}
		});
		phaser.arriveAndDeregister();
	}

	@Override
	public boolean shutdown(long timeout, TimeUnit unit) {
		shutdownRequested.set(true);
		Timer timer = new Timer();
		final Thread currentThread = Thread.currentThread();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// shutdown time out
				currentThread.interrupt();
			}
		}, TimeUnit.MILLISECONDS.convert(timeout, unit));
		try {
			// wait till all tasks in graph have been submitted to thread pool
			phaser.awaitAdvanceInterruptibly(0);
			// all tasks have been submitted to thread pool, we can shutdown now
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			// all tasks have finished before time out if we get here
			timer.cancel();
			return true;
		} catch (InterruptedException e) {
			// time out, cancelled by timer
			return false;
		}
	}

}
