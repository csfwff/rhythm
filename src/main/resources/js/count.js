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
const Count = {

    generateInterval: 0,
    data: {},
    init: function () {
        const data = JSON.parse(localStorage.getItem("count")) || {};
        // 初始化时间，930代表早上9点半，1800代表下午6点
        data.time = data.time || "1800";
        data.lunch = data.lunch || "1130";
        this.data = data
        this.save()
        if (data.status !== 'disabled') {
            // 初始化HTML
            this.initHtml();
            // 开始倒计时
            this.start();
        }
    },

    initHtml: function () {
        const wrap = document.createElement("div"), data = this.data;
        wrap.id = "timeContent";
        if (data.left && data.top) {
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
                Count.data = data
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
        const year = new Date().getFullYear();
        const month = `0${new Date().getMonth() + 1}`.slice(-2);
        const day = `0${new Date().getDate()}`.slice(-2);
        const dateString = `${year}-${month}-${day}`;
        const time = Count.data.time.match(/\d{2}/g);
        const setDate = new Date(`${dateString} ${time[0]}:${time[1]}:00`);
        // 计算倒计时
        const nowDate = new Date();
        const lunch = Count.data.lunch.match(/\d{2}/g);
        const lunchDate = new Date(`${dateString} ${lunch[0]}:${lunch[1]}:00`);
        let eatTime = lunchDate.getTime() - nowDate.getTime();
        let eatHour = Math.floor(eatTime / (1000 * 60 * 60) % 24);
        let eatMinute = Math.floor(eatTime / (1000 * 60) % 60);
        let eatSecond = Math.floor(eatTime / 1000 % 60);
        if (eatHour >= 0 && eatMinute >= 0 && eatSecond >= 0) {
            eatHour = `0${eatHour}`.slice(-2)
            eatMinute = `0${eatMinute}`.slice(-2)
            eatSecond = `0${eatSecond}`.slice(-2)
            eatTime = eatHour + ":" + eatMinute + ":" + eatSecond;
            if (eatHour === "00" && eatMinute === "00" && eatSecond === "00") {
                Util.notice("success", 30000, "中午咯，该订饭啦～");
            }
            document.getElementById("countRemainBox").innerHTML = "订饭🍲<br><span id='countRemain'>" + eatTime + "</span>";
        }
        let leftTime = setDate.getTime() - nowDate.getTime();
        let leftHour = Math.floor(leftTime / (1000 * 60 * 60) % 24);
        let leftMinute = Math.floor(leftTime / (1000 * 60) % 60);
        let leftSecond = Math.floor(leftTime / 1000 % 60);
        if (leftHour >= 0 && leftMinute >= 0 && leftSecond >= 0 && eatHour < 0 && eatMinute < 0 && eatSecond < 0) {
            leftHour = `0${leftHour}`.slice(-2)
            leftMinute = `0${leftMinute}`.slice(-2)
            leftSecond = `0${leftSecond}`.slice(-2)
            leftTime = leftHour + ":" + leftMinute + ":" + leftSecond;
            if (leftHour === "00" && leftMinute === "02" && leftSecond === "00") {
                Util.notice("danger", 30000, "马上就要下班啦，赶快收拾收拾吧～");
            }
            if (leftHour === "00" && leftMinute === "00" && leftSecond === "00") {
                Util.notice("success", 30000, "下班了！下班了！下班了！！！");
            }
            document.getElementById("countRemainBox").innerHTML = "下班🏠<br><span id='countRemain'>" + leftTime + "</span>";
        } else {
            if (eatHour < 0 && eatMinute < 0 && eatSecond < 0) {
                document.getElementById("countRemainBox").innerText = "下班\n时间到 🎉";
                clearInterval(Count.generateInterval);
            }
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
        Util.alert(`<div class="form fn__flex-column" style="width: 100%;border:none;box-shadow:none;background:none;">
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
            const time = Count.data.time.match(/\d{2}/g);
            document.getElementById("countSettingsTime").value = `${time[0]}:${time[1]}`;

            const lunch = Count.data.lunch.match(/\d{2}/g);
            document.getElementById("lunchSettingsTime").value = `${lunch[0]}:${lunch[1]}`;

            document.getElementById("countSettingStatus").value = Count.data.status === "disabled" ? "disabled" : "enabled";
        }, 500);
    },

    saveSettings: function () {
        // 保存时间
        Count.data.time = document.getElementById("countSettingsTime").value.replace(":", "");
        Count.data.lunch = document.getElementById("lunchSettingsTime").value.replace(":", "");
        // 保存状态
        Count.data.status = document.getElementById("countSettingStatus").value;
        Count.save();
        Util.closeAlert();
        location.reload();
    }
};

$(document).ready(function () {
    Count.init();
});
