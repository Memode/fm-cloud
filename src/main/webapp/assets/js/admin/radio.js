/**
 * 全局radio
 */
$unique.admin.radio = {};
$(function() {

	var uploader = new plupload.Uploader({
		runtimes : 'html5,flash',
		browse_button : 'uploader', // you can pass in id...
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
	uploader.bind('FilesAdded', function(uploader, files) {
		uploader.start();
	});
	uploader.bind('Error', function(uploader,errObject) {
		if(errObject.code == -601){
			alert('请选择正确的电台格式！');
		}
	});
	uploader.bind('FileUploaded', function(uploader, file, response) {
		var data = $.parseJSON(response.response);
		$('#upload_radio_form').find('input[name="url"]').val(data.key).attr('readonly', true);
		$('#upload_radio_form').find('input[name="title"]').val(data.file_name);
		uploader.removeFile(file);
		uploader.refresh();
	});
	uploader.init(); //初始化
	
	$('#upload_radio_form').validator({
		display: function(el){
	        return el.getAttribute('placeholder') || '';
	    },
		fields : {
			title : 'required; length[1~50]',
			url : 'required; '
		},
		valid : function(form) {
			var url = $unique.base + '/admin/radio/save';
			var param = $(form).serializeArray();
			$.post(url, param, function(data) {
				if (data) {
					if (data === 'success') {
						$unique.alert('保存成功！');
						window.location.href = $unique.base + '/admin/radio';
					} else {
						$unique.alert('保存失败！');
					}
				}
			});

		}
	});
});


/**
 * 删除电台
 */
$unique.admin.radio.del = function(id) {
	art.dialog({
		lock : true,
		content : '确定删除该电台吗',
		icon : 'error',
		ok : function() {
			var url = $unique.base + '/admin/radio/del';
			var param = {
				id : id
			};
			$.post(url, param, function(data) {
				if (data && data === 'success') {
					$unique.alert('删除成功！');
					window.location.href = $unique.base + '/admin/radio';
				} else {
					$unique.alert('删除失败！');
				}
			}, 'text');
		},
		cancel : true
	});
}
