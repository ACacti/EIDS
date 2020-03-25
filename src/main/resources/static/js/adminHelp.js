$(function(){
    let contextPath = $('p.contextpath').text();
    layui.use(['layer', 'element', 'table'], function () {
        let layer = layui.layer;
        let element = layui.element;
        let table = layui.table;
        let tableIns = table.render({
            elem: '#helpTable'
            ,url:contextPath + '/admin/helptable/help'
            ,title: '疫情援助请求表'
            ,method:"POST"
            ,toolbar:'#toolbar'
            ,cols: [[
                {type: 'checkbox', fixed: 'left'}
                ,{field:'id', title:'ID', unresize: true, sort: true}
                ,{field:'title', title:'标题'}
                ,{file:'content', title:'内容'}
                ,{field:'publisherEmail', title:'发布者', templet: function(res){
                        return '<em>'+ res.publisherEmail +'</em>'
                    }}
                ,{field:'releaseTime', title:'发布时间',  sort: true}
                ,{field:'weight', title:'权重', sort: true}
                ,{title:'操作', toolbar: '#bar'}
            ]]
            ,page: true
            ,parseData: function(res){
                res.data.forEach(function(element, index){
                    element.publisherEmail = element.publisher.email;
                    let d = new Date(element.releaseTime);
                    element.releaseTime = `${d.getFullYear()}-${d.getMonth()}-${d.getDay()} ${d.getHours()}:${d.getMinutes()}`;
                });
            }
        });
        //监听行工具事件
        table.on('tool(helpTable)', function(obj){
            let data = obj.data;
            switch (obj.event) {
                case 'upgrade':{
                    let load = layer.load(2);
                    //增加援助请求权重回调
                    $.post(contextPath + '/admin/helptable/upgrade',{id: data.id}, function (res) {
                        layer.close(load);
                        if(res.msg ==='success'){
                            obj.update({
                                weight: res.data
                            });
                        }else{
                            layer.msg("服务器出现了点问题...");
                        }
                    }, "JSON");
                    break;
                }
                case 'downgrade':{
                    //降级援助请求权重回调
                    let load = layer.load(2);
                    $.post(contextPath + '/admin/helptable/downgrade',{id: data.id}, function (res) {
                        layer.close(load);
                        if(res.msg ==='success'){
                            obj.update({
                                weight: res.data
                            });
                        }else{
                            layer.msg("服务器出现了点问题...");
                        }
                    },"JSON");
                    break;
                }
            }
        });
        //援助请求表格头部工具栏事件监听
        table.on('toolbar(helpTable)', function (obj) {
            var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'allDel':{
                    //批量删除援助请求回调
                    layer.confirm('确定要删除<strong>所有选中项<strong/>？', function (index) {
                        layer.close(index);
                        let load = layer.load(2);
                        let data = checkStatus.data;
                        let ids = [];
                        data.forEach(function(element){
                            ids.push(element.id);
                        });
                        $.ajax({
                            url: contextPath + '/admin/helptable/delete',
                            data: {ids: ids},
                            dataType: "JSON",
                            type: "POST",
                            traditional:true,
                            success: function (res) {
                                layer.close(load);
                                if(res.msg == 'success'){
                                    tableIns.reload();
                                }else{
                                    layer.msg("服务器出了点小问题...");
                                }
                            },
                            error: function () {
                                layer.msg("网络异常");
                            }
                        });
                    })
                    break;
                }
                case 'addEvent':{
                    //发布援助请求回调
                    layer.open({
                        type: 2,
                        title: '发布疫情援助请求',
                        shadeClose: true,
                        shade: false,
                        maxmin: true, //开启最大化最小化按钮
                        area: ['893px', '600px'],
                        content: contextPath + '/admin/helptable/postaid',
                        end: function () {
                            location.reload();
                        }
                    });
                    break;
                }
            }
        });
    });
});