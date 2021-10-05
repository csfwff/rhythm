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
                                    <div class="avatar tooltipped tooltipped-ne"
                                         aria-label="${activity1A0001Label}" style="background-image:url('${staticServePath}/images/activities/1A0001.png')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/activity/1A0001">${activity1A0001Label}</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">
                                            下注后，请在当天 16-24 点在本页面进行兑奖，逾期作废！
                                        </span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar tooltipped tooltipped-ne"
                                         aria-label="${characterLabel}" style="background-image:url('${staticServePath}/images/activities/char.png')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/activity/character">${characterLabel}</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">${activityCharacterTitleLabel}</span>
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class='fn-flex'>
                                    <div class="avatar tooltipped tooltipped-ne"
                                         aria-label="ADarkRoom" style="background-image:url('${staticServePath}/games/adarkroom/img/adr.png')"></div>
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
                                    <div class="avatar tooltipped tooltipped-ne"
                                         aria-label="${chatRoomLabel}" style="background-image:url('${staticServePath}/images/activities/chat.png')"></div>
                                    <div class="fn-flex-1">
                                        <h2 class="ft-13">
                                            <a href="${servePath}/cr">${chatRoomLabel}</a>
                                        </h2>
                                        <span class="ft-fade vditor-reset">${activityChatTitleLabel}</span>
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
