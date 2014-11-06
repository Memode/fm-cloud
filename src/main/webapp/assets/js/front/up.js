$(function() {
	var uploader = new plupload.Uploader({
		runtimes : 'html5,flash,silverlight,html4',
		browse_button : 'pickfiles', // you can pass in id...
		container : document.getElementById('uploader'), // ... or DOM Element itself
		url : $unique.base + '/upload/images',
		flash_swf_url : $unique.base + '/assets/plugins/plupload/Moxie.swf',
		silverlight_xap_url : $unique.base + '/assets/plugins/plupload/Moxie.xap',
		filters : {
			max_file_size : '10mb',
			mime_types : [ {
				title : "图片文件",
				extensions : "jpg,jpeg,gif,png"
			}]
		},
		init : {PostInit : function() {
					$('#uploadfiles').html("上传文件");
					$("#uploadfiles").on('click', function() {
						uploader.start();
						return false;
				});
			},
			FilesAdded : function(up, files) {
				var html = "";
				plupload.each(files, function(file) {
					html = '<input type="text" name="thumbimg" class="form-control '
						+ file.id + '" value="'
						+ file.name
						+ '"/><label for="none" class="control-label processbar col-md-1 '
						+ file.id + '" ></label>';
				});
				$("#filelist").html(html);
			},
			UploadProgress : function(up, file) {
				$("label." + file.id).html(file.percent + '%');
			},
			FileUploaded : function(up, file, response) {
				var res = $.parseJSON(response.response);
				$("input." + file.id).val(res);
			},
			Error : function(up, err) {
				document.getElementById('console').innerHTML += "\nError #" + err.code + ": " + err.message;
			}
		}
	});
	uploader.init();
});