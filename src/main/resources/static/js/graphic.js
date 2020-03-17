$(function () {
    //加载layui element模块
    layui.use(['element', 'layer'], function () {
        let element = layui.element;
        let layer = layui.layer;

        //图标绘制部分
        let contextPath = $("p.contextpath").text();
        let epidemicId = $("p.epidemicId").text();
        let epidemicName = $('p.epidemicName').text();
        let regionName = $('p.regionName').text();
        let mapDom1 = $("#map1").get(0);
        let mapDom2 = $("#map2").get(0);
        let pieDom = $("#pie").get(0);
        let lineAndBarDom = $("#lineAndBar").get(0);
        let mapChart1 = echarts.init(mapDom1);
        mapChart1.showLoading();
        let mapOption = {
            title:{
                show:true,
                text:"累计确诊人数地理分布"
            },
            tooltip: {
                formatter: function (params, ticket, callback) {
                    return params.name + '<br />' +  params.seriesName + '：' + params.value
                }//数据格式化
            },
            visualMap: {
                type:'piecewise',
                min:0,
                max: 10000,
                left:'left',
                bottom:'20%',

                pieces:[
                    {max:0, label:'无', color:'#FFFFFF'},
                    {min:1, max:9, label:'1~9', color: '#FFF8DC'},
                    {min:10, max:99, label:'10~99', color:'#FFF68F'},
                    {min:100, max:999, label:'100~999', color:'#FFAEB9'},
                    {min:1000, max:9999, label:'1000~9999', color:'#FF6347'},
                    {min: 10000, lable:'>10000', color:'#FF0000'}

                ],
                show:true,
                selectedMode:'multiple'
            },
            geo: {
                show: true,
                map: regionName,
                roam: true,
                zoom: 1.1,
                label: {
                    show: true,
                    fontSize: '10',
                    color: 'rgba(0,0,0,0.7)'
                },
                itemStyle: {
                    borderColor: 'rgba(0, 0, 0, 0.2)',
                    shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
                emphasis: {
                    itemStyle: {
                        areaColor: '#F5F5F5',//鼠标选择区域颜色
                        shadowOffsetX: 0,
                        shadowOffsetY: 0,
                        shadowBlur: 20,
                        borderWidth: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            },
            series:
                {
                    id: 1,
                    name: '累计确诊',
                    type: 'map',
                    geoIndex: 0,
                    data: [],
                }

        };

        //开始渲染疫情地图部分
        let mapData = null;
        mapChart1.showLoading();
        //请求疫情地图数据
        $.ajax({
            url: contextPath + '/graphic/mapGraphicData/' + epidemicId + '/' + regionName,
            type: 'POST',
            dataType: "JSON",
            timeout: 2000,
            success: function (data, status) {
                console.log("请求疫情地图数据成功！");
                mapData = data;
                mapOption.series.data = data[0];
                mapChart1.hideLoading();
                mapChart1.setOption(mapOption);
                //全国地图可以点击进入省级地图
                if (regionName == 'china') {
                    mapChart1.on('click', function (params) {
                        layer.msg('查看' + epidemicName + '在' + params.name + '的详细疫情信息？', {
                            time: 0 //不自动关闭
                            , btn: ['是', '否']
                            , yes: function (index) {
                                layer.close(index);
                                window.open(contextPath + '/index/' + epidemicName + '/' + params.name);
                            }
                        });
                    });
                }
            },//success
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === 'timeout') {
                    alert('請求超時');
                    setTimeout(function () {
                        alert('重新请求');
                    }, 2000);
                }
                //alert(errorThrown);
            }
        });//ajax 疫情地图;
        // 点解标签栏切换地图数据
        element.on('tab(docDemoTabBrief)', function(data){
            mapOption.series.data = mapData[data.index];
            if(data.index===0){
                mapOption.title.text = "累计确诊人数地理分布";
                mapOption.series.name = "累计确诊";
            }else{
                mapOption.title.text = "现有确诊人数地理分布";
                mapOption.series.name = "现有确诊";
            }
            mapChart1.setOption(mapOption);
        });

        let pieChart = echarts.init(pieDom);
        pieChart.showLoading();
        //请求饼图数据
        $.ajax({
            url: contextPath + "/graphic/pieGraphic/" + epidemicId + '/' + regionName,
            type: 'POST',
            dataType: "JSON",
            timeout: 2000,
            success: function (data, status) {
                console.log("请求饼图数据成功！");
                pieChart.hideLoading();
                pieChart.setOption({
                    tooltip: {
                        trigger: 'item',
                    },
                    title: {
                        show: true,
                        text: '各种症状患者占比'
                    },
                    legend: {
                        top: 'top',
                        right: '10px',
                        width: '12%',
                        show: true,
                    },
                    series: [
                        {
                            left: '0px',
                            name: '患者分布',
                            type: 'pie',
                            radius: '55%',
                            data: data,
                            roseType: 'angle'
                        }
                    ]
                });            // pieChart.setOption(option);

            },//success
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === 'timeout') {
                    alert('請求超時');
                    setTimeout(function () {
                        alert('重新请求');
                    }, 2000);
                }
                //alert(errorThrown);
            }
        });//ajax 疫情饼图;

        let lineAndBarChart = echarts.init(lineAndBarDom);
        lineAndBarChart.showLoading();
        //请求折线图和柱状图数据
        $.ajax({
            url: contextPath + "/graphic/lineAndBarGraphic/" + epidemicId + '/' + regionName,
            type: "POST",
            dataType: "JSON",
            timeout: 2000,
            success: function (data, status) {
                console.log("请求折线图和柱状图数据成功");
                let option = {
                    title:{
                        show:true,
                        text:'人数统计及趋势',
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    dataset: {
                        source: data,
                        sourceHeader: true,//第一行是维度名
                    },
                    legend: {
                        // type:'scroll',
                        show: true,
                        width: "40%"
                    },
                    xAxis: {
                        name: "日期",
                        type: 'time'
                    },
                    yAxis: [
                        {
                            type: "value",
                            name: '累计人数',
                        },
                        {
                            type: 'value',
                            name: '现有人数',
                        }
                    ],
                    dataZoom: {
                        type: 'slider',
                        show: true,
                        xAxisIndex: [0],
                        dataBackground: {
                            lineStyle:{
                                color: {
                                    type: 'linear',
                                    x: 0,
                                    y: 0,
                                    colorStops: [{
                                        offset: 0, color: 'red' // 0% 处的颜色
                                    }, {
                                        offset: 1, color: 'blue' // 100% 处的颜色
                                    }],
                                    global: false // 缺省为 false
                                }
                            }
                        },
                        showDetail: true,
                        showDataShadow: true,
                    },
                    series: [{
                        type: 'line',
                        name: '累计治愈数量',
                        yAxisIndex: 0,
                        encode: {
                            x: 0,
                            y: 1
                        }
                    },
                        {
                            type: 'bar',
                            name: '现有轻微数量',
                            yAxisIndex: 1,
                            encode: {
                                x: 0,
                                y: 2
                            }
                        },
                        {
                            type: 'bar',
                            name: '现有危重数量',
                            yAxisIndex: 1,
                            encode: {
                                x: 0,
                                y: 3
                            }
                        },
                        {
                            type: 'line',
                            name: '累计死亡数量',
                            yAxisIndex: 0,
                            encode: {
                                x: 0,
                                y: 4
                            }
                        },
                        {
                            type: 'line',
                            name: '累计确诊数量',
                            yAxisIndex: 0,
                            encode: {
                                x: 0,
                                y: 5
                            }
                        },
                        {
                            type: 'bar',
                            name: '新增确诊数量',
                            yAxisIndex: 1,
                            encode: {
                                x: 0,
                                y: 6
                            }
                        }
                    ]//series
                };
                lineAndBarChart.hideLoading();
                lineAndBarChart.setOption(option);
            }
        });
    })

});