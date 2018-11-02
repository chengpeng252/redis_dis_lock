package start.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import start.dao.RedisDao;
import start.redislock.DisLock;

@Controller
public class ResdisDisLockTestServiceImpl {
	@Autowired
	DisLock disLock;
	@Autowired
	RedisDao redisDao;
	
	public String test(String key){
		System.err.println("");
		System.err.println("===============分布式加锁任务结果==================");
		final String key1=key;
		for(int i=0;i<100;i++) {
			new Thread(new Runnable() {
				public void run() {
					disLock.lock(key1);
					String value = redisDao.getValue(key1);
					if(value==null) {
						redisDao.setValue(key1, "1");
					}else {
						Integer valuei = Integer.valueOf(value);
						valuei=valuei+1;
						redisDao.setValue(key1, valuei+"");
					}
					String value2 = redisDao.getValue(key1);
					System.out.print(redisDao.getValue(key1)+",");
					if("53".equals(value2)) {
						System.out.println();
					}
					disLock.unlock(key1);
				}
			}).start();
		}
		return redisDao.getValue(key1);
	}
	
	public String test1(String key){
		System.err.println("");
		System.err.println("=================没有分布式加锁任务结果==================");
		final String key1=key;
		for(int i=0;i<100;i++) {
			new Thread(new Runnable() {
				public void run() {
					String value = redisDao.getValue(key1);
					if(value==null) {
						redisDao.setValue(key1, "1");
					}else {
						Integer valuei = Integer.valueOf(value);
						valuei=valuei+1;
						redisDao.setValue(key1, valuei+"");
					}
					String value2 = redisDao.getValue(key1);
					System.out.print(redisDao.getValue(key1)+",");
					if("53".equals(value2)) {
						System.out.println();
					}
				}
			}).start();
		}
		return redisDao.getValue(key1);
	}
	
	
	
}
