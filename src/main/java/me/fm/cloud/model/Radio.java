package me.fm.cloud.model;

import org.unique.ioc.annotation.Component;
import org.unique.plugin.dao.Model;
import org.unique.plugin.dao.Table;

/**
 * 电台
 * @author:rex
 * @date:2014年9月30日
 * @version:1.0
 */
@Component
@Table(name = "t_radio")
public class Radio extends Model<Radio> {

	private static final long serialVersionUID = 1L;
	public static Radio db = new Radio();
	private Integer id;
	private Integer uid;
	private String title;
	private Integer sid;
	private String url;
	private Integer create_time;
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
}
