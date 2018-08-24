package q2p.quickclick;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import q2p.quickclick.client.ClientPool;

public class LogicTick implements Runnable {
	private long last = 0;
	private long tps = 0;
	private boolean needBenchmark = false;
	public void run() {
		if(needBenchmark) {
			final long currentTime = System.currentTimeMillis();
			while(currentTime-last > 1000) {
				last += 1000;
				ClientPool.sendMessageForEachLoggedAdmin("Ticks per second: "+tps);
				tps = 0;
			}
			tps++;
		}
		WheatherEnvironment.tick();
	}
	
	public final boolean benchmarkCommand(final Command command, final CommandSender sender) {
		if(command.getName().equals("benchmark") && ClientPool.isLoggedAdminOrConsole(sender)) {
			needBenchmark = !needBenchmark;
			tps = 0;
			last = System.currentTimeMillis();
			ClientPool.sendMessageForEachLoggedAdmin("Benchmark turned "+(needBenchmark?"on":"off")+".");
			return true;
		}
		return false;
	}
}