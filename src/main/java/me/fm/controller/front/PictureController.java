package me.fm.controller.front;

import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.AlbumService;

import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.plugin.dao.Page;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;

/**
 * 相册/图片管理
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Controller("/pictures")
public class PictureController extends BaseController {

	@Autowired
	private AlbumService pictureService;
	
	@Action
	public void index(){
		String mode = r.getPara("mode");
		Page<Map<String, Object>> picPage = pictureService.getPageMapList(uid, null, 1, page, pageSize, "create_time desc");
		if(StringUtils.isNotBlank(mode) && mode.equals("ajax")){
			r.renderJson(picPage);
			return;
		} else{
			r.setAttr("picPage", picPage);
		}
		r.render("/picture");
	}
	
	/**
	 * 相册详情
	 */
	@Action("/pictures/@id")
	public void detail(Integer id){
		if(null != id){
			Map<String, Object> pictrue = pictureService.getMap(null, id);
			r.setAttr("pictrue", pictrue);
		}
		r.render("/preview");
	}
}
