package me.fm.service;

import java.util.List;
import java.util.Map;

import me.fm.cloud.model.Focus;

import org.unique.plugin.dao.Page;

/**
 * 焦点图接口
 * @author:rex
 * @date:2014年10月10日
 * @version:1.0
 */
public interface FocusService {

	Focus get(Integer id);
	
	Map<String, Object> getMap(Focus focus, Integer id);
	
	boolean save(String title, String introduce, String pic, Integer type);
	
	int update(Integer id, String title, String introduce, String pic, Integer type, Integer status);
	
	List<Map<String, Object>> getList(Integer type, String title, Integer status, String order);
	
	Page<Focus> getPageList(Integer type, String title, Integer status, Integer page, Integer pageSize, String order);
	
	Page<Map<String, Object>> getPageMapList(Integer type, String title, Integer status, Integer page, Integer pageSize, String order);
	
	boolean enable(Integer id, Integer status);

}
