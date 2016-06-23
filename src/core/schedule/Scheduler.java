package core.schedule;

import java.util.concurrent.TimeUnit;

import core.graph.Graph;
import core.schedule.exception.ScheduleFailedException;
import core.task.AbstractTask;

public interface Scheduler<T extends AbstractTask<?, ?>> {

	void schedule(Graph<T> graph) throws ScheduleFailedException;

	boolean shutdown(long timeout, TimeUnit unit);

}
