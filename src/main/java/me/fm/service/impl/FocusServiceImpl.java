package me.fm.service.impl;

import java.util.List;
import java.util.Map;

import me.fm.api.QiniuApi;
import me.fm.cloud.model.Focus;
import me.fm.cloud.model.Special;
import me.fm.service.FileService;
import me.fm.service.FocusService;
import me.fm.util.BeanUtil;
import me.fm.util.WebConst;

import org.apache.log4j.Logger;
import org.unique.common.tools.CollectionUtil;
import org.unique.common.tools.DateUtil;
import org.unique.common.tools.FileUtil;
import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.ioc.annotation.Service;
import org.unique.plugin.dao.Page;
import org.unique.plugin.dao.SqlBase;
import org.unique.plugin.db.exception.UpdateException;

@Service
public class FocusServiceImpl implements FocusService {

	private Logger logger = Logger.getLogger(FocusServiceImpl.class);
	@Autowired
	private FileService fileService;
	
	@Override
	public Focus get(Integer id) {
		return Focus.db.findByPK(id);
	}

	@Override
	public Map<String, Object> getMap(Focus focus, Integer id) {
		Map<String, Object> resultMap = CollectionUtil.newHashMap();
		if (null == focus) {
			focus = this.get(id);
		}
		if (null != focus) {
			resultMap = BeanUtil.toMap(focus);
			// 焦点图
			if (StringUtils.isNotBlank(focus.getPic())) {
				if (focus.getPic().startsWith("http://")) {
					resultMap.put("pic_url", focus.getPic());
				} else {
					String cover_url = QiniuApi.getUrlByKey(focus.getPic());
					resultMap.put("pic_url", cover_url);
				}
			}
			if(null != focus.getCreate_time()){
				resultMap.put("date_zh", DateUtil.convertIntToDatePattern(focus.getCreate_time(), "yyyy/MM/dd"));
			}
		}
		return resultMap;
	}

	@Override
	public boolean save(String title, String introduce, String pic, Integer type) {
		int count = 0;
		String key = "";
		if (StringUtils.isNotBlank(pic)) {
			key = pic;
			String filePath = WebConst.getWebRootPath() + pic;
			if (!pic.startsWith("http://") && FileUtil.isFile(filePath)) {
				//上传音乐
				fileService.upload(key, filePath);
			}
		}
		try {
			Integer create_time = DateUtil.getCurrentTime();
			count = Focus.db.insert("insert into t_focus(title, introduce, pic, type, create_time, status) values(?,?,?,?,?,?)", 
					title, introduce, key, type, create_time, 1);
		} catch (UpdateException e) {
			logger.warn("保存焦点图失败：" + e.getMessage());
			count = 0;
		}
		return count > 0;
	}

	@Override
	public int update(Integer id, String title, String introduce, String pic, Integer type, Integer status) {
		int count = 0;
		if (null != id) {
			Focus focus = this.get(id);
			if (null != focus) {
				SqlBase base = SqlBase.update("update t_focus");

				if (StringUtils.isNotBlank(title) && !title.equals(focus.getTitle())) {
					base.set("title", title);
				}
				if (StringUtils.isNotBlank(introduce) && !introduce.equals(focus.getIntroduce())) {
					base.set("introduce", introduce);
				}
				// 幻灯片是否修改
				if (StringUtils.isNotBlank(pic)) {
					String pic_key = pic;
					String filePath = WebConst.getWebRootPath() + pic;
					if (!pic.startsWith("http://") && FileUtil.isFile(filePath)) {
						//上传封面
						fileService.upload(pic_key, filePath);
						//删除原有文件
						fileService.delete(focus.getPic());
					}
					base.set("pic", pic_key);
				}
				base.eq("id", id);
				try {
					if(base.getSetMap().size() > 0){
						count = Focus.db.update(base.getSQL(), base.getParams());
					}
				} catch (UpdateException e) {
					logger.warn("更新焦点图失败：" + e.getMessage());
					count = 0;
				}
			}
		}
		return count;
	}

	@Override
	public List<Map<String, Object>> getList(Integer type, String title, Integer status, String order) {
		SqlBase base = SqlBase.select("select t.* from t_focus t");
		base.eq("type", type).likeLeft("title", title).eq("status", status).order(order);
		List<Focus> list = Focus.db.findList(base.getSQL(), base.getParams());
		return this.getFocusMapList(list);
	}

	private List<Map<String, Object>> getFocusMapList(List<Focus> list) {
		List<Map<String, Object>> mapList = CollectionUtil.newArrayList();
		for (int i = 0, len = list.size(); i < len; i++) {
			Focus focus = list.get(i);
			if (null != focus) {
				mapList.add(this.getMap(focus, null));
			}
		}
		return mapList;
	}

	@Override
	public Page<Focus> getPageList(Integer type, String title, Integer status, Integer page, Integer pageSize,
			String order) {
		SqlBase base = SqlBase.select("select t.* from t_focus t");
		base.eq("type", type).likeLeft("title", title).eq("status", status).order(order);
		return Focus.db.findListPage(page, pageSize, base.getSQL(), base.getParams());
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer type, String title, Integer status, Integer page,
			Integer pageSize, String order) {
		Page<Focus> pageList = this.getPageList(type, title, status, page, pageSize, order);

		List<Focus> focusList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount(), pageList.getPage(),
				pageList.getPageSize());

		List<Map<String, Object>> listMap = this.getFocusMapList(focusList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public boolean enable(Integer id, Integer status) {
		if (null != id) {
			try {
				SqlBase base = SqlBase.update("update t_focus");
				base.set("status", status).eq("id", id);
				return Special.db.update(base.getSQL(), base.getParams()) > 0;
			} catch (UpdateException e) {
				logger.warn("删除焦点图失败：" + e.getMessage());
				return false;
			}
		}
		return false;
	}

}
