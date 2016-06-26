package core.schedule.latch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import core.task.AbstractTask;
import core.task.exception.UpstreamFailedException;

public abstract class LatchTask<I, O> extends AbstractTask<I, O> {

	private final LatchScheduler scheduler;
	// concurrent hash set
	protected Collection<LatchTask<?, ?>> upstreamTasks = ConcurrentHashMap
			.newKeySet();
	// concurrent hash set
	protected Collection<LatchTask<?, ?>> downstreamTasks = ConcurrentHashMap
			.newKeySet();
	private int numberOfUpstreamDone;

	private final Object latchRelatedLock;

	public LatchTask(LatchScheduler scheduler, String id, I input) {
		this(scheduler, id, input, Type.Critical);
	}

	public LatchTask(LatchScheduler scheduler, String id, I input, Type type) {
		this(scheduler, id, input, type, new HashSet<>(), new HashSet<>());
	}

	public LatchTask(LatchScheduler scheduler, String id, I input, Type type,
			Collection<LatchTask<?, ?>> upstreamTasks,
			Collection<LatchTask<?, ?>> downstreamTasks) {
		super(id, input, type);
		this.scheduler = scheduler;
		if (upstreamTasks != null) {
			this.upstreamTasks.addAll(upstreamTasks);
		}
		if (downstreamTasks != null) {
			this.downstreamTasks.addAll(downstreamTasks);
		}
		numberOfUpstreamDone = 0;
		latchRelatedLock = new Object();
	}

	@Override
	public abstract O executeTask() throws Throwable;

	public void addToUpstream(Collection<LatchTask<?, ?>> tasks) {
		if (tasks != null) {
			upstreamTasks.addAll(tasks);
		}
	}

	public void addToDownstream(Collection<LatchTask<?, ?>> tasks) {
		if (tasks != null) {
			downstreamTasks.addAll(tasks);
		}
	}

	@Override
	public void onUptreamStateChange(AbstractTask<?, ?> task) {
		switch (task.getState()) {
		case Done:
			updateLatch(task);
			break;
		case Failed:
			if (task.getType() == Type.Critical) {
				fail(new UpstreamFailedException());
				// make scheduler aware of this
				scheduler.schedule(this);
			} else {
				updateLatch(task);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public Collection<LatchTask<?, ?>> getUpstreamTasks() {
		return Collections.unmodifiableCollection(upstreamTasks);
	}

	@Override
	public Collection<LatchTask<?, ?>> getDownstreamTasks() {
		return Collections.unmodifiableCollection(downstreamTasks);
	}

	private void updateLatch(AbstractTask<?, ?> upstreamTask) {
		synchronized (latchRelatedLock) {
			++numberOfUpstreamDone;
			if (upstreamTasks != null
					&& upstreamTasks.size() == numberOfUpstreamDone) {
				scheduler.schedule(this);
			}
		}
	}

}
