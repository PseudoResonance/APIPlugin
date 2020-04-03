package com.github.pseudoresonance.resonantbot.apiplugin;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class Expirable {

	private final long creation;
	private final long expiry;
	
	public Expirable(long expiry, TimeUnit unit) {
		this.creation = System.currentTimeMillis();
		this.expiry = creation + TimeUnit.MILLISECONDS.convert(expiry, unit);
	}
	
	public Expirable(long expiry) {
		this.creation = System.currentTimeMillis();
		this.expiry = creation + expiry;
	}
	
	public long getCreationTime() {
		return creation;
	}
	
	public Date getCreationDate() {
		return new Date(creation);
	}
	
	public boolean isExpired() {
		return System.currentTimeMillis() >= expiry;
	}
	
}
