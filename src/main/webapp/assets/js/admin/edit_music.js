/**
 * 全局music
 */
$unique.admin.music = {};
var lrc;
$(function() {
	
	var uploader1 = new plupload.Uploader({
		runtimes : 'html5,flash',
		browse_button : 'uploader1', // you can pass in id...
		multi_selection : false,
		chunk_size : '1mb',
		max_file_size : '10mb',
		url : $unique.base + '/upload/file/mp3',
		flash_swf_url : $unique.base + '/assets/plugins/plupload/js/Moxie.swf',
		dragdrop : true,
		filters : {
			mime_types : [ {
				title : "MP3文件",
				extensions : "mp3"
			}]
		}
	});
	uploader1.bind('FilesAdded', function(uploader, files) {
		uploader.start();
	});
	uploader1.bind('FileUploaded', function(uploader, file, response) {
		var data = $.parseJSON(response.response);
		$('#upload_music_form').find('input[name="song_path"]').val(data.key).attr('readonly', true);
		$('#upload_music_form').find('input[name="song"]').val(data.file_name);
		uploader.removeFile(file);
		uploader.refresh();
	});
	uploader1.bind('Error', function(uploader,errObject) {
		if(errObject.code == -601){
			alert('请选择正确的音乐格式！');
		}
	});
	uploader1.init(); //初始化
	
	var uploader2 = new plupload.Uploader({
		runtimes : 'html5,flash',
		browse_button : 'uploader2', // you can pass in id...
		multi_selection : false,
		unique_names : true,
		max_file_size : '10mb',
		url : $unique.base + '/upload/file/pic',
		flash_swf_url : $unique.base + '/assets/plugins/plupload/js/Moxie.swf',
		filters : {
			mime_types : [ {
				title : "Image Files",
				extensions : "jpg,png,gif,jpeg"
			}]
		}
	});
	uploader2.bind('FilesAdded', function(uploader, files) {
		uploader.start();
	});
	uploader2.bind('Error', function(up, err) {    
        alert("errcode=" + err.code + ", Message: " + err.message + err.file + err.file.name);
        up.refresh(); // Reposition Flash/Silverlight   
    });
	uploader2.bind('FileUploaded', function(uploader, file, response) {
		var data = $.parseJSON(response.response);
		$('#upload_music_form').find('input[name="cover_path"]').val(data.key).attr('readonly', true);
		uploader.removeFile(file);
		uploader.refresh();
	});
	uploader2.bind('Error', function(uploader,errObject) {
		if(errObject.code == -601){
			alert('请选择正确的图片格式！');
		}
	});
	uploader2.init(); //初始化
	
	$('#upload_music_form').validator({
		rules : {
			mobile : [ /^1[3458]\d{9}$/, '请检查手机号格式' ]
		},
		fields : {
			song : 'required; length[1~50]',
			singer : 'length[1~50]',
			introduce : 'length[1~200]',
			song_path : 'required; ',
			cover_path : 'required; '
		},
		valid : function(form) {
			var cids = [];
			$(form).find('button[name="cidbtn"][ok="1"]').each(function(k, v) {
				cids.push($(v).attr('cid'));
			});
			$(form).find('#cids').val(cids.join(','));
			var url = $unique.base + '/admin/music/save';
			var param = $(form).serializeArray();
			$.post(url, param, function(data) {
				if (data) {
					if (data === 'success') {
						$unique.alert('保存成功！');
						window.location.href = $unique.base + '/admin/music';
					} else {
						$unique.alert('保存失败！');
					}
				}
			});

		}
	});

	lrc = UE.getEditor('lrc', {
		toolbars : [ [ 'anchor', // 锚点
		'bold', // 加粗
		'indent', // 首行缩进
		'italic', // 斜体
		'underline', // 下划线
		'strikethrough', // 删除线
		'subscript', // 下标
		'superscript', // 上标
		'formatmatch', // 格式刷
		'source', // 源代码
		'blockquote', // 引用
		'pasteplain', // 纯文本粘贴模式
		'selectall', // 全选
		'preview', // 预览
		'horizontal', // 分隔线
		'removeformat', // 清除格式
		'unlink', // 取消链接
		'deletecaption', // 删除表格标题
		'inserttitle', // 插入标题
		'fontfamily', // 字体
		'fontsize', // 字号
		'paragraph', // 段落格式
		'link', // 超链接
		'emotion', // 表情
		'searchreplace', // 查询替换
		'justifyleft', // 居左对齐
		'justifyright', // 居右对齐
		'justifycenter', // 居中对齐
		'justifyjustify', // 两端对齐
		'forecolor', // 字体颜色
		'backcolor', // 背景色
		'insertorderedlist', // 有序列表
		'insertunorderedlist', // 无序列表
		'fullscreen', // 全屏
		'directionalityltr', // 从左向右输入
		'directionalityrtl', // 从右向左输入
		'pagebreak', // 分页
		'imagecenter', // 居中
		'lineheight', // 行间距
		'edittip ', // 编辑提示
		'customstyle', // 自定义标题
		'autotypeset', // 自动排版
		'background', // 背景
		'inserttable', // 插入表格
		] ],
		autoHeightEnabled : true,
		autoFloatEnabled : true,
		initialFrameHeight : 300
	});
});

$unique.admin.music.tabok = function(obj) {
	var ok = $(obj).attr('ok');
	if (ok === '1') {
		$(obj).attr('ok', '0');
	} else {
		$(obj).attr('ok', '1');
	}
	$(obj).find('i').toggle();
}
