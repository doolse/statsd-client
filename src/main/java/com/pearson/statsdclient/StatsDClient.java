package com.pearson.statsdclient;

public interface StatsDClient
{
	void increment(String key);

	void count(String key, int delta);

	void count(String key, Object delta, double rate);

	void gauge(String key, double value);

	void gauge(String key, long value);

	void time(String key, int timeInMs);
}