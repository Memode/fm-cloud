package me.fm.util;

import javax.servlet.http.HttpServletRequest;

import me.fm.cloud.model.User;

import org.unique.web.core.ActionContext;
import org.unique.web.core.Const;

/**
 * 设置session
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
public class SessionUtil {

	/**
	 * 设置登录用户
	 * @param user
	 */
	public static void setLoginUser(User user) {
		ActionContext.single().getHttpSession().setAttribute(WebConst.LOGIN_USER_SESSION_KEY, user);
	}

	/**
	 * 获取登录用户
	 * @return
	 */
	public static User getLoginUser() {
		Object user = ActionContext.single().getHttpSession().getAttribute(WebConst.LOGIN_USER_SESSION_KEY);
		if (null != user) {
			return (User) user;
		}
		return null;
	}
	
	/**
	 * 获取验证码
	 * @param request
	 * @return
	 */
	public static String getVerifyCode(HttpServletRequest request) {
		if(null != request.getSession().getAttribute(Const.SESSION_CAPTCH_TOKEN)){
			return request.getSession().getAttribute(Const.SESSION_CAPTCH_TOKEN).toString();
		}
		return "";
	}

	public static void removeLoginUser() {
		ActionContext.single().getHttpSession().removeAttribute(WebConst.LOGIN_USER_SESSION_KEY);
	}
	
}
