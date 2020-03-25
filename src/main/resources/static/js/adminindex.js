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
                ,{field:'introduction', title:'简介'}
                ,{fixed: 'right', title:'权限操作', toolbar: '#barMethod'}
            ]]
            ,page: true
        };
        //渲染表格
        let tableIns = table.render(tableOption1);

       //监听行工具事件
       table.on('tool(tablefilter)', function(obj){
           let data = obj.data;
           if(obj.event === 'upgrade'){
               console.log("tool时间监听");
                //升级权限
               let index = layer.load(2);
               $.post(contextPath + '/admin/usertable/upgrade/' + data.id, function (res) {
                    layer.close(index);
                   if(res.msg == 'error'){
                       layer.msg("服务器出了点小问题");
                       return;
                   }
                    //更新服务器传回的数据
                    obj.update({
                        level:res.level
                    });
               }, "JSON")
           } else if(obj.event === 'downgrade'){
               let index = layer.load(2);
               //降级权限
               $.post(contextPath + '/admin/usertable/downgrade/' + data.id, function (res) {
                   layer.close(index);
                   if(res.msg == 'error'){
                       layer.msg("服务器出了点小问题");
                       return;
                   }
                   //更新服务器传回的数据
                   obj.update({
                       level:res.level
                   });
               }, "JSON")
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
                       return '<em>'+ res.uemail +'</em>'
                   }}
               ,{field:'uemail', title:'用户', templet: function(res){
                       return '<em>'+ res.aemail +'</em>'
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