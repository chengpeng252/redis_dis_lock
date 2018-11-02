package util;

public class CommonException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String messge;
	public CommonException() {
	
	}
	public CommonException(String code,String messge) {
		this.code=code;
		this.messge=messge;
	}
	public static CommonException CommonExceptionLockTime() {
		return new CommonException("400001","ËøµÈ´ý³¬Ê±");
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessge() {
		return messge;
	}
	public void setMessge(String messge) {
		this.messge = messge;
	}

}
