package frist.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisDao {
	@Autowired
	RedisTemplate<String,String> redisTemplate;
	
	public String bpop(String key,TimeUnit unit,long timeout) {
		String leftPop = redisTemplate.opsForList().leftPop(key, timeout, unit);
		return leftPop;
	}
	
	public void push(String key) {
		redisTemplate.opsForList().rightPush(key,"1");
	}
	
	public long incr(String key,long incr,TimeUnit unit,long timeout) {
		Long increment = redisTemplate.opsForValue().increment(key, incr);
		if(timeout!=0) {
			redisTemplate.expire(key, timeout, unit);
		}
		return increment;
	}
	
	public void setValue(String key,String value) {
		redisTemplate.opsForValue().set(key, value);
	}
	public String getValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}

}
