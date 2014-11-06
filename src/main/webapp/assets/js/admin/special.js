/**
 * 全局music
 */
$unique.admin.special = {};
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
		$('#upload_special_form').find('input[name="cover"]').val(data.key).attr('readonly', true);
		uploader.removeFile(file);
		uploader.refresh();
	});
	uploader.init(); //初始化
	
	$('#upload_special_form').validator({
	    display: function(el){
	        return el.getAttribute('placeholder') || '';
	    },
	    fields: {
	    	title: 'required; length[1~50]',
	    	introduce : 'required;; length[1~200]',
	    	cover : 'required; '
	    },
	    valid: function(form){
	    	var url = $unique.base + '/admin/special/save';
			var param = $(form).serializeArray();
			$.post(url, param, function(data) {
				if(data){
					if(data === 'success'){
						$unique.alert('保存成功！');
						window.location.href = $unique.base + '/admin/special';
					} else{
						$unique.alert('保存失败！');
					}
				}
			});
			
	    }
	});
});


/**
 * 禁用专辑
 */
$unique.admin.special.enable = function(sid, status){
	if(status == 1){
		var url = $unique.base + '/admin/special/enable';
		var param = { sid : sid, status : status };
		$.post(url, param, function(data) {
			if(data && data === 'success'){
				$unique.alert('禁用成功！');
				window.location.href = $unique.base + '/admin/special';
			} else{
				$unique.alert('禁用失败！');
			}
		},'text');
	} else{
		art.dialog({
		    lock: true,
		    content: '确定禁用该专辑吗',
		    icon: 'error',
		    ok: function () {
		    	var url = $unique.base + '/admin/special/enable';
				var param = { sid : sid, status : status };
				$.post(url, param, function(data) {
					if(data && data === 'success'){
						$unique.alert('禁用成功！');
						window.location.href = $unique.base + '/admin/special';
					} else{
						$unique.alert('禁用失败！');
					}
				},'text');
		    },
		    cancel: true
		});
	}
}
