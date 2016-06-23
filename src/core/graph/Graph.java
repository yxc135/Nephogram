package core.graph;

import java.util.Collection;
import java.util.HashSet;

import core.task.AbstractTask;

public class Graph<T extends AbstractTask<?, ?>> {

	private Collection<T> tasks;

	public Graph() {
		tasks = new HashSet<>();
	}

	public Graph(Collection<T> tasks) {
		this.tasks = tasks;
	}

	public void add(Collection<T> tasks) {
		if (this.tasks == null) {
			this.tasks = new HashSet<>();
		}
		if (tasks != null) {
			this.tasks.addAll(tasks);
		}
	}

	public Collection<T> getBootstrapTasks() {
		Collection<T> root = new HashSet<>();
		if (tasks != null) {
			for (T task : tasks) {
				if (task.getUpstreamTasks().size() == 0) {
					root.add(task);
				}
			}
		}
		return root;
	}

	public int size() {
		if (tasks == null) {
			return 0;
		} else {
			return tasks.size();
		}
	}

}
