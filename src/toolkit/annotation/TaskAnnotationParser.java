package toolkit.annotation;

import java.lang.reflect.Field;

import toolkit.annotation.exception.AnnotationParseException;
import toolkit.annotation.exception.UndefinedTaskException;
import core.task.AbstractTask;

public abstract class TaskAnnotationParser<T extends AbstractTask<?, ?>> {

	public abstract void addDependency(T upstreamTask, T downstreamTask);

	public void parse(TaskRepository<T> repository)
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

}
