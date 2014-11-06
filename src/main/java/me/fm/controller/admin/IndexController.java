package me.fm.controller.admin;


import me.fm.cloud.model.User;
import me.fm.controller.BaseController;
import me.fm.service.UserService;
import me.fm.util.SessionUtil;
import me.fm.util.WebConst;

import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;
import org.unique.web.core.R;

/**
 * 用户后台首页
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Controller("/admin")
public class IndexController extends BaseController {

	@Autowired
	private UserService userService;
	
	/**
	 * 后台首页
	 */
	@Action
	public void index(R r) {
		r.render(this, "index");
	}

	/**
	 * 管理员退出
	 */
	@Action
	public void logout() {
		SessionUtil.removeLoginUser();
		r.redirect("/");
	}

	/**
	 * 用户登录
	 */
	@Action
	public void login() {
		String step = r.getPara("step");
		if (null != step && step.equals("login")) {
			String email = r.getPara("login_name");
			String verify_code = r.getPara("verify_code");
			String pass_word = r.getPara("pass_word");
			String currentcode = SessionUtil.getVerifyCode(r.getRequest());
			if(StringUtils.isNotBlank(verify_code) && verify_code.equalsIgnoreCase(currentcode)){
				User user = userService.login(email, pass_word);
				//登录成功
				if (null != user) {
					SessionUtil.setLoginUser(user);
					r.renderText(WebConst.MSG_SUCCESS);
				} else {
					r.renderText(WebConst.MSG_FAILURE);
				}
			} else{
				r.renderText(WebConst.MSG_VERIFY_ERROR);
			}
			return;
		}
		r.render(this, "login");
	}
	
}
