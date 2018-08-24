package q2p.quickclick.base.irresponsive;

import q2p.quickclick.base.queue.*;

public final class IrresponsiveTask {
	private final IrresponsiveExecutor executor;

	private final Object lock = new Object();

	private final QueueElement<IrresponsiveTask> queueElement = QueueElement.create(this);
	QueueElement<IrresponsiveTask> getQueueElement() {
		return queueElement;
	}

	enum State {
		Idle,
		Executed,
		Canceled
	}
	private State state = State.Idle;
	
	private Runnable task;
	
	IrresponsiveTask(final IrresponsiveExecutor executor, final Runnable task) {
		this.executor = executor;
		this.task = task;
	}
	
	void process() {
		synchronized(lock) {
			if(state != State.Idle)
				return;
			
			state = State.Executed;
		}

		task.run();

		// Garbage Collection
		task = null;

		// Сброс прерывания
		Thread.currentThread().isInterrupted();
	}

	/**
	 * Пытается отменить исполнение задачи
	 * @return {@code true} если задача была убрана из очереди, {@code false} если задача находится в процессе исполнения или уже завершила исполнение.
	 */
	public boolean tryToPreventExecution() {
		synchronized(lock) {
			switch(state) {
				case Idle:
					state = State.Canceled;
					synchronized(executor.getLock()) {
						if(queueElement.isQueued())
							queueElement.remove();
					}
				case Canceled:
					return true;
				default:
					return false;
			}
		}
	}
}