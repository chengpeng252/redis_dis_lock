package frist.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import util.CommonException;
/**
 * 分布式阻塞锁 消息队列
 * @author chengpeng23
 * redis配置如下:
 * spring.redis.database=0
 * spring.redis.host=127.0.0.1
 * spring.redis.port=6379
 * spring.redis.pool.max-active=-1
 * spring.redis.pool.max-wait=-1
 * spring.redis.pool.max-idle=1000
 * spring.redis.pool.min-idle=0
 * spring.redis.timeout=0
 * */

@Service
public class DisLock {
	@Autowired
	RedisDao redisDao;
	/**
	 *字符串锁队列后缀
	 * */
	private final static String LOCK_QUEUE_PRE=":lockQueuePre";
	/**
	 *字符串锁序列列后缀
	 * */
	private final static String LOCK_INCR_PRE=":lockIncrPre";
	/**
	 *线程信息:线程是否上锁
	 * */
	private final static String TL_IS_LOCK="isLock";
	/**
	 *线程信息:超时时间单位
	 * */
	private final static String TL_TIMEUNIT_NAME="timeUnitName";
	/**
	 *线程信息:超时时长
	 * */
	private final static String TL_TIME_OUT="timeOut";
	/**
	 *线程信息
	 * */
	private ThreadLocal<Map<String,String>> threadLocal=new ThreadLocal<Map<String, String>>();
	/**
	 * 没有超时机制的锁
	 * */
	public void lock(String key) {
		try {
			lock(key,0,TimeUnit.MINUTES);
		} catch (CommonException e) {
			
		}
	}
	
	/**
	 * 加锁方法
	 * redis的连接超时机制最好关掉。
	 * 如果redis的timeout小于锁的timeout，
	 * 则锁的timeout无效
	 * @author chengpeng23
	 * @param key 加锁的字符串
	 * @param timeout 超时时间
	 * @param timeUnit 超时时间单位
	 * */
	public void lock(String key,long timeout,TimeUnit timeUnit) throws CommonException {
		Map<String,String>  tlMap = new HashMap<String,String>();
		Map<String, String> map = threadLocal.get();
		if(map==null||(map!=null&&!"lock".equals(map.get(TL_IS_LOCK)))) {
			tlMap.put(TL_TIME_OUT, timeout+"");
			tlMap.put(TL_TIMEUNIT_NAME, timeUnit.name());
			tlMap.put(TL_IS_LOCK, "lock");
			threadLocal.set(tlMap);
			//计数器 （还有多少个线程在阻塞 ）
			long incr = redisDao.incr(key+LOCK_INCR_PRE, 1,timeUnit, timeout);
			//可以修改这个地方的1，达到多线程栅栏的所用，具体的实现我就没写了，需要用到栅栏的大佬自己写一下呗
			if(incr==1) {
				redisDao.push(key+LOCK_QUEUE_PRE);
			}
			pop(key,timeout,timeUnit);
		}
		
	}
	/**
	 * 封装队列的出栈方法
	 * @author chengpeng23
	 * */
	private void pop(String key,long timeout,TimeUnit timeUnit) throws CommonException {
		boolean flag=true;
		while(flag) {
			try {
				String bpop = redisDao.bpop(key+LOCK_QUEUE_PRE, timeUnit, timeout);
				flag=false;
				if(bpop==null) {//队列等待超时会返回空值
					redisDao.incr(key+LOCK_INCR_PRE, -1,timeUnit, timeout);
					threadLocal.remove();
					throw CommonException.CommonExceptionLockTime();
				}
			}catch (CommonException e) {
				throw e;
			} catch (Exception e) {//这个地方是防止redis设置了超时时间
				flag=true;
			}
			
		}
	}

	/**
	 * 解锁方法
	 * @author chengpeng23
	 * */
	public void unlock(String key) {
		//得到线程信息
		Map<String, String> map = threadLocal.get();
		//如果线程没有加锁不做任何操作
		if(map!=null&&"lock".equals(map.get(TL_IS_LOCK))) {
			//得到超时的信息
			String timeUnitName=map.get(TL_TIMEUNIT_NAME);
			String timeOutStr = map.get(TL_TIME_OUT);
			Long timeOut = Long.valueOf(timeOutStr);
			TimeUnit timeUnit = getTimeUnit(timeUnitName);
			long incr = redisDao.incr(key, -1,timeUnit, timeOut);
			//最后一个线程离开时队列应该为空所以不能加值
			if(incr!=0) {
				redisDao.push(key+LOCK_QUEUE_PRE);
			}
			//移除该线程的信息
			threadLocal.remove();
		}
	}
	/**
	 * 根据timeUnitname得到timeUnit对象
	 * @author chengpeng23
	 * */
	private TimeUnit getTimeUnit(String timeUnitname) {
		TimeUnit[] values = TimeUnit.values();
		for(TimeUnit timeUnit:values) {
			if(timeUnit.name().equals(timeUnitname)) {
				return timeUnit;
			}
		}
		return null;
	}
	
	
}
