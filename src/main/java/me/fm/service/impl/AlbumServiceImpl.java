package me.fm.service.impl;

import java.util.List;
import java.util.Map;

import me.fm.api.QiniuApi;
import me.fm.cloud.model.Album;
import me.fm.service.AlbumService;
import me.fm.service.FileService;
import me.fm.util.BeanUtil;
import me.fm.util.WebConst;

import org.apache.log4j.Logger;
import org.unique.common.tools.CollectionUtil;
import org.unique.common.tools.DateUtil;
import org.unique.common.tools.FileUtil;
import org.unique.common.tools.JSONUtil;
import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.ioc.annotation.Service;
import org.unique.plugin.dao.Page;
import org.unique.plugin.dao.SqlBase;
import org.unique.plugin.db.exception.UpdateException;

@Service
public class AlbumServiceImpl implements AlbumService {

	private Logger logger = Logger.getLogger(AlbumServiceImpl.class);
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private AlbumService albumService;
	
	@Override
	public Album get(Integer id) {
        return Album.db.findByPK(id);
	}

	@Override
	public Map<String, Object> getMap(Album picture, Integer id) {
		Map<String, Object> resultMap = CollectionUtil.newHashMap();
		if (null == picture) {
			picture = this.get(id);
		}
		if (null != picture) {
			resultMap = BeanUtil.toMap(picture);
			if(StringUtils.isNotBlank(picture.getPics())){
				List<Map<String, Object>> pics = JSONUtil.json2List(picture.getPics());
				resultMap.put("pics", pics);
			}
			if(StringUtils.isNotBlank(picture.getCover())){
				resultMap.put("cover_url", QiniuApi.getUrlByKey(picture.getCover()));
			}
			if(null != picture.getCreate_time()){
				resultMap.put("time_zh", DateUtil.convertIntToDatePattern(picture.getCreate_time(), "yyyy-MM-dd HH:mm:ss"));
			}
		}
		return resultMap;
	}

	@Override
	public List<Map<String, Object>> getList(Integer uid, String title, Integer status, String order) {
		SqlBase base = SqlBase.select("select t.* from t_album t");
		base.eq("uid", uid).likeLeft("title", title).eq("status", status).order(order);
		List<Album> list = Album.db.findList(base.getSQL(), base.getParams());
		return this.getPicMapList(list);
	}

	private List<Map<String, Object>> getPicMapList(List<Album> list) {
		List<Map<String, Object>> mapList = CollectionUtil.newArrayList();
		for (int i = 0, len = list.size(); i < len; i++) {
			Album picture = list.get(i);
			if (null != picture) {
				mapList.add(this.getMap(picture, null));
			}
		}
		return mapList;
	}
	
	public Page<Album> getPageList(Integer uid, String title, Integer status, Integer page,
			Integer pageSize, String order) {
		SqlBase base = SqlBase.select("select t.* from t_album t");
		base.eq("uid", uid).likeLeft("title", title).eq("status", status).order(order);
		return Album.db.findListPage(page, pageSize, base.getSQL(), base.getParams());
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(Integer uid, String title, Integer status, Integer page,
			Integer pageSize, String order) {
		Page<Album> pageList = this.getPageList(uid, title, status, page, pageSize, order);

		List<Album> pictureList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount(), pageList.getPage(),
				pageList.getPageSize());
		
		List<Map<String, Object>> listMap = this.getPicMapList(pictureList);
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public boolean save(Integer uid, String title, String introduce, String cover, String pics, Integer status) {
		int count = 0, upCount = 0;
		uid = (null == uid) ? 1 : uid;
		if(StringUtils.isNotBlank(pics)){
			List<Map<String, Object>> picList = JSONUtil.json2List(pics);
			for(int i=0,len=picList.size(); i<len; i++){
				Map<String, Object> map = picList.get(i);
				if(map.size() > 0){
					String key = map.get("savepath").toString();
					String filePath = WebConst.getWebRootPath() + key;
					if (FileUtil.isFile(filePath)) {
						//上传图片
						fileService.upload(key, filePath);
						map.put("key", key);
						map.put("url", QiniuApi.getUrlByKey(key));
						upCount++;
					}
					map.remove("savepath");
					if(null == cover && i == 0){
						cover = key;
					}
				} else{
					picList.remove(map);
				}
			}
			String picJson = JSONUtil.list2JSON(picList);
			try {
				if(upCount > 0){
					Integer create_time = DateUtil.getCurrentTime();
					count = Album.db.insert("insert into t_album(uid, title, introduce, cover, pics, status, create_time) values(?,?,?,?,?,?,?)", 
							uid, title, introduce, cover, picJson, status, create_time);
				}
			} catch (UpdateException e) {
				logger.warn("保存图库失败：" + e.getMessage());
				count = 0;
			}
		}
		return count > 0;
	}

	@Override
	public boolean delete(Integer id) {
		if (null != id) {
			try {
				return Album.db.deleteByPK(id) > 0;
			} catch (UpdateException e) {
				logger.warn("删除图库失败：" + e.getMessage());
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean update(Integer id, String title, String introduce, String cover, String pics, Integer status) {
		if(null != id){
			Album pic = this.get(id);
			int upCount = 0;
			if(null != pic){
				SqlBase base = SqlBase.update("update t_album");
				if(StringUtils.isNotBlank(title)){
					base.set("title", title);
				}
				if(StringUtils.isNotBlank(introduce)){
					base.set("introduce", introduce);
				}
				if(StringUtils.isNotBlank(cover)){
					base.set("cover", cover);
				} 
				if(StringUtils.isNotBlank(pics)){
					
					List<Map<String, Object>> picList = JSONUtil.json2List(pics);
					List<Map<String, Object>> newList = CollectionUtil.newArrayList();
					for(int i=0,len=picList.size(); i<len; i++){
						Map<String, Object> map = picList.get(i);
						if(map.size() > 0){
							String key = map.get("savepath").toString();
							if(key.startsWith("upload/images/") && null != map.get("isold")){
								map.put("key", key);
								map.put("url", QiniuApi.getUrlByKey(key));
								upCount++;
								newList.add(map);
							} else{
								String filePath = WebConst.getWebRootPath() + key;
								if (FileUtil.isFile(filePath)) {
									//上传图片
									fileService.upload(key, filePath);
									map.put("key", key);
									map.put("url", QiniuApi.getUrlByKey(key));
									upCount++;
									newList.add(map);
								}
							}
							map.remove("savepath");
							if(StringUtils.isBlank(cover) && StringUtils.isNotBlank(key) && i == 0){
								base.set("cover", key);
							}
						}
					}
					String picJson = JSONUtil.list2JSON(newList);
					base.set("pics", picJson);
				}
				
				if(null != status){
					base.set("status", status);
				}
				base.eq("id", id);
				try {
					if(base.getSetMap().size() > 0 && upCount > 0){
						return Album.db.update(base.getSQL(), base.getParams()) > 0;
					}
				} catch (UpdateException e) {
					logger.warn("更新图库失败：" + e.getMessage());
				}
			}
		}
		return false;
	}

}
