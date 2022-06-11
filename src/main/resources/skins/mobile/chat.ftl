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
<#include "macro-pagination.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="私信 - ${symphonyLabel}">
    </@head>
    <link rel="stylesheet" href="${staticServePath}/css/mobile-base.css?${staticResourceVersion}" />
    <link rel="stylesheet" href="${staticServePath}/css/home.css?${staticResourceVersion}"/>
</head>
<body>
<#include "header.ftl">
<div class="main">
    <div class="wrapper">
        <div class="side">
            <div class="module person-info">
                <div class="module-panel" style="padding: 0">
                    <nav class="home-menu">
                        <a href="http://localhost:8080/notifications/reply">
                            <div class="avatar"
                                 style="display: inline-block; background-image:url('https://pwl.stackoverflow.wiki/2021/09/1552202503861_1562141298909-67b099fd.jpeg')"></div>
                            <div style="display: inline-block; vertical-align: -12px;">
                                adlered<br>
                                <span style="color: #868888">最近一条消息</span>
                            </div>
                        </a>
                    </nav>
                </div>
            </div>
        </div>
        <div class="content">
            <div class="content chat-room" style="margin-left: 15px; padding: 10px">
                <div class="ft__gray ft__center">
                    和
                    <a href="${servePath}/member/adlered">adlered</a>
                    聊天中
                </div>
                <br>
                <div id="messageContent"></div>
                <br>
                <div class="fn-clear">
                    <div class="fn-right">
                        <button class="red" id="rmChatBtn" data-name="88250">全部删除</button>
                        <span class="fn__space5"></span>
                        <button class="green" id="sendChatBtn" data-name="88250">发送</button>
                    </div>
                </div>
                <br>
                <@pagination url="${servePath}/chat"/>
                <br>
                <div class="module" style="min-height: 200px; margin-top: 20px;">
                    <div id="chats">
                        <div class="" style="display: block;">
                            <div class="" style="display: block;">
                                <div id="chatroom1652258109114" class="fn__flex chats__item">
                                    <a href="/member/csfwff" style="height: 38px">
                                        <div class="avatar tooltipped__user" aria-label="csfwff"
                                             style="background-image: url('https://pwl.stackoverflow.wiki/2021/09/head-1f9ac1b7.jpg');"></div>
                                    </a>
                                    <div class="chats__content">
                                        <div class="chats__arrow"></div>
                                        <div id="userName" class="ft__fade ft__smaller"
                                             style="padding-bottom: 3px;border-bottom: 1px solid #eee">
                                            <span class="ft-gray">唐墨夏 (csfwff)</span>
                                        </div>
                                        <div style="margin-top: 4px" class="vditor-reset ft__smaller ">
                                            <div class="hongbao__item fn__flex-inline"
                                                 onclick="ChatRoom.unpackRedPacket('1652258109114')">
                                                <svg class="ft__red hongbao__icon">
                                                    <use xlink:href="#redPacketIcon"></use>
                                                </svg>
                                                <div>
                                                    <div>摸鱼者，事竟成！<br><b>拼手气红包</b></div>
                                                    <div class="ft__smaller ft__fade redPacketDesc">
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="ft__smaller ft__fade fn__right date-bar">
                                            2022-05-11 16:35:09
                                            <span class="fn__space5"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div id="chatroom1654591045140" class="fn__flex chats__item chats__item--me">
                                <a href="/member/adlered" style="height: 38px">
                                    <div class="avatar tooltipped__user" aria-label="adlered"
                                         style="background-image: url('https://pwl.stackoverflow.wiki/2021/09/1552202503861_1562141298909-67b099fd.jpeg');"></div>
                                </a>
                                <div class="chats__content">
                                    <div class="chats__arrow"></div>
                                    <div style="margin-top: 4px" class="vditor-reset ft__smaller ">
                                        <p>1</p>
                                    </div>
                                    <div class="ft__smaller ft__fade fn__right date-bar">
                                        2022-06-07 16:37:25
                                    </div>
                                </div>
                            </div>
                            <div id="chatroom1654591044537" class="fn__flex chats__item chats__item--me">
                                <a href="/member/adlered" style="height: 38px">
                                    <div class="avatar tooltipped__user" aria-label="adlered"
                                         style="background-image: url('https://pwl.stackoverflow.wiki/2021/09/1552202503861_1562141298909-67b099fd.jpeg');"></div>
                                </a>
                                <div class="chats__content">
                                    <div class="chats__arrow"></div>
                                    <div style="margin-top: 4px" class="vditor-reset ft__smaller ">
                                        <p>1</p>
                                    </div>
                                    <div class="ft__smaller ft__fade fn__right date-bar">
                                        2022-06-07 16:37:24
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <br>
                <@pagination url="${servePath}/chat"/>
                <br>
            </div>
        </div>
    </div>
</div>
<#include "footer.ftl">
</body>
</html>
<script src="${staticServePath}/js/chat${miniPostfix}.js?${staticResourceVersion}"></script>
<script src="${staticServePath}/js/channel${miniPostfix}.js?${staticResourceVersion}"></script>
