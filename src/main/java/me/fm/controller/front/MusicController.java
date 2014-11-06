package me.fm.controller.front;

import java.util.List;
import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.MusicService;
import me.fm.util.WebConst;

import org.unique.ioc.annotation.Autowired;
import org.unique.plugin.dao.Page;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;
import org.unique.web.core.R;

/**
 * 前台音乐展示
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
@Controller("/music")
public class MusicController extends BaseController {
	
	@Autowired
	private MusicService musicService;
	
	public MusicController() {
		System.out.println("aaaaaa");
	}
	
	@Action
	public void index(R r){
		String singer = r.getPara("singer");
		String song = r.getPara("song");
		Page<Map<String, Object>> pageData = musicService.getPageMapList(uid, singer, song, null, null, 1, page, 9,
				"create_time desc");
		r.setAttr("pageEntity", pageData);
		r.render("/music");
	}
	
	/**
	 * 最新10首歌
	 */
	@Action
	public void new_music(){
		Page<Map<String, Object>> top10List = musicService.getPageMapList(null, null, null, null, null, 1, page, 9, "create_time desc");
		r.renderJson(top10List);
	}
	
	/**
	 * 最热门的歌
	 */
	@Action
	public void hot(){
		Page<Map<String, Object>> top10List = musicService.getPageMapList(null, null, null, null, null, 1, page, 9, "like_count desc");
		r.renderJson(top10List);
	}
	
	/**
	 * 按歌名查询
	 */
	@Action
	public void search(R r){
		String song = r.getPara("song");
		String singer = r.getPara("singer");
		Page<Map<String, Object>> top10List = musicService.getPageMapList(null, singer, song, null, null, 1, 1, 10, "like_count desc");
		r.renderJson(top10List);
	}
	
	/**
	 * 随机推荐
	 */
	@Action
	public void random(R r){
		List<Map<String, Object>> randomList = musicService.getRandom(10);
		r.renderJson(randomList);
	}
	
	/**
	 * 删除一首音乐
	 */
	@Action
	public void delete_music(R r){
		Integer mid = r.getParaToInt("mid");
		int count = musicService.delete(mid);
		if(count > 0){
			r.renderText(WebConst.MSG_SUCCESS);
		} else{
			r.renderText(WebConst.MSG_FAILURE);
		}
	}
	
	/**
	 * 1喜欢/2收听/3下载音乐 点击+1
	 */
	@Action
	public void hit(R r){
		Integer mid = r.getParaToInt("mid");
		Integer type = r.getParaToInt("type");
		musicService.like(mid, type);
		r.renderText(WebConst.MSG_SUCCESS);
	}
	
	/**
	 * 查询音乐信息
	 */
	@Action("get_music")
	public void getMusic(R r){
		Integer mid = r.getParaToInt("mid");
		if(null != mid){
			Map<String, Object> map = musicService.getMap(null, mid);
			r.renderJson(map);
		}
	}
	
}
