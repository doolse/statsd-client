package com.pearson.statsdclient.impl;

import com.pearson.statsdclient.StatsDClient;

public class NoopStatsDClient implements StatsDClient
{

	@Override
	public void increment(String key)
	{
		// noop
	}

	@Override
	public void count(String key, int delta)
	{
		// noop
	}

	@Override
	public void count(String key, Object delta, double rate)
	{
		// noop
	}

	@Override
	public void gauge(String key, double value)
	{
		// noop
	}

	@Override
	public void gauge(String key, long value)
	{
		// noop
	}

	@Override
	public void time(String key, int timeInMs)
	{
		// noop
	}

}
