$(function () {
    let contextPath = $('p.contextpath').text();
   layui.use(['layer', 'table', 'element'], function () {
        let layer = layui.layer;
        let table = layui.table;
        let element = layui.element;
        let tableOption1 = {
            elem: '#usertable'
            ,url:contextPath + '/admin/usertable/users'
            ,where:{}
            ,method: 'POST'
            ,width: 1150
            ,defaultToolbar: ['filter', 'exports', 'print']
            ,title: '用户数据表'
            ,cols: [[
                // {type: 'checkbox', fixed: 'left'}
                {field:'id', width:110, title:'ID', unresize: true, sort: true}
                ,{field:'email', title:'邮箱', templet: function(res){
                        return '<em>'+ res.email +'</em>'
                    }}
                ,{field:'level', title:'权限等级', sort: true}
                ,{field:'introduction', title:'认证名'}
                ,{fixed: 'right', title:'权限操作', toolbar: '#barMethod'}
            ]]
            ,page: true
        };
        //渲染表格
        let tableIns = table.render(tableOption1);

       //监听行工具事件
       table.on('tool(tablefilter)', function(obj){
           let data = obj.data;
           switch (obj.event) {
               case 'upgrade':
                   if (data.level === 1) {
                       layer.prompt({
                           formType: 0,
                           value: "请输入认证名",
                           title: '提升为防疫资讯作者',
                       }, function (value, index) {
                            //检查输入的认证名长度
                           if(value.length >= 20){
                               layer.msg("认证名最大长度为20个字符");
                               return;
                           }
                           //通过检测，异步请求后台修改用户信息
                           let load = layer.load(2);
                           $.post(contextPath + '/admin/usertable/upgrade', {
                               id: data.id,
                               introduction: value
                           }, function (res) {
                               layer.close(load);
                               if (res.msg === 'error') {
                                   layer.msg("服务器出了点小问题");
                                   return;
                               }
                               layer.close(index);
                               //更新服务器传回的数据
                               obj.update({
                                   level: res.level,
                                   introduction: value
                               });
                           }, "JSON");
                       });
                   }else{
                       $.post(contextPath + '/admin/usertable/upgrade',{id: data.id}, function (res) {
                           layer.close(index);
                           if(res.msg === 'error'){
                               layer.msg("服务器出了点小问题");
                               return;
                           }
                           //更新服务器传回的数据
                           obj.update({
                               level:res.level,
                               introduction: value
                           });
                       }, "JSON");
                   }
                   break;
               case 'downgrade':
                   let load = layer.load(2);
                   //请求函数
                   let post = function () {
                       $.post(contextPath + '/admin/usertable/downgrade/' + data.id, function (res) {
                           layer.close(load);
                           if (res.msg === 'error') {
                               layer.msg("服务器出了点小问题");
                               return;
                           }
                           //更新服务器传回的数据
                           obj.update({
                               level: res.level,
                               introduction: ""
                           });
                       }, "JSON")
                   };
                   //降级权限
                   if(data.id == 2){
                       layer.confirm("该用户权限级别为2，降级此用户会删除此用户发布的全部文章，确定要降级吗？", function () {
                           post();
                       })
                   }else{
                       post();
                   }

           }
       });
       //邮箱查找用户监听事件
        $('div.tableSearch .layui-btn').click(function(){
           let email = $('div.tableSearch .layui-input').val();
           tableOption1.where = {email:email};
           tableOption1.page = {curr: 1};
           //表格重载
           tableIns.reload(tableOption1);
        });
        //管理员对用户操作记录表
       let tableOption2 = {
           elem: '#record'
           ,url:contextPath + '/admin/usertable/records'
           ,where:{}
           ,method: 'POST'
           ,width: 1150
           ,defaultToolbar: ['filter', 'exports', 'print']
           ,title: '管理员对用户操作记录表'
           ,cols: [[
               // {type: 'checkbox', fixed: 'left'}
               {field:'id', width: 110, title:'ID', unresize: true, sort: true}
               ,{field:'aemail', title:'管理员', templet: function(res){
                       return '<em>'+ res.aemail +'</em>'
                   }}
               ,{field:'uemail', title:'用户', templet: function(res){
                       return '<em>'+ res.uemail +'</em>'
                   }}
               ,{field:'recordTime', title:'时间', sort: true}
               ,{field:'recordType', title:'类型'}
           ]]
           ,page: true
           ,parseData: function(res){ //res 即为原始返回的数据
               if (res.data !== undefined){
                   res.data.forEach(function(element){
                       element.uemail = element.user.email;
                       element.aemail = element.admin.email;
                       let d = new Date(element.recordTime);
                       element.recordTime = `${d.getFullYear()}-${d.getMonth()}-${d.getDay()} ${d.getHours()}:${d.getMinutes()}`;
                   });
               }
               return {
                   "code": res.code, //解析接口状态
                   "msg": res.msg, //解析提示文本
                   "count": res.count, //解析数据长度
                   "data": res.data //解析数据列表
               };
           }
       };
       //渲染表格
       let tableIns2 = table.render(tableOption2);

       //邮箱查找用户监听事件
       $('div.recordSearch .layui-btn').click(function(){
           let email = $('div.recordSearch .layui-input').val();
           tableOption2.where = {email:email};
           tableOption2.page = {curr: 1};
           //表格重载
           tableIns2.reload(tableOption2);
       });
   });//layui.use
});