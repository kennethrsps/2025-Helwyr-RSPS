package com.rs.cores;

/**
 * The thread for the minigames (FOG).
 * 
 * @ausky _Waterfiend <skype:alco-wahidi>
 *
 * Created in Apr 30, 2017 at 8:50:50 PM.
 */
public class MinigameThread extends Thread {

	/**
	 * The number of ticks.
	 */
	private int tick = 0;

	/**
	 * The minigame thread with a minimum priority.
	 */
	protected MinigameThread() {
		setPriority(Thread.MIN_PRIORITY);
		setName("Minigames Thread");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		/*while (!CoresManager.shutdown) {
			try {
				tick++;
				if (tick >= 10) {
					FOGManager.get().getFOGInstance().process();
					tick = 0;
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}*/
	}
}
