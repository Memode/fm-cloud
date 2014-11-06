/**
 * 全局pic
 */
$unique.admin.pic = {};

var introduce;

$(function() {
	
	if($('#publish input[name="id"]').val() != ''){
		$('div#publish input[type="button"]').removeClass('disabled');
	}
	
	var uploader = new plupload.Uploader({
		runtimes : 'html5,flash',
		browse_button : 'uploader', // you can pass in id...
		//multi_selection : false,
		chunk_size : '500kb',
		url : $unique.base + '/upload/file/pic',
		flash_swf_url : $unique.base + '/assets/plugins/plupload/js/Moxie.swf',
		dragdrop : true,
		filters : {
			max_file_size : '10mb',
			mime_types : [ {
				title : "图片文件",
				extensions : "jpg,jpeg,gif,png"
			}]
		}
	});
	uploader.bind('Error', function(uploader,errObject) {
		if(errObject.code == -601){
			alert('请选择正确的图片格式！');
		}
	});
	uploader.bind('FilesAdded',function(uploader,files){
		uploader.start();
	});
	uploader.bind('FileUploaded',function(uploader, file, response) {
		var data = $.parseJSON(response.response);
		var inner = '<div class="form-group">'+
		'	<label class="col-md-2 control-label">新图片：</label>'+
		'	<div class="col-md-10">'+
		'		<div class="pull-left">'+
		'			<img class="img-circle" width="90" height="90" src="'+data.url+'" />'+
		'		</div>'+
		'		<div class="pull-left imginfo">'+
		'			<textarea maxlength="100" onKeyDown="if (this.value.length>=100){event.returnValue=false}" savepath="'+data.key+'" name="desc" rows="3" cols="80" class="form-control" placeholder="描述，不超过100字"></textarea>'+
		'		</div>'+
		'		<div class="pull-left">'+
		'			&nbsp;&nbsp;<button type="button" class="btn" onclick="$unique.admin.pic.delpic(this);">删除</button>'+
		'		</div>'+
		'	</div>'+
		'</div>';
		$('#file-list').append(inner);
		$('div#publish input[type="button"]').removeClass('disabled');
		uploader.removeFile(file);
		uploader.refresh();
	});
	uploader.init(); //初始化
	
	introduce = UE.getEditor('introduce', {
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
		initialFrameHeight : 100
	});
	
});


$unique.admin.pic.delpic = function(obj){
	$(obj).parents('.form-group').eq(0).remove();
}


/**
 * 发布
 */
$unique.admin.pic.publish = function(status){
	var picJson = [];
	$('#file-list').find('div.imginfo').each(function(){
		var savepath = $(this).find('textarea').attr('savepath');
		var desc = $(this).find('textarea').val();
		
		if(savepath != ''){
			var isold = $(this).find('textarea').attr('isold');
			if(isold != ''){
				picJson.push({savepath: savepath, desc: desc, isold : isold});
			} else{
				picJson.push({savepath: savepath, desc: desc});
			}
		}
		
	});
	$('#picinput').val(JSON.stringify(picJson));
	//alert(JSON.stringify(picJson));
	$('#picstatus').val(status);
	var url = $unique.base + '/admin/pic/save';
	var param = $('#upload_pic_form').serializeArray();
	$.post(url, param, function(data) {
		if (data) {
			if (data === 'success') {
				$unique.alert('保存成功！');
				window.location.href = $unique.base + '/admin/pic';
			} else {
				$unique.alert('保存失败！');
			}
		}
	},'text');
}
