package me.fm.controller.admin;

import java.util.List;
import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.RadioService;
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
public class RadioController extends BaseController {

	@Autowired
	private RadioService radioService;
	@Autowired
	private SpecialService specialService;

	/**
	 * 后台首页
	 */
	@Action("/admin/radio/index")
	public void index() {
		String title = r.getPara("title");
		Integer sid = r.getParaToInt("sid");
		Integer status = r.getParaToInt("status");
		Page<Map<String, Object>> radioPage = radioService.getPageMapList(uid, title, sid, status, page, pageSize,
				"id desc");
		r.setAttr("pageMap", radioPage);
		r.render("/admin/radio");
	}

	/**
	 * 编辑电台
	 */
	@Action("/admin/radio/@id")
	public void edit_radio(Integer id) {
		// 编辑
		if (null != id) {
			Map<String, Object> radio = radioService.getMap(null, id);
			r.setAttr("radio", radio);
		}
		List<Map<String, Object>> specialList = specialService.getList(null, 2, null, null, 1, "id desc");
		r.setAttr("specialList", specialList);
		r.render("/admin/edit_radio");
	}

	/**
	 * 保存音乐
	 */
	@Action("/admin/radio/save")
	public void save() {
		String step = r.getPara("step");
		if (StringUtils.isNoneBlank(step)) {
			Integer id = r.getParaToInt("id");
			String title = r.getPara("title");
			Integer sid = r.getParaToInt("sid");
			String url = r.getPara("url");

			boolean flag = false;
			if (null != id) {
				flag = radioService.update(id, title, sid, url);
			} else {
				flag = radioService.save(uid, title, sid, url);
			}
			if (flag) {
				r.renderText(WebConst.MSG_SUCCESS);
			} else {
				r.renderText(WebConst.MSG_ERROR);
			}
			return;
		}
		List<Map<String, Object>> specialList = specialService.getList(null, 2, null, null, 1, "id desc");
		r.setAttr("specialList", specialList);
		r.render("/admin/edit_radio");
	}

	/**
	 * 删除电台
	 */
	@Action("/admin/radio/del")
	public void del() {
		Integer id = r.getParaToInt("id");
		boolean flag = false;
		if (null != id) {
			flag = radioService.delete(id);
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
