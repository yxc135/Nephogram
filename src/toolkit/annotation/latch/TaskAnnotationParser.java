package toolkit.annotation.latch;

import java.lang.reflect.Field;

import toolkit.annotation.Task;
import toolkit.annotation.exception.AnnotationParseException;
import toolkit.annotation.exception.UndefinedTaskException;
import core.schedule.latch.LatchTask;

public class TaskAnnotationParser {

	public void parse(TaskRepository repository)
			throws AnnotationParseException {
		for (LatchTask<?, ?> task : repository.getAllTasks()) {
			parse(task, repository);
		}
	}

	private void parse(LatchTask<?, ?> task, TaskRepository repository)
			throws AnnotationParseException {
		Class<?> clazz = task.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Task.class)) {
				Task taskAnnotation = field.getAnnotation(Task.class);
				String taskId = taskAnnotation.id();
				LatchTask<?, ?> upstreamTask = repository.getTask(taskId);
				if (upstreamTask == null) {
					throw new AnnotationParseException(
							new UndefinedTaskException());
				}
				upstreamTask.addToDownstream(task);
				task.addToUpstream(upstreamTask);
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
