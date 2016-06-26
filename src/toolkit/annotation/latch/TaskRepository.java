package toolkit.annotation.latch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import core.schedule.latch.LatchTask;

public class TaskRepository {

	private final Map<String, LatchTask<?, ?>> repository = new HashMap<>();

	public LatchTask<?, ?> getTask(String id) {
		return repository.get(id);
	}

	public Collection<LatchTask<?, ?>> getAllTasks() {
		return repository.values();
	}

	public void addTask(LatchTask<?, ?> task) {
		repository.put(task.getId(), task);
	}

	public void addTasks(Collection<? extends LatchTask<?, ?>> tasks) {
		for (LatchTask<?, ?> task : tasks) {
			addTask(task);
		}
	}

}
