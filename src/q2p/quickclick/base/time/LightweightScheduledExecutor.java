package q2p.quickclick.base.time;

/**
 * Исполнитель, позволяющий выполнять задачи в указанное Unix время.<br>
 * <b>Примечание</b>: Исполнитель опирается на системное время и поэтому чуствителен к скачкам системного времени.<br>
 * <b>Примечание</b>: Исполнитель не оптимизирован на долгие по времени выполнения задачи.
 */
public final class LightweightScheduledExecutor implements Runnable {
	private static final byte STATE_IDLE = 0;
	private static final byte STATE_STARTED = 1;
	private static final byte STATE_ENDED = 2;

	private static final LightweightScheduledExecutor instance = new LightweightScheduledExecutor();
	private static final Thread thread = new Thread(instance);
	static {
		thread.setName("LightweightScheduledExecutor");
		thread.start();
	}

	private static ScheduledTask firstTask = null;

	private static boolean running = true;

	public void run() {
		ScheduledTask toRun;
		try {
			while(true) {
				synchronized(instance) {
					while(true) {
						if(firstTask == null) {
							if(!running)
								return;
							instance.wait();
						} else {
							final long dist = firstTask.unixTime - System.currentTimeMillis();
							if(dist > 0)
								instance.wait(dist);
							else
								break;
						}
					}

					toRun = firstTask;
					toRun.state = STATE_STARTED;
					firstTask = toRun.next;
					if(firstTask != null)
						firstTask.previous = null;
				}
				toRun.next = null;
				toRun.task.run();
				synchronized(instance) {
					toRun.state = STATE_ENDED;
				}
			}
		} catch(InterruptedException ignore) {}
	}

	/**
	 * Добавляет задачу в очередь.
	 *
	 * @param task задача для выполнения.
	 * @param unixTime Unix-время в которое будет выполнена задача. (Возможны небольшие задержки)
	 * @return {@link ScheduledTask ScheduledTask} позволяющий контролировать выполнение задачи.
	 */
	public static ScheduledTask schedule(final Runnable task, final long unixTime) {
		synchronized(instance) {
			if(firstTask == null) {
				firstTask = new ScheduledTask(task, unixTime, null, null);

				instance.notify();
				return firstTask;
			}

			if(unixTime < firstTask.unixTime) {
				ScheduledTask inserted = new ScheduledTask(task, unixTime, null, firstTask);
				firstTask.previous = inserted;
				firstTask = inserted;

				instance.notify();
				return firstTask;
			}

			ScheduledTask currentTask = firstTask;
			while(currentTask.next != null && currentTask.next.unixTime < unixTime)
				currentTask = currentTask.next;

			ScheduledTask inserted = new ScheduledTask(task, unixTime, currentTask, currentTask.next);

			if(currentTask.next != null)
				currentTask.next.previous = inserted;

			currentTask.next = inserted;

			return inserted;
		}
	}

	/**
	 * Начинает завершение выполнения исполнителя.<br>
	 * <b>Примечание</b>: Данный метод должен вызываться только при завершении приложения.
	 */
	public static void destroy() {
		synchronized(instance) {
			assert running;
			running = false;
			instance.notify();
		}
	}

	/**
	 * Ожидает завершения исполнителя.<br>
	 * <b>Примечание</b>: Данный метод должен вызываться только при завершении приложения.
	 */
	public static void join() {
		while(true) {
			try {
				thread.join();
				break;
			} catch(InterruptedException ignore) {}
		}
	}

	public static final class ScheduledTask {
		private byte state = STATE_IDLE;

		private final Runnable task;
		private final long unixTime;
		/** @return Unix время в которое задача будет выполнена. */
		public long getUnixTime() {
			return unixTime;
		}

		private ScheduledTask previous;
		private ScheduledTask next;

		private ScheduledTask(final Runnable task, final long unixTime, ScheduledTask previous, ScheduledTask next) {
			this.task = task;
			this.unixTime = unixTime;
			this.previous = previous;
			this.next = next;
		}

		/** @return {@code true} если в будущем задача будет выполнена исполнителем, {@code false} иначе. */
		public boolean willBeExecuted() {
			synchronized(instance) {
				return state != STATE_ENDED;
			}
		}
		/** @return {@code true} если задача была отменена до выполнения, {@code false} если задача была отменена или выполнена ранее. */
		public boolean cancel() {
			synchronized(instance) {
				if(state == STATE_IDLE) {
					state = STATE_ENDED;

					if(previous == null) {
						firstTask = next;
					} else {
						previous.next = next;
					}

					if(next != null) {
						next.previous = previous;
						next = null;
					}

					previous = null;
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private LightweightScheduledExecutor() {}
}