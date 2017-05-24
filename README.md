# java-rate-limiter

基于Guava的RateLimiter、Redis封装了并发控制、限流管理功能，支持配置文件和硬编码形式进行限流控制，控制粒度在方法级别，使用简单。

- **限流控制**：主要用于服务的请求频率限制，避免由于服务吞吐量跟不上造成上层调用请求堆积过大而导致服务垮掉甚至应用奔溃问题。目前提供了单机版限流和分布式限流功能。简而言之，通过限流控制，我们可以在方法级别的粒度控制到在XXX秒内最多接收XXX次调用。

- **并发控制**：与限流控制类似，并发控制更加强调的是并发量。该功能能够保证服务在任何时刻的并发量控制。目前提供了单机和分布式两个版本的并发控制。简而言之，通过并发控制，我们可以在方法级别的粒度控制到某个服务同时间最多不能超过XXX次请求被调用。

## 一、使用示例：

#### 1、自定义RateLimiterConfig配置
> CustomRateLimiterConfig代码引用

	public class CustomRateLimiterConfig extends RateLimiterConfig {
	
		private final String FILE_NAME = "test-rate-limiter.properties";
		
		@Override
		public String getConfigFile() {
			return FILE_NAME;
		}
	
	}
继承RateLimiterConfig类并且重写getConfigFile方法重新制定配置文件名称即可.

配置文件模板如：[test-rate-limiter.properties](https://github.com/cpthack/java-rate-limiter/blob/master/src/test/resources/test-rate-limiter.properties)所示，主要参考配置项

#### 2、自定义RedisConfig配置
> RateRedisConfig代码引用

	public class RateRedisConfig extends RedisConfig {
	
		private final String FILE_NAME = "test-redis_config.properties";
		
		@Override
		public String getConfigFile() {
			return FILE_NAME;
		}
	}
继承RedisConfig类并且重写getConfigFile方法重新制定配置文件名称即可.

配置文件模板如：[test-redis_config.properties](https://github.com/cpthack/java-rate-limiter/blob/master/src/test/resources/test-redis_config.properties)所示，主要参考配置项

#### 3、限流控制使用
> LimiterTest.java代码引用

		private final static Logger	logger		= LoggerFactory.getLogger(LimiterTest.class);
		private static Limiter		limiter		= null;
		private volatile static int	maxNum		= 0;
		private final static String	ROUTER_NAME	= "/thread-test";
		
		public static void main(String[] args) {
			RedisConfig redisConfig = new RateRedisConfig();
			RateLimiterConfig rateLimiterConfig = new CustomRateLimiterConfig();
			
			singleLimiter(rateLimiterConfig);// 实例化单机限流对象
			
			// DistributedLimiter(rateLimiterConfig, redisConfig); // 示例化分布式限流对象
			
			simulateConcurrentThread(); // 模拟并发线程
		}
		
		private static void simulateConcurrentThread() {
			DoThing dt = null;
			Thread t = null;
			for (int i = 0; i < 6; i++) {
				dt = new DoThing("Thread " + i);
				t = new Thread(dt);
				t.start();
			}
		}
		
		private static void singleLimiter(RateLimiterConfig rateLimiterConfig) {
			limiter = LimiterFactory.getInstance().single(rateLimiterConfig);
		}
		
		private static void DistributedLimiter(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
			limiter = LimiterFactory.getInstance().distributed(rateLimiterConfig, redisConfig);
		}
		
		/**
		 * 自定义线程，用于模拟并发
		 */
		static class DoThing implements Runnable {
			String name;
			
			public DoThing(String name) {
				this.name = name;
			}
			
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				try {
					for (int i = 0; i < 20; i++) {
						
						if (!limiter.execute(ROUTER_NAME, 4, 1)) {// 进行限流控制
							
							logger.info("Thread Name is [{}]，调用频率太高了.", name);
							Thread.currentThread().sleep(1000);
							continue;
						}
						maxNum++;
						logger.info("Thread Name is [{}]，最新maxNum的值 = [" + maxNum + "]", name);
					}
				}
				catch (InterruptedException e) {
					logger.error("Thread Name [{}] is Error.", name, e);
				}
			}
		}

#### 4、并发控制
> LimiterTest.java代码引用

	private final static Logger	logger	   = LoggerFactory.getLogger(LockTest.class);
	
	private static String		UNIQUE_KEY = "/lock1";
	private static Lock			lock	   = null;
	
	private volatile static int	successNum = 0;
	
	public static void main(String[] args) {
		RateLimiterConfig rateLimiterConfig = new CustomRateLimiterConfig();
		// lock = getSingleLock(rateLimiterConfig);
		RedisConfig redisConfig = new RateRedisConfig();
		
		lock = getDistributedLock(rateLimiterConfig, redisConfig);
		
		// RedisClientFactory.getClient(redisConfig).set("/lock1", "10");//
		// 模拟releaseLock没有执行导致的缓存中存在较多正数值得锁KEY
		
		simulateConcurrentThread(80); // 模拟并发线程
	}
	
	private static Lock getSingleLock(RateLimiterConfig rateLimiterConfig) {
		return LockFactory.getInstance().single(rateLimiterConfig);
	}
	
	private static Lock getDistributedLock(RateLimiterConfig rateLimiterConfig, RedisConfig redisConfig) {
		return LockFactory.getInstance().distributed(rateLimiterConfig, redisConfig);
	}
	
	private static void simulateConcurrentThread(int threadNum) {
		DoThing dt = null;
		Thread t = null;
		for (int i = 0; i < threadNum; i++) {
			dt = new DoThing("Thread " + i);
			t = new Thread(dt);
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}// 模拟程序执行时间
			t.start();
		}
	}
	
	/**
	 * 自定义线程，用于模拟并发
	 */
	static class DoThing implements Runnable {
		String name;
		
		public DoThing(String name) {
			this.name = name;
		}
		
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			try {
				if (lock.lock(UNIQUE_KEY)) {// 进行并发控制
					
					logger.info("Thread Name is [{}] 成功获得锁，正在处理中...", name);
					
					successNum++;
					logger.info("当前成功并发数successNum的值为 [" + successNum + "]");
					Thread.currentThread().sleep(2000);// 模拟程序执行时间
					
					successNum--;
					lock.releaseLock(UNIQUE_KEY);
				}
				else {
					logger.warn("Thread Name is [{}] 尝试获得锁失败", name);
				}
			}
			catch (InterruptedException e) {
				logger.error("Thread Name [{}] is Error.", name, e);
			}
		}
	}

## 二、具体参考：

* 依赖redis-client项目：[redis-client](https://github.com/cpthack/redis-client)

* 限流控制示例参考类：[LimiterTest.java](https://github.com/cpthack/java-rate-limiter/blob/master/src/test/java/com/github/cpthack/commons/ratelimiter/limiter/LimiterTest.java)

* 并发控制示例参考类：[LockTest.java](https://github.com/cpthack/java-rate-limiter/blob/master/src/test/java/com/github/cpthack/commons/ratelimiter/lock/LockTest.java)