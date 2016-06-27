package toolkit.annotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import core.task.AbstractTask;

public class TaskRepository<T extends AbstractTask<?, ?>> {

	private final Map<String, T> repository = new HashMap<>();

	public T get(String id) {
		return repository.get(id);
	}

	public Collection<T> getAll() {
		return repository.values();
	}

	public void add(T task) {
		repository.put(task.getId(), task);
	}

	public void addAll(Collection<T> tasks) {
		for (T task : tasks) {
			add(task);
		}
	}

}
