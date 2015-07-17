package com.mofang.feed.global;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.mofang.framework.data.mysql.pool.BoneCPPool;
import com.mofang.framework.data.mysql.pool.MysqlPool;
import com.mofang.framework.data.redis.RedisExecutor;
import com.mofang.framework.data.redis.pool.RedisPoolConfig;
import com.mofang.framework.data.redis.pool.RedisPoolProvider;
import com.mofang.framework.net.http.HttpClientConfig;
import com.mofang.framework.net.http.HttpClientProvider;

/**
 * 
 * @author zhaodx
 *
 */
public class GlobalObject
{
	/**
	 * Master Redis Executor Instance
	 */
	public final static RedisExecutor REDIS_MASTER_EXECUTOR = new RedisExecutor();
	
	/**
	 * Slave Redis Executor Instance
	 */
	public final static RedisExecutor REDIS_SLAVE_EXECUTOR = new RedisExecutor();
	
	/**
	 * Mysql Pool Instance
	 */
	public static MysqlPool MYSQL_CONNECTION_POOL = null;
	
	/**
	 * Global TaskService API Http Client Instance
	 */
	public static CloseableHttpClient HTTP_CLIENT_TASKSERVICE;
	
	/**
	 * Global ChatService Http Client Instance
	 */
	public static CloseableHttpClient HTTP_CLIENT_CHATSERVICE;
	
	/**
	 * Global User Http Client Instance
	 */
	public static CloseableHttpClient HTTP_CLIENT_USERSERVICE;
	
	/**
	 * Global SensitiveWord Http Client Instance
	 */
	public static CloseableHttpClient HTTP_CLIENT_SENSITIVEWORD;
	
	/**
	 * Global Video Http Client Instance
	 */
	public static CloseableHttpClient HTTP_CLIENT_VIDEOSERVICE;
	
	/**
	 * Global Game Http Client Instance
	 */
	public static CloseableHttpClient HTTP_CLIENT_GAMESERVICE;
	
	/**
	 * Global Fahao Http Client Instance
	 */
	public static CloseableHttpClient HTTP_CLIENT_FAHAOSERVICE;
	
	public static CloseableHttpClient HTTP_CLIENT_VIPERSERVICE;
	
	/**
	 * Global Solr Server for Forum Core
	 */
	public static SolrServer SOLR_SERVER_FORUM = new HttpSolrServer(GlobalConfig.SOLR_SERVER_HOST + GlobalConfig.SOLR_CORE_FORUM);
	
	/**
	 * Global Solr Server for Thread Core
	 */
	public static SolrServer SOLR_SERVER_THREAD = new HttpSolrServer(GlobalConfig.SOLR_SERVER_HOST + GlobalConfig.SOLR_CORE_THREAD);
	
	/**
	 * Global Solr Server for Post Core
	 */
	public static SolrServer SOLR_SERVER_POST = new HttpSolrServer(GlobalConfig.SOLR_SERVER_HOST + GlobalConfig.SOLR_CORE_POST);
	
	/**
	 * Global Solr Server for Comment Core
	 */
	public static SolrServer SOLR_SERVER_COMMENT = new HttpSolrServer(GlobalConfig.SOLR_SERVER_HOST + GlobalConfig.SOLR_CORE_COMMENT);
	
	/**
	 * Global Solr Index Executor Pool
	 */
	public static ExecutorService SOLR_INDEX_EXECUTOR = Executors.newFixedThreadPool(20);
	
	/**
	 * Global Async Operation Executor Pool, as push notify ect. 
	 */
	public static ExecutorService ASYN_HTTP_EXECUTOR = Executors.newFixedThreadPool(20);
	
	/**
	 *  异步mysql 任务线程池子
	 */
	public static ExecutorService ASYN_DAO_EXECUTOR = Executors.newFixedThreadPool(20);
	
	/**
	 * Global Info Logger Instance 
	 */
	public final static Logger INFO_LOG = Logger.getLogger("feed.info");
	
	/**
	 * Global Error Logger Instance
	 */
	public final static Logger ERROR_LOG = Logger.getLogger("feed.error");
	
	/***************************************初始化系统对象*************************************/
	private final static String JdbcUrlFat = "jdbc:mysql://%s:%s/%s?user=%s&password=%s&useUnicode=true&characterEncoding=%s&autoReconnect=true&failOverReadOnly=false";
	private final static String Driver = "com.mysql.jdbc.Driver";
	
	public static void initRedisMaster(String configPath) throws Exception
	{
		RedisPoolConfig config = getRedisConfig(configPath);
		JedisPool pool = RedisPoolProvider.getRedisPool(config);
		REDIS_MASTER_EXECUTOR.setJedisPool(pool);
	}
	
	public static void initRedisSlave(String configPath) throws Exception
	{
		RedisPoolConfig config = getRedisConfig(configPath);
		JedisPool pool = RedisPoolProvider.getRedisPool(config);
		REDIS_SLAVE_EXECUTOR.setJedisPool(pool);
	}
	
	private static RedisPoolConfig getRedisConfig(String configPath) throws Exception
	{
        try
        {
        		Properties configurations = loadConfig(configPath);
			String host = configurations.getProperty("host");
			int port = Integer.valueOf(configurations.getProperty("port"));
			int timeout = Integer.valueOf(configurations.getProperty("timeout"));
			int maxActive = Integer.valueOf(configurations.getProperty("maxActive"));
			int maxIdle = Integer.valueOf(configurations.getProperty("maxIdle"));
			boolean testOnBorrow = Boolean.valueOf(configurations.getProperty("testOnBorrow"));
			
			RedisPoolConfig config = new RedisPoolConfig();
			JedisPoolConfig poolConf = new JedisPoolConfig();
			poolConf.setMaxActive(maxActive);
			poolConf.setMaxIdle(maxIdle);
			poolConf.setTestOnBorrow(testOnBorrow);
			config.setConfig(poolConf);
			config.setHost(host);
			config.setPort(port);
			config.setTimeout(timeout);
			return config;
        }
        catch(Exception e)
        {
        		throw e;
        }
	}

	public static void initMysql(String configPath) throws Exception
	{
		BoneCPConfig config = getMysqlConfig(configPath);
		Class.forName(Driver);
		BoneCP pool = new BoneCP(config);
		MYSQL_CONNECTION_POOL = new BoneCPPool(pool);
	}
	
	private static BoneCPConfig getMysqlConfig(String configPath) throws Exception
	{
        try
        {
        		Properties configurations = loadConfig(configPath);
			String host = configurations.getProperty("host");
			String port = configurations.getProperty("port");
			String user = configurations.getProperty("user");
			String password = configurations.getProperty("password");
			String charset = configurations.getProperty("charset");
			String dbname = configurations.getProperty("dbname");
			int partitionCount = Integer.valueOf(configurations.getProperty("partitionCount"));
			int maxConnectionsPerPartition = Integer.valueOf(configurations.getProperty("maxConnectionsPerPartition"));
			int minConnectionsPerPartition = Integer.valueOf(configurations.getProperty("minConnectionsPerPartition"));
			int acquireIncrement = Integer.valueOf(configurations.getProperty("acquireIncrement"));
			int releaseHelperThreads = Integer.valueOf(configurations.getProperty("releaseHelperThreads"));
			
			String jdbcUrl = String.format(JdbcUrlFat, host, port, dbname, user, password, charset);
			BoneCPConfig config = new BoneCPConfig();
			config.setJdbcUrl(jdbcUrl);
			config.setPartitionCount(partitionCount);
			config.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
			config.setMinConnectionsPerPartition(minConnectionsPerPartition);
			config.setAcquireIncrement(acquireIncrement);
			config.setReleaseHelperThreads(releaseHelperThreads);
			config.setIdleConnectionTestPeriod(60, TimeUnit.SECONDS);
			config.setIdleMaxAgeInSeconds(240);
			return config;
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
	
	public static void initTaskServiceHttpClient(String configPath) throws Exception
	{
        try
        {
        		HttpClientProvider provider = getHttpClientProvider(configPath);
			HTTP_CLIENT_TASKSERVICE = provider.getHttpClient();
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
	
	public static void initChatServiceHttpClient(String configPath) throws Exception
	{
        try
        {
        		HttpClientProvider provider = getHttpClientProvider(configPath);
			HTTP_CLIENT_CHATSERVICE = provider.getHttpClient();
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
	
	public static void initUserServiceHttpClient(String configPath) throws Exception
	{
        try
        {
        		HttpClientProvider provider = getHttpClientProvider(configPath);
			HTTP_CLIENT_USERSERVICE = provider.getHttpClient();
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
	
	public static void initSensitiveWordHttpClient(String configPath) throws Exception
	{
        try
        {
        		HttpClientProvider provider = getHttpClientProvider(configPath);
			HTTP_CLIENT_SENSITIVEWORD = provider.getHttpClient();
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
	
	public static void initVideoServiceHttpClient(String configPath) throws Exception
	{
        try
        {
        		HttpClientProvider provider = getHttpClientProvider(configPath);
			HTTP_CLIENT_VIDEOSERVICE = provider.getHttpClient();
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
	
	public static void initGameServiceHttpClient(String configPath) throws Exception
	{
        try
        {
        		HttpClientProvider provider = getHttpClientProvider(configPath);
			   HTTP_CLIENT_GAMESERVICE = provider.getHttpClient();
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
	
	public static void initFahaoServiceHttpClient(String configPath) throws Exception
	{
        try
        {
        		HttpClientProvider provider = getHttpClientProvider(configPath);
			   HTTP_CLIENT_FAHAOSERVICE = provider.getHttpClient();
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
	
	public static void initViperServiceHttpClient(String configPath) throws Exception
	{
		try 
		{
			HttpClientProvider provider = getHttpClientProvider(configPath);
			HTTP_CLIENT_VIPERSERVICE = provider.getHttpClient();
		} catch (Exception e)
		{
			throw e;
		}
	}
	
	private static HttpClientProvider getHttpClientProvider(String configPath) throws Exception
	{
		Properties configurations = loadConfig(configPath);
		String host = configurations.getProperty("host");
		int port = Integer.valueOf(configurations.getProperty("port"));
		int maxTotal = Integer.valueOf(configurations.getProperty("maxTotal"));
		String charset = configurations.getProperty("charset");
		int connTimeout = Integer.valueOf(configurations.getProperty("connTimeout"));
		int socketTimeout = Integer.valueOf(configurations.getProperty("socketTimeout"));
		int keepAliveTimeout = Integer.valueOf(configurations.getProperty("keepAliveTimeout"));
		int checkIdleInitialDelay = Integer.valueOf(configurations.getProperty("checkIdleInitialDelay"));
		int checkIdlePeriod = Integer.valueOf(configurations.getProperty("checkIdlePeriod"));
		int closeIdleTimeout = Integer.valueOf(configurations.getProperty("closeIdleTimeout"));
		
		HttpClientConfig config = new HttpClientConfig();
		config.setHost(host);
		config.setPort(port);
		config.setMaxTotal(maxTotal);
		config.setCharset(charset);
		config.setConnTimeout(connTimeout);
		config.setSocketTimeout(socketTimeout);
		config.setDefaultKeepAliveTimeout(keepAliveTimeout);
		config.setCheckIdleInitialDelay(checkIdleInitialDelay);
		config.setCheckIdlePeriod(checkIdlePeriod);
		config.setCloseIdleTimeout(closeIdleTimeout);
		
		HttpClientProvider provider = new HttpClientProvider(config);
		return provider;
	}
	
	private static Properties loadConfig(String configPath) throws Exception
	{
		Properties configurations = new Properties();
        File file = new File(configPath);
        try
        {
	        	configurations.load(new FileInputStream(file));
	        	return configurations;
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
}