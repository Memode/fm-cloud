(function(){
	$('#verify_code').click(function(){
		$(this).attr('src', $unique.base + '/verify_code?time=' + new Date().getTime());
	});
	
	$('#login_form').validator({
	    fields: {
	        'login_name': '用户名:required;',
	        'pass_word': '密码:required;',
	        'verify_code' : '验证码:required;length[4]'
	    }
	}).on("click", "btn.btn-primary", function(e){
	    $(e.delegateTarget).trigger("validate");
	}).bind('valid.form', function(form){
		var url = $unique.base + '/admin/login';
		var param = $(this).serializeArray();
		$.post(url, param, function(data) {
			if(data){
        		if(data === 'success'){
        			window.location.href = $unique.base + '/admin/index';
        		} else if(data === 'verify_error'){
        			$unique.alert('验证码不正确！');
        		} else{
        			$unique.alert('用户名或密码不正确！');
        		}
        	}
		},'text');
	});
})(window);