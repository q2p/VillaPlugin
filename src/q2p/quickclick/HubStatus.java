package q2p.quickclick;

public class HubStatus {
	static LogicTick logicTick = null;
	private static int logicTickId = -1;
		
	static void initilize() {
		Log.initLog(QuickClick.getPluginInstance().getLogger());
				
		logicTick = new LogicTick();
    logicTickId = QuickClick.getPluginInstance().getServer().getScheduler().scheduleSyncRepeatingTask(QuickClick.getPluginInstance(), logicTick, 0, 1);
	}
	
	static void deinitilize() {
		QuickClick.getPluginInstance().getServer().getScheduler().cancelTask(logicTickId);
		logicTickId = -1;
		logicTick = null;
	}
}
