
$(function () {
    layui.use(['form', 'layer', 'element'], function(){
        let form = layui.form;
        let element = layui.element;
        let layer = layui.layer;
        let contextPath = $("p.contextpath").text();
        let flag = false;
        let isAdmin = $("input[name=isAdmin]");
        let emailInput = $("input[name=email]");
        let rawPasswordInput = $("input[name=password1]");
        let passwordInput = $("input[name=password]");
        let emailRex = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
        rawPasswordInput.val("");
        rawPasswordInput.on("blur", function () {
            let email = emailInput.val();
            let rawPassword = rawPasswordInput.val();
            flag = false;
            if(!emailRex.test(email) || rawPassword===""){

            }else{
                let password = hex_md5(rawPassword);
                $.ajax({
                    url:contextPath + "/login/m-identify",
                    type:"post",
                    dataType:"json",
                    data:{'email': email, 'password': password, 'isAdmin':isAdmin.prop("checked")},
                    timeout: 2000,
                    success: function (data, status) {
                        console.log("身份验证成功，验证结果：" + data);
                        if(data['identification']){
                            passwordInput.val(password);
                            flag = true;
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
            }
        });

        form.on("submit(fsubmit)", function (data) {
            if(flag){
                return true;
            }else{
                layer.msg("密码错误或邮箱未注册");
                return false;
            }
        });


        $("#forgetpass").click(function () {
            layer.prompt({title: '输入注册邮箱', formType: 0}, function(email, index){
                if(/^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/.test(email) === false){
                    layer.msg("请输入正确的邮箱");
                    return;
                }
                layer.close(index);
                let loading = layer.load(1, {
                    shade: [0.1,'#fff'] //0.1透明度的白色背景
                });

                $.ajax({
                    url: contextPath +"/login/m-sendCaptcha",
                    type: "POST",
                    dataType: "json",
                    data: {"email": email},
                    success: function (data, status) {
                        layer.close(loading);
                        if(data['result']==="success"){
                            layer.msg("验证码已发送到您的邮箱");
                            setTimeout(function () {
                                layer.prompt({title: '输入验证码', formType: 0}, function(pass, index){
                                    if(pass === data['modifyPassowrdCaptcha']){
                                        layer.close(index);
                                        layer.prompt({title: '输入新密码', formType: 1}, function(text, index){
                                            if(/^[a-zA-Z0-9\_\@\.]{6,12}$/.test(text)){
                                                layer.close(index);
                                                let password = hex_md5(text);
                                                $.ajax({
                                                    url: contextPath + "/login/m-modifyPass",
                                                    type: "POST",
                                                    dataType:"json",
                                                    data: {"email": email, "newPass":password},
                                                    timeout: 2000,
                                                    success: function (data, status) {
                                                        layer.close(index);
                                                        if(data['result']==='success'){
                                                            layer.msg("修改成功！")
                                                        }else{
                                                            console.log(JSON.stringify(data));
                                                            layer.msg(data['msg']);
                                                        }
                                                    },
                                                    error: function(){
                                                        layer.close(index);
                                                        layer.msg("服务器出了点小问题，请稍后再试吧");
                                                    }
                                                })
                                            }else {
                                                layer.msg("密码由6-12位英文字母数字或._@组成");
                                            }
                                        });
                                    }else{
                                        layer.msg("验证码错误");
                                    }
                                });
                            }, 2000); //setTimeout
                        }else{
                            layer.msg(data['msg']);
                        }
                    },
                    error:function (XMLHttpRequest,textStatus,errorThrown) {
                        console.log(textStatus);
                        layer.close(loading);
                        layer.msg("服务器出了点小问题，请稍后再试");
                    }

                });//$.ajax
            });
        });
    });
});
