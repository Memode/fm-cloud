package me.fm.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.fm.api.QiniuConst;
import me.fm.util.HttpDownload;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.unique.common.tools.FileUtil;
import org.unique.plugin.dao.DB;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;

/**
 * 删除无用资源
 * @author:rex
 * @date:2014年10月13日
 * @version:1.0
 */
public class SynQiniu {

	public static final String FROM_BUCKETNAME = "";
	public static final String FROM_ACCESS_KEY = "";
	public static final String FROM_SECRET_KEY = "";
	
	public static final String TO_BUCKETNAME = "fm-cloud";
	public static final String TO_ACCESS_KEY = "7lnGp2l1tNYAYktryv7yR52ZTYdOJpaPrTj2w4a2";
	public static final String TO_SECRET_KEY = "oIdgcZ2-G4LOMIAO3MaYM-nzhjvVz-4bK4_tMDvF";
	
	public static void main(String[] args) {
		
		List<String> focusKey = DB.findColumnList("SELECT f.`pic` FROM t_focus f WHERE NOT (f.`pic` REGEXP '^http://') AND f.`pic` IS NOT NULL AND f.`pic` <> ''");
		List<String> musicKey1 = DB.findColumnList("SELECT t.`song_path` FROM t_music t WHERE NOT (t.`song_path` REGEXP '^http://') AND t.`song_path` IS NOT NULL AND t.`song_path` <> ''");
		List<String> musicKey2 = DB.findColumnList("SELECT t.`cover_path` FROM t_music t WHERE NOT (t.`cover_path` REGEXP '^http://') AND t.`cover_path` IS NOT NULL AND t.`cover_path` <> ''");
		List<String> picKey = DB.findColumnList("SELECT t.`cover` FROM t_album t WHERE NOT (t.`cover` REGEXP '^http://') AND t.`cover` IS NOT NULL AND t.`cover` <> ''");
		List<String> radioKey = DB.findColumnList("SELECT t.`url` FROM t_radio t WHERE NOT (t.`url` REGEXP '^http://') AND t.`url` IS NOT NULL AND t.`url` <> ''");
		List<String> specialKey = DB.findColumnList("SELECT t.`cover` FROM t_special t WHERE NOT (t.`cover` REGEXP '^http://') AND t.`cover` IS NOT NULL AND t.`cover` <> ''");
		
		List<String> qiniuKeys = new ArrayList<String>();
		qiniuKeys.addAll(focusKey);
		qiniuKeys.addAll(musicKey1);
		qiniuKeys.addAll(musicKey2);
		qiniuKeys.addAll(picKey);
		qiniuKeys.addAll(radioKey);
		qiniuKeys.addAll(specialKey);
		
		for(String key : qiniuKeys){
			String filepath = download(key, null);
			upload(filepath, key);
			System.out.println(key + "：同步成功！");
		}
	}
	
	public static void upload(final String filePath, final String key){
		if (FileUtil.isFile(filePath) && StringUtils.isNotBlank(key)) {
			new Thread() {
				public void run() {
					PutPolicy putPolicy = new PutPolicy(TO_BUCKETNAME);
					Mac mac = new Mac(TO_ACCESS_KEY, TO_SECRET_KEY);
					try {
						String uptoken  = putPolicy.token(mac);;
						PutExtra extra = new PutExtra();
						PutRet ret = IoApi.putFile(uptoken, key, filePath, extra);
						System.out.println("上传结果：" + ret.getResponse());
					} catch (AuthException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}
	
	public static String download(final String key, String savePath){
		final String downloadUrl = QiniuConst.DOMAIN + "/" + key;
		System.out.println("key:" + key);
		System.out.println("downloadUrl:" + downloadUrl);
		final String filepath = "F:/keys/" + key.substring(key.lastIndexOf("/")+1);
		System.out.println("filepath:" + filepath);
		if(!new File(filepath).exists()){
			HttpDownload.download(downloadUrl, filepath, null);
		}
		return filepath;
	}
}
