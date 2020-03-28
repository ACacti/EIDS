$(function(){
    let contextPath = $('p.contextpath').text();
    layui.use(['layer', 'element', 'table', 'form'], function () {
        let layer = layui.layer;
        let element = layui.element;
        let table = layui.table;
        let form = layui.form;
        let option1 = {
            elem: '#articleTable'
            ,url:contextPath + '/admin/articletable/article'
            ,title: '防疫资讯表'
            ,method:"POST"
            ,toolbar:'#toolbar'
            ,cols: [[
                {type: 'checkbox', fixed: 'left'}
                ,{field:'id', title:'ID', unresize: true, sort: true}
                ,{field:'title', title:'标题',templet: function(res){
                        return `<a href="${contextPath}/user/article/display/${res.id}" class="layui-table-link">` + res.title +'</a>'
                    }}
                ,{field:'content', title:'内容'}
                ,{field:'authorEmail', title:'发布者', templet: function(res){
                        return '<em>'+ res.authorEmail +'</em>'
                    }}
                ,{field:'releaseDate', title:'发布时间',  sort: true}
                ,{field:'weight', title:'权重', sort: true}
                ,{field:'views', title:"阅读量", sort: true}
                ,{title:'操作', toolbar: '#bar'}
            ]]
            ,page: true
            ,parseData: function(res){
                if(res.data != undefined){
                    res.data.forEach(function(element, index){
                        element.authorEmail = element.author.email;
                        let d = new Date(element.releaseDate);
                        element.releaseDate = `${d.getFullYear()}-${d.getMonth().toString().padStart(2,'0')}-${d.getDay().toString().padStart(2,'0')} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`;
                    });
                }
            }
        };
        let tableIns1 = table.render(option1);
        //监听行工具事件
        table.on('tool(articleTable)', function(obj){
            let data = obj.data;
            switch (obj.event) {
                case 'upgrade':{
                    let load = layer.load(2);
                    //增加防疫资讯权重回调
                    $.ajax({
                        url: contextPath + '/admin/articletable/update',
                        data: {ids: [data.id], order: 'upgrade'},
                        dataType: "JSON",
                        type: "POST",
                        traditional:true,
                        success: function (res) {
                            layer.close(load);
                            if(res.msg == 'success'){
                                obj.update({
                                    weight: res.data
                                });
                            }else{
                                layer.msg("服务器出了点小问题...");
                            }
                        },
                        error: function () {
                            layer.msg("网络异常");
                        }
                    });
                    break;
                }
                case 'downgrade':{
                    //降级援助请求权重回调
                    let load = layer.load(2);
                    $.ajax({
                        url: contextPath + '/admin/articletable/update',
                        data: {ids: [data.id], order: 'downgrade'},
                        dataType: "JSON",
                        type: "POST",
                        traditional:true,
                        success: function (res) {
                            layer.close(load);
                            if(res.msg == 'success'){
                                obj.update({
                                    weight: res.data
                                });
                            }else{
                                layer.msg("服务器出了点小问题...");
                            }
                        },
                        error: function () {
                            layer.msg("网络异常");
                        }
                    });
                    break;
                }
            }
        });
        //援助请求表格头部工具栏事件监听
        table.on('toolbar(articleTable)', function (obj) {
            let checkStatus = table.checkStatus(obj.config.id);
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
                            url: contextPath + '/admin/articletable/update',
                            data: {ids: ids, order: 'delete'},
                            dataType: "JSON",
                            type: "POST",
                            traditional:true,
                            success: function (res) {
                                layer.close(load);
                                if(res.msg == 'success'){
                                    tableIns1.reload();
                                }else{
                                    layer.msg("服务器出了点小问题...");
                                }
                            },
                            error: function () {
                                layer.msg("网络异常");
                            }
                        });
                    });
                    break;
                }
            }
        });
        //内容查找事件监听
        $('div.tableSearch .layui-btn').click(function(){
            let content = $('div.tableSearch .layui-input').val();
            option1.where = {content:content};
            option1.page = {curr: 1};
            //表格重载
            tableIns1.reload(option1);
        });

        let tableIns2 = table.render({
            elem: '#recordTable'
            ,url:contextPath + '/admin/articletable/record'
            ,where: {onlyMine: false}
            ,title: '疫情援助请求操作记录表'
            ,method:"POST"
            ,cols: [[
                {field:'id', title:'ID', unresize: true, sort: true}
                ,{field:'admin', title:'管理员', templet: function(res){
                        return '<em>'+ res.admin +'</em>'
                    }}
                ,{field:'title', title:'资讯标题'}
                ,{field:'recordTime', title:'操作时间', sort: true}
                ,{field:'recordType', title:'操作类型'}
            ]]
            ,page: true
            ,parseData: function(res){
                if(res.data != undefined){
                    res.data.forEach(function(element, index){
                        let d = new Date(element.recordTime);
                        element.recordTime = `${d.getFullYear()}-${d.getMonth()}-${d.getDay()} ${d.getHours()}:${d.getMinutes()}`;
                        element.admin = element.admin.email;
                        element.title = element.msg.title;
                    });
                }
            }
        });

        //只看我 事件监听
        form.on('switch(onlyMine)', function(data){
            if(data.elem.checked){
                tableIns2.reload({
                    where: {onlyMine: true}
                    ,page:{curr: 1}
                });
            }else{
                tableIns2.reload({
                    where: {onlyMine: false}
                    ,page:{curr: 1}
                })
            }
        });
    });
});