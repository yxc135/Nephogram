package core.task;

import java.util.Collection;

public abstract class AbstractTask<I, O> {

	public enum State {
		New, Executing, Done, Failed
	}

	public enum Type {
		Common, Critical
	}

	protected final String id;
	protected final I input;
	protected O output;
	protected Throwable error;
	protected State state;
	protected final Type type;

	// use locks in small granularity (CAUTION - dead lock : pay attention to
	// lock order when collaborating with other locks)
	protected final Object stateRelatedLock;

	public AbstractTask(String id, I input, Type type) {
		this.id = id;
		this.input = input;
		output = null;
		error = null;
		state = State.New;
		this.type = type;
		stateRelatedLock = new Object();
	}

	public final void execute() {
		synchronized (stateRelatedLock) {
			// another thread may have failed this task
			if (state == State.New) {
				state = State.Executing;
				notifyDownstream();
			} else {
				return;
			}
		}
		try {
			output = executeTask();
			synchronized (stateRelatedLock) {
				// another thread may have failed this task
				if (state == State.Executing) {
					state = State.Done;
					notifyDownstream();
				}
			}
		} catch (Throwable t) {
			synchronized (stateRelatedLock) {
				error = t;
				state = State.Failed;
				notifyDownstream();
			}
		}
	}

	public abstract O executeTask() throws Throwable;

	public final String getId() {
		return id;
	}

	public final O getOutput() {
		synchronized (stateRelatedLock) {
			return output;
		}
	}

	public final Throwable getError() {
		synchronized (stateRelatedLock) {
			return error;
		}
	}

	public final State getState() {
		synchronized (stateRelatedLock) {
			return state;
		}
	}

	public final Type getType() {
		return type;
	}

	// this is called by another thread to explicitly fail this task
	public boolean fail(Exception reason) {
		synchronized (stateRelatedLock) {
			if (state == State.New || state == State.Executing) {
				error = reason;
				state = State.Failed;
				notifyDownstream();
				return true;
			} else {
				return false;
			}
		}
	}

	public abstract void onUptreamStateChange(AbstractTask<?, ?> task);

	public abstract Collection<? extends AbstractTask<?, ?>> getUpstreamTasks();

	public abstract Collection<? extends AbstractTask<?, ?>> getDownstreamTasks();

	private void notifyDownstream() {
		for (AbstractTask<?, ?> task : getDownstreamTasks()) {
			task.onUptreamStateChange(this);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Task - ").append(id).append(" [\n");
		sb.append("Type ").append(type).append("\n");
		sb.append("State ").append(state).append("\n");
		sb.append("Upstream [ ");
		for (AbstractTask<?, ?> task : getUpstreamTasks()) {
			sb.append(task.getId()).append(" ");
		}
		sb.append("]\n");
		sb.append("Downstream [ ");
		for (AbstractTask<?, ?> task : getDownstreamTasks()) {
			sb.append(task.getId()).append(" ");
		}
		sb.append("]\n");
		sb.append("]");
		return sb.toString();
	}

}
