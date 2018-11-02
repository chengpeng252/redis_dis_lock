package start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import start.service.ResdisDisLockTestServiceImpl;


@Controller
public class TestController {
	@Autowired
	ResdisDisLockTestServiceImpl resdisDisLockTestService;
	
	@RequestMapping("/resdisDisLockTest")
	@ResponseBody
	public String test(@RequestParam("key")  String key) {
		resdisDisLockTestService.test(key);
		return "成功";
	}
	
	@RequestMapping("/resdisDisLockTest1")
	@ResponseBody
	public String test1(@RequestParam("key")  String key) {
		resdisDisLockTestService.test1(key);
		return "成功";
	}
}
