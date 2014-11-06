package me.fm.controller.admin;

import java.io.File;
import java.util.Map;

import me.fm.controller.BaseController;
import me.fm.service.FileService;
import me.fm.service.SettingService;
import me.fm.util.WebConst;

import org.unique.ioc.annotation.Autowired;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;
import org.unique.web.core.Const;

/**
 * 系统后台设置
 * @author:rex
 * @date:2014年10月13日
 * @version:1.0
 */
@Controller
public class SysController extends BaseController {

	@Autowired
	private SettingService settingService;
	@Autowired
	private FileService fileService;
	
	/**
	 * 系统设置
	 */
	@Action("/admin/sys/setting")
	public void setting() {
		Map<String, String> setting = settingService.getAllSetting();
		r.setAttr("setting", setting);
		r.render("/admin/setting");
	}

	/**
	 * 保存系统配置
	 */
	@Action("/admin/sys/save_setting")
	public void save_setting() {
		String site_title = r.getPara("site_title");
		String site_keywords = r.getPara("site_keywords");
		String site_description = r.getPara("site_description");
		String sina_weibo = r.getPara("sina_weibo");
		String tencent_weibo = r.getPara("tencent_weibo");
		try {
			settingService.save("site_title", site_title);
			settingService.save("site_keywords", site_keywords);
			settingService.save("site_description", site_description);
			settingService.save("sina_weibo", sina_weibo);
			settingService.save("tencent_weibo", tencent_weibo);
			r.renderText(WebConst.MSG_SUCCESS);
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		r.renderText(WebConst.MSG_FAILURE);
	}

	/**
	 * 清空缓存
	 */
	@Action("/admin/sys/cleanCache")
	public void cleanCache() {
		String temp = r.getRequest().getServletContext().getRealPath("/")
				+ Const.CONST_MAP.get("unique.web.upload.path").toString() + File.separator + "temp" + File.separator;
		File tempDir = new File(temp);
		if (tempDir.isDirectory()) {
			File[] files = tempDir.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
		r.renderText(WebConst.MSG_SUCCESS);
	}
	
	/**
	 * 清空七牛无用资源
	 */
	@Action("/admin/sys/cleanQiniu")
	public void cleanQiniu(){
		Integer type = r.getParaToInt("type");
		String key = r.getPara("key");
		fileService.clean(type, key);
		r.renderText(WebConst.MSG_SUCCESS);
	}
}
