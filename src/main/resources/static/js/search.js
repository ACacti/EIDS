$(function () {
    layui.use(['element', 'layer', 'upload', 'table', 'form'], function () {
        let element = layui.element;
        let layer = layui.layer;
        let upload = layui.upload;
        let form = layui.form;
        let table = layui.table;
        let tableIns1 = undefined;
        let tableIns2 = undefined;
        let contextPath = $('p.contextpath').text();
        //数据表格配置
        //选择地理位置搜索结果数据表格配置
        let tableOption1 = {
            url: contextPath + '/user/search/patientInfo',
            method: "post",
            where: {},
            elem: '#dataTable',
            limit:12,
            page: true,
            width: 1070,
            loading: true,
            even:true,//隔行背景
            skin: "row",
            cols:[[
                {field: 'id', title: 'ID', width:100, sort: true, fixed: 'left'},
                {field: 'name', title: '姓名', width:150, fixed: 'left'},
                {field: 'locationProvince', title: '所在省/直辖市', width:150, fixed: 'left'},
                {field: 'locationCity', title: '所在市/区', width:150, fixed: 'left'},
                {field: 'epidemicName', title: '疫情事件', width:200, fixed: 'left'},
                {field: "status", title:"状态", width:120, fixed:'left'},
                {field: "reportingTime", title:"确诊时间", width: 200, fixed:'left', sort:true, align: 'center'}
            ]],
            parseData: function (res) {
                if(res.data !== undefined){
                    res.data.forEach(function (element) {
                        let d = new Date(element.reportingTime);
                        element.reportingTime = `${d.getFullYear()}-${d.getMonth().toString().padStart(2,'0')}-${d.getDay().toString().padStart(2,'0')} ${d.getHours().toString().padStart(2,'0')}:${d.getMinutes().toString().padStart(2,'0')}`;
                        element.epidemicName = element.epidemicEvent.name;
                    })
                }
            }
        };
        //人脸搜索结果数据表格的配置
        let tableOption2 = {
            elem: '#dataTable',
            limit:12,
            page: true,
            loading: true,
            width: 1140,
            even:true,//隔行背景
            skin: "row",
            cols:[[
                {field: 'id', title: 'ID', width:100, sort: true, fixed: 'left'},
                {field: 'name', title: '姓名', width:150, fixed: 'left'},
                {field: 'locationProvince', title: '所在省/直辖市', width:150, fixed: 'left'},
                {field: 'locationCity', title: '所在市/区', width:150, fixed: 'left'},
                {field: 'epidemicName', title: '疫情事件', width:200, fixed: 'left'},
                {field: "status", title:"状态", width:120, fixed:'left'},
                {field: "reportingTime", title:"确诊时间", width: 150, fixed:'left', sort:true, align:'center'},
                {field: "score", title:"相似度%", width: 120, fixed:'left', sort:true, align:'center'},

            ]]
        };
        layer.tips('觉得搜索麻烦？试试人脸识别！<br>上传一张照片即可快速查找患者信息！', $('#btn-face-recognition'), {
            tips: [2, '#3595CC'],
            time: 6000
        });
        //监听省复选框点击事件
        form.on('select(province)', function(data){
            let citySelect = $('select[name=city]');
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
                    //请求回城市信息，重载城市项select下拉列表
                    form.render('select');
                }, "JSON");
            let province = data.value;
        });
        //数据表格渲染
        $('#confirm').click(function () {
            let province = $('select[name=province]').val();
            let city = $('select[name=city]').val();
            if(province === '' || city === ''){
                layer.msg("请选择省市名称");
            }else{
                //获取搜索信息，绑定数据表格实例
                if(tableIns1 !== undefined){
                    //如果已有表格实例，则重载表格
                    tableIns1.reload({
                        where:{province: province, city: city}
                    });
                }else{
                    //否则绑定表格实例
                    tableOption1.where={province: province, city: city};
                    tableIns1 = table.render(tableOption1);
                }
            }
        });

        //人脸识别监听
        upload.render({
            elem: '#btn-face-recognition',
            url: contextPath + '/user/search/uploadImg',
            accept: "image/jpg, image/png",
            auto: true,
            size: 10 * 1024,
            multiple: false,
            drag: true,

            before: function(){
                layer.load(2);
            },
            done: function (res) {
                layer.closeAll("loading");
                switch (res.code) {
                    //请求成功
                    case 0:
                        if(tableIns2 !== undefined){
                            //如果数据表格已经有实例，就重载表格实例
                            tableIns2.reload();
                        }else{
                            //无表格实例，就绑定表格实例
                            tableOption2.data = res.data;
                            tableIns2 = table.render(tableOption2);
                        }
                        break;
                    //请求异常
                    case 1:
                        layer.msg(res.msg);
                        break;
                }
            },
            error:function () {
                layer.closeAll("loading");
                layer.msg("网络异常,请稍后再试...");
            }
        });
    });
});