$(function () {
    let contextPath = $("p.contextpath").text();
    let flag1 = false;
    let flag2 = false;
    let flag3 = false;
    let flag4 = false;
    let btnSendCaptcha = $("#sendcaptcha");
    let inputEmail = $("input[name=email]");
    let inputCaptcha = $("input[name=captcha]");
    let inputPass1 = $("input[name=password1]");
    let inputPass2 = $('input[name=password2]');
    let hasTime;
    let submitBtn = $('#registersubmitebtn');
    let layer;
    layui.use(['element', 'form', 'layer'], function(){
        var element = layui.element;
        var form = layui.form;
        layer = layui.layer;
    });
    //检查密码输入规范
    let checkPass1 = function(){
        $("#passwordtip1").text("");
        if(/^[a-zA-Z_0-9]{6,12}$/.test(inputPass1.val())){
            flag2=true;
        }else {
            flag2=false;
            $("#passwordtip1").text("密码必须6到12位,为包括下划线在内的的任何单词字符.");
            return '密码必须6到12位,为包括下划线在内的的任何单词字符';
        }
    };
    //输入密码框失去焦点时检查密码是否符合规范
    inputPass1.on("blur", function () {
        checkPass1();
    });

    //检查确认密码框输入规范
    let checkComfirmPass = function(){
        $("#passwordtip2").text("");
        let password = inputPass1.val();
        if(inputPass2.val() !== password){
            flag3=false;
            $("#passwordtip2").text("两次输入密码密码不一致.");
            return "两次输入密码密码不一致"
        }else{
            flag3=true;
        }
    };
    //确认输入密码框失去焦点时检查密码是否符合规范
    inputPass2.on("blur", function () {
        checkComfirmPass();
    });

    //检查验证码输入是否正确
    let checkInputCaptcha = function(){
        $("#captchatip").text("");
        $.ajax({
            url:contextPath+"/register/m-getCaptcha",
            dataType:"json",
            type:"post",
            async:true,
            data:{"captcha":$("input[name=captcha]").val()},
            success:function (data, status) {
                console.log("验证验证码请求成功");
                console.log("请求结果：" + data);
                if(!data){
                    flag4 = false;
                    $("#captchatip").text("验证码错误");
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
    };
    inputCaptcha.on("blur", function () {
        checkInputCaptcha();
    });
    //发送验证码按钮响应事件
    btnSendCaptcha.on("click", function () {
        if(!flag1){
            layer.msg("请输入正确的邮箱");
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

    let checkEmail = function(){
        $("#emailtip").text("");
        let email = inputEmail.val();
        if(/^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/.test(email) === false){
            flag1 = false;
            $("#emailtip").text("请输入正确的邮箱");
            return;
        }
        $.ajax({
            url:contextPath+"/register/m-hasEmail",
            dataType:"json",
            type:"post",
            data:{"email":email},
            async:true,
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
    };
    inputEmail.on("blur", function () {
        checkEmail();
    });
    submitBtn.on("click", function () {
        checkEmail();
        checkPass1();
        checkComfirmPass();
        checkInputCaptcha();
        if(flag1&& flag2 &&flag3 && flag4){
            $("#submitedPassword").val(hex_md5($("input[name=password1]").val()));
            layer.msg("正在注册，注册成功后页面跳转");
            setTimeout(function () {
                $('#registerInfo').submit();
            }, 3000);
        }else{
            layer.msg("请检查信息填写是否正确");
        }
    })
});