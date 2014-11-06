package me.fm.cloud.model;

import org.unique.ioc.annotation.Component;
import org.unique.plugin.dao.Model;
import org.unique.plugin.dao.Table;

/**
 * 资源哈希表
 * @author:rex
 * @date:2014年10月15日
 * @version:1.0
 */
@Component
@Table(name = "t_hash")
public class Hash extends Model<Hash> {

	private static final long serialVersionUID = 1L;
	public static Hash db = new Hash();
	private Integer id;
	private String hash_code;
	private String file_key;
	private String file_name;
	private String save_path;
	private Long length;
	private Integer dateline;
	private Integer status;

	public Hash() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDateline() {
		return dateline;
	}

	public void setDateline(Integer dateline) {
		this.dateline = dateline;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getSave_path() {
		return save_path;
	}

	public void setSave_path(String save_path) {
		this.save_path = save_path;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getHash_code() {
		return hash_code;
	}

	public void setHash_code(String hash_code) {
		this.hash_code = hash_code;
	}

	public String getFile_key() {
		return file_key;
	}

	public void setFile_key(String file_key) {
		this.file_key = file_key;
	}

}
