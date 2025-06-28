package com.rs.cores;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.*;

import com.rs.utils.Logger;

/**
 * The CoresManager is responsbile for initializing thread behaviour.
 *
 * The key things to remember about the game engine are as follows:<br/><br/>
 *
 * The main thread handles game-tick based tasks, such as {@code WorldTask}s and
 * general game actions. Therefore, if performing a scheduled or delayed execution
 * based on game ticks, use the {@code WorldTasksManager.schedule(...)} approach.<br/><br/>
 *
 * The {@code slowExecutor} manages a pool of threads dedicated to running either continuously
 * repeated tasks, or {@link FixedLengthRunnable} objects. In either of these cases, the frequency
 * of the {@code run()} call is not bound to game ticks - it runs for the specified interval with a
 * specified time unit.<br/><br/>
 *
 * The {@code fastExecutor} manages a pool of threads dedicated to running single-execution {@link Runnable}s
 * immediately after they are submitted for exeuction. Like the {@code slowExecutor}, the start delay of the
 * {@code run()} call is not bound to game ticks - it will run as soon as the thread pool supplies
 * a thread to run it.<br/><br/>
 *
 * "then it (the {@code fastExecutor}) shouldn't carry the downfalls of the timer based system. "<br/>
 * <pre>
 *     - Noele, when (indirectly) talking about the inconsistencies
 *       of mixing threading APIs in a fully multi-threaded system.
 * </pre>
 *
 *
 * Exactly! But, with the way we have implemented it, it (the {@code fastExecutor}) doesn't!
 * The underlying thread pool executor objects ({@code slowExecuter} and {@code fastExecutor})
 * are of a different type; one is a subclass of a {@link ScheduledExecutorService} ({@code slowExecuter})
 * and the other is a subclasss of {@link ExecutorService} ({@code fastExecutor}).<br/><br/>
 *
 * The {@code fastExecutor} can only call {@code execute(Runnable r)}, {@code call(Callable c)}, and
 * {@code submit(Runnable r)}, all of which do the same thing: run a {@link Runnable} once and only
 * once, and as soon as a thread is supplied from the thread pool.<br/><br/>
 *
 * The {@code slowExecuter} also has {@code execute(Runnable r)}, but also has things like {@code schedule(...)},
 * {@code scheduleWithFixedDelay(...)}, ... , which allow it to repeat tasks, or start tasks after a delay.<br/><br/>
 *
 * These methods might be common knowledge, but it is important to stress the fundamental reason that
 * both the {@code slowExecuter} and {@code fastExecutor} exist; each has a separate thread pool maintaining
 * them. Unique thread pools mapped to a unique type of threaded service. Good for organization, good for
 * resource management.<br/><br/>
 *
 * Furthermore, with the {@link ServiceProvider}, all of this functionality is wrapped in a class which is
 * responsible for choosing the correct executor service to use.<br/><br/>
 *
 * For those of you TL;DR nerds: we no longer use a {@link Timer} object for the {@code fastExecutor}, as its
 * functionality will be deprecated in Java 9, and quite frankly, because it is ancient history when compared
 * to the {@code Executors} framework. We instead use a custom manager called a {@code ServiceProvider} to
 * use the executor services.<br/><br/>
 *
 * What you used to do with a {@code TimerTask}:<br/>
 *	<pre>
 *     CoresManager.fastExecutor.scheduleAtFixedRate(new TimerTask {
 *     		int someStuff;
 *     		boolean stop;
 *
 *     		public void run() { if(stop) cancel(); }
 *
 *     }, delay, freq);
 *	</pre>
 * What we do with the {@link ServiceProvider}
 * <pre>
 *     CoresManager.getServiceProvider().scheduleFixedLengthTask(new
 *     				FixedLengthRunnable() {
 *         boolean stop;
 *
 *         public boolean repeat() {
 *             // do logic
 *             if(someCondition) stop = true;
 *             else 		 stop = false;
 *             return stop;
 *         }
 *     }, 0, 1, TimeUnit.SECONDS);
 * </pre>
 *
 */
public final class CoresManager {

	public static WorldThread worldThread;
	public static ExecutorService serverWorkerChannelExecutor;
	public static ExecutorService serverBossChannelExecutor;

	@Deprecated
	public static Timer fastExecutor;

	public static ScheduledExecutorService slowExecutor;
	public static ExecutorService fastExecutorV2;

	public static int serverWorkersCount;
	protected static volatile boolean shutdown;

	private static ServiceProvider serviceProvider;

	public static void init() {
		worldThread = new WorldThread();
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		serverWorkersCount = availableProcessors >= 6 ? availableProcessors - (availableProcessors >= 12 ? 7 : 5) : 1;
		serverWorkerChannelExecutor = availableProcessors >= 6 ? Executors.newFixedThreadPool(availableProcessors - (availableProcessors >= 12 ? 7 : 5), new DecoderThreadFactory()) : Executors.newSingleThreadExecutor(new DecoderThreadFactory());
		serverBossChannelExecutor = Executors.newSingleThreadExecutor(new DecoderThreadFactory());
		fastExecutorV2 = new FastThreadPoolExecutor(availableProcessors >= 12 ? 4 : availableProcessors >= 6 ? 2 : 1,
						 new FastThreadFactory(new FastThreadHandler()));
		fastExecutor = new Timer("Fast Executor");
		slowExecutor = new SlowThreadPoolExecutor(availableProcessors >= 12 ? 4 : availableProcessors >= 6 ? 2 : 1,
					   new SlowThreadFactory(new SlowThreadHandler()));
		serviceProvider = new ServiceProvider(false);
		worldThread.start();
		serviceProvider.scheduleAndTrackRepeatingTask(new TrackedRunnable() {
			@Override
			public void run() {
				Logger.log(serviceProvider.log("Service Provider status report"));
				Logger.log("Total requests received since server start: " + serviceProvider.requests);
				Logger.log("Tracked future keys:");
				serviceProvider.trackedFutures.keySet().forEach(Logger::log);
				Logger.log("Total tracked futures: " + serviceProvider.trackedFutures.size());
				Logger.log(serviceProvider.log("Status: Healthy"));
			}
		}, 30, 3600, TimeUnit.SECONDS);
	}

	/**
	 * Schedules a {@link FixedLengthRunnable} task with the slow executor service.
	 * @param r - the runnable to be repeated
	 * @param startDelay - time delay before first execution
	 * @param delayCount - time interval between executions
     * @param unit - the {@link TimeUnit}
	 * @deprecated Use {@link ServiceProvider#scheduleFixedLengthTask(FixedLengthRunnable, long, long, TimeUnit)}
     */
	@Deprecated
	public static void scheduleRepeatedTask(FixedLengthRunnable r, long startDelay, long delayCount, TimeUnit unit) {
		Future<?> f = slowExecutor.scheduleWithFixedDelay(r, startDelay, delayCount, unit);
		r.assignFuture(f);
	}

	/**
	 * Returns the core's {@code ServiceProvider} used for accessing the executor services.
	 * @return the core {@link ServiceProvider}
     */
	public static ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public static void shutdown() {
		serverWorkerChannelExecutor.shutdown();
		serverBossChannelExecutor.shutdown();
		fastExecutor.cancel();
		slowExecutor.shutdown();
		fastExecutorV2.shutdown();
		shutdown = true;
	}

	@Deprecated
	public Timer fastExecutor() {
		return fastExecutor;
	}

	private CoresManager() {

	}

	static void purgeSlowExecutor() {
		((SlowThreadPoolExecutor) slowExecutor).purge();
	}

	/**
	 * Serves as a centralized hub for executor services in the
	 * context of the game engine. New developers should not
	 * have to know which executor to use, but should rather be
	 * able to call wrapper methods with generic names and descriptions,
	 * and let the {@code ServiceProvider} choose the correct
	 * {@link java.util.concurrent.ExecutorService};
	 * @ausky David O'Neill
	 */
	public static class ServiceProvider {

		private Map<String, Future<?>> trackedFutures;
		private int requests = 0;
		private boolean verbose;
		
		private ServiceProvider(boolean verbose) {
			trackedFutures = new ConcurrentHashMap<>();
			this.verbose = verbose;
			Logger.log("ServiceProvider active and waiting for requests.");
		}
		
		/**
		 * Schedules a {@code Runnable} to be executed after the supplied
		 * start delay, and continuously executed thereafter at some
		 * specified frequency. This method should be used when there is
		 * no intention of stopping the task before server shutdown.<br/>
		 * The start delay and repetition frequency
		 * time unit must be supplied.
		 * @param r a {@link Runnable} to repeat
		 * @param startDelay time delay before execution begins
		 * @param delayCount frequency at which the {@code run()} method is called.
		 * @param unit the specified time unit
		 */
		public void scheduleRepeatingTask(Runnable r, long startDelay, long delayCount, TimeUnit unit) {
			CoresManager.slowExecutor.scheduleWithFixedDelay(r, startDelay, delayCount, unit);
			requests++;
		}

		/**
		 * Schedules a {@code Runnable} to be executed after the supplied
		 * start delay, and continuously executed thereafter at some
		 * specified frequency. This method should be used when there is
		 * no intention of stopping the task before server shutdown.<br/>
		 * The start delay and repetition frequency
		 * time unit is assumed to be {@link TimeUnit#SECONDS}.
		 * @param r a {@link Runnable} to repeat
		 * @param startDelay time delay before execution begins
		 * @param delayCount frequency at which the {@code run()} method is called.
		 */
		public void scheduleRepeatingTask(Runnable r, long startDelay, long delayCount) {
			CoresManager.slowExecutor.scheduleWithFixedDelay(r, startDelay, delayCount, TimeUnit.SECONDS);
			requests++;
		}

		/**
		 * Schedules a {@link FixedLengthRunnable} to be executed after the supplied
		 * start delay, and continuously executed thereafter until
		 * {@link FixedLengthRunnable#repeat()} returns false. This method should be used
		 * when there is absolute certainty the task will stop executing based on a future
		 * condition.<br/>
		 * The start delay and repetition frequency
		 * time unit must be supplied.
		 * @param r a {@link FixedLengthRunnable} to repeat
		 * @param startDelay time delay before execution begins
		 * @param delayCount frequency at which the {@code run()} method is called.
		 * @param unit the specified time unit
		 */
		public void scheduleFixedLengthTask(FixedLengthRunnable r, long startDelay, long delayCount, TimeUnit unit) {
			Future<?> f = CoresManager.slowExecutor.scheduleWithFixedDelay(r, startDelay, delayCount, unit);
			r.assignFuture(f);
			requests++;
		}

		/**
		 * Schedules a {@link FixedLengthRunnable} to be executed after the supplied
		 * start delay, and continuously executed thereafter until
		 * {@link FixedLengthRunnable#repeat()} returns false. This method should be used
		 * when there is absolute certainty the task will stop executing based on a future
		 * condition.<br/>
		 * The start delay and repetition frequency
		 * time unit is assumed to be {@link TimeUnit#SECONDS}.
		 * @param r a {@link FixedLengthRunnable} to repeat
		 * @param startDelay time delay before execution begins
		 * @param delayCount frequency at which the {@code run()} method is called.
		 */
		public void scheduleFixedLengthTask(FixedLengthRunnable r, long startDelay, long delayCount) {
			Future<?> f = CoresManager.slowExecutor.scheduleWithFixedDelay(r, startDelay, delayCount, TimeUnit.SECONDS);
			r.assignFuture(f);
			requests++;
		}
		

		/**
		 * Schedules a {@link TrackedRunnable} to be executed after the supplied
		 * start delay, and continuously executes it thereafter at some
		 * specified frequency. Furthermore, the associated {@link Future} is registered 
		 * with the {@code ServiceProvider} via the runnables tracking key. The {@link Future}
		 * can then be accessed with the key at a later time. This method should be used when the task
		 * will not necessarily be cancelled after a fixed iteration period, but may
		 * need to be shutdown at a later, unknown time. In order to retrieve the tracking key,
		 * you must have a reference to the {@link TrackedRunnable}, so using an anonymous first argument
		 * is discouraged.<br/>
		 * If the String key supplied is already registered with the {@code ServiceProvider},
		 * the task will NOT be scheduled!<br/>
		 * The start delay and repetition frequency
		 * time unit must be supplied.
		 * @param r a {@link Runnable} to repeat
		 * @param startDelay time delay before execution begins
		 * @param delayCount frequency at which the {@code run()} method is called.
		 * @param unit the specified time unit
		 */
		public void scheduleAndTrackRepeatingTask(TrackedRunnable r, long startDelay, long delayCount, TimeUnit unit) {
			if (trackedFutures.containsKey(r.getTrackingKey())) {
				System.err.println(log("Attempted to add Future to tracking map, but duplicate key was found. Aborting."));
				return;
			}
			Future<?> future = CoresManager.slowExecutor.scheduleWithFixedDelay(r, startDelay, delayCount, unit);
			trackedFutures.put(r.getTrackingKey(), future);
			if(verbose)
				Logger.log(log("Tracking new future with key: " + r.getTrackingKey()));
			requests++;
		}
		
		/**
		 * Attempts to retrieve a {@link Future} mapped to the supplied key. If the {@link Future}
		 * is present in the {@code ServiceProvider} mapping, it will be cancelled and purged
		 * from the executor pool.
		 * @param key the String key (acquired via {@link TrackedRunnable#getTrackingKey()}
		 *            to lookup a mapped {@link Future}
		 * @param interrupt whether or not the executor service should stop the current
		 * 					execution of the {@link Future}'s associated {@link Runnable}
		 * 					if an execution is in progress.
		 */
		public void cancelTrackedTask(String key, boolean interrupt) {
			Future<?> future = trackedFutures.remove(key);
			if (future != null) {
				future.cancel(interrupt);
				CoresManager.purgeSlowExecutor();
				if(verbose)
					Logger.log(log("Cancelled future with key: " + key));
			}
		}

		/**
		 * Schedules a {@code Runnable} for a one-time execution, but only after a
		 * specified start delay. THe start delay time unit must be supplied.
		 * @param r a {@link Runnable} to execute once
		 * @param startDelay time delay before execution begins
		 * @param unit the specified time unit
		 */
		public void executeWithDelay(Runnable r, long startDelay, TimeUnit unit) {
			CoresManager.slowExecutor.schedule(r, startDelay, unit);
			requests++;
		}

		/**
		 * Immediately (as soon as a thread from the thread pool is provided) performs
		 * a one-time exeuction of a supplied {@code Runnable}.
		 * @param r a {@link Runnable} to execute once
		 */
		public void executeNow(Runnable r) {
			CoresManager.fastExecutorV2.execute(r);
			requests++;
		}

		private String log(String message) {
			String prefix = "[Service Provider] => ";
			return prefix + message;
		}

	}
}