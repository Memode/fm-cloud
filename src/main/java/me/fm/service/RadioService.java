package me.fm.service;

import java.util.List;
import java.util.Map;

import me.fm.cloud.model.Radio;

import org.unique.plugin.dao.Page;



/**
 * 电台接口
 * @author:rex
 * @date:2014年9月30日
 * @version:1.0
 */
public interface RadioService {

	Radio get(Integer id);
	
	Map<String, Object> getMap(Radio radio, Integer id);
	
	boolean save(Integer uid, String title, Integer sid, String url);
	
	boolean update(Integer id, String title, Integer sid, String url);
	
	List<Map<String, Object>> getList(Integer uid, String title, Integer sid, Integer status, String order);
	
	Page<Map<String, Object>> getPageMapList(Integer uid, String title, Integer sid, Integer status, Integer page, Integer pageSize, String order);
	
	boolean delete(Integer id);
	
	boolean enable(Integer id, Integer status);
	
}
