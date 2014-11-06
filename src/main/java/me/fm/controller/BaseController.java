package me.fm.controller;

import me.fm.cloud.model.User;
import me.fm.util.SessionUtil;
import me.fm.util.WebConst;

import org.unique.web.annotation.Controller;
import org.unique.web.core.ActionContext;
import org.unique.web.core.R;

/**
 * 控制器基类
 * @author:rex
 * @date:2014年9月9日
 * @version:1.0
 */
@Controller("base")
public class BaseController{

	public R r;
	
	protected Integer uid;
	
	protected Integer page;

	protected Integer pageSize;
	
	public BaseController(){
		if(null != ActionContext.single().getHttpServletRequest()){
			
			r = new R(ActionContext.single().getHttpServletRequest(), ActionContext.single().getHttpServletResponse());
			
			this.page = r.getParaToInt("page", 1);
			
			this.pageSize = r.getParaToInt("pageSize", WebConst.PAGE_SIZE);
			
			User user = SessionUtil.getLoginUser();
			if(null != user){
				uid = user.getUid();
				if(user.getIs_admin() == 1){
					uid = null;
				}
			}
		}
	}
}
