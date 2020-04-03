package com.github.pseudoresonance.resonantbot.apiplugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RateLimiter {

	private Semaphore semaphore;
	private int maxPermits;
	private long timePeriod;
	private TimeUnit timeUnit;
	private ScheduledExecutorService scheduler;

	public static RateLimiter create(int permits, long timePeriod, TimeUnit timeUnit) {
		RateLimiter limiter = new RateLimiter(permits, timePeriod, timeUnit);
		limiter.schedulePermitReplenishment();
		return limiter;

	}

	private RateLimiter(int permits, long timePeriod, TimeUnit timeUnit) {
		this.semaphore = new Semaphore(permits);
		this.maxPermits = permits;
		this.timePeriod = timePeriod;
		this.timeUnit = timeUnit;
	}
	
	public void acquire() throws InterruptedException {
		semaphore.acquire();
	}
	
	public void acquire(long timePeriod, TimeUnit timeUnit) throws InterruptedException {
		if (semaphore.tryAcquire(timePeriod, timeUnit))
			return;
		else
			throw new RequestTimeoutException();
	}

	public boolean tryAcquire() {
		return semaphore.tryAcquire();
	}
	
	public boolean tryAcquire(long timePeriod, TimeUnit timeUnit) throws InterruptedException {
		return semaphore.tryAcquire(timePeriod, timeUnit);
	}

	public void stop() {
		scheduler.shutdownNow();
	}

	public void schedulePermitReplenishment() {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(() -> {
			semaphore.release(maxPermits - semaphore.availablePermits());
		}, timePeriod, timeUnit);
	}
	
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}
	
	public long getTimePeriod() {
		return this.timePeriod;
	}
	
	public int getMaxPermits() {
		return this.maxPermits;
	}

}