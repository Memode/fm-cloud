/**
 * 全局music
 */
$unique.admin.focus = {};
$(function() {

	var uploader = new plupload.Uploader({
		runtimes : 'html5,flash',
		browse_button : 'uploader', // you can pass in id...
		multi_selection : false,
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
		$('#upload_focus_form').find('input[name="pic"]').val(data.key).attr('readonly', true);
		uploader.removeFile(file);
		uploader.refresh();
	});
	uploader.init(); //初始化
	
	
	$('#upload_focus_form').validator({
	    display: function(el){
	        return el.getAttribute('placeholder') || '';
	    },
	    fields: {
	    	title: 'required; length[1~50]',
	    	introduce : 'required;; length[1~200]',
	    	pic : 'required; '
	    },
	    valid: function(form){
	    	var url = $unique.base + '/admin/focus/save';
			var param = $(form).serializeArray();
			$.post(url, param, function(data) {
				if(data){
					if(data === 'success'){
						$unique.alert('保存成功！');
						window.location.href = $unique.base + '/admin/focus';
					} else{
						$unique.alert('保存失败！');
					}
				}
			});
			
	    }
	});
});


/**
 * 删除焦点图
 */
$unique.admin.focus.enable = function(id, status){
	art.dialog({
	    lock: true,
	    content: '确定删除该焦点图吗',
	    icon: 'error',
	    ok: function () {
	    	var url = $unique.base + '/admin/focus/enable';
			var param = { id : id, status : status };
			$.post(url, param, function(data) {
				if(data && data === 'success'){
					$unique.alert('删除成功！');
					window.location.href = $unique.base + '/admin/focus';
				} else{
					$unique.alert('删除失败！');
				}
			},'text');
	    },
	    cancel: true
	});
}
