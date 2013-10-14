package com.pearson.statsdclient;


public class StatsDMetric
{
	private final String key;
	private final StatsDType type;
	private final Object value;
	private final Double rate;

	public StatsDMetric(String key, StatsDType type, Object value)
	{
		this(key, type, value, null);
	}

	public StatsDMetric(String key, StatsDType type, Object value, Double rate)
	{
		this.key = key;
		this.type = type;
		this.value = value;
		this.rate = rate;
	}

	public String getKey()
	{
		return key;
	}

	public StatsDType getType()
	{
		return type;
	}

	public Object getValue()
	{
		return value;
	}

	public Double getRate()
	{
		return rate;
	}

}
