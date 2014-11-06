
$unique.admin.setting = {};
/**
 * 清空缓存
 */
$unique.admin.setting.clean = function() {
	var url = $unique.base + '/admin/cleanCache';
	var param = null;
	$.post(url, param, function(data) {
		if (data && data === 'success') {
			$unique.alert('清空成功！', 1);
		} else {
			$unique.alert('清空失败！', 1);
		}
	}, 'text');
}

$(function(){
	$('#setting_form').validator({
		fields : {
			song : 'required; length[1~50]',
			singer : 'length[1~50]',
			introduce : 'length[1~200]',
			song_path : 'required; ',
			cover_path : 'required; '
		},
		valid : function(form) {
			var url = $unique.base + '/admin/save_setting';
			var param = $(form).serializeArray();;
			$.post(url, param, function(data) {
				if (data && data === 'success') {
					$unique.alert('保存成功！');
					window.location.reload();
				} else {
					$unique.alert('保存失败！', 1);
				}
			}, 'text');
		}
	});
	
	$('#qiniu_setting_form').validator({
		fields : {
			key : 'required; length[1~50]'
		},
		valid : function(form) {
			var url = $unique.base + '/admin/sys/cleanQiniu';
			var param = $(form).serializeArray();;
			$.post(url, param, function(data) {
				if (data && data === 'success') {
					$unique.alert('执行成功！');
					window.location.reload();
				} else {
					$unique.alert('执行失败！', 1);
				}
			}, 'text');
		}
	});
});