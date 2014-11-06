package me.fm.cloud.model;

import org.unique.ioc.annotation.Component;
import org.unique.plugin.dao.Model;
import org.unique.plugin.dao.Table;

/**
 * 图片
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Component
@Table(name="t_album")
public class Album extends Model<Album> {
	
	private static final long serialVersionUID = 1L;
	public static Album db = new Album();
	private Integer id;
	private Integer uid;
	private String title;
	private String introduce;
	private String cover;
	private String pics;
	private Integer status;
	private Integer create_time;

	public Album(){
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getPics() {
		return pics;
	}

	public void setPics(String pics) {
		this.pics = pics;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
}

