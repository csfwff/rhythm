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
<#include "../macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${activityLabel} - ${symphonyLabel}">
        </@head>
    </head>
    <body>
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper">
                <div class="fn-hr10"></div>
                <div class="module">
                    <div class="module-header">
                        <h2>${activityLabel}</h2>
                    </div>
                    <div class="module-panel">
                        <ul class="module-list">
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar"
                                         style="background-image:url('https://pwl.stackoverflow.wiki/evolve/evolved.ico')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/games/evolve/">《进化-Evolve》</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                                在本游戏中，您首先由原生质选择进化为某种智慧生物，然后带领他们走向繁荣昌盛，最后迈向星空，征服宇宙。<br>
                                                支持第三方存档云同步（需手动在游戏设定中开启），参与 <a href="${servePath}/top/evolve">进化排行榜</a>
                                        </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar"
                                         style="background-image:url('https://pwl.stackoverflow.wiki/adarkroom/img/adr.png')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/games/adarkroom/?lang=zh_cn&ignorebrowser=true">《A Dark Room》</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                                极简主义的游戏杰作，有上世纪 ascii 游戏的遗风。<br>
                                                支持存档云同步，参与 <a href="${servePath}/top/adr">${symphonyLabel} ADR 游戏总分排行榜</a><br>
                                                <span style="color: #8b0000">注意：使用手机游玩 ADR 可能无法控制飞行器，需要插键盘或使用电脑进行通关</span>
                                        </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar"
                                         style="background-image:url('${staticServePath}/images/mofish.png')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="https://p.hancel.org/" target="_blank">摸鱼大闯关</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                                一个非常烧脑的 CTF 类闯关游（酷）戏（刑）。这个游戏的目标就只有一个：进入下一关。在大部分关卡中，你需要利用网页上的提示来找到密码并进入下一关。<br>
                                                参与 <a href="${servePath}/top/mofish">摸鱼大闯关排行榜</a>
                                            </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar"
                                         style="background-image:url('${staticServePath}/games/handle/favicon.svg')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/games/handle/" target="_blank">汉兜</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                                汉兜是一款猜四字词语的有趣游戏，你有十次的机会猜一个四字词语。<br>
                                                <b>每天首次游戏获积分奖励</b>，快来猜猜吧！
                                            </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar"
                                         style="background-image:url('${staticServePath}/images/restart.png')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/games/lifeRestart/view/">人生重开模拟器</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                                如果你不满意现在的人生，那就来重开吧～<br>
                                                支持存档云同步，参与 <a href="${servePath}/top/lifeRestart">人生重开模拟器成就排行榜</a>
                                            </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar" style=" font-size: 2rem; margin-bottom: 0.5rem;display: flex;
                                            justify-content: center;align-items: center;">😘</div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/games/emojiPair">Emoji 真假小黄脸</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                                调皮的小黄脸玩起了真假美猴王的游戏，在众黄脸中找出他们吧！<br>
                                                <b>每天首次游戏获积分奖励</b>，封顶30积分
                                            </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar"
                                         style="background-image:url('${staticServePath}/images/activities/cat.jpg')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/activity/catch-the-cat">围住小猫</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                                你能围住这只调皮的小猫么？
                                            </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar"
                                         style="background-image:url('${staticServePath}/images/activities/2048.png')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/activity/2048">2048</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                              加强版2048，来吧~
                                            </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar"
                                         style="background-image:url('${staticServePath}/images/activities/char.png')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/activity/character">${characterLabel}</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">${activityCharacterTitleLabel}</span>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="side">
                    <#include "../side.ftl">
                </div>
            </div>
        </div>
        <#include "../footer.ftl">
    </body>
</html>
