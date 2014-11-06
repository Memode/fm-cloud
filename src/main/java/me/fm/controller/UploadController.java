package me.fm.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.fm.service.HashService;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.unique.common.tools.CollectionUtil;
import org.unique.common.tools.DateUtil;
import org.unique.common.tools.FileUtil;
import org.unique.common.tools.StringUtils;
import org.unique.ioc.annotation.Autowired;
import org.unique.web.annotation.Action;
import org.unique.web.annotation.Controller;
import org.unique.web.core.Const;
import org.unique.web.core.R;

import com.baidu.ueditor.ActionEnter;

/**
 * 文件上传
 * @author:rex
 * @date:2014年8月19日
 * @version:1.0
 */
@Controller("/upload")
public class UploadController extends BaseController {

	private Logger logger = Logger.getLogger(UploadController.class);
	private static final int BUFFER_SIZE = 100 * 1024;
	private String savePath;
	private String tempPath;
	private String imagePath;
	private String mp3Path;
	private String videoPath;
	private String filePath;
	private String domainBase;
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Autowired
	private HashService hashService;

	private String rootDir;
	private String uplodDir = Const.CONST_MAP.get("unique.web.upload.path").toString();
	private Integer uid = 1;

	public void init(){
		request = r.getRequest();
		response = r.getResponse();
		if (null == rootDir) {
			rootDir = request.getServletContext().getRealPath("/");
			this.uid = (null == super.uid) ? 1 : super.uid;
			tempPath = rootDir + uplodDir + File.separator + "temp";
			imagePath = rootDir + uplodDir + File.separator + "images" + File.separator + this.uid;
			mp3Path = rootDir + uplodDir + File.separator + "mp3" + File.separator + this.uid;
			videoPath = rootDir + uplodDir + File.separator + "video" + File.separator + this.uid;
			filePath = rootDir + uplodDir + File.separator + "files" + File.separator + this.uid;
		}
		if (null == domainBase) {
			String ctx = request.getContextPath();
			domainBase = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + ctx
					+ "/";
		}
	}

	/**
	 * 上传临时文件
	 * @throws Exception 
	 */
	@Action
	public void temp(R r) throws Exception {
		init();
		savePath = tempPath;
		fileupload(savePath, r);
	}

	/**
	 * 上传用户目录文件
	 * @throws Exception 
	 */
	@Action
	public void file(R r) throws Exception {
		init();
		savePath = filePath;
		fileupload(savePath, r);
	}

	/**
	 * 上传图片文件
	 * @throws Exception 
	 */
	@Action("file/pic")
	public void pic(R r) throws Exception {
		init();
		savePath = imagePath;
		fileupload(savePath, r);
	}

	/**
	 * 上传音乐文件
	 * @throws Exception
	 */
	@Action("file/mp3")
	public void mp3(R r) throws Exception {
		init();
		savePath = mp3Path;
		fileupload(savePath, r);
	}

	/**
	 * 上传视频文件
	 * @throws Exception
	 */
	@Action("file/video")
	public void video(R r) throws Exception {
		init();
		savePath = videoPath;
		fileupload(savePath, r);
	}

	private void appendFile(InputStream in, File destFile) {
		OutputStream out = null;
		try {
			// plupload 配置了chunk的时候新上传的文件append到文件末尾
			if (destFile.exists()) {
				out = new BufferedOutputStream(new FileOutputStream(destFile, true), BUFFER_SIZE);
			} else {
				out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE);
			}
			in = new BufferedInputStream(in, BUFFER_SIZE);

			int len = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * 文件上传处理
	 * @throws Exception 
	 */
	private void fileupload(String savePath, R r) throws Exception {
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {

				String fileName = "";
				Integer chunk = 0, chunks = 0;

				//检查文件目录，不存在则创建
				File uploadDir = new File(savePath);
				if (!uploadDir.exists()) {
					uploadDir.mkdirs();
				}

				DiskFileItemFactory diskFactory = new DiskFileItemFactory();
				// threshold 极限、临界值，即硬盘缓存 1M  
				diskFactory.setSizeThreshold(4 * 1024);

				ServletFileUpload upload = new ServletFileUpload(diskFactory);
				// 设置允许上传的最大文件大小（单位MB）
				upload.setSizeMax(1024 * 1048576);
				upload.setHeaderEncoding("UTF-8");

				try {
					List<FileItem> fileList = upload.parseRequest(request);
					Iterator<FileItem> it = fileList.iterator();
					while (it.hasNext()) {
						FileItem item = it.next();
						String name = item.getFieldName();
						InputStream input = item.getInputStream();
						if ("name".equals(name)) {
							fileName = Streams.asString(input);
							continue;
						}
						if ("chunk".equals(name)) {
							chunk = Integer.valueOf(Streams.asString(input));
							continue;
						}
						if ("chunks".equals(name)) {
							chunks = Integer.valueOf(Streams.asString(input));
							continue;
						}
						// 处理上传文件内容
						if (!item.isFormField()) {

							//目标文件
							File destFile = new File(savePath, fileName);

							logger.debug("[chunk=" + chunk + ",chunks=" + chunks + "]");

							//文件已存在删除旧文件（上传了同名的文件） 
							if (chunk == 0 && destFile.exists()) {
								destFile.delete();
								destFile = new File(savePath, fileName);
							}

							// 非分块
							if (chunk == 0 && chunks == 0) {

								String saveFilePath = getFileName(fileName);

								item.write(new File(saveFilePath));

								Map<String, Object> fileInfo = this.getResponse(saveFilePath, fileName, item.getSize());
								logger.info("非分块上传结果：" + fileInfo);
								r.renderJson(fileInfo);
								return;
							}

							//合成文件
							appendFile(input, destFile);

							// 分块
							if (chunk == chunks - 1) {
								String saveFilePath = getFileName(fileName);
								boolean flag = destFile.renameTo(new File(saveFilePath));
								if (flag) {
									Map<String, Object> fileInfo = this.getResponse(saveFilePath, fileName,
											item.getSize());
									logger.info("分块上传结果：" + fileInfo);
									r.renderJson(fileInfo);
								}
							} else {
								logger.info("还剩[" + (chunks - 1 - chunk) + "]个块文件");
							}
						}
					}
				} catch (FileUploadException ex) {
					logger.warn("上传文件失败：" + ex.getMessage());
					return;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 获取返回结果map
	 * @param saveFilePath
	 * @param fileName
	 * @param length
	 * @return
	 */
	private Map<String, Object> getResponse(String saveFilePath, String fileName, Long length) {
		Map<String, Object> fileInfo = CollectionUtil.newHashMap();
		fileInfo.put("save_path", saveFilePath);
		fileInfo.put("save_name", saveFilePath.substring(saveFilePath.lastIndexOf(".") + 1));
		fileInfo.put("file_name", fileName);
		String url = domainBase + saveFilePath.replace(rootDir, "").replaceAll("\\\\", "/");
		fileInfo.put("url", url);
		fileInfo.put("key", saveFilePath.replace(rootDir, "").replaceAll("\\\\", "/"));
		fileInfo.put("length", length);
		return fileInfo;
	}

	/**
	 * 获取保存的文件名称
	 * @param fileName
	 * @return
	 */
	private String getFileName(String fileName) {
		// 扩展名格式：  
		String extName = FileUtil.getExtension(fileName);

		// 生成文件名：  
		String dateStr = DateUtil.convertIntToDatePattern(DateUtil.getCurrentTime(), "yyyyMMddHHmmss");
		String random = StringUtils.randomNum(5);
		String saveFilePath = savePath + File.separator + dateStr + random + extName;
		return saveFilePath;
	}

	public void ueditor(R r) throws UnsupportedEncodingException, JSONException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/html");
		String rootPath = request.getServletContext().getRealPath("/");
		r.renderText(new ActionEnter(request, rootPath).exec());
	}
}
