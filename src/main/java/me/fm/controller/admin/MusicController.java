package me.fm.controller.admin;

import java.util.List;
import java.util.Map;

import me.fm.cloud.model.Mcat;
import me.fm.controller.BaseController;
import me.fm.service.McatService;
import me.fm.service.MusicService;
import me.fm.service.SpecialService;
import me.fm.util.WebConst;

import org.apache.commons.lang3.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.plugin.dao.Page;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;

/**
 * 音乐管理
 * @author:rex
 * @date:2014年10月11日
 * @version:1.0
 */
@Controller
public class MusicController extends BaseController {

	@Autowired
	private MusicService musicService;
	@Autowired
	private McatService mcatService;
	@Autowired
	private SpecialService specialService;

	/**
	 * 音乐列表
	 */
	@Action("/admin/music/index")
	public void index() {
		String singer = r.getPara("singer");
		String song = r.getPara("song");
		Page<Map<String, Object>> musicPage = musicService.getPageMapList(uid, singer, song, null, null, 1, page,
				pageSize, "id desc");
		r.setAttr("pageMap", musicPage);
		r.render("/admin/music");
	}

	/**
	 * 显示音乐
	 */
	@Action("/admin/music/@id")
	public void edit_music(Integer mid) {
		// 编辑
		if (null != mid) {
			Map<String, Object> music = musicService.getMap(null, mid);
			r.setAttr("music", music);
		}
		List<Mcat> mcatList = mcatService.getList(1);
		List<Map<String, Object>> specialList = specialService.getList(null, 1, null, null, 1, "id desc");
		r.setAttr("catList", mcatList);
		r.setAttr("specialList", specialList);
		r.render("/admin/edit_music");
	}

	/**
	 * 保存音乐
	 */
	@Action("/admin/music/save")
	public void save() {
		String step = r.getPara("step");
		if (StringUtils.isNoneBlank(step)) {
			Integer mid = r.getParaToInt("mid");
			String singer = r.getPara("singer");
			String song = r.getPara("song");
			Integer sid = r.getParaToInt("sid");
			String song_path = r.getPara("song_path");
			String cover_path = r.getPara("cover_path");
			String introduce = r.getPara("introduce");
			String lrc = r.getPara("lrc");
			String cids = r.getPara("cids");

			boolean flag = false;
			uid = 1;
			if (null != mid) {
				flag = musicService.update(mid, singer, song, song_path, cover_path, introduce, cids, lrc, null, sid) > 0;
			} else {
				flag = musicService.save(uid, singer, song, song_path, cover_path, introduce, cids, lrc, null, sid);
			}
			if (flag) {
				r.renderText(WebConst.MSG_SUCCESS);
			} else {
				r.renderText(WebConst.MSG_ERROR);
			}
			return;
		}
		List<Mcat> mcatList = mcatService.getList(1);
		List<Map<String, Object>> specialList = specialService.getList(null, 1, null, null, 1, "id desc");
		r.setAttr("catList", mcatList);
		r.setAttr("specialList", specialList);
		r.render("/admin/edit_music");
	}

	/**
	 * 删除音乐
	 */
	@Action("/admin/music/del")
	public void del() {
		Integer mid = r.getParaToInt("mid");
		boolean flag = false;
		if (null != mid) {
			flag = musicService.enable(mid, 0);
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
