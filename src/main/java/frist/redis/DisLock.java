package frist.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import util.CommonException;
/**
 * �ֲ�ʽ������ ��Ϣ����
 * @author chengpeng23
 * redis��������:
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
	 *�ַ��������к�׺
	 * */
	private final static String LOCK_QUEUE_PRE=":lockQueuePre";
	/**
	 *�ַ����������к�׺
	 * */
	private final static String LOCK_INCR_PRE=":lockIncrPre";
	/**
	 *�߳���Ϣ:�߳��Ƿ�����
	 * */
	private final static String TL_IS_LOCK="isLock";
	/**
	 *�߳���Ϣ:��ʱʱ�䵥λ
	 * */
	private final static String TL_TIMEUNIT_NAME="timeUnitName";
	/**
	 *�߳���Ϣ:��ʱʱ��
	 * */
	private final static String TL_TIME_OUT="timeOut";
	/**
	 *�߳���Ϣ
	 * */
	private ThreadLocal<Map<String,String>> threadLocal=new ThreadLocal<Map<String, String>>();
	/**
	 * û�г�ʱ���Ƶ���
	 * */
	public void lock(String key) {
		try {
			lock(key,0,TimeUnit.MINUTES);
		} catch (CommonException e) {
			
		}
	}
	
	/**
	 * ��������
	 * redis�����ӳ�ʱ������ùص���
	 * ���redis��timeoutС������timeout��
	 * ������timeout��Ч
	 * @author chengpeng23
	 * @param key �������ַ���
	 * @param timeout ��ʱʱ��
	 * @param timeUnit ��ʱʱ�䵥λ
	 * */
	public void lock(String key,long timeout,TimeUnit timeUnit) throws CommonException {
		Map<String,String>  tlMap = new HashMap<String,String>();
		Map<String, String> map = threadLocal.get();
		if(map==null||(map!=null&&!"lock".equals(map.get(TL_IS_LOCK)))) {
			tlMap.put(TL_TIME_OUT, timeout+"");
			tlMap.put(TL_TIMEUNIT_NAME, timeUnit.name());
			tlMap.put(TL_IS_LOCK, "lock");
			threadLocal.set(tlMap);
			//������ �����ж��ٸ��߳������� ��
			long incr = redisDao.incr(key+LOCK_INCR_PRE, 1,timeUnit, timeout);
			//�����޸�����ط���1���ﵽ���߳�դ�������ã������ʵ���Ҿ�ûд�ˣ���Ҫ�õ�դ���Ĵ����Լ�дһ����
			if(incr==1) {
				redisDao.push(key+LOCK_QUEUE_PRE);
			}
			pop(key,timeout,timeUnit);
		}
		
	}
	/**
	 * ��װ���еĳ�ջ����
	 * @author chengpeng23
	 * */
	private void pop(String key,long timeout,TimeUnit timeUnit) throws CommonException {
		boolean flag=true;
		while(flag) {
			try {
				String bpop = redisDao.bpop(key+LOCK_QUEUE_PRE, timeUnit, timeout);
				flag=false;
				if(bpop==null) {//���еȴ���ʱ�᷵�ؿ�ֵ
					redisDao.incr(key+LOCK_INCR_PRE, -1,timeUnit, timeout);
					threadLocal.remove();
					throw CommonException.CommonExceptionLockTime();
				}
			}catch (CommonException e) {
				throw e;
			} catch (Exception e) {//����ط��Ƿ�ֹredis�����˳�ʱʱ��
				flag=true;
			}
			
		}
	}

	/**
	 * ��������
	 * @author chengpeng23
	 * */
	public void unlock(String key) {
		//�õ��߳���Ϣ
		Map<String, String> map = threadLocal.get();
		//����߳�û�м��������κβ���
		if(map!=null&&"lock".equals(map.get(TL_IS_LOCK))) {
			//�õ���ʱ����Ϣ
			String timeUnitName=map.get(TL_TIMEUNIT_NAME);
			String timeOutStr = map.get(TL_TIME_OUT);
			Long timeOut = Long.valueOf(timeOutStr);
			TimeUnit timeUnit = getTimeUnit(timeUnitName);
			long incr = redisDao.incr(key, -1,timeUnit, timeOut);
			//���һ���߳��뿪ʱ����Ӧ��Ϊ�����Բ��ܼ�ֵ
			if(incr!=0) {
				redisDao.push(key+LOCK_QUEUE_PRE);
			}
			//�Ƴ����̵߳���Ϣ
			threadLocal.remove();
		}
	}
	/**
	 * ����timeUnitname�õ�timeUnit����
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
