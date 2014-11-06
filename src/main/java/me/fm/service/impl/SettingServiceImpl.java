package me.fm.service.impl;

import java.util.List;
import java.util.Map;

import me.fm.cloud.model.Setting;
import me.fm.service.SettingService;

import org.unique.common.tools.CollectionUtil;
import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Service;
import org.unique.plugin.dao.SqlBase;
import org.unique.plugin.db.exception.UpdateException;

@Service
public class SettingServiceImpl implements SettingService {
	
	@Override
	public Setting get(String key) {
		SqlBase base = SqlBase.select("select t.* from t_setting t");
        base.eq("skey", key);
        return Setting.db.find(base.getSQL(), base.getParams());
	}

	@Override
	public int save(String key, String value) {
		int count = 0;
		try {
			if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)){
				Setting setting = this.get(key);
				if(null != setting){
					if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)){
						try {
							count = Setting.db.update("update t_setting set svalue = ? where id = ?", value, setting.getId());
						} catch (UpdateException e) {
							e.printStackTrace();
							count = 0;
						}
					}
				} else{
					count = Setting.db.insert("insert into t_setting(skey, svalue) values(?, ?)", key, value);
				}
			}
		} catch (UpdateException e) {
			e.printStackTrace();
			count = 0;
		}
		return count;
	}

	@Override
	public int update(String key, String value) {
		int count = 0;
		if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)){
			try {
				count = Setting.db.update("update t_setting t set svalue = ? where skey = ?", value, key);
			} catch (UpdateException e) {
				e.printStackTrace();
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int delete(String key) {
		int count = 0;
		if(StringUtils.isNotBlank(key)){
			try {
				count = Setting.db.delete("delete from t_setting t where skey = ?", key);
			} catch (UpdateException e) {
				e.printStackTrace();
				count = 0;
			}
		}
		return count;
	}
	
	@Override
	public Map<String, String> getAllSetting() {
		Map<String, String> map = CollectionUtil.newHashMap();
		List<Setting> settingList = Setting.db.findList("select t.* from t_setting t");
		for(Setting setting : settingList){
			map.put(setting.getSkey(), setting.getSvalue());
		}
		return map;
	}

}
