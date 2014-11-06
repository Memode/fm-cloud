package me.fm.controller.admin;

import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.AlbumService;
import me.fm.util.WebConst;

import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.plugin.dao.Page;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;
import org.unique.web.core.R;

/**
 * 图库管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Controller("/admin/pic")
public class AlbumController extends BaseController {
	
	@Autowired
	private AlbumService pictureService;
	
	/**
	 * 图库列表
	 */
	@Action
	public void index(R r) {
		String title = r.getPara("title");
		Integer status = r.getParaToInt("status");
		Page<Map<String, Object>> picPage = pictureService.getPageMapList(uid, title, status, page, pageSize, "id desc");
		r.setAttr("picPage", picPage);
		r.render("/admin/picture");
	}
	
	/**
	 * 保存图库
	 */
	@Action
	public void save(R r){
		String step = r.getPara("step");
		if(StringUtils.isNotBlank(step) && step.equals("submit")){
			Integer id = r.getParaToInt("id");
			String title = r.getPara("title");
			String introduce = r.getPara("introduce");
			String pics = r.getPara("pics");
			String cover = r.getPara("cover");
			Integer status = r.getParaToInt("status");
			boolean flag = false;
			if(null != id){
				flag = pictureService.update(id, title, introduce, cover, pics, status);
			} else{
				flag = pictureService.save(uid, title, introduce, null, pics, status);
			}
			if(flag){
				r.renderText(WebConst.MSG_SUCCESS);
			} else{
				r.renderText(WebConst.MSG_FAILURE);
			}
			return;
		}
		r.render("/admin/edit_pic");
	}
	
	/**
	 * 显示图库
	 */
	@Action("/admin/pic/@id")
	public void edit(Integer id, R r) {
		// 编辑
		if (null != id) {
			Map<String, Object> picture = pictureService.getMap(null, id);
			r.setAttr("picture", picture);
		}
		r.render("/admin/edit_pic");
	}
	
}
