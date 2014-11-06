package me.fm.controller.front;

import java.io.IOException;

import me.fm.cloud.model.Open;
import me.fm.cloud.model.User;
import me.fm.controller.BaseController;
import me.fm.service.OpenService;
import me.fm.service.UserService;
import me.fm.util.SessionUtil;
import me.fm.util.WebConst;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;
import org.unique.web.render.impl.PatchcaRender;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;

/**
 * 前台首页
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
@Controller("/")
public class IndexController extends BaseController {

	private Logger logger = Logger.getLogger(IndexController.class);
			
	@Autowired
	private UserService userService;
	@Autowired
	private OpenService openService;
	
	@Action("index")
	public void index() {
		r.render("index");
	}

	@Action
	public void up(){
		r.render("up");
	}
	
	/**
	 * 用qq登录
	 */
	@Action
	public void qq_login() {
		try {
			r.getResponse().sendRedirect(new Oauth().getAuthorizeURL(r.getRequest()));
		} catch (QQConnectException e) {
			logger.warn(e.getMessage());
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}

	/**
	 * 用qq登录回调
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@Action
	public void qq_callback() throws ClientProtocolException, IOException {
		try {
			AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(r.getRequest());

			String accessToken = null, openID = null;
			long tokenExpireIn = 0L;

			if (accessTokenObj.getAccessToken().equals("")) {
				// 我们的网站被CSRF攻击了或者用户取消了授权做一些数据统计工作
				System.out.print("没有获取到响应参数");
				r.redirect("/admin/login");
			} else {
				accessToken = accessTokenObj.getAccessToken();
				tokenExpireIn = accessTokenObj.getExpireIn();
				
				WebConst.QQ_TOKEN = accessToken;
				WebConst.QQ_TOKEN_EXPIREIN = tokenExpireIn;
				
				// 利用获取到的accessToken 去获取当前用的openid -------- start
				OpenID openIDObj = new OpenID(accessToken);
				openID = openIDObj.getUserOpenID();
				
				//去数据库查询是否绑定openid
				Open openUser = openService.get(null, openID, 1);
				if(null != openUser){
					User user = userService.get(openUser.getEmail(), 1);
					if(null != user){
						SessionUtil.setLoginUser(user);
						r.redirect("/admin/index");
					}
				} else{
					UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
					UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
					r.setAttr("openid", openID);
					r.setAttr("nickname", userInfoBean.getNickname());
					r.render("/bind_qq");
				}
			}
		} catch (QQConnectException e) {
			logger.warn(e.getMessage());
		}
	}
	
	/**
	 * 确认绑定qq
	 */
	@Action
	public void save_bind_qq(){
		String openId = r.getPara("openid");
		String email = r.getPara("email");
		String nickname = r.getPara("nickname");
		String password = r.getPara("password");
		if(StringUtils.isNotBlank(openId) && StringUtils.isNotBlank(email)){
			if(null == openService.get(email, null, 1)){
				int count = openService.save(email, 1, openId);
				if(count > 0){
					User user = userService.get(email, 1);
					//登录管理员 放入session
					if (null != user) {
						SessionUtil.setLoginUser(user);
						r.renderText(WebConst.MSG_SUCCESS);
					} else {
						user = userService.register(nickname, email, password, StringUtils.getIP(r.getRequest()));
						if(null != user){
							SessionUtil.setLoginUser(user);
							r.renderText(WebConst.MSG_SUCCESS);
						} else{
							r.renderText(WebConst.MSG_FAILURE);
						}
					}
				} else{
					r.renderText(WebConst.MSG_FAILURE);
				}
			} else{
				// 已经绑定过
				r.renderText(WebConst.MSG_EXIST);
			}
			return;
		}
		r.renderText(WebConst.MSG_ERROR);
	}

	@Action
	public void verify_code() {
		r.render(new PatchcaRender());
	}
}
