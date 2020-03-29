$(function(){
    let contextPath = $('p.contextpath').text();
    let eventId = $('p.eventId').text();
    layui.use(['layer', 'element', 'table', 'form'], function () {

        let layer = layui.layer;
        let element = layui.element;
        let form = layui.form;
        let table = layui.table;
        let option = {
            elem: '#patientTable'
            ,url:contextPath + '/admin/patienttable/patientinfo'
            ,where:{eventId: eventId}
            ,title: '疫情事件数据表'
            ,method:"POST"
            ,toolbar:'#toolbar'
            ,cols: [[
                {type: 'checkbox', fixed: 'left'}
                ,{field:'id', title:'ID', unresize: true, sort: true, width:70, align:"center"}
                ,{field:'name', title:'名字',width: 120, align:"center"}
                ,{field:'locationProvince', title:'所在省', align:"center"}
                ,{field:'locationCity', title:'所在市', align:"center"}
                ,{field:'locationDetail', title:'详细地址', width: 190, align:"center"}
                ,{field:'idNumber', title:'身份证号', width: 170, align:"center"}
                ,{field:'reportingTime', title:'确诊时间', width: 150, align:"center"}
                ,{field:'status', title:'病情', width: 80, align:"center"}
                ,{field:'faceAuthentication', title:'是否人脸认证', width: 130,align:"center"}
                ,{title:'操作', toolbar: '#bar', width: 170, align:"center"}
            ]]
            ,page: true
            ,parseData: function(res){
                if(res !== undefined){
                    res.data.forEach(function(element, index){
                        let d = new Date(element.reportingTime);
                        element.reportingTime = `${d.getFullYear()}-${d.getMonth().toString().padStart(2,'0')}-${d.getDay().toString().padStart(2,'0')} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`;
                        element.faceAuthentication = element.faceAuthentication ? "是" : "否";
                    });
                }
            }
        };
        let tableIns = table.render(option);

        //搜索行事件监听

        /*
         * @Title:
         * @Description: 这个函数给select下拉组件实现选择省份后，异步向后台请求该省份所有城市，然后可以下拉选择该省份的所有城市的功能
         * @param selectFilter: 例如 'select(filter)'
         * @param citySelect: 显示城市的下拉框的DOM
         * @return
         * @Author: ShangJin
         * @Date: 2020/3/29
         */
        function bindSelect (selectFilter, citySelect){
            form.on(selectFilter, function(data){
                citySelect.children().remove();
                citySelect.append(`<option value="">所在市/区</option>`);
                let cities = [];
                //异步请求该省所有的市
                $.post(contextPath + '/user/search/province',
                    {province: data.value}, function (res) {
                        res.forEach(function (element, index) {
                            cities.push(`<option value="${element}">${element}</option>`);
                        });
                        citySelect.append(cities.join(" "));
                        form.render('select');
                    }, "JSON");
            });
        }

        //点击所在省更新相应的市
        bindSelect('select(province)', $('select[name=city]'));
        // form.on('select(province)', function(data){
        //     let citySelect = $('select[name=city]');
        //     citySelect.children().remove();
        //     citySelect.append(`<option value="">所在市/区</option>`);
        //     let cities = [];
        //     //异步请求该省所有的市
        //     $.post(contextPath + '/user/search/province',
        //         {province: data.value}, function (res) {
        //             res.forEach(function (element, index) {
        //                 cities.push(`<option value="${element}">${element}</option>`);
        //             });
        //             citySelect.append(cities.join(" "));
        //             form.render('select');
        //         }, "JSON");
        // });

        //点击搜索事件监听
        $('#confirm').click(function () {
            let province = $('select[name=province]').val();
            let city = $('select[name=city]').val();
            let idNumber = $('input[name=idNumber]').val();
            let where = {};
            where.province = province;
            where.city = city;
            where.idNumber = idNumber;
            where.eventId = eventId;
            tableIns.reload({where:where});
        });

        //监听行工具事件
        table.on('tool(patientTable)', function(obj){
            let data = obj.data;
            //console.log(obj)
            switch (obj.event) {
                //编辑患者信息事件
                case 'edit':
                    layer.open({
                        type: 1,
                        skin: 'layui-layer-rim', //加上边框
                        area: ['600px', '500px'], //宽高
                        title: '患者信息',
                        content: $('#patientInfoForm').html(),
                        success: function (layero, index) {
                            form.render('select', 'patientInfoForm');
                            //给输入框绑定默认值
                            let dom = $('#layerForm');
                            dom.find("input[name=idNumber]").val(data.idNumber);
                            let name = dom.find('input[name=name]');
                            name.val(data.name);
                            let locationDetail = dom.find('input[name=locationDetail]');
                            locationDetail.val(data.locationDetail);
                            let idNumber = dom.find('input[name=idNumber]');
                            idNumber.val(data.idNumber);
                            let time = dom.find('input[name=reportingTime]');
                            time.val(data.reportingTime);
                            //给下拉框绑定事件
                            bindSelect('select(layerProvince)', $('#layerCity'));

                            //表单提交检查
                            dom.find('#fsubmit').click(function () {
                                let inputName = name.val();
                                if(inputName.length >= 20){
                                   layer.msg("名字过长");
                                   return;
                                }
                                let inputProvince = dom.find('select[name=province]').val();
                                let inputCity = dom.find('select[name=city]').val();

                                if(inputProvince === ''|| inputCity == ''){
                                    layer.msg("请选择所在地");
                                    return;
                                }
                                let inputStatus = dom.find('select[name=status]').val();
                                if(inputStatus === ''){
                                    layer.msg("请选择病情");
                                    return;
                                }
                                let load = layer.load(2);
                                // 请求后台修改患者信息
                                $.ajax({
                                    url: contextPath + '/admin/patienttable/update',
                                    type: 'POST',
                                    data:{
                                        idNumber: data.idNumber,
                                        name: inputName,
                                        province: inputProvince,
                                        city: inputCity,
                                        detail: dom.find('input[name=locationDetail]').val(),
                                        status: inputStatus,
                                        order: 'edit'
                                    },
                                    timeout:2000,//超时
                                    dataType: 'JSON',
                                    success:function (res, status) {
                                        layer.close(load);
                                        if(res.msg === 'success'){
                                            layer.close(index);
                                            layer.msg('修改成功');
                                            obj.update({
                                                name: inputName,
                                                locationProvince: inputProvince,
                                                locationDetail: dom.find('input[name=locationDetail]').val(),
                                                locationCity: inputCity,
                                                status: inputStatus
                                            });
                                        }else{
                                            layer.msg("服务器发生了点问题...");
                                        }
                                    },
                                    error:function(XMLHttpRequest,textStatus,errorThrown){
                                        layer.close(load);
                                        layer.msg("网络异常");
                                    }
                                });//ajax 请求后台修改患者信息
                            });
                        }
                    });
                    break;
                //注册人脸事件
                case 'registerFace':
                    break;

            }
        });

        //表格头部工具栏事件监听
        table.on('toolbar(patientTable)', function (obj) {
            let checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'delete':
                    //删除选中患者信息回调
                    layer.confirm('确定要删除<strong>所有选中项<strong/>？', function (index) {
                        layer.close(index);
                        let load = layer.load(2);
                        let data = checkStatus.data;
                        let ids = [];
                        data.forEach(function(element){
                            ids.push(element.id);
                        });
                        $.ajax({
                            url: contextPath + '/admin/patienttable/update',
                            data: {ids: ids, order:'delete'},
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
                    });
                    break;
                case 'import':
                    //导入患者信息事件
                    break;
            }
        });
    });
});