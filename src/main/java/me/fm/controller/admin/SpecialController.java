package me.fm.controller.admin;

import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.SpecialService;
import me.fm.util.WebConst;

import org.apache.commons.lang3.StringUtils;
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
public class SpecialController extends BaseController {

	@Autowired
	private SpecialService specialService;

	/**
	 * 后台首页
	 */
	@Action("/admin/special/index")
	public void index() {
		Page<Map<String, Object>> specialPage = specialService.getPageMapList(null, null, null, null, null, page,
				pageSize, "last_time desc");
		r.setAttr("specialPage", specialPage);
		r.render("/admin/special");
	}

	/**
	 * 保存专辑
	 */
	@Action("/admin/special/save")
	public void save() {
		String step = r.getPara("step");
		if (StringUtils.isNoneBlank(step)) {
			Integer id = r.getParaToInt("id");
			Integer is_fine = r.getParaToInt("is_fine");
			String title = r.getPara("title");
			String introduce = r.getPara("introduce");
			String cover = r.getPara("cover");
			Integer status = r.getParaToInt("status");
			boolean flag = false;
			uid = 1;
			if (null != id) {
				flag = specialService.update(id, uid, title, introduce, cover, is_fine, status) > 0;
			} else {
				flag = specialService.save(uid, title, introduce, cover, is_fine, 1);
			}
			if (flag) {
				r.renderText(WebConst.MSG_SUCCESS);
			} else {
				r.renderText(WebConst.MSG_ERROR);
			}
			return;
		}
		r.render("/admin/edit_special");
	}

	/**
	 * 编辑专辑
	 */
	@Action("/admin/special/@sid")
	public void edit_special(Integer sid) {
		// 编辑
		if (null != sid) {
			Map<String, Object> music = specialService.getMap(null, sid);
			r.setAttr("special", music);
		}
		r.render("/admin/edit_special");
	}

	/**
	 * 禁用专辑
	 */
	@Action("/admin/special/enable")
	public void enable() {
		Integer sid = r.getParaToInt("sid");
		Integer status = r.getParaToInt("status");
		boolean flag = false;
		if (null != sid && null != status) {
			flag = specialService.enable(sid, status);
			if (flag) {
				r.renderText(WebConst.MSG_SUCCESS);
			} else {
				r.renderText(WebConst.MSG_FAILURE);
			}
		} else {
			r.renderText(WebConst.MSG_FAILURE);
		}
	}
	
}
