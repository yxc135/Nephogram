package toolkit.annotation;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import toolkit.annotation.exception.AnnotationParseException;
import toolkit.annotation.exception.UndefinedTaskException;
import core.graph.Graph;
import core.task.AbstractTask;

public abstract class TaskAnnotationParser<T extends AbstractTask<?, ?>> {

	public abstract void addDependency(T upstreamTask, T downstreamTask);

	public Graph<T> parse(Collection<T> annotatedTasks)
			throws AnnotationParseException {
		TaskRepository<T> repository = new TaskRepository<>();
		for (T task : annotatedTasks) {
			repository.add(task);
		}
		parse(repository);
		Graph<T> graph = new Graph<>();
		graph.add(repository.getAll());
		return graph;
	}

	private void parse(TaskRepository<T> repository)
			throws AnnotationParseException {
		for (T task : repository.getAll()) {
			parse(task, repository);
		}
	}

	private void parse(T task, TaskRepository<T> repository)
			throws AnnotationParseException {
		Class<?> clazz = task.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Task.class)) {
				Task taskAnnotation = field.getAnnotation(Task.class);
				String taskId = taskAnnotation.id();
				T upstreamTask = repository.get(taskId);
				if (upstreamTask == null) {
					throw new AnnotationParseException(
							new UndefinedTaskException());
				}
				addDependency(upstreamTask, task);
				boolean accessible = field.isAccessible();
				field.setAccessible(true);
				try {
					field.set(task, upstreamTask);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new AnnotationParseException(e);
				} finally {
					field.setAccessible(accessible);
				}
			}
		}
	}

	private static class TaskRepository<T extends AbstractTask<?, ?>> {

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

		@SuppressWarnings("unused")
		public void addAll(Collection<T> tasks) {
			for (T task : tasks) {
				add(task);
			}
		}

	}

}
