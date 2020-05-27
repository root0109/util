/**
 * 
 */
package com.zaprit.scope.db;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * A QueryStats object whose responsibility is to log slow queries in the
 * current environment.
 * 
 * @author vaibhav.singh
 *
 */
@Slf4j
public final class QueryStats
{
	private final String	query;
	private int				lastQueryTimeIndex;
	private boolean			slowQueryLogEnabled;
	private int				slowQueryTime;
	private int				noOfQueriesToTrack;
	private int[]			lastQueryTimes;

	public QueryStats(String query, int noOfQueriesToTrack, boolean slowQueryLogEnabled, int slowQueryTime)
	{
		this.query = query;
		this.noOfQueriesToTrack = noOfQueriesToTrack;
		this.slowQueryLogEnabled = slowQueryLogEnabled;
		this.slowQueryTime = slowQueryTime;
		lastQueryTimes = new int[noOfQueriesToTrack];
	}

	public synchronized void addExecutionTime(int time)
	{
		if (lastQueryTimeIndex == noOfQueriesToTrack)
			lastQueryTimeIndex = 0;
		int index = (lastQueryTimeIndex++) % noOfQueriesToTrack;
		lastQueryTimes[index] = time;
	}

	public void log(int time)
	{
		if (slowQueryLogEnabled && time >= slowQueryTime)
		{
			log.info("[QUERY_STATS] timetakenInMillis=" + time + " ,lastQueryTimes=" + Arrays.toString(lastQueryTimes) + " ,Abbreviate-Query="
			         + StringUtils.abbreviate(query.trim(), 150));
		}
	}
}
