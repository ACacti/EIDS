var out;
$(function () {
    layui.use(['element', 'layer', 'upload', 'table', 'form'], function () {
        let element = layui.element;
        let layer = layui.layer;
        let upload = layui.upload;
        let form = layui.form;
        let table = layui.table;
        let contextPath = $('p.contextpath').text();
        layer.tips('觉得搜索麻烦？试试人脸识别！', $('#btn-face-recognition'), {
            tips: [2, '#3595CC'],
            time: 6000
        });
        //监听省复选框点击事件
        form.on('select(province)', function(data){
            console.log(data.elem); //得到select原始DOM对象
            console.log(data.value); //得到被选中的值
            console.log(data.othis); //得到美化后的DOM对象
            let citySelect = $('select[name=city]');
            citySelect.children().remove();
            citySelect.append(`<option value="">所在市/区</option>`);
            let cities = [];
            //异步请求该省所有的市
            $.post(contextPath + '/user/search/province',
                {province: data.value}, function (res) {
                    out = res;
                    res.forEach(function (element, index) {
                        cities.push(`<option value="${element}">${element}</option>`);
                    });
                    citySelect.append(cities.join(" "));
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
                table.render({
                    url: contextPath + '/user/search/patientInfo',
                    method: "post",
                    where: {province: province, city: city},
                    elem: '#dataTable',
                    limit:12,
                    page: true,
                    loading: true,
                    even:true,
                    skin: "row",
                    cols:[[
                        {field: 'id', title: 'ID', width:100, sort: true, fixed: 'left'},
                        {field: 'name', title: '姓名', width:150, fixed: 'left'},
                        {field: 'locationProvince', title: '所在省/直辖市', width:150, fixed: 'left'},
                        {field: 'locationCity', title: '所在市/区', width:150, fixed: 'left'},
                        {field: 'epidemicName', title: '疫情事件', width:200, fixed: 'left'},
                        {field: "status", title:"状态", width:120, fixed:'left'},
                        {field: "reportingTime", title:"确诊时间", width: 200, fixed:'left', sort:true}
                    ]]
                })
            }
        });

        //人脸识别监听
        upload.render({
            elem: '#btn-face-recognition',
            url: contextPath + '/user/search/upload',
            accept: "image/jpg, image/png",
            auto: true,
            size: 10 * 1024,
            multiple: false,
            drag: true,
            done: function () {

            },
            error: function () {
                
            }

        })
    });
});