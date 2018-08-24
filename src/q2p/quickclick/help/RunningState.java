package q2p.quickclick.help;

public enum RunningState {
	STARTING(true,  false),
	RUNNING (true,  true),
	STOPPING(false, true),
	DEAD    (false, false);
	
	public final boolean desireToWork;
	public final boolean working;
	
	RunningState(final boolean desireToWork, final boolean working) {
		this.desireToWork = desireToWork;
		this.working = working;
	}
	
	public static RunningState byState(final boolean desireToWork, final boolean working) {
		if(desireToWork) {
			if(working) return RUNNING;
			else        return STARTING;
		} else {
			if(working) return STOPPING;
			else        return DEAD;
		}
	}
}