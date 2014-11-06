package me.fm.service.impl;

import me.fm.cloud.model.Hash;
import me.fm.service.HashService;

import org.unique.common.tools.DateUtil;
import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Service;

@Service
public class HashServiceImpl implements HashService {

	@Override
	public Hash get(Integer id, String hash) {
		if (null != id) {
			return Hash.db.findByPK(id);
		}
		if (StringUtils.isNotBlank(hash)) {
			return Hash.db.find("select * from t_hash where status = 1 and hash_code = ?", hash);
		}
		return null;
	}

	@Override
	public boolean save(String fileName, String savePath, Long length, String hash, String key) {
		if (StringUtils.isNotBlank(hash) && StringUtils.isNotBlank(key)) {
			Integer dateline = DateUtil.getCurrentTime();
			return Hash.db.insert("insert into t_hash(hash_code, file_key, file_name, save_path, length, dateline) values(?,?,?,?,?,?)", hash, key, fileName, savePath, length, dateline) > 0;
		}
		return false;
	}

}
