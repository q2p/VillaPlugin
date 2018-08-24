package q2p.quickclick.base.irresponsive;

import q2p.quickclick.base.queue.*;

final class IrresponsiveWorker implements Runnable {
	private final IrresponsiveExecutor owner;
	private final QueueElement<IrresponsiveWorker> queueElement = QueueElement.create(this);
	QueueElement<IrresponsiveWorker> getQueueElement() {
		return queueElement;
	}

	IrresponsiveWorker(final IrresponsiveExecutor owner) {
		assert owner != null;

		final Thread thread = new Thread(this);
		thread.setName("Irresponsive Worker");
		thread.start();

		this.owner = owner;
	}

	public void run() {
		IrresponsiveTask task;
		try {
			while(true) {
				synchronized(owner.getLock()) {
					while(true) {
						if(owner.removeFromQueueIfNecessary(queueElement))
							return;

						task = owner.pullTask();
						if(task != null)
							break;

						owner.getLock().wait();
					}
				}

				task.process();

				// Garbage Collection
				task = null;
			}
		} catch(final InterruptedException ignore) {
			assert false;
		}
	}
}