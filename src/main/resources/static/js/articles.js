var out;
function dateFormat(fmt, date) {
    let ret;
    const opt = {
        "Y+": date.getFullYear().toString(),        // 年
        "m+": (date.getMonth() + 1).toString(),     // 月
        "d+": date.getDate().toString(),            // 日
        "H+": date.getHours().toString(),           // 时
        "M+": date.getMinutes().toString(),         // 分
        "S+": date.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
    };
    for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        };
    };
    return fmt;
}
$(function () {
    let contextPath = $('p.contextpath').text();
    layui.use(['element', 'flow'], function () {
        let element = layui.element;
        let flow = layui.flow;
        let dom = $('#article-item').get(0);//样本DOM
        flow.load({
            elem: '#articles' //指定列表容器
            ,done: function(page, next){ //到达临界点（默认滚动触发），触发下一页
                let lis = [];

                $.post(contextPath + '/user/articles/' +page, function(res){
                    console.log("请求成功");
                    out = res;
                    //假设你的列表返回在data集合中
                    layui.each(res.data, function(index, item){
                        let jDom = $(dom.cloneNode(true));
                        jDom.find('#title').text(item.title);
                        jDom.find('#title').attr("href",contextPath + '/user/article/display/' + item.id);
                        jDom.find('.article-content').html(item.content.length < 250? item.content: item.content + '...');
                        jDom.find('p').addClass("article-content");
                        jDom.find('#authorName').text(item.author.introduction);
                        let dateStr = dateFormat('YYYY-mm-dd HH:MM',new Date(item.releaseDate));
                        jDom.find("#publishTime").text(dateStr);
                        jDom.find("#views").text(item.views);
                        console.log(jDom.html());
                        lis.push(jDom.html());
                    });
                    //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
                    //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
                    next(lis.join(''), page < res.pages);
                }, "JSON");
            }
        });
    });
});