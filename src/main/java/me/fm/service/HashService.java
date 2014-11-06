package me.fm.service;

import me.fm.cloud.model.Hash;

/**
 * hash值记录接口
 * @author:rex
 * @date:2014年10月15日
 * @version:1.0
 */
public interface HashService {

	Hash get(Integer id, String hash);
	
	boolean save(String fileName, String savePath, Long length, String hash, String key);
	
}
