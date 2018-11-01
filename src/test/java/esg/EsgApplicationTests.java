package esg;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import esg.EsgApplicationTests.AccessTokenResponse;
import frist.redis.EsgApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EsgApplication.class)
public class EsgApplicationTests {

	private RestTemplate restTemplate = new RestTemplate();

	private static final String TOKEN_URL = "https://test-api.pingan.com.cn:20443/oauth/oauth2/access_token";

	private String client_id = "P_WANHU_HIS";
	private String grant_type = "client_credentials";
	private String client_secret = "ECdsG511";
	private String UPLOAD_SETTLEMENT_URL = "http://test-api.pingan.com.cn/open/appsvr/yb/open/bill/uploadSettlement?access_token=%s&request_id=%s";

	@Test
	public void getTokenTest() {

		Map<String, String> requestBody = new HashMap<String, String>();
		requestBody.put("client_id", client_id);
		requestBody.put("grant_type", grant_type);
		requestBody.put("client_secret", client_secret);
		JSONObject response = restTemplate.postForObject(TOKEN_URL, requestBody, JSONObject.class);
		System.out.println(response);
		AccessTokenResponse respObj = JSON.parseObject(JSON.toJSONString(response.get("data")),
				AccessTokenResponse.class);
		String token = respObj.getAccess_token();
		System.out.println(token);
	}

	@Test
	public void invokeEsgApiTest() {
		String token = "2DC64724C4E2413994EFD022676F43A3";

		String requestUrl = String.format(UPLOAD_SETTLEMENT_URL, token, System.currentTimeMillis());
		
		System.out.println(requestUrl);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json;charset=UTF-8");

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("orderNo", "101127");
		body.put("billNo", "billNo222");
		body.put("invoiceNo", "invoiceNo666");
		body.put("visitNo", "vn112233445566");
		body.put("siCardNo", "siCardNo111");
		body.put("pharmacyCode", "wanhu");
		body.put("phaymacyName", "万家");
		body.put("hospitalCode", "0001");
		body.put("hospitalName", "枣庄市立医院");
		body.put("settleTime", "2018-09-10 17:40:00");
		body.put("insuranceType", "1");
		body.put("billType", "0");
		body.put("sumAmount", "100.01");
		body.put("applyPayAmount", "30.01");
		body.put("selfPayAnount", "70.00");
		body.put("fundPayAmount", "30.01");
		body.put("selfAccountAmount", "70.00");
		body.put("selfPayRatio", "0.7");
		body.put("remark", "remark");
		
		Map<String, String> detail1 = new HashMap<String, String>();
		detail1.put("recipeDetailNo", "No000001");
		detail1.put("medicineCode", "pmc2222222201");
		detail1.put("medicineName", "阿米西林");
		detail1.put("price", "22.22");
		
		body.put("recipeDetails", detail1);
		
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(body, headers);

		ResponseEntity<OpenApiResponse> exchange = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity,
				OpenApiResponse.class);
		
		System.out.println(exchange);

	}
	
	@Test
	public void demoTest() {
		String token = "2DC64724C4E2413994EFD022676F43A3";
		String url="http://test-api.pingan.com.cn/open/appsvr/yb/open/bill/test?access_token=%s&request_id=%s&timestamp=%s&signature=%s";

		String requestUrl = String.format(url, token, System.currentTimeMillis(),"265372562","signature");
		
		System.out.println(requestUrl);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json;charset=UTF-8");

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("orderNo", "101127");
		body.put("billNo", "billNo222");
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(body, headers);

		ResponseEntity<OpenApiResponse> exchange = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity,
				OpenApiResponse.class);
		
		System.out.println(exchange);
		
	}
	

	public static class AccessTokenResponse {
		private String access_token;
		private String expires_in;
		private String openid;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(String expires_in) {
			this.expires_in = expires_in;
		}

		public String getOpenid() {
			return openid;
		}

		public void setOpenid(String openid) {
			this.openid = openid;
		}

		@Override
		public String toString() {
			return "AccessTokenResponse [access_token=" + access_token + ", expires_in=" + expires_in + ", openid="
					+ openid + "]";
		}

	}

	public static class OpenApiResponse {
		private boolean success;
		private String resultCode;
		private String message;

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getResultCode() {
			return resultCode;
		}

		public void setResultCode(String resultCode) {
			this.resultCode = resultCode;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return "OpenApiResponse [success=" + success + ", resultCode=" + resultCode + ", message=" + message + "]";
		}

	}

}
