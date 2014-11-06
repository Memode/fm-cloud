/**
 * 全局music
 */
$unique.radio = {};
$(function() {
});

/**
 * 播放电台
 */
$unique.radio.play = function(obj, id){
	var this_ = $(obj);
	if(this_.attr('class') === 'list-group-item'){
		this_.addClass("active").siblings().removeClass('active');
		this_.addClass("active").siblings().find('i').attr('class', 'icon-play pull-right');
	}
	// 暂停播放
	if(this_.find('i').attr('class') === 'icon-play pull-right'){
		this_.find('i').attr('class', 'icon-stop pull-right');
		
	} else{
		
		this_.find('i').attr('class', 'icon-play pull-right');
	}
}