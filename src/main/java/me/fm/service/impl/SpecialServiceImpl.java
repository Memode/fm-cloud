package me.fm.service.impl;

import java.util.List;
import java.util.Map;

import me.fm.api.QiniuApi;
import me.fm.cloud.model.Special;
import me.fm.cloud.model.User;
import me.fm.service.FileService;
import me.fm.service.MusicService;
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
public class SpecialServiceImpl implements SpecialService {

	private Logger logger = Logger.getLogger(SpecialServiceImpl.class);

	@Autowired
	private FileService fileService;
	@Autowired
	private MusicService musicService;
	@Autowired
	private RadioService radioService;
	@Autowired
	private UserService userService;

	@Override
	public Special get(Integer sid) {
		return Special.db.findByPK(sid);
	}

	@Override
	public Map<String, Object> getMap(Special special, Integer sid) {
		Map<String, Object> resultMap = CollectionUtil.newHashMap();
		if (null == special) {
			special = this.get(sid);
		}
		if (null != special) {
			resultMap = BeanUtil.toMap(special);
			
			// 封面
			if (StringUtils.isNotBlank(special.getCover())) {
				if (special.getCover().startsWith("http://")) {
					resultMap.put("cover_url", special.getCover());
				} else {
					String cover_url = QiniuApi.getUrlByKey(special.getCover());
					resultMap.put("cover_url", cover_url);
				}
			}
			// 创建时间
			if (null != special.getCreate_time()) {
				resultMap.put("date_zh",
						DateUtil.convertIntToDatePattern(special.getCreate_time(), "yyyy-MM-dd"));
				resultMap.put("create_time_zh",
						DateUtil.convertIntToDatePattern(special.getCreate_time(), "yyyy-MM-dd HH:mm"));
			}
			// 上传人
			if(null != special.getUid()){
				User user = userService.getByUid(special.getUid());
				resultMap.put("nickname", user.getNickname());
			}
			// 最后更新时间
			if (null != special.getLast_time()) {
				resultMap.put("last_time_zh",
						DateUtil.convertIntToDatePattern(special.getLast_time(), "yyyy-MM-dd HH:mm"));
			}

		}
		return resultMap;
	}

	@Override
	public boolean save(Integer uid, String title, String introduce, String cover,
			Integer is_fine, Integer status) {
		int count = 0;
		Integer currentTime = DateUtil.getCurrentTime();
		uid = (null == uid) ? 1 : uid;
		
		String cover_key = "";
		if (StringUtils.isNotBlank(cover)) {
			cover_key = cover;
			String filePath = WebConst.getWebRootPath() + cover;
			if (!cover.startsWith("http://") && FileUtil.isFile(filePath)) {
				//上传专辑封面
				fileService.upload(cover_key, filePath);
			}
		}
		try {
			count = Special.db.insert(
					"insert into t_special(uid, title, introduce, cover, create_time, last_time, is_fine, status) "
							+ "values(?,?,?,?,?,?,?,?,?,?)", uid, title, introduce, cover_key, currentTime, currentTime, is_fine, status);
		} catch (UpdateException e) {
			logger.warn("保存专辑失败：" + e.getMessage());
			count = 0;
		}
		return count > 0;
	}

	@Override
	public int update(Integer sid, Integer uid, String title, String introduce, String cover,
			Integer is_fine, Integer status) {
		int count = 0;
		uid = (null == uid) ? 1 : uid;
		if (null != sid) {
			Special special = this.get(sid);
			if (null != special) {
				SqlBase base = SqlBase.update("update t_special");

				if (StringUtils.isNotBlank(title) && !title.equals(special.getTitle())) {
					base.set("title", title);
				}
				
				if (StringUtils.isNotBlank(introduce) && !introduce.equals(special.getIntroduce())) {
					base.set("introduce", introduce);
				}
				// 封面是否修改
				
				if (StringUtils.isNotBlank(cover)) {
					String cover_key = cover;
					String filePath = WebConst.getWebRootPath() + cover;
					if (!cover.startsWith("http://") && FileUtil.isFile(filePath)) {
						//上传封面
						fileService.upload(cover_key, filePath);
						//删除原有文件
						fileService.delete(special.getCover());
					}
					base.set("cover", cover_key);
				}
				if(null != is_fine){
					base.set("is_fine", is_fine);
				}
				base.set("last_time", DateUtil.getCurrentTime());
				base.eq("id", sid);
				try {
					count = Special.db.update(base.getSQL(), base.getParams());
				} catch (UpdateException e) {
					logger.warn("更新专辑失败：" + e.getMessage());
					count = 0;
				}
			}
		}
		return count;
	}

	@Override
	public List<Map<String, Object>> getList(Integer uid, Integer type, Integer is_fine, String title, Integer status, String order) {
		SqlBase base = SqlBase.select("select t.* from t_special t");
		base.eq("uid", uid).eq("type", type).eq("is_fine", is_fine).likeLeft("title", title).eq("status", status).order(order);
		List<Special> list = Special.db.findList(base.getSQL(), base.getParams());
		return this.getSpecialMapList(list);
	}

	private List<Map<String, Object>> getSpecialMapList(List<Special> list) {
		List<Map<String, Object>> mapList = CollectionUtil.newArrayList();
		for (int i = 0, len = list.size(); i < len; i++) {
			Special special = list.get(i);
			if (null != special) {
				mapList.add(this.getMap(special, null));
			}
		}
		return mapList;
	}

	private Page<Special> getPageList(Integer uid, Integer type, Integer is_fine,
			String title, Integer status, Integer page,
			Integer pageSize, String order) {
		SqlBase base = SqlBase.select("select t.* from t_special t");
		base.eq("uid", uid).eq("type", type).eq("is_fine", is_fine).likeLeft("title", title).eq("status", status).order(order);
		return Special.db.findListPage(page, pageSize, base.getSQL(), base.getParams());
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer uid, Integer type, Integer is_fine,
			String title, Integer status,
			Integer page, Integer pageSize, String order) {
		Page<Special> pageList = this.getPageList(uid, type, is_fine, title, status, page, pageSize, order);

		List<Special> specialList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount(), pageList.getPage(),
				pageList.getPageSize());

		List<Map<String, Object>> listMap = this.getSpecialMapList(specialList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public int delete(Integer sid) {
		int count = 0;
		if (null != sid) {
			try {
				SqlBase base = SqlBase.update("update t_special");
				base.set("status", 0).eq("id", sid);
				count = Special.db.update(base.getSQL(), base.getParams());
				//Special.db.delete("delete from t_special where id = ?", sid);
			} catch (UpdateException e) {
				logger.warn("删除专辑失败：" + e.getMessage());
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int hit(Integer sid) {
		if (null != sid) {
			return Special.db.update("update t_special hit = (hit+1) where id = ?", sid);
		}
		return 0;
	}

	@Override
	public boolean enable(Integer sid, Integer status) {
		if (null != sid) {
			try {
				SqlBase base = SqlBase.update("update t_special");
				base.set("status", status).eq("id", sid);
				return Special.db.update(base.getSQL(), base.getParams()) > 0;
			} catch (UpdateException e) {
				logger.warn("停用启用专辑失败：" + e.getMessage());
				return false;
			}
		}
		return false;
	}

}
