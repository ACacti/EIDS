$(function(){
    let contextPath = $('p.contextpath').text();
    layui.use(['layer', 'element', 'table', 'form'], function () {
        let layer = layui.layer;
        let element = layui.element;
        let table = layui.table;
        let form = layui.form;
        let option1 = {
            elem: '#adminTable'
            ,url:contextPath + '/admin/adminManagement/admintable/admin'
            ,title: '疫情援助请求表'
            ,method:"POST"
            ,toolbar:'#toolbar'
            ,cols: [[
                {type: 'checkbox', fixed: 'left'}
                ,{field:'id', title:'ID', unresize: true, sort: true}
                ,{field:'email', title:'邮箱', templet: function(res){
                        return '<em>'+ res.email +'</em>'
                    }}
                ,{field:'level', title:'权限'}
                ,{title:'操作', toolbar: '#bar'}
            ]]
            ,page: true
            // ,parseData: function (res) {
            //     if(res.data !== undefined){
            //         res.data.forEach(function (element) {
            //             element.email =
            //         })
            //     }
            //
            // }
        };
        let tableIns1 = table.render(option1);
        //监听行工具事件
        table.on('tool(adminTable)', function(obj){
            let data = obj.data;
            switch (obj.event) {
                case 'modifyPassword':{
                    //修改密码事件回调
                    layer.prompt({title: '输入新密码', formType: 1},function(password, index){
                        let passRex = /^[a-zA-Z_0-9]{6,12}$/;
                        if(!passRex.test(password)){
                            layer.msg("密码由6-12位包括下划线在内的数字和字母字符组成");
                            return;
                        }
                        let load = layer.load(2);
                        $.post(contextPath + '/admin/adminManagement/adminTable/modifyPassword', {id: data.id, newPass: hex_md5(password)}
                        , function (res) {
                            layer.close(load);
                            if(res.msg === 'success'){
                                layer.close(index);
                                layer.msg("修改成功");
                                //重载操作记录表格
                                tableIns2.reload();
                            }else{
                                layer.msg(res.msg);
                            }
                        }, 'JSON');

                    });
                    break;
                }
            }
        });
        //操作记录表
        let tableIns2 = table.render({
            elem: '#recordTable'
            ,url:contextPath + '/admin/adminManagement/adminTable/record'
            ,title: '管理员操作表'
            ,method:"POST"
            ,cols: [[
                {field:'id', title:'ID', unresize: true, sort: true}
                ,{field:'', title:'操作者', templet: function(res){
                        return '<em>'+ res.processor.email +'</em>'
                    }}
                ,{field:'', title:'被操作者', templet: function(res){
                        return '<em>'+ res.managedAdmin.email +'</em>'
                    }}
                ,{field:'recordTime', title:'操作时间', sort: true}
                ,{field:'recordType', title:'操作类型'}
            ]]
            ,page: true
            ,parseData: function(res){
                if(res.data !== undefined){
                    res.data.forEach(function(element, index){
                        let d = new Date(element.recordTime);
                        element.recordTime = `${d.getFullYear()}-${d.getMonth()}-${d.getDay()} ${d.getHours()}:${d.getMinutes()}`;
                    });
                }
            }
        });

        //管理员表表格头部工具栏事件监听
        table.on('toolbar(adminTable)', function (obj) {
            let checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'allDel':{
                    //批量删除管理员事件回调
                    let ids = [];
                    if(checkStatus.data !== undefined){
                        layer.confirm("确定要删除<strong>所有</strong>选中的管理员?",
                            function (index) {
                                checkStatus.data.forEach(function (element) {
                                    ids.push(element.id);
                                });
                                let load = layer.load(2);
                                $.ajax({
                                    url: contextPath + '/admin/adminManagement/adminTable/delete',
                                    data: {ids: ids},
                                    dataType: "JSON",
                                    type: "POST",
                                    traditional:true,
                                    success: function (res) {
                                        layer.close(load);
                                        layer.close(index);
                                        if(res.msg === 'success'){
                                            layer.msg("删除成功");
                                            tableIns1.reload();
                                        }else{
                                            layer.msg(res.msg);
                                        }
                                    },
                                    error: function () {
                                        layer.close(load);
                                        layer.msg("网络异常");
                                    }
                                });
                            })
                    }
                    break;
                }
                case 'addAdmin':{
                    //添加管理员事件回调
                    layer.prompt({title: '新管理员邮箱'}, function (email, index) {
                        let emailRex = /^([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})$/;
                        //检查邮箱格式
                        if(!emailRex.test(email)){
                            layer.msg("请输入正确的邮箱");
                            return;
                        }
                        let load = layer.load(2);
                        $.post(contextPath + '/admin/adminManagement/adminTable/sendCaptcha', {email: email}, function (res) {
                            layer.close(index);
                            layer.close(load);
                            if(res.msg === 'success'){
                                layer.prompt({title: '验证码'}, function (captcha, index) {
                                    layer.close(index);
                                    $.post(contextPath + '/admin/adminManagement/adminTable/verifyCaptcha', {captcha: captcha}, function (res) {
                                        if(res.msg === 'success'){
                                            layer.prompt({title: '输入注册密码', formType: 1}, function (pass, index) {
                                                let passRex = /^[a-zA-Z_0-9]{6,12}$/;
                                                //验证密码格式
                                                if(!passRex.test(pass)){
                                                    layer.msg("密码必须6到12位,为包括下划线在内的的任何单词字符");
                                                    return;
                                                }
                                                $.post(contextPath + '/admin/adminManagement/adminTable/addAdmin', {email: email, password: hex_md5(pass)},
                                                    function (res) {
                                                        layer.close(index);
                                                        if(res.msg === 'success'){
                                                            layer.msg("注册成功！");
                                                            tableIns2.reload();//操作记录表重载
                                                            tableIns1.reload();//管理员表重载
                                                        }else{
                                                            layer.msg("注册失败..");
                                                        }
                                                    }, "JSON");
                                            })
                                        }else{
                                            layer.msg("验证码错误");
                                        }
                                    }, "JSON")
                                });
                                layer.msg("我们向这个邮箱发送了一个验证码，请注意接收");
                            }else{
                                layer.msg("请检查此邮箱是否已被注册");
                            }
                        }, "JSON");
                    });
                    break;
                }
            }
        });


        //内容查找事件监听
        $('#search').click(function(){
            let email = $('div.tableSearch .layui-input').val();
            //表格重载
            if(email === ''){
                tableIns2.reload({where:undefined});
            }else{
                tableIns2.reload({where: {managedEmail: email}});
            }
        });

    });
});