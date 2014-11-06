/**
 * 全局music
 */

$unique.bind = {};

$(function(){
	$('#bind_qq_form').validator({
		fields : {
			nickname : 'required; length[1~50]',
			email : 'required; email',
			password : 'required; length[6~20]'
		},
		valid : function(form) {
			
			var url = $unique.base + '/save_bind_qq';
			var param = $(form).serializeArray();
			$.post(url, param, function(data) {
				if (data) {
					if (data === 'success') {
						$unique.alert('绑定成功！');
						window.location.href = $unique.base + '/admin/index';
					} else if(data === 'exist'){
						$unique.alert('绑定失败，该邮箱已经绑定过QQ，请重新输入！');
					} else {
						$unique.alert('绑定失败，请重新登录！');
					}
				}
			});

		}
	});
});

/**
 * 播放MP3
 * 
 * @param mid
 */
$unique.bind.qq_sub = function(mid) {
	if (mid) {
		var url = $unique.base + '/m/get_music';
		var param = {
			mid : mid
		};
		$.get(url, param, function(data) {
			if (data) {
				myPlaylist.setPlaylist([ {
					title : data.song,
					artist : data.singer,
					mp3 : data.mp3_url,
					poster : data.cover_url
				} ]);
				// 设置自动播放
				myPlaylist.option("autoPlay", true);
				myPlaylist.play(0);
				if(data.lrc && data.lrc != ''){
					$('#album-right').find('div.lrc').css('overflow-y', 'scroll').html(data.lrc);
				} else{
					$('#album-right').find('div.lrc').css('overflow','hidden').html('暂无歌词');
				}
			}
		}, 'json');
	}
}
