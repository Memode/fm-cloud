package me.fm.util;

import javax.servlet.http.HttpServletRequest;

import org.unique.web.core.ActionContext;

/**
 * 应用常量数据
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
public class WebConst {
	
	public static final String LOGIN_USER_SESSION_KEY = "login_user";
	
	public static final String MSG_SUCCESS = "success";
	
	public static final String MSG_FAILURE = "failure";
	
	public static final String MSG_ERROR = "error";
	
	public static final String MSG_VERIFY_ERROR = "verify_error";
	
	public static final String MSG_EXIST = "exist";
	
	public static final String ADMIN_LOGIN = "/admin/login";
	
	public static final Integer PAGE_SIZE = 10;
	
	public static String QQ_TOKEN = "";
	
	public static Long QQ_TOKEN_EXPIREIN = 0L;
	
	public static String getWebRootPath(){
		HttpServletRequest request = ActionContext.single().getHttpServletRequest();
		return request.getServletContext().getRealPath("/");
	}
}
