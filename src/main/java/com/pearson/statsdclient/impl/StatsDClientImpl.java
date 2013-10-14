package com.pearson.statsdclient.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pearson.statsdclient.StatsDClient;
import com.pearson.statsdclient.StatsDMetric;
import com.pearson.statsdclient.StatsDType;

@SuppressWarnings("nls")
public class StatsDClientImpl implements StatsDClient
{
	private static final String RATE_FORMAT = "|@0.0####";

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private Logger LOGGER = LoggerFactory.getLogger(StatsDClientImpl.class);
	private BlockingQueue<StatsDMetric> queue = new LinkedBlockingQueue<StatsDMetric>();
	private DatagramChannel channel;

	public StatsDClientImpl(String host, int port)
	{
		this(host, port, Executors.newSingleThreadExecutor(new ThreadFactory()
		{
			private ThreadFactory delegate = Executors.defaultThreadFactory();

			@Override
			public Thread newThread(Runnable r)
			{
				Thread thread = delegate.newThread(r);
				thread.setName("Statsd flusher");
				return thread;
			}
		}));
	}

	public StatsDClientImpl(String hostname, int port, ExecutorService service)
	{
		try
		{
			channel = DatagramChannel.open();
			channel.connect(new InetSocketAddress(hostname, port));
		}
		catch( Exception e )
		{
			throw new RuntimeException("Failed to start StatSD client", e);
		}
		service.submit(new Runnable()
		{

			@Override
			public void run()
			{
				DecimalFormat format = new DecimalFormat(RATE_FORMAT);
				ByteBuffer buffer = ByteBuffer.allocate(1400);
				while( true )
				{
					try
					{
						StatsDMetric metric = queue.poll();
						if( metric == null )
						{
							if( buffer.position() > 0 )
							{
								flush(buffer);
							}
							metric = queue.take();
						}
						byte[] bytes = convert(format, metric).getBytes(UTF_8);
						if( buffer.remaining() < bytes.length )
						{
							flush(buffer);
						}
						buffer.put(bytes);
					}
					catch( InterruptedException e )
					{
						// nothing
					}
				}
			}

			private void flush(ByteBuffer buffer)
			{
				buffer.flip();
				try
				{
					channel.write(buffer);
				}
				catch( IOException e )
				{
					LOGGER.warn("Failed to send statsd packet", e);
				}
				buffer.clear();
			}

		});
	}

	public static String convert(StatsDMetric metric)
	{
		return convert(new DecimalFormat(RATE_FORMAT), metric);
	}

	static String convert(DecimalFormat format, StatsDMetric metric)
	{
		String rateString = "";
		Double rate = metric.getRate();
		if( rate != null )
		{
			rateString = format.format(rate);
		}
		return String.format(Locale.ENGLISH, "%s:%s|%s%s\n", metric.getKey(), metric.getValue(), metric.getType()
			.getShortString(), rateString);
	}

	@Override
	public void increment(String key)
	{
		queue.add(new StatsDMetric(key, StatsDType.COUNTER, 1));
	}

	@Override
	public void count(String key, int delta)
	{
		queue.add(new StatsDMetric(key, StatsDType.COUNTER, delta));
	}

	@Override
	public void gauge(String key, double value)
	{
		queue.add(new StatsDMetric(key, StatsDType.GAUGE, value));
	}

	@Override
	public void time(String key, int timeInMs)
	{
		queue.add(new StatsDMetric(key, StatsDType.TIMER, timeInMs));
	}

	@Override
	public void count(String key, Object delta, double rate)
	{
		queue.add(new StatsDMetric(key, StatsDType.COUNTER, delta, rate));
	}

	@Override
	public void gauge(String key, long value)
	{
		queue.add(new StatsDMetric(key, StatsDType.GAUGE, value));
	}
}
