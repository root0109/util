/**
 * 
 */
package com.zaprit.spring.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.lang.Nullable;

import com.zaprit.scope.db.QueryStats;

import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author vaibhav.singh
 *
 */
@Setter
@NoArgsConstructor
public final class CustomJdbcTemplate extends JdbcTemplate
{
	private boolean											slowQueryLogEnabled		= false;
	private int												slowQueryTimeInMillis	= 200;
	private int												noOfQueriesToTrack		= 2;
	private static final ConcurrentMap<String, QueryStats>	queryStatsMap			= new ConcurrentHashMap<>();

	public CustomJdbcTemplate(DataSource dataSource, boolean lazyInit)
	{
		super(dataSource, lazyInit);
	}

	public CustomJdbcTemplate(DataSource dataSource)
	{
		super(dataSource);
	}

	private void noteQueryExecutionTime(String query, long startTime)
	{
		int time = (int) (System.currentTimeMillis() - startTime);
		QueryStats queryStats = queryStatsMap.get(query);
		if (queryStats == null)
		{
			queryStats = new QueryStats(query, noOfQueriesToTrack, slowQueryLogEnabled, slowQueryTimeInMillis);
			QueryStats temp = queryStatsMap.putIfAbsent(query, queryStats);
			if (temp != null)
			{
				queryStats = temp;
			}
		}
		queryStats.addExecutionTime(time);
		queryStats.log(time);
	}

	@Override
	public void execute(String sql) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		super.execute(sql);
		noteQueryExecutionTime(sql, ts);
	}

	@Override
	public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.query(sql, rse);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public void query(String sql, RowCallbackHandler rch) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		super.query(sql, rch);
		noteQueryExecutionTime(sql, ts);
	}

	@Override
	public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException
	{
		// long ts = System.currentTimeMillis();
		return super.query(sql, rowMapper);
		// noteQueryExecutionTime(sql, ts);
	}

	@Override
	public Map<String, Object> queryForMap(String sql) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		Map<String, Object> t = super.queryForMap(sql);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.queryForObject(sql, rowMapper);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.queryForObject(sql, requiredType);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<T> t = super.queryForList(sql, elementType);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<Map<String, Object>> list = super.queryForList(sql);
		noteQueryExecutionTime(sql, ts);
		return list;
	}

	@Override
	public SqlRowSet queryForRowSet(String sql) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		SqlRowSet sqlRowSet = super.queryForRowSet(sql);
		noteQueryExecutionTime(sql, ts);
		return sqlRowSet;
	}

	@Override
	public int update(String sql) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int count = super.update(sql);
		noteQueryExecutionTime(sql, ts);
		return count;
	}

	@Override
	public int[] batchUpdate(String... sql) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int count[] = super.batchUpdate(sql);
		noteQueryExecutionTime(String.join(",", sql), ts);
		return count;
	}

	@Override
	public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.execute(sql, action);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.query(sql, args, argTypes, rse);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.query(sql, rse, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		super.query(sql, pss, rch);
		noteQueryExecutionTime(sql, ts);
	}

	@Override
	public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		super.query(sql, args, argTypes, rch);
		noteQueryExecutionTime(sql, ts);
	}

	@Override
	public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		super.query(sql, args, rch);
		noteQueryExecutionTime(sql, ts);
	}

	@Override
	public void query(String sql, RowCallbackHandler rch, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		super.query(sql, rch, args);
		noteQueryExecutionTime(sql, ts);
	}

	@Override
	public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<T> t = super.query(sql, pss, rowMapper);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<T> t = super.query(sql, args, argTypes, rowMapper);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<T> t = super.query(sql, rowMapper, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.queryForObject(sql, args, argTypes, rowMapper);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.queryForObject(sql, args, rowMapper);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.queryForObject(sql, rowMapper, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.queryForObject(sql, args, argTypes, requiredType);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.queryForObject(sql, args, requiredType);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.queryForObject(sql, requiredType, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		Map<String, Object> t = super.queryForMap(sql, args, argTypes);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		Map<String, Object> t = super.queryForMap(sql, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<T> t = super.queryForList(sql, args, argTypes, elementType);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<T> t = super.queryForList(sql, args, elementType);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<T> t = super.queryForList(sql, elementType, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<Map<String, Object>> t = super.queryForList(sql, args, argTypes);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<Map<String, Object>> t = super.queryForList(sql, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		SqlRowSet t = super.queryForRowSet(sql, args, argTypes);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		SqlRowSet t = super.queryForRowSet(sql, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public int update(String sql, PreparedStatementSetter pss) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int t = super.update(sql, pss);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int t = super.update(sql, args, argTypes);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public int update(String sql, Object... args) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int t = super.update(sql, args);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int[] t = super.batchUpdate(sql, pss);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int[] t = super.batchUpdate(sql, batchArgs);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public int[] batchUpdate(String sql, List<Object[]> batchArgs, int[] argTypes) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int[] t = super.batchUpdate(sql, batchArgs, argTypes);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> int[][] batchUpdate(String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss)
	                throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		int[][] t = super.batchUpdate(sql, batchArgs, batchSize, pss);
		noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.execute(callString, action);
		noteQueryExecutionTime(callString, ts);
		return t;
	}

	@Override
	public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		T t = super.execute(psc, action);
		String sql = getSql(psc);
		if (sql != null)
			noteQueryExecutionTime(sql, ts);
		return t;
	}

	/**
	 * Determine SQL from potential provider object.
	 * 
	 * @param sqlProvider object which is potentially an SqlProvider
	 * @return the SQL string, or {@code null} if not known
	 * @see SqlProvider
	 */
	@Nullable
	private static String getSql(Object sqlProvider)
	{
		if (sqlProvider instanceof SqlProvider)
		{
			return ((SqlProvider) sqlProvider).getSql();
		}
		else
		{
			return null;
		}
	}

	@Override
	public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException
	{
		String sql = getSql(psc);
		long ts = System.currentTimeMillis();
		T t = super.query(psc, rse);
		if (sql != null)
			noteQueryExecutionTime(sql, ts);
		return t;
	}

	@Override
	public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		super.query(psc, rch);
		String sql = getSql(psc);
		if (sql != null)
		{
			noteQueryExecutionTime(sql, ts);
		}
	}

	@Override
	public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException
	{
		long ts = System.currentTimeMillis();
		List<T> t = super.query(psc, rowMapper);
		String sql = getSql(psc);
		if (sql != null)
		{
			noteQueryExecutionTime(sql, ts);
		}
		return t;
	}

	@Override
	protected int update(PreparedStatementCreator psc, PreparedStatementSetter pss) throws DataAccessException
	{
		String sql = getSql(psc);
		long ts = System.currentTimeMillis();
		int count = super.update(psc, pss);
		if (sql != null)
			noteQueryExecutionTime(sql, ts);
		return count;
	}

	@Override
	public int update(PreparedStatementCreator psc) throws DataAccessException
	{
		String sql = getSql(psc);
		long ts = System.currentTimeMillis();
		int count = super.update(psc);
		if (sql != null)
			noteQueryExecutionTime(sql, ts);
		return count;
	}

	@Override
	public int update(PreparedStatementCreator psc, KeyHolder generatedKeyHolder) throws DataAccessException
	{
		String sql = getSql(psc);
		long ts = System.currentTimeMillis();
		int count = super.update(psc, generatedKeyHolder);
		if (sql != null)
			noteQueryExecutionTime(sql, ts);
		return count;
	}

	@Override
	public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException
	{
		String sql = getSql(csc);
		long ts = System.currentTimeMillis();
		T t = super.execute(csc, action);
		if (sql != null)
			noteQueryExecutionTime(sql, ts);
		return t;
	}

}
