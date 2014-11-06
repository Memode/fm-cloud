package me.fm.service.impl;

import java.util.List;
import java.util.Map;

import me.fm.api.QiniuApi;
import me.fm.cloud.model.Music;
import me.fm.cloud.model.Radio;
import me.fm.cloud.model.Special;
import me.fm.cloud.model.User;
import me.fm.service.FileService;
import me.fm.service.RadioService;
import me.fm.service.SpecialService;
import me.fm.service.UserService;
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
public class RadioServiceImpl implements RadioService {

	private Logger logger = Logger.getLogger(RadioServiceImpl.class);

	@Autowired
	private FileService fileService;
	@Autowired
	private SpecialService specialService;
	@Autowired
	private UserService userService;

	@Override
	public Radio get(Integer id) {
		return Radio.db.findByPK(id);
	}

	@Override
	public Map<String, Object> getMap(Radio radio, Integer id) {
		Map<String, Object> resultMap = CollectionUtil.newHashMap();
		if (null == radio) {
			radio = this.get(id);
		}
		if (null != radio) {
			resultMap = BeanUtil.toMap(radio);
			// 歌曲路径
			if (StringUtils.isNotBlank(radio.getUrl())) {
				if (radio.getUrl().startsWith("http://")) {
					resultMap.put("radio_url", radio.getUrl());
				} else {
					String url = QiniuApi.getUrlByKey(radio.getUrl());
					resultMap.put("radio_url", url);
				}
			}
			// 上传日期
			if (null != radio.getCreate_time()) {
				resultMap.put("date_zh", DateUtil.convertIntToDatePattern(radio.getCreate_time(), "yyyy/MM/dd"));
				resultMap.put("time_zh",
						DateUtil.convertIntToDatePattern(radio.getCreate_time(), "yyyy-MM-dd HH:mm:ss"));
			}
			// 上传人
			if(null != radio.getUid()){
				User user = userService.getByUid(radio.getUid());
				resultMap.put("nickname", user.getNickname());
			}
			// 所属专辑
			if(null != radio.getSid()){
				Special special = specialService.get(radio.getSid());
				resultMap.put("special", special.getTitle());
			}
		}
		return resultMap;
	}

	@Override
	public boolean save(Integer uid, String title, Integer sid, String url) {
		int count = 0;
		uid = (null == uid) ? 1 : uid;
		String url_key = "";
		if (StringUtils.isNotBlank(url)) {
			url_key = url;
			String filePath = WebConst.getWebRootPath() + url;
			if (!url.startsWith("http://") && FileUtil.isFile(filePath)) {
				//上传radio
				fileService.upload(url_key, filePath);
			}
		}
		try {
			count = Music.db.insert("insert into t_radio(uid, title, sid, url, create_time, status) "
					+ "values(?, ?, ?, ?, ?, ?)", uid, title, sid, url_key, DateUtil.getCurrentTime(), 1);
		} catch (UpdateException e) {
			logger.warn("添加电台失败：" + e.getMessage());
			count = 0;
		}
		return count > 0;
	}

	@Override
	public boolean update(Integer id, String title, Integer sid, String url) {
		int count = 0;
		if (null != id) {
			Radio radio = this.get(id);
			String url_key = "";
			SqlBase base = SqlBase.update("update t_radio");
			//判断音乐文件是否修改
			if (StringUtils.isNotBlank(url)) {
				url_key = url;
				String filePath = WebConst.getWebRootPath() + url;
				if (!url.startsWith("http://") && FileUtil.isFile(filePath)) {
					//上传电台文件
					fileService.upload(url_key, filePath);
					//删除原有文件
					fileService.delete(radio.getUrl());
				}
				base.set("url", url_key);
			}
			
			if (StringUtils.isNotBlank(title)) {
				base.set("title", title);
			}
			if (null != sid) {
				base.set("sid", sid);
			}
			base.eq("id", radio.getId());
			try {
				if (base.getSetMap().size() == 0) {
					return true;
				}
				count = Radio.db.update(base.getSQL(), base.getParams());
			} catch (UpdateException e) {
				logger.warn("更新电台失败：" + e.getMessage());
				count = 0;
			}
		}
		return count > 0;
	}

	@Override
	public List<Map<String, Object>> getList(Integer uid, String title, Integer sid, Integer status, String order) {
		SqlBase base = SqlBase.select("select t.* from t_radio t");
		base.eq("uid", uid).likeLeft("title", title).eq("status", status).eq("sid", sid).order(order);
		List<Radio> list = Radio.db.findList(base.getSQL(), base.getParams());
		return this.getRadioMapList(list);
	}

	private List<Map<String, Object>> getRadioMapList(List<Radio> list) {
		List<Map<String, Object>> mapList = CollectionUtil.newArrayList();
		for (int i = 0, len = list.size(); i < len; i++) {
			Radio radio = list.get(i);
			if (null != radio) {
				Map<String, Object> map = this.getMap(radio, null);
				mapList.add(map);
			}
		}
		return mapList;
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer uid, String title, Integer sid, Integer status,
			Integer page, Integer pageSize, String order) {
		Page<Radio> pageList = this.getPageList(uid, title, sid, status, page, pageSize, order);

		List<Radio> musicList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount(), pageList.getPage(),
				pageList.getPageSize());

		List<Map<String, Object>> listMap = this.getRadioMapList(musicList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	private Page<Radio> getPageList(Integer uid, String title, Integer sid, Integer status, Integer page,
			Integer pageSize, String order) {
		SqlBase base = SqlBase.select("select t.* from t_radio t");
		base.eq("t.uid", uid).likeLeft("t.title", title).eq("t.status", status).eq("t.sid", sid).order(order);
		return Radio.db.findListPage(page, pageSize, base.getSQL(), base.getParams());
	}
	
	@Override
	public boolean delete(Integer id) {
		if (null != id) {
			Radio radio = this.get(id);
			if(null != radio){
				if(null != radio.getUrl() && !radio.getUrl().startsWith("http://")){
					fileService.delete(radio.getUrl());
				}
				return Radio.db.update("delete from t_radio where id = ?", id) > 0;
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean enable(Integer id, Integer status) {
		if (null != id) {
			return Radio.db.update("update t_radio set status = ? where id = ?", status, id) > 0;
		}
		return false;
	}

}
