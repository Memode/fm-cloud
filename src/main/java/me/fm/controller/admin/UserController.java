package me.fm.controller.admin;

import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.UserService;
import me.fm.util.WebConst;

import org.unique.ioc.annotation.Autowired;
import org.unique.plugin.dao.Page;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;

/**
 * 用户后台
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Controller
public class UserController extends BaseController {

	@Autowired
	private UserService userService;
	
	/**
	 * 后台首页
	 */
	@Action("/admin/user/index")
	public void index() {
		Page<Map<String, Object>> userPage = userService.getPageMapList(null, null, null, page, pageSize, "uid desc");
		r.setAttr("userPage", userPage);
		r.render("/admin/user");
	}

	/**
	 * 删除用户
	 */
	@Action("/admin/user/del")
	public void del() {
		Integer uid = r.getParaToInt("uid");
		int count = userService.update(uid, null, null, null, 0);
		if (count > 0) {
			r.renderText(WebConst.MSG_SUCCESS);
		} else {
			r.renderText(WebConst.MSG_FAILURE);
		}
	}

	/**
	 * 保存用户
	 */
	@Action("/admin/user/save")
	public void save() {
		Integer uid = r.getParaToInt("uid");
		if (null != uid) {
			String nickname = r.getPara("nickname");
			Long space_size = r.getParaToLong("space_size");
			int count = userService.update(uid, null, nickname, space_size, null);
			if (count > 0) {
				r.renderText(WebConst.MSG_SUCCESS);
			} else {
				r.renderText(WebConst.MSG_FAILURE);
			}
			return;
		}
		r.render("/admin/edit_user");
	}

	/**
	 * 编辑用户
	 */
	@Action("/admin/users/@uid")
	public void edit_user(Integer uid) {
		// 编辑
		if (null != uid) {
			Map<String, Object> user = userService.getMap(null, uid);
			r.setAttr("user", user);
		}
		r.render("/admin/edit_user");
	}
	
}
