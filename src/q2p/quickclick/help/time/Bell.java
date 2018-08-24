package q2p.quickclick.help.time;

public final class Bell {
	private boolean haveNotification = false;
	
	private final Object lock = new Object();
	
	public void putNotification() {
		synchronized(lock) {
			if(!haveNotification) {
				haveNotification = true;
				lock.notifyAll();
			}
		}
	}
	
	public boolean haveNotification() {
		synchronized(lock) {
			return haveNotification;
		}
	}
	
	public void releaseNotification() throws InterruptedException {
		synchronized(lock) {
			if(!haveNotification)
				lock.wait();
			haveNotification = false;
		}
	}
}