package me.fm.service;

import java.util.List;
import java.util.Map;

import me.fm.cloud.model.Album;

import org.unique.plugin.dao.Page;

/**
 * 图片处理接口
 * @author:rex
 * @date:2014年8月20日
 * @version:1.0
 */
public interface AlbumService {

	/**
	 * 查询一个图片
	 * @param id
	 * @return
	 */
	Album get(Integer id);
	
	Map<String, Object> getMap(Album picture, Integer id);
	
	/**
	 * 查询图片列表
	 * @param uid
	 * @param name
	 * @param album_id
	 * @param order
	 * @return
	 */
	List<Map<String, Object>> getList(Integer uid, String title, Integer status, String order);
	
	/**
	 * 分页查询图片列表
	 * @param uid
	 * @param title
	 * @param status
	 * @param page
	 * @param pageSize
	 * @param order
	 * @return
	 */
	Page<Map<String, Object>> getPageMapList(Integer uid, String title, Integer status, Integer page, Integer pageSize, String order);
	
	/**
	 * 保存一张新图库
	 * @param uid
	 * @param title
	 * @param introduce
	 * @param cover
	 * @param pics
	 * @param status
	 * @return
	 */
	boolean save(Integer uid, String title, String introduce, String cover, String pics, Integer status);
	
	/**
	 * 删除一个图库
	 * @param id
	 * @return
	 */
	boolean delete(Integer id);
	
	/**
	 * 更新图片信息
	 * @param id
	 * @param name
	 * @param introduce
	 * @return
	 */
	boolean update(Integer id, String title, String introduce, String cover, String pics, Integer status);
	
}
