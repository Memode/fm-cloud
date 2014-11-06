package me.fm.controller.front;

import java.util.List;
import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.FocusService;
import me.fm.service.MusicService;
import me.fm.service.OpenService;
import me.fm.service.RadioService;
import me.fm.service.SpecialService;
import me.fm.service.UserService;

import org.unique.ioc.annotation.Autowired;
import org.unique.plugin.dao.Page;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;

@Controller("/special")
public class SpecialController extends BaseController {

	@Autowired
	private UserService userService;
	@Autowired
	private MusicService musicService;
	@Autowired
	private SpecialService specialService;
	@Autowired
	private OpenService openService;
	@Autowired
	private FocusService focusService;
	@Autowired
	private RadioService radioService;
	
	@Action
	public void index(){
		//焦点图
		List<Map<String, Object>> focusList = focusService.getList(1, null, 1, "create_time desc");
		//精选电台
		Page<Map<String, Object>> finePage = specialService.getPageMapList(null, 2, 1, null, 1, page, pageSize, "last_time desc");
		//热门电台
		Page<Map<String, Object>> hotPage = specialService.getPageMapList(null, 2, null, null, 1, page, pageSize, "hit desc");
		r.setAttr("focusList", focusList);
		r.setAttr("finePage", finePage);
		r.setAttr("hotPage", hotPage);
		r.render("/special");
	}
	
	/**
	 * 电台列表
	 */
	@Action("/special/@sid")
	public void special_content(Integer sid) {
		if(null != sid){
			// 专辑信息
			Map<String, Object> specialMap = specialService.getMap(null, sid);
			// 电台列表
			List<Map<String, Object>> radioList = radioService.getList(null, null, sid, 1, "create_time desc");
			r.setAttr("specialMap", specialMap);
			r.setAttr("radioList", radioList);
		}
		r.render("/radio");
	}
	
}
