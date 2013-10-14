package com.pearson.statsdclient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.pearson.statsdclient.impl.StatsDClientImpl;

/**
 * Unit test for simple App.
 */
public class ConvertTest extends TestCase
{
	/**
	 * Create the test case
	 * 
	 * @param testName name of the test case
	 */
	public ConvertTest(String testName)
	{
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(ConvertTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testConversions()
	{
		assertEquals("test:1|c\n", StatsDClientImpl.convert(new StatsDMetric("test", StatsDType.COUNTER, 1)));
		assertEquals("test:2|c|@0.1\n", StatsDClientImpl.convert(new StatsDMetric("test", StatsDType.COUNTER, 2, 0.1)));
		assertEquals("test:2|c|@1.01\n",
			StatsDClientImpl.convert(new StatsDMetric("test", StatsDType.COUNTER, 2, 1.01)));
		assertEquals("test:2|g\n", StatsDClientImpl.convert(new StatsDMetric("test", StatsDType.GAUGE, 2)));
		assertEquals("test:3.0|g\n", StatsDClientImpl.convert(new StatsDMetric("test", StatsDType.GAUGE, 3.0)));
		assertEquals("test:123|ms\n", StatsDClientImpl.convert(new StatsDMetric("test", StatsDType.TIMER, 123)));
	}
}
