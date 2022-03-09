var Count = {

    data: {},
    time: "",

    init: function () {
        data = JSON.parse(localStorage.getItem("count"));
        if (data === null) {
            // 初始化
            localStorage.setItem("count", "{}");
        }
        // 初始化样式
        Count.initStyle();
        // 初始化HTML
        Count.initHtml();
        // 初始化时间，930代表早上9点半，1800代表下午6点
        Count.time = data.time === undefined ? "1800" : data.time;
        // 开始倒计时
        Count.start();
    },

    initHtml: function () {
        var wrap = document.createElement("div");
        wrap.setAttribute("class", "time_content");
        wrap.setAttribute("id", "timeContent");
        if (data.left !== undefined && data.top !== undefined) {
            if (document.documentElement.clientHeight > data.top && document.documentElement.clientWidth > data.left) {
                wrap.setAttribute("style", "left:" + data.left + "px;top:" + data.top + "px;");
            }
        }
        wrap.innerHTML = "<a class='time_box' >距离下班<br><span id='countRemain'></span></a>";
        var first = document.body.firstChild;
        document.body.insertBefore(wrap, first);
        // 获取拖拽实验对象
        let el = document.getElementById("timeContent");
        // 在该对象上绑定鼠标点击事件
        el.onmousedown = (e) => {
            // 鼠标按下，计算鼠标触点距离元素左侧的距离
            let disX = e.clientX - el.offsetLeft;
            let disY = e.clientY - el.offsetTop;
            let latestT;
            let latestP;
            document.onmousemove = function (e) {
                // 计算需要移动的距离
                let t = e.clientX - disX;
                let P = e.clientY - disY;
                latestT = t;
                latestP = P;
                data.left = t;
                data.top = P;
                Count.save();
                // 移动当前元素
                if (t >= 0 && t <= window.innerWidth - el.offsetWidth) {
                    el.style.left = t + 'px';
                }
                // 移动当前元素
                if (P >= 0 && P <= window.innerHeight - el.offsetHeight) {
                    el.style.top = P + 'px';
                }
            };
            // 鼠标松开时，注销鼠标事件，停止元素拖拽。
            document.onmouseup = function (e) {
                document.onmousemove = null;
                document.onmouseup = null;
                if (latestT === undefined && latestP === undefined) {
                    Count.settings();
                }
            };
        }
    },

    initStyle: function () {
        let style_element = document.createElement("style");
        style_element.innerHTML = ("" +
            ".time_content {" +
            "   position: fixed;" +
            "   top: 60px;" +
            "   right: 24px;" +
            "   width: 80px;" +
            "   z-index: 1999;" +
            "}" +
            ".time_content a.time_box {" +
            "   position: relative;" +
            "   display: -webkit-box;" +
            "   display: -ms-flexbox;" +
            "   display: flex;" +
            "   -webkit-box-orient: vertical;" +
            "   -webkit-box-direction: normal;" +
            "   -ms-flex-direction: column;" +
            "   flex-direction: column;" +
            "   -webkit-box-align: center;" +
            "   -ms-flex-align: center;" +
            "   align-items: center;" +
            "   -webkit-box-pack: center;" +
            "   -ms-flex-pack: center;" +
            "   justify-content: center;" +
            "   border-radius: 50%;" +
            "   background: #000;" +
            "   -webkit-box-shadow: 0 2px 4px 0 rgb(0 0 0 / 5%);" +
            "   box-shadow: 0 2px 4px 0 rgb(0 0 0 / 5%);" +
            "   background-color: #fff;" +
            "   text-align: center;" +
            "   height: 75px;" +
            "   cursor: pointer;" +
            "   margin-top: 8px;" +
            "   text-decoration: none;" +
            "   color: #616161;" +
            "   -moz-user-select: none;" +
            "   -ms-user-select: none;" +
            "   -webkit-user-select: none;" +
            "   user-select: none;" +
            "}");
        document.body.appendChild(style_element);
    },

    settings: function () {
        alert("set")
    },

    start: function () {
        setInterval(function () {
            // 生成设定时间为Date
            let year = new Date().getFullYear();
            let month = new Date().getMonth() + 1;
            let day = new Date().getDate();
            if (month < 10) {
                month = "0" + month;
            }
            if (day < 10) {
                day = "0" + day;
            }
            let dateString = year + "-" + month + "-" + day;
            let hh;
            let mm;
            if (Count.time.length === 3) {
                hh = "0" + Count.time.substr(0, 1);
                mm = Count.time.substr(1, 2);
            } else {
                hh = Count.time.substr(0, 2);
                mm = Count.time.substr(2, 2);
            }
            dateString = dateString + " " + hh + ":" + mm + ":00";
            let setDate = new Date(dateString.replace(/-/g, '/'));
            // 计算倒计时
            let nowDate = new Date();
            let leftTime = setDate.getTime() - nowDate.getTime();
            let leftHour = Math.floor(leftTime / (1000 * 60 * 60) % 24);
            let leftMinute = Math.floor(leftTime / (1000 * 60) % 60);
            let leftSecond = Math.floor(leftTime / 1000 % 60);
            if (leftHour < 10) {
                leftHour = "0" + leftHour;
            }
            if (leftMinute < 10) {
                leftMinute = "0" + leftMinute;
            }
            if (leftSecond < 10) {
                leftSecond = "0" + leftSecond;
            }
            leftTime = leftHour + ":" + leftMinute + ":" + leftSecond;
            document.getElementById("countRemain").innerText = leftTime;
        }, 1000);
    },

    save: function () {
        localStorage.setItem("count", JSON.stringify(data));
    }
}

$(document).ready(function () {
    Count.init();
});
