$(function () {
    let contextPath = $("p.contextpath").text();
    let flag1 = false;
    let flag2 = false;
    let flag3 = false;
    let flag4 = false;
    let btnSendCaptcha = $("#sendcaptcha");
    let inputEmail = $("input[name=email]");
    let inputCaptcha = $("input[name=captcha]");
    let hasTime;
    let layer;
    layui.use(['element', 'form', 'layer'], function(){
        var element = layui.element;
        var form = layui.form;
        layer = layui.layer;
        form.verify({
            pass:function (value, item) {
                $("#passwordtip1").text("");
                if(/^[a-zA-Z_0-9]{6,12}$/.test(value)){
                    flag2=true;
                }else {
                    flag2=false;
                    $("#passwordtip1").text("密码必须6到12位,为包括下划线在内的的任何单词字符.");
                    return '密码必须6到12位,为包括下划线在内的的任何单词字符';
                }
            },

            confirmpass: function (value, item) {
                $("#passwordtip2").text("");
                var password = $("input[name=password1]").val();
                if(value != password){
                    flag3=false;
                    $("#passwordtip2").text("两次输入密码密码不一致.");
                    return "两次输入密码密码不一致"
                }else{
                    flag3=true;
                }
            },
            captcha: function (value, item) {
                $("#captchatip").text("");
                if(flag4){
                    return;
                }
                $("#captchatip").text("验证码错误.");
                return "验证码错误"
            }
        });
        form.on("submit(fsubmit)", function (data) {
            if(flag1&& flag2 &&flag3 && flag4){
                $("#submitedPassword").val(hex_md5($("input[name=password1]").val()));
                return true;
            }
            return false;
        })
    });
    inputCaptcha.on("blur", function () {
        $.ajax({
            url:contextPath+"/register/m-getCaptcha",
            dataType:"json",
            type:"post",
            data:{"captcha":$("input[name=captcha]").val()},
            success:function (data, status) {
                console.log("验证验证码请求成功");
                console.log("请求结果：" + data);
                if(!data){
                    flag4 = false;
                }else{
                    flag4=true;
                }
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                if(textStatus==='timeout'){
                    alert('請求超時');
                    setTimeout(function(){
                        alert('重新请求');
                    },2000);
                }
                //alert(errorThrown);
            }
        });
    });
    btnSendCaptcha.on("click", function () {
        if(!flag1){
            layer.msg("邮箱不合法");
            flag4 = false;
            return;
        }
        var index = layer.load(0, {shade: false});
        $.ajax({
            url:contextPath+"/register/m-sendCaptcha",
            type:"post",
            dataType:"json",
            data:{"email":$("input[name=email]").val()},
            success:function(data, status){
                console.log("已发送验证码");
                layer.close(index);
                layer.msg('我们的验证码已发送到您的邮箱');
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                if(textStatus==='timeout'){
                    alert('請求超時');
                    setTimeout(function(){
                        alert('重新请求');
                    },2000);
                }
                //alert(errorThrown);
            }
        });
        btnSendCaptcha.attr("disabled", true);
        hasTime = 30;
        var timeInterval = setInterval(function () {
            hasTime -= 1;
            btnSendCaptcha.text(hasTime + "s后重发验证码")
        }, 1000);
        setTimeout(function () {
            clearInterval(timeInterval);
            btnSendCaptcha.attr("disabled", false);
            btnSendCaptcha.text("发送验证码")
        }, 30000);
    });

    inputEmail.on("blur", function () {
        $("#emailtip").text("");
        let email = $("input[name=email]").val();
        if(/^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/.test(email) === false){
            flag1 = false;
            return;
        }
        $.ajax({
            url:contextPath+"/register/m-hasEmail",
            dataType:"json",
            type:"post",
            data:{"email":email},
            success:function (data, status) {
                console.log("请求成功");
                console.log("请求结果：" + data);
                if(data){
                    $("#emailtip").text("该邮箱已被注册.");
                    flag1 =false;
                }else{
                    flag1=true;
                }
            },
            error:function(XMLHttpRequest,textStatus,errorThrown){
                if(textStatus==='timeout'){
                    alert('請求超時');
                    setTimeout(function(){
                        alert('重新请求');
                    },2000);
                }
                //alert(errorThrown);
            }
        });
    });
});