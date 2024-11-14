<#--

    Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
    Modified version from Symphony, Thanks Symphony :)
    Copyright (C) 2012-present, b3log.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

-->
<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="${chargePointLabel} - ${symphonyLabel}">
    </@head>
</head>
<body>
<#include "header.ftl">
<div class="main">
    <div class="wrapper">
        <div class="content">
            <h2 class="sub-head">❤️ 捐助摸鱼派</h2>
            <div style="padding: 15px">
                <p>鱼油你好！摸鱼派是由<a href="https://github.com/Programming-With-Love" target="_blank">用爱发电开源组织</a>衍生的科技社区。我们希望构建一个属于科技爱好者们、以<b>摸鱼</b>为社区精神的综合性社区。</p>
                <p>摸鱼派的运营资金由 <em>摸鱼派管理组</em> 自掏腰包，如果你喜欢摸鱼派的氛围，欢迎通过捐助支持我们继续运营下去！你捐助的所有资金，我们都会在当前页面公示，收到的资金会被<b>完全用于摸鱼派的社区运营</b> :)</p><br>
                <p style="color: rgba(205,80,0,0.91)">感谢你的捐助！作为答谢，我们会向你的摸鱼派账号赠送对应的积分 😄 积分赠送标准为80积分/1元，<b>请不要为了充积分而捐助哦！</b></p><br>
                <div style="text-align: center">
                    <input id="doMoney" style="display: inline; width: 20%" type="text" placeholder="捐助金额">
                    <input id="doNote" style="display: inline; width: 70%" type="text" placeholder="捐助附言，最多32字">
                </div>
                <div style="text-align: right; margin-top: 15px">
                    <button onclick="doWeChat()"><svg style="vertical-align: -2px;color: #44B549"><use xlink:href="#wechat"></use></svg> 使用微信捐助</button>
                    <!--<button onclick="doAlipay()"><svg style="vertical-align: -2px;"><use xlink:href="#alipay"></use></svg> 使用支付宝捐助</button>-->
                </div>
            </div>
            <script>
                function doWeChat() {
                    let doMoney = $("#doMoney").val();
                    let doNote = $("#doNote").val();
                    if (doMoney === "" || doNote === "") {
                        Util.alert("请填写捐助金额和捐助附言 :)");
                    } else if (isNaN(doMoney)) {
                        Util.alert("金额不合法！");
                    } else {
                        $.ajax({
                            url: "${servePath}/pay/wechat?total_amount=" + doMoney + "&note=" + doNote,
                            type: "GET",
                            async: false,
                            success: function (data) {
                                let url = data.QRcode_url;
                                Util.alert("" +
                                    "<div><img src='" + url + "' height='200' width='200'></div>" +
                                    "<div style='padding-top: 10px'><svg style='vertical-align: -2px;color: #44B549'><use xlink:href='#wechat'></use></svg> 请使用微信扫码支付</div>" +
                                    "<div style='padding-top: 30px'><button class='btn green' onclick='javascript:location.reload()'>我已完成支付</button></div>")
                            }
                        });
                    }
                }

                function doAlipay() {
                    let doMoney = $("#doMoney").val();
                    let doNote = $("#doNote").val();
                    if (doMoney === "" || doNote === "") {
                        Util.alert("请填写捐助金额和捐助附言 :)");
                    } else if (isNaN(doMoney)) {
                        Util.alert("金额不合法！");
                    } else {
                        location.href = "${servePath}/pay/alipay?total_amount=" + doMoney + "&note=" + doNote + "&subject_type=001";
                    }
                }
            </script>
            <#if isSponsor>
                <h2 class="sub-head"><span class="ft-red">✨</span> 您的捐助信息</h2>
                <div style="padding: 15px 50px">
                    <div class="TGIF__item" style="display: flex; justify-content: center">
                        <div style="text-align: center">
                            亲爱的鱼油，感谢你对摸鱼派的支持与喜爱 ❤️
                            <br><br>
                            已累计捐助：<b>${donateTimes} 笔</b><br>
                            总捐助金额：<b>${donateCount} ¥</b><br>
                            为社区运营续航：<b>${donateMakeDays} 天</b>
                            <br><br>
                            <#list donateList as donate>
                                <p style="margin-bottom: 5px" class="tooltipped tooltipped-e" aria-label="${donate.message}" ><span class="count">🧧 ${donate.date} ${donate.time} ${donate.amount} ¥</span></p>
                            </#list>
                        </div>
                    </div>
                </div>
            </#if>
            <h2 class="sub-head"><span class="ft-red">🤗</span> 捐助称号回馈</h2>
            <div style="padding: 15px">
                <p>感谢你的捐助，作为回馈，当你<b>累计</b>捐助至某个金额时，我们会赠送一个称号作为回馈：</p>
                <div style="padding-bottom: 15px"></div>
                <div class="TGIF__item" style="display: flex; justify-content: center">
                    <div>
                        <img src="https://fishpi.cn/gen?ver=0.1&scale=0.79&txt=%E6%91%B8%E9%B1%BC%E6%B4%BE%E7%B2%89%E4%B8%9D&url=https://file.fishpi.cn/2021/12/ht1-d8149de4.jpg&backcolor=ffffff&fontcolor=ff3030" />
                        &nbsp;&nbsp;
                        <b style="line-height: 25px">16 ¥</b>
                        <br>
                        <img src="https://fishpi.cn/gen?ver=0.1&scale=0.79&txt=%E6%91%B8%E9%B1%BC%E6%B4%BE%E5%BF%A0%E7%B2%89&url=https://file.fishpi.cn/2021/12/ht2-bea67b29.jpg&backcolor=87cefa&fontcolor=efffff" />
                        &nbsp;&nbsp;
                        <b style="line-height: 25px">256 ¥</b>
                        <br>
                        <img src="https://fishpi.cn/gen?ver=0.1&scale=0.79&txt=%E6%91%B8%E9%B1%BC%E6%B4%BE%E9%93%81%E7%B2%89&url=https://file.fishpi.cn/2021/12/ht3-b97ea102.jpg&backcolor=ee3a8c&fontcolor=ffffff" />
                        &nbsp;&nbsp;
                        <b style="line-height: 25px">1024 ¥</b>
                    </div>
                </div>
            </div>
            <h2 class="sub-head">🙏 不胜感激</h2>
            <style>
                .fn__space5 {
                    width: 5px;
                    display: inline-block;
                }
                .ft__gray {
                    color: var(--text-gray-color);
                }
                .fn__flex-1 {
                    flex: 1;
                    min-width: 1px;
                }
                .ft__original7 {
                    color: #569e3d;
                }
                .list>ul>li {
                    padding: 15px;
                }
            </style>
            <div class="list">
                <ul>
                    <#list sponsors as sponsor>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                ${sponsor.date}<br>
                                <span class="ft-gray">${sponsor.time}</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                ${sponsor.message}
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">${sponsor.amount} RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="${servePath}/member/${sponsor.userName}" class="tooltipped__user">${sponsor.userName}</a>
                            </div>
                        </li>
                    </#list>
                </ul>
           </div>
        </div>
        <div class="fn-hr10"></div>
        <div class="side">
            <#include "side.ftl">
        </div>
    </div>
</div>
<#include "footer.ftl">
</body>
</html>
