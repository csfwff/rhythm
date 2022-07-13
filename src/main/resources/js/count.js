/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
var Count = {

    generateInterval: 0,
    data: {},
    time: "",
    lunch: 0,

    init: function () {
        let data = JSON.parse(localStorage.getItem("count"));
        if (data === null) {
            // 初始化
            localStorage.setItem("count", "{}");
            data = {};
        }
        data.time = data.time || "1800";
        data.lunch = data.lunch || "1130";
        this.data = data
        this.save()
        if (data.status !== 'disabled') {
            // 初始化样式
            // Count.initStyle();
            // 初始化HTML
            // 初始化时间，930代表早上9点半，1800代表下午6点
            Count.initHtml();
            // 开始倒计时
            Count.start();
        }
    },

    initHtml: function () {
        const data = this.data
        const wrap = document.createElement("div");
        wrap.setAttribute("class", "time_content");
        wrap.setAttribute("id", "timeContent");
        if (data.left !== undefined && data.top !== undefined) {
            if (document.documentElement.clientHeight > data.top && document.documentElement.clientWidth > data.left) {
                wrap.setAttribute("style", "left:" + data.left + "px;top:" + data.top + "px;");
            }
        }
        wrap.innerHTML = "<a class='time_box' id='countRemainBox'>距离下班:<br><span id='countRemain'></span></a>";
        document.body.insertBefore(wrap, document.body.firstChild);
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

    generate: function () {
        // 生成设定时间为Date
        const data = this.data
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

        let t = ("0" + data.time).match(/(\d{2})(\d{2})$/);
        let timeDateString = dateString + " " + t[1] + ":" + t[2] + ":00";
        let setDate = new Date(timeDateString.replace(/-/g, '/'));
        // 计算倒计时
        let nowDate = new Date();
        t = ("0" + data.lunch).match(/(\d{2})(\d{2})$/);
        let lunchString = dateString + " " + t[1] + ":" + t[2] + ":00";
        let lunchDate = new Date(lunchString.replace(/-/g, '/'));
        if (nowDate.toString() === lunchDate.toString()) {
            Util.notice("success", 15000, "中午咯，该订饭啦～");
        }
        let leftTime = setDate.getTime() - nowDate.getTime();
        let leftHour = Math.floor(leftTime / (1000 * 60 * 60) % 24);
        let leftMinute = Math.floor(leftTime / (1000 * 60) % 60);
        let leftSecond = Math.floor(leftTime / 1000 % 60);
        if (leftHour >= 0 && leftMinute >= 0 && leftSecond >= 0) {
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
            if (leftHour === "00" && leftMinute === "02" && leftSecond === "00") {
                Util.notice("danger", 3000, "马上就要下班啦，赶快收拾收拾吧～");
            }
            if (leftHour === "00" && leftMinute === "00" && leftSecond === "00") {
                Util.notice("success", 3000, "下班了！下班了！下班了！！！");
            }
            document.getElementById("countRemain").innerText = leftTime;
        } else {
            document.getElementById("countRemainBox").innerText = "下班\n时间到 🎉";
            clearInterval(Count.generateInterval);
        }
    },

    start: function () {
        Count.generate();
        Count.generateInterval = setInterval(function () {
            Count.generate();
        }, 1000);
    },

    save: function () {
        localStorage.setItem("count", JSON.stringify(this.data));
    },

    settings: function () {
        const data = this.data
        Util.alert(`<div class="form fn__flex-column">
<label>
  <div class="ft__smaller ft__fade" style="float: left">状态（关闭后可点击右上角头像找到下班倒计时设定）</div>
  <div class="fn-hr5 fn__5"></div>
  <select id="countSettingStatus">
  <option value="enabled" selected>开启</option>  <option value="disabled">关闭</option>  </select>
</label>
<label>
  <div class="ft__smaller ft__fade" style="float: left">下班时间</div>
  <div class="fn-hr5 fn__5"></div>
  <input id="countSettingsTime" type="time"/>
</label>
<label>
  <div class="ft__smaller ft__fade" style="float: left">订饭时间</div>
  <div class="fn-hr5 fn__5"></div>
  <input id="lunchSettingsTime" type="time"/>
</label>
<div class="fn-hr5"></div>
<div class="fn__flex" style="margin-top: 15px">
<div class="fn__flex-1 fn__flex-center" style="text-align: left;"></div>
  <button class="btn btn--confirm" onclick='Count.saveSettings()'>保存</button>
</div>
</div>`, "下班倒计时设定");
        setTimeout(function () {
            let t = ("0" + data.time).match(/(\d{2})(\d{2})$/);
            document.getElementById("countSettingsTime").value = t[1] + ":" + t[2];

            t = ("0" + data.lunch).match(/(\d{2})(\d{2})$/);
            document.getElementById("lunchSettingsTime").value = t[1] + ":" + t[2];
            switch (data.status) {
                case 'enabled':
                    document.getElementById("countSettingStatus").value = "enabled";
                    break;
                case 'disabled':
                    document.getElementById("countSettingStatus").value = "disabled";
                    break;
            }
        }, 300);
    },

    saveSettings: function () {
        const data = this.data
        let setTime = document.getElementById("countSettingsTime").value.replace(":", "");
        if (setTime !== "") {
            data.time = setTime;
        }
        let lunchTime = document.getElementById("lunchSettingsTime").value.replace(":", "");
        if (lunchTime !== "") {
            data.lunch = lunchTime;
        }
        // 保存状态
        data.status = document.getElementById("countSettingStatus").value;
        this.data = data
        Count.save();
        Util.closeAlert();
        location.reload();
    }
}

$(document).ready(function () {
    Count.init();
});
