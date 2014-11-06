package me.fm.controller.admin;

import java.util.List;

import me.fm.cloud.model.Mcat;
import me.fm.controller.BaseController;
import me.fm.service.McatService;
import me.fm.util.WebConst;

import org.unique.ioc.annotation.Autowired;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;

/**
 * 分类管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Controller("/admin/mcat")
public class McatController extends BaseController {

	@Autowired
	private McatService mcatService;

	/**
	 * 分类列表
	 */
	@Action
	public void index() {
		List<Mcat> mcatList = mcatService.getList(null);
		r.setAttr("mcatList", mcatList);
		r.render("/admin/mcat");
	}

	/**
	 * 保存分类
	 */
	@Action
	public void save() {
		Integer id = r.getParaToInt("id");
		String name = r.getPara("name");
		boolean flag = false;
		if (null != id) {
			flag = mcatService.update(id, name, null);
		} else {
			flag = mcatService.save(name);
		}
		if (flag) {
			r.renderText(WebConst.MSG_SUCCESS);
		} else {
			r.renderText(WebConst.MSG_FAILURE);
		}
	}

}
