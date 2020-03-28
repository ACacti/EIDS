$(function(){
    let contextPath = $('p.contextpath').text();
    layui.use(['layer', 'element', 'table'], function () {
        let layer = layui.layer;
        let element = layui.element;
        let table = layui.table;
        let tableIns = table.render({
            elem: '#eventTable'
            ,url:contextPath + '/admin/eventtable/event'
            ,title: '疫情事件数据表'
            ,method:"POST"
            ,toolbar:'#toolbar'
            ,cols: [[
                {field:'id', title:'ID', unresize: true, sort: true}
                ,{field:'name', title:'疫情事件名称', }
                ,{field:'publisherEmail', title:'发布者', templet: function(res){
                        return '<em>'+ res.publisherEmail +'</em>'
                    }}
                ,{field:'releaseTime', title:'发布时间',  sort: true}
                ,{field:'count', title:'患者数量', sort: true}
                ,{title:'操作', toolbar: '#bar'}
            ]]
            ,page: true
            ,parseData: function(res){
                res.data.forEach(function(element, index){
                    element.publisherEmail = element.publisher.email;
                    element.count = res.supplement[index];
                    let d = new Date(element.releaseTime);
                    element.releaseTime = `${d.getFullYear()}-${d.getMonth().toString().padStart(2,'0')}-${d.getDay().toString().padStart(2,'0')} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`;
                });
            }
        });

        //监听行工具事件
        table.on('tool(eventTable)', function(obj){
            let data = obj.data;
            //console.log(obj)
            if(obj.event === 'del'){
                //删除疫情事件回调
                layer.confirm('确定要删除 <strong>' + data.name + '</strong> 疫情事件？<br>该操作会删除此疫情事件下的所有患者信息！', function(index){
                    let load = layer.load(1);
                    $.get(contextPath + '/admin/eventtable/delete/' + data.id, function(res){
                        layer.close(load);
                        if(res.msg === 'success'){
                            obj.del();
                            layer.close(index);
                            location.reload();
                        }else{
                            layer.msg("服务器出了点问题..");
                        }
                    }, "JSON");
                });
            } else if(obj.event === 'edit'){
                //编辑疫情事件名字回调
                layer.prompt({
                    formType: 0
                    ,value: data.name
                    ,title: "新事件名称"
                }, function(value, index){
                    //开启loading
                    let load = layer.load(1);
                    //异步请求后台修改名字
                    $.post(contextPath + '/admin/eventtable/edit', {eventName: data.name, newName: value},function(res){
                        layer.close(load);
                        if(res.msg === 'success'){
                            //修改成功
                            obj.update({
                                name: value
                            });
                            layer.close(index);
                            location.reload()
                        }else{
                            layer.msg("操作异常，请检查新名字是否与已有事件名字重复。");
                        }
                    }, "JSON");
                });
            }
        });
        //发布疫情事件按钮点击事件监听
        table.on('toolbar(eventTable)', function (obj) {
            if (obj.event === 'addEvent') {
                layer.prompt({title: '输入事件名称', formType: 0}, function(name, index){
                    let load = layer.load(2);
                    $.post(contextPath + '/admin/eventtable/addEvent', {name: name}, function(res){
                        layer.close(load);
                        console.log(res);
                        if(res.msg === 'success'){
                            layer.close(index);
                            layer.msg("发布成功");
                            location.reload();
                        }else{
                            layer.msg("操作异常，请检查事件名称和已有事件是否重名");
                        }
                    }, "JSON");
                });
            }
        });
    });
});