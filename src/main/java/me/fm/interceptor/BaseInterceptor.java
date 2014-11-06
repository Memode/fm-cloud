package me.fm.interceptor;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.fm.service.SettingService;
import me.fm.service.impl.SettingServiceImpl;
import me.fm.util.SessionUtil;
import me.fm.util.WebConst;

import org.apache.log4j.Logger;
import org.unique.web.annotation.Intercept;
import org.unique.web.core.ActionContext;
import org.unique.web.core.ActionInvocation;
import org.unique.web.interceptor.AbstractInterceptor;

/**
 * 全局拦截器
 * @author Rex
 */
@Intercept(loadOnStartup=0)
public class BaseInterceptor extends AbstractInterceptor{

	private Logger logger = Logger.getLogger(BaseInterceptor.class);
	
	private SettingService settingService = new SettingServiceImpl();
	
	@Override
	public Object intercept(ActionInvocation ai) throws Exception {
	    
		//System.out.println("全局before");
	    HttpServletRequest request = ActionContext.single().getHttpServletRequest();
	    HttpServletResponse response = ActionContext.single().getHttpServletResponse();
	    
	    Map<String, String> setting =  settingService.getAllSetting();
	    
	    //设置basepath
	    String basePath = request.getContextPath();
	    request.setAttribute("base", basePath);
	    request.setAttribute("static_v", "1.0");
	    request.setAttribute("setting", setting);
	    request.setAttribute("cdn", request.getContextPath());
	    
	    String reqUrl = request.getRequestURI();
	    
	    if(reqUrl.contains("login")){
	    	return ai.invoke();
	    }
	    //拦截后台登录
	    if(reqUrl.startsWith(basePath + "/admin/")){
	    	if(null == SessionUtil.getLoginUser()){
	    		try {
					response.sendRedirect(basePath + WebConst.ADMIN_LOGIN);
					return null;
				} catch (IOException e) {
					logger.warn(e.getMessage());
				}
	    	}
	    	request.setAttribute("login_user", SessionUtil.getLoginUser());
	    }
	    return ai.invoke();
	}
	
}
