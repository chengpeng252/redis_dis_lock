package frist.redis;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ResdisService {
	@Autowired
	DisLock disLock;
	@Autowired
	RedisDao redisDao;
	@RequestMapping("mySpringBoot")
	@ResponseBody
	public String test(@RequestParam("key")  String key){
		int j=0;
		final String key1=key;
		for(int i=0;i<100;i++) {
			new Thread(new Runnable() {
				public void run() {
					disLock.lock(key1);
					String value = redisDao.getValue(key1+"v");
					if(value==null) {
						redisDao.setValue(key1+"v", "1");
					}else {
						Integer valuei = Integer.valueOf(value);
						valuei=valuei+1;
						redisDao.setValue(key1+"v", valuei+"");
					}
					System.out.println(redisDao.getValue(key1+"v"));
					disLock.unlock(key1);
				}
			}).start();
		}
		
		return "´ó·É¸ç";
	}
	@RequestMapping("test1")
	@ResponseBody
	public String test(){
		
		return "1133";
	}
}
