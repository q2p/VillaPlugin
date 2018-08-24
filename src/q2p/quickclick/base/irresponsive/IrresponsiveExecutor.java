package q2p.quickclick.base.irresponsive;

import q2p.quickclick.base.queue.*;

// TODO: в данном классе потоки каждый раз реаллоцирются, нужно создать класс, где потоки не будут удаляться для увеличения производительности
public final class IrresponsiveExecutor {
	public static final IrresponsiveExecutor defaultExecutor = new IrresponsiveExecutor(0);

	private static void initializeDefaultExecutor(final int desiredAmountOfThreads) {
		defaultExecutor.setDesiredAmountOfThreads(desiredAmountOfThreads);
	}

	private int desiredAmountOfThreads = 0;
	
	public IrresponsiveExecutor(final int desiredAmountOfThreads) {
		setDesiredAmountOfThreads(desiredAmountOfThreads);
	}
	
	private final Object activityLock = new Object();
	Object getLock() {
		return activityLock;
	}

	public int getDesiredAmpuntOfThreads() {
		synchronized(activityLock) {
			return desiredAmountOfThreads;
		}
	}

	public int getCurrentAmountOfThreads() {
		synchronized(activityLock) {
			return workersAmount;
		}
	}

	public void setDesiredAmountOfThreads(final int threads) {
		assert threads >= 0;
		
		synchronized(activityLock) {
			desiredAmountOfThreads = threads;
			
			if(workersAmount > desiredAmountOfThreads) {
				activityLock.notifyAll();
			} else {
				while(workersAmount != desiredAmountOfThreads) {
					workers.insertLast(new IrresponsiveWorker(this).getQueueElement());
					workersAmount++;
				}
			}
		}
	}

	private int workersAmount = 0;
	private final Queue<IrresponsiveWorker> workers = Queue.create();

	/** @apiNote Данный метод не ожидает очищения списка задач. Потоки могут завершиться, а задачи остаться в очереди. */
	public void waitForThreadsToFinish() throws InterruptedException { // TODO: использовать
		synchronized(activityLock) {
			while(!workers.isEmpty())
				activityLock.wait();
		}
	}

	private final Queue<IrresponsiveTask> tasks = Queue.create();
	public IrresponsiveTask pushTask(final Runnable task) {
		synchronized(activityLock) {
			final IrresponsiveTask iTask = new IrresponsiveTask(this, task);
			tasks.insertLast(iTask.getQueueElement());
			activityLock.notify();
			return iTask;
		}
	}
	IrresponsiveTask pullTask() {
		final QueueElement<IrresponsiveTask> element = tasks.removeFirst();
		return element == null ? null : element.getValue();
	}

	/** @implNote Синхронизация должна происходить запрашивателем */
	boolean removeFromQueueIfNecessary(final QueueElement<IrresponsiveWorker> queueElement) {
		if(workersAmount > desiredAmountOfThreads) {
			assert queueElement.isQueued();
			queueElement.remove();
			activityLock.notify();

			return true;
		}
		return false;
	}
}