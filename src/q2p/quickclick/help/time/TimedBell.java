package q2p.quickclick.help.time;

public final class TimedBell {
	private static final int maxSleepTime = 5000;
	
	private static final byte lackNotification = -2;
	private static final byte haveNotification = -1;
	private long notificationTime = lackNotification;
	
	private final Object lock = new Object();
	
	public void putNotification() {
		synchronized(lock) {
			if(notificationTime != haveNotification) {
				notificationTime = haveNotification;
				lock.notifyAll();
			}
		}
	}
	
	public void putNotification(final long GMT) {
		synchronized(lock) {
			if(notificationTime == lackNotification || GMT < notificationTime) {
				notificationTime = GMT;
				if(System.currentTimeMillis() >= notificationTime) {
					notificationTime = haveNotification;
					lock.notifyAll();
				}
			}
		}
	}
	
	public boolean haveNotification() {
		synchronized(lock) {
			if(notificationTime == haveNotification)
				return true;
			
			if(notificationTime == lackNotification)
				return false;
			
			if(System.currentTimeMillis() >= notificationTime) {
				notificationTime = haveNotification;
				lock.notifyAll();
				return true;
			}
			
			return false;
		}
	}
	
	public void releaseNotification() throws InterruptedException {
		synchronized(lock) {
			if(notificationTime == haveNotification) {
				notificationTime = lackNotification;
				return;
			}
			
			if(notificationTime == lackNotification) {
				lock.wait();
				if(notificationTime == haveNotification) {
					notificationTime = lackNotification;
					return;
				}
			}
			
			while(true) {
				final long cTime = System.currentTimeMillis();
				if(cTime >= notificationTime) {
					notificationTime = lackNotification;
					return;
				}
				lock.wait(Math.min(notificationTime - cTime, maxSleepTime));
				if(notificationTime == haveNotification) {
					notificationTime = lackNotification;
					return;
				}
			}
		}
	}
}