package com.pearson.statsdclient;

@SuppressWarnings("nls")
public enum StatsDType
{
	TIMER("ms"), // Times the method execution
	COUNTER("c"), // Simple counter increment
	GAUGE("g"); // Send a fixed value

	private final String statType;
	
	StatsDType(String statType)
	{
		this.statType = statType;
	}

	public String getShortString()
	{
		return statType;
	}
}
