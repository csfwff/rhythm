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
    <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}"/>
    <link rel="canonical" href="${servePath}/charge/point">
</head>
<body>
<#include "header.ftl">
<div class="main">
    <div class="wrapper">
        <div class="content">
            <div class="module">
                <h2 class="sub-head"><span class="ft-red">❤</span> 捐助摸鱼派</h2>
                <div style="padding: 15px">
                    <p>鱼油你好！摸鱼派是由<a href="https://github.com/Programming-With-Love" target="_blank">用爱发电开源组织</a>衍生的科技社区。我们希望构建一个属于科技爱好者们、以<b>摸鱼</b>为社区精神的综合性社区。</p>
                    <p>摸鱼派的运营资金由 <em>摸鱼派管理组</em> 自掏腰包，如果你喜欢摸鱼派的氛围，欢迎通过捐助支持我们继续运营下去！你捐助的所有资金，我们都会在当前页面公示，收到的资金会被<b>完全用于摸鱼派的社区运营</b> :)</p><br>
                    <p style="color: rgba(205,80,0,0.91)"><b>请一定要在扫码时填写备注</b> <em>你的社区用户名+写给我们的话</em>，不要当不留名的雷锋哦！作为答谢，我们会向你的摸鱼派账号赠送对应的积分 😄 积分赠送标准为80积分/1元，<b>请不要为了充积分而捐助哦！</b></p><br>
                    <div style="text-align: center">
                        <b style="color: #3caf36">
                            <svg viewBox="0 0 1024 1024" style="width: 20px; height: 20px"><path d="M380.8 636.8c-3.2 3.2-9.6 3.2-16 3.2-12.8 0-22.4-6.4-28.8-19.2l-3.2-6.4-91.2-209.6c0-3.2-3.2-6.4-3.2-6.4 0-9.6 6.4-16 16-16 3.2 0 6.4 0 9.6 3.2l107.2 78.4c12.8 9.6 28.8 9.6 44.8 6.4l512-233.6C836.8 128 684.8 54.4 513.6 54.4 235.2 54.4 8 248 8 488c0 129.6 70.4 249.6 174.4 329.6 12.8 9.6 16 25.6 12.8 38.4L171.2 944c0 3.2-3.2 9.6-3.2 12.8 0 9.6 6.4 16 16 16 3.2 0 6.4 0 9.6-3.2l110.4-67.2c9.6-6.4 16-9.6 25.6-9.6 6.4 0 9.6 0 16 3.2 54.4 16 110.4 25.6 164.8 25.6 278.4 0 505.6-196.8 505.6-433.6 0-72-22.4-142.4-57.6-203.2L377.6 633.6l3.2 3.2z" fill="#54d11c" p-id="2422"></path></svg>
                            请使用微信扫码</b><br>
                        <img src="${servePath}/images/donate_wechat.png" style="height:256px"/>
                    </div>
                </div>
                <h2 class="sub-head"><span class="ft-red">🤗</span> 捐助称号回馈</h2>
                <div style="padding: 15px">
                    <p>感谢你的捐助，作为回馈，当你<b>累计</b>捐助至某个金额时，我们会赠送一个称号作为回馈：</p>
                    <div style="padding-bottom: 5px"></div>
                    <div class="TGIF__item" style="display: flex; justify-content: center">
                        <div>
                            <img src="https://fishpi.cn/gen?scale=0.79&txt=%E6%91%B8%E9%B1%BC%E6%B4%BE%E7%B2%89%E4%B8%9D&url=https://pwl.stackoverflow.wiki/2021/12/ht1-d8149de4.jpg&backcolor=ffffff&fontcolor=ff3030" />
                            &nbsp;&nbsp;
                            <b style="line-height: 25px">16 ¥</b>
                            <br>
                            <img src="https://fishpi.cn/gen?scale=0.79&txt=%E6%91%B8%E9%B1%BC%E6%B4%BE%E5%BF%A0%E7%B2%89&url=https://pwl.stackoverflow.wiki/2021/12/ht2-bea67b29.jpg&backcolor=87cefa&fontcolor=efffff" />
                            &nbsp;&nbsp;
                            <b style="line-height: 25px">256 ¥</b>
                            <br>
                            <img src="https://fishpi.cn/gen?scale=0.79&txt=%E6%91%B8%E9%B1%BC%E6%B4%BE%E9%93%81%E7%B2%89&url=https://pwl.stackoverflow.wiki/2021/12/ht3-b97ea102.jpg&backcolor=ee3a8c&fontcolor=ffffff" />
                            &nbsp;&nbsp;
                            <b style="line-height: 25px">1024 ¥</b>
                        </div>
                    </div>
                </div>
                <h2 class="sub-head">😘 感谢你们</h2>
                <div style="padding: 10px 0px 10px 15px">
                    <h6>上次更新日期：${recent}</h6>
                </div>
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
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2022-01-21<br>
                                <span class="ft-gray">15:11:39</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                没有填写捐助信息 :)
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">16 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/imlinhanchao" class="tooltipped__user">imlinhanchao</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2022-01-12<br>
                                <span class="ft-gray">14:24:40</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                域名鱼皮纪念！
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/SImov" class="tooltipped__user">SImov</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2022-01-11<br>
                                <span class="ft-gray">11:06:06</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                加油呀
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/daxing" class="tooltipped__user">daxing</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2022-01-11<br>
                                <span class="ft-gray">10:07:16</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                等阿达年底女装
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">23.33 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/Yui" class="tooltipped__user">Yui</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2022-01-07<br>
                                <span class="ft-gray">16:38:44</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                新人报道！
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">16 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/Alex05" class="tooltipped__user">Alex05</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2022-01-06<br>
                                <span class="ft-gray">20:54:33</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                没有填写捐助信息 :)
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">10 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/qwer123" class="tooltipped__user">qwer123</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2022-01-04<br>
                                <span class="ft-gray">15:38:38</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                摸鱼派牛逼
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/min" class="tooltipped__user">min</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-12-25<br>
                                <span class="ft-gray">22:16:41</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                什么时候女装？
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">22.22 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/huny" class="tooltipped__user">huny</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-12-23<br>
                                <span class="ft-gray">11:05:38</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                没有填写捐助信息 :)
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">50 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/MBL186" class="tooltipped__user">MBL186</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-12-23<br>
                                <span class="ft-gray">11:01:27</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                加油
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/bongbongdan" class="tooltipped__user">bongbongdan</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-12-22<br>
                                <span class="ft-gray">17:17:45</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                爱你
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/BiuXiaoXiong" class="tooltipped__user">BiuXiaoXiong</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-12-22<br>
                                <span class="ft-gray">11:14:03</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                女装
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/Fairyfox" class="tooltipped__user">Fairyfox</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-12-15<br>
                                <span class="ft-gray">15:09:52</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                破60人纪念！
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/SImov" class="tooltipped__user">SImov</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-11-26<br>
                                <span class="ft-gray">17:05:00</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                有点小清新，支持
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/iwpz" class="tooltipped__user">iwpz</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-10-16<br>
                                <span class="ft-gray">17:53:35</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                没有填写捐助信息 :)
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">10 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/LLaamar" class="tooltipped__user">LLaamar</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-10-13<br>
                                <span class="ft-gray">10:09:01</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                没有填写捐助信息 :)
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">30 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/Suvern" class="tooltipped__user">Suvern</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-10-13<br>
                                <span class="ft-gray">10:03:49</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                加油
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/xuwujing" class="tooltipped__user">xuwujing</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-10-13<br>
                                <span class="ft-gray">09:58:06</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                女装 😎
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">50 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/danbai" class="tooltipped__user">danbai</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-10-13<br>
                                <span class="ft-gray">09:55:48</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                摸鱼社区加油
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">18.88 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/SignV" class="tooltipped__user">SignV</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-09-27<br>
                                <span class="ft-gray">12:14:37</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                社区很棒！大佬加油
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">20 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/FreerKnight" class="tooltipped__user">FreerKnight</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-09-23<br>
                                <span class="ft-gray">12:42:16</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                没有填写捐助信息 :)
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">10 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/camden" class="tooltipped__user">camden</a>
                            </div>
                        </li>
                        <li class="fn__flex">
                            <div class="ft-nowrap">
                                2021-09-23<br>
                                <span class="ft-gray">12:37:05</span>
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <div class="ft__gray fn__flex-1">
                                没有填写捐助信息 :)
                            </div>
                            <span class="fn__space5"></span>
                            <span class="fn__space5"></span>
                            <b class="ft__original7" style="width: 90px">7.71 RMB</b>
                            <div class="ft__gray" style="width: 70px;text-align: right">
                                <a href="https://fishpi.cn/member/camden" class="tooltipped__user">camden</a>
                            </div>
                        </li>
                    </ul>
                </div>
                <div class="top-ranking">
                    <#include "common/ranking.ftl">
                </div>
                <br/>
            </div>
        </div>
        <div class="side">
            <#include "side.ftl">
        </div>
    </div>
</div>
<#include "footer.ftl">
</body>
</html>
