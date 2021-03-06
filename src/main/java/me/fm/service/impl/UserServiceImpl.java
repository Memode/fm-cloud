package me.fm.service.impl;

import java.util.List;
import java.util.Map;

import me.fm.cloud.model.Open;
import me.fm.cloud.model.User;
import me.fm.service.ActiveService;
import me.fm.service.OpenService;
import me.fm.service.UserService;
import me.fm.util.Base64;
import me.fm.util.BeanUtil;
import me.fm.util.EncrypUtil;

import org.unique.common.tools.CollectionUtil;
import org.unique.common.tools.DateUtil;
import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.ioc.annotation.Service;
import org.unique.plugin.dao.Page;
import org.unique.plugin.dao.SqlBase;
import org.unique.plugin.db.exception.UpdateException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private OpenService openService;
	@Autowired
	private ActiveService activeService;

	private User find(Integer uid, String email, Integer is_admin, Integer status) {
		SqlBase base = SqlBase.select("select t.* from t_user t");
		base.eq("t.uid", uid).eq("t.is_admin", is_admin).eq("t.email", email).eq("t.status", status);
		return User.db.find(base.getSQL(), base.getParams());
	}

	@Override
	public User getByUid(Integer uid) {
		return User.db.findByPK(uid);
	}

	@Override
	public User register(String nickname, String email, String password, String ip) {
		User user = null;
		//密码规则:md5(email+password)
		String md5pwd = EncrypUtil.md5(email + password);
		Integer currtime = DateUtil.getCurrentTime();
		int count = 0;
		try {
			count = User.db.update("insert into t_user(nickname, email, password, reg_ip, reg_time, log_time, status) "
					+ "values(?, ?, ?, ?, ?, ?, ?)", nickname, email, md5pwd, ip, currtime, currtime, 1);
		} catch (UpdateException e) {
			count = 0;
		}
		if (count > 0) {
			user = this.find(null, email, null, 1);
			// 生成激活码: sha1(email) 激活码
			String code = Base64.encoder(email);
			activeService.save(user.getUid(), code);
//			String url = "http://fm.im90.me/active?code="+code;
//			// 发送邮件
//			SendMail.asynSend("七牛云音乐电台激活帐号通知", 
//					"点击链接激活您的邮箱  <a herf='"+url+"' target='_blank'>"+url+"</a>", email);
		}
		return user;
	}

	@Override
	public boolean exists(String email) {
		return null == this.find(null, email, null, 1);
	}

	@Override
	public List<User> getList(String nickname, String email, Integer status, String order) {
		SqlBase base = SqlBase.select("select u.* from t_user u");
		base.likeLeft("u.nickname", nickname).likeLeft("u.email", email).eq("u.status", status).order("u." + order);
		return User.db.findList(base.getSQL(), base.getParams());
	}

	@Override
	public Page<User> getPageList(String nickname, String email, Integer status, Integer page, Integer pageSize,
			String order) {
		SqlBase base = SqlBase.select("select u.* from t_user u");
		base.likeLeft("u.nickname", nickname).likeLeft("u.email", email).eq("u.status", status).order("u." + order);
		return User.db.findListPage(page, pageSize, base.getSQL(), base.getParams());
	}

	@Override
	public int delete(String email, Integer uid) {
		int count = 0;
		if (null != uid) {
			try {
				count = User.db.delete("delete from t_user where uid = ?", uid);
			} catch (UpdateException e) {
				count = 0;
			}
		}
		if (StringUtils.isNotBlank(email)) {
			try {
				count = User.db.delete("delete from t_user where email = ?", email);
			} catch (UpdateException e) {
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int deleteBatch(String uids) {
		int count = 0;
		if (null != uids) {
			try {
				count = User.db.delete("delete from t_user where uid in (?)", uids);
			} catch (UpdateException e) {
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int enable(String email, Integer uid, Integer status) {
		if (null != uid) {
			return User.db.update("update t_user u set u.status = ? where u.uid = ?", status, uid);
		}
		if (StringUtils.isNotBlank(email)) {
			return User.db.update("update t_user u set u.status = ? where u.email = ?", status, email);
		}
		return 0;
	}

	@Override
	public User login(String email, String password) {
		String pwd = EncrypUtil.md5(email + password);
		User user = this.find(null, email, 1, 1);
		if (null != user && user.getPassword().equals(pwd)) {
			return user;
		}
		return null;
	}

	@Override
	public int updateUseSize(Integer uid, Long useSpace) {
		int count = 0;
		if (null != uid && null != useSpace) {
			try {
				count = User.db.update("update t_user u set u.use_size = (u.use_size + ?) where u.uid = ?", useSpace,
						uid);
			} catch (UpdateException e) {
				count = 0;
			}
		}
		return count;
	}

	@Override
	public User openLogin(String openid, Integer type) {
		User user = null;
		Open open = openService.get(null, openid, type);
		if (null != open) {
			user = this.find(null, open.getEmail(), null, 1);
		}
		return user;
	}

	@Override
	public User openBind(Integer type, String openid, String nickName, String email, String ip) {
		String pwd = StringUtils.randomStr(6);
		User user = this.register(nickName, email, pwd, ip);
		openService.save(email, type, openid);
		return user;
	}

	@Override
	public User get(String email, Integer status) {
		return this.find(null, email, null, status);
	}

	@Override
	public int update(Integer uid, String email, String nickName, Long space_size, Integer status) {
		int count = 0;
		SqlBase base = SqlBase.update("update t_user u");
		base.set("u.status", status).set("nickName", nickName).set("space_size", space_size).eq("u.uid", uid).eq("u.email", email);
		try {
			count = User.db.update(base.getSQL(), base.getParams());
		} catch (UpdateException e) {
			count = 0;
		}
		return count;
	}

	@Override
	public Page<Map<String, Object>> getPageMapList(String username, String email, Integer status, Integer page,
			Integer pageSize, String order) {
		Page<User> pageList = this.getPageList(username, email, status, page, pageSize, order);

		List<User> userList = pageList.getResults();
		Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(pageList.getTotalCount() , pageList.getPage(), pageList.getPageSize());

		List<Map<String, Object>> listMap = CollectionUtil.newArrayList();
		for (int i = 0, len = userList.size(); i < len; i++) {
			User user = userList.get(i);
			if (null != user) {
				listMap.add(this.getMap(user, null));
			}
		}
		pageMap.setResults(listMap);
		return pageMap;
	}

	@Override
	public Map<String, Object> getMap(User user, Integer uid) {
		Map<String, Object> resultMap = CollectionUtil.newHashMap();
		if (null == user) {
			user = this.find(uid, null, null, null);
		}
		if (null != user) {
			resultMap = BeanUtil.toMap(user);
			if(null != user.getReg_time()){
				resultMap.put("reg_time_zh", DateUtil.convertIntToDatePattern(user.getReg_time(), "yyyy/MM/dd HH:mm"));
			}
			if(null != user.getLog_time()){
				resultMap.put("last_login_time", DateUtil.convertIntToDatePattern(user.getLog_time(), "yyyy/MM/dd HH:mm"));
			}
		}
		return resultMap;
	}

}
