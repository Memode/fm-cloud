package me.fm.controller.admin;

import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.FocusService;
import me.fm.util.WebConst;

import org.apache.commons.lang3.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.plugin.dao.Page;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;
import org.unique.web.core.R;

/**
 * 焦点图管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Controller
public class FocusController extends BaseController {

	@Autowired
	private FocusService focusService;

	/**
	 * 焦点图列表
	 */
	@Action("/admin/focus")
	public void index(R r) {
		Page<Map<String, Object>> focusPage = focusService.getPageMapList(null, null, null, page, pageSize, "create_time desc");
		r.setAttr("focusPage", focusPage);
		r.render("/admin/focus");
	}


	/**
	 * 编辑焦点图
	 */
	@Action("/admin/focus/@id")
	public void edit_focus(Integer id) {
		// 编辑
		if (null != id) {
			Map<String, Object> focusMap = focusService.getMap(null, id);
			r.setAttr("focus", focusMap);
		}
		r.render("/admin/edit_focus");
	}

	/**
	 * 保存焦点图
	 */
	@Action
	public void save(R r) {
		String step = r.getPara("step");
		if (StringUtils.isNoneBlank(step) && step.equals("submit")) {
			Integer id = r.getParaToInt("id");
			Integer type = r.getParaToInt("type");
			String title = r.getPara("title");
			String introduce = r.getPara("introduce");
			String pic = r.getPara("pic");
			Integer status = r.getParaToInt("status");
			boolean flag = false;
			uid = 1;
			if (null != id) {
				flag = focusService.update(id, title, introduce, pic, type, status) > 0;
			} else {
				flag = focusService.save(title, introduce, pic, type);
			}
			if (flag) {
				r.renderText(WebConst.MSG_SUCCESS);
			} else {
				r.renderText(WebConst.MSG_ERROR);
			}
			return;
		}
		r.render("/admin/edit_focus");
	}
	
}
