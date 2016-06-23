package core.task;

import core.task.exception.MaxRetryException;

public abstract class RetryTask<I, O> extends AbstractTask<I, O> {

	public static final int DEFAULT_RETRY_NUMBER = 3;
	public static final long DEFAULT_RETRY_INTERVAL = 1000;

	private final int retryNumber;
	private final long retryInterval;
	private final RetryListener retryListener;

	public RetryTask(String id, I input, Type type) {
		this(id, input, type, DEFAULT_RETRY_NUMBER, DEFAULT_RETRY_INTERVAL,
				null);
	}

	public RetryTask(String id, I input, Type type, int retryNumber,
			long retryInterval, RetryListener retryListener) {
		super(id, input, type);
		this.retryNumber = retryNumber;
		this.retryInterval = retryInterval;
		this.retryListener = retryListener;
	}

	@Override
	public final O executeTask() throws Throwable {
		int currentRetryNumber = 0;
		try {
			while (currentRetryNumber < retryNumber) {
				return executeTaskOnce();
			}
		} catch (Throwable t) {
			if (retryListener != null) {
				retryListener.onRetry(currentRetryNumber, t);
			}
			++currentRetryNumber;
			if (retryInterval > 0) {
				Thread.sleep(retryInterval);
			}
		}
		throw new MaxRetryException();
	}

	public abstract O executeTaskOnce() throws Throwable;

	public static interface RetryListener {
		void onRetry(int currentRetryNumber, Throwable error);
	}

}
