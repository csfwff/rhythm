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
<#include "common/sub-nav.ftl">
<#include "common/index-nav.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="${chatRoomLabel} - ${symphonyLabel}">
        <meta name="description" content="${chatRoomLabel}"/>
    </@head>
    <link rel="stylesheet" href="${staticServePath}/css/viewer.min.css"/>
    <link rel="stylesheet" href="https://file.fishpi.cn/cxColor/css/jquery.cxcolor.css">
</head>
<body>
<div class="mobile-head">
    <#include "header.ftl">
    <@indexNav ""/>

</div>
<div style="height: 74px;width: 1px;"></div>
<div class="main">
    <#--            <@subNav 'community' ''/>-->
    <div class="wrapper">
        <div class="content chat-room">
            <div class="discuss_title">
                <a style="text-decoration: none; display: inline-block; cursor: default">
                    <span style="color: #616161">当前话题：</span><span class="ft-green"># <span
                                id="discuss-title">加载中...</span> #</span>
                </a>
                <div style="padding-left: 5px;display: inline-block;vertical-align: -2px;">
                    <a onclick="ChatRoom.setDiscuss()" class="ft-a-title tooltipped tooltipped-se" aria-label="编辑话题"
                       style="text-decoration: none;">
                        <svg>
                            <use xlink:href="#edit-discuss"></use>
                        </svg>
                    </a>
                    <a onclick="ChatRoom.useDiscuss()" class="ft-a-title tooltipped tooltipped-se" aria-label="引用话题"
                       style="text-decoration: none;">
                        <svg>
                            <use xlink:href="#pound"></use>
                        </svg>
                    </a>
                </div>
            </div>
            <div class="reply">
                <#if isLoggedIn>
                    <div id="chatContent"></div>
                    <div id="liveliness"></div>
                    <#if nightDisableMode == true>
                        <div class="discuss_title" style="border-radius: 10px">
                            <a style="text-decoration: none; display: inline-block; cursor: default; font-weight: normal; background-color: #f6f6f670;">
                                <span style="color: #616161">现在是聊天室宵禁时间 (19:30-08:00)，您发送的消息将不会产生活跃度，请早点下班休息 :)</span>
                            </a>
                        </div>
                    </#if>
                    <div class="fn-clear" style="margin-bottom: 5px; margin-top: 10px;">
                        <svg id="redPacketBtn" style="width: 30px; height: 30px; cursor:pointer;">
                            <use xlink:href="#redPacketIcon"></use>
                        </svg>
                        <svg id="emojiBtn" style="width: 30px; height: 30px; cursor:pointer;">
                            <use xlink:href="#emojiIcon"></use>
                        </svg>
                        <svg id="paintBtn" style="width: 30px; height: 30px; cursor:pointer;">
                            <use xlink:href="#icon-paint"></use>
                        </svg>
                        <div class="hide-list" id="emojiList">
                            <div class="hide-list-emojis" id="emojis" style="max-height: 200px">
                            </div>
                            <div class="hide-list-emojis__tail">
                                        <span>
                                        <a onclick="ChatRoom.fromURL()">从URL导入表情包</a>
                                        </span>
                                <span class="hide-list-emojis__tip"></span>
                                <span>
                                            <a onclick="$('#uploadEmoji input').click()">上传表情包</a>
                                        </span>
                                <form style="display: none" id="uploadEmoji" method="POST"
                                      enctype="multipart/form-data">
                                    <input type="file" name="file">
                                </form>
                            </div>
                        </div>
                        <div class="fn-right">
                            <#if level3Permitted == true>
                                <button id="groupRevoke" onclick="ChatRoom.startGroupRevoke()" class="button">
                                    <svg style="vertical-align: -2px;">
                                        <use xlink:href="#administration"></use>
                                    </svg>
                                    批量撤回
                                </button>
                            </#if>
                            <button class="red"
                                    onclick="$('#chats').empty();page=0;ChatRoom.more();">${cleanScreenLabel}</button>
                            <button class="green" onclick="ChatRoom.send()">${postLabel}</button>
                        </div>
                    </div>
                    <div id="paintContent" style="display: none">
                        <div style="margin: 20px 0 0 0;">
                            <input id="selectColor" name="mycolor" type="text" class="input_cxcolor" readonly="" style="background-color: rgb(0, 0, 0);">
                            <input id="selectWidth" type="number" inputmode="decimal" pattern="[0-9]*" min="1" value="3" style="width: 50px">
                        </div>
                        <canvas id="paintCanvas" width="306" height="300"></canvas>
                        <div class="fn-right">
                            <button onclick="ChatRoom.revokeChatacter('paintCanvas')">撤销</button>
                            <button class="red" onclick="ChatRoom.clearCharacter('paintCanvas')">${clearLabel}</button>
                            <button class="green"
                                    onclick="ChatRoom.submitCharacter('paintCanvas')">${submitLabel}</button>
                        </div>
                    </div>
                    <div class="fn-clear comment-submit">
                        <div class="fn-left online-cnt">${onlineVisitorCountLabel} <span id="onlineCnt"></span></div>
                        <div class="tip fn-left" id="chatContentTip"></div>
                        <a onclick="ChatRoom.toggleOnlineAvatar()" style="cursor:pointer;">
                            <svg style="vertical-align: -10px;" id="toggleAvatarBtn">
                                <use xlink:href="#showMore"></use>
                            </svg>
                        </a>
                    </div>
                    <div id="chatRoomOnlineCnt" class="chats__users" style="display: none">
                    </div>
                <#else>
                    <div class="comment-login">
                        <a rel="nofollow"
                           href="javascript:window.scrollTo(0,0);Util.goLogin();">${loginDiscussLabel}</a>
                    </div>
                </#if>
            </div>
            <div class="list" style="height: 100%">
                <div id="chats">
                </div>
                <#if !isLoggedIn>
                    <div style="color:rgba(0,0,0,0.54);">登录后查看更多</div></#if>
            </div>
        </div>
    </div>
</div>
<div id="goToTop" style="position:fixed;bottom:20px;right:10%;display:none;"><a href="#">
        <svg style="width:30px;height:30px;color:#626262;">
            <use xlink:href="#toTopIcon"></use>
        </svg>
    </a></div>
<#include "footer.ftl">
<script>
    Label.uploadLabel = "${uploadLabel}";
</script>
<script src="https://file.fishpi.cn/cxColor/js/jquery.cxcolor.min.js"></script>
<script src="${staticServePath}/js/lib/jquery/file-upload-9.10.1/jquery.fileupload.min.js"></script>
<script src="${staticServePath}/js/channel${miniPostfix}.js?${staticResourceVersion}"></script>
<script src="${staticServePath}/js/chat-room${miniPostfix}.js?${staticResourceVersion}"></script>
<script src="${staticServePath}/js/lib/viewer.min.js"></script>
<script>
    Label.addBoldLabel = '${addBoldLabel}';
    Label.addItalicLabel = '${addItalicLabel}';
    Label.insertQuoteLabel = '${insertQuoteLabel}';
    Label.addBulletedLabel = '${addBulletedLabel}';
    Label.addNumberedListLabel = '${addNumberedListLabel}';
    Label.addLinkLabel = '${addLinkLabel}';
    Label.undoLabel = '${undoLabel}';
    Label.redoLabel = '${redoLabel}';
    Label.previewLabel = '${previewLabel}';
    Label.helpLabel = '${helpLabel}';
    Label.fullscreenLabel = '${fullscreenLabel}';
    Label.uploadFileLabel = '${uploadFileLabel}';
    Label.insertEmojiLabel = '${insertEmojiLabel}';
    Label.currentUser = '<#if currentUser??>${currentUser.userName}</#if>';
    Label.currentUserId = '<#if currentUser??>${currentUser.oId}</#if>';
    Label.level3Permitted = ${level3Permitted?string("true", "false")};
    Label.chatRoomPictureStatus = "<#if 0 == chatRoomPictureStatus> blur</#if>";
    Label.latestMessage = "";
    Label.plusN = 0;
    Label.hasMore = true;
    ChatRoom.init();
    // Init [ChatRoom] channel
    ChatRoomChannel.init("${wsScheme}://${serverHost}:${serverPort}${contextPath}/chat-room-channel");
    var page = 0;
    var pointsArray = [];
    var linesArray = [];
    if ('${contextMode}' === 'no') {
        ChatRoom.more();
    } else {
        page = 1;
        let contextOId = '${contextOId}';
        $.ajax({
            url: Label.servePath + '/chat-room/getMessage?size=25&mode=0&oId=' + contextOId,
            type: 'GET',
            cache: false,
            async: false,
            success: function (result) {
                if (result.data.length !== 0) {
                    for (let i in result.data) {
                        let data = result.data[i];
                        if ($("#chatroom" + data.oId).length === 0) {
                            ChatRoom.renderMsg(data, 'more');
                        }
                        ChatRoom.resetMoreBtnListen();
                    }
                    Util.listenUserCard();
                    ChatRoom.imageViewer();
                    let html = "<div class='redPacketNotice' style='color: rgb(50 50 50);margin-bottom: 8px;text-align: center;display: none;'>您当前处于指定消息预览模式，将显示指定消息的前后25条消息，如需查看最新消息请 <a onclick='location.href = \"/cr\"' style='cursor:pointer;'>点击这里</a></div>";
                    $('#chats').prepend(html);
                    $(".redPacketNotice").slideDown(500);
                    location.hash = '#chatroom' + contextOId;
                } else {
                    alert("没有更多聊天消息了！");
                    Label.hasMore = false;
                }
            }
        });
    }
    Label.onlineAvatarData = "";
</script>
<script type="text/javascript">
    function getActivityStatus() {
        $.ajax({
            url: Label.servePath + "/user/liveness",
            method: "get",
            cache: false,
            async: false,
            success: function (result) {
                let liveness = result.liveness;
                $('#liveliness').css("width", liveness + '%');
            }
        });
    }
    $(document).ready(function () {
        $(window).scroll(function () {
            var scrollTop = $(this).scrollTop();
            var scrollHeight = $(document).height();
            var windowHeight = $(this).height();
            if (scrollTop + windowHeight + 500 >= scrollHeight) {
                ChatRoom.more();
            }
            if ($(this).scrollTop() > 1) {
                $("#goToTop").fadeIn();
            } else {
                $("#goToTop").fadeOut();
            }
        });
        $("#goToTop a").click(function () {
            $("html,body").animate({scrollTop: 0}, 800);
            return false;
        });

        setInterval(function () {
            getActivityStatus();
        }, 30000);
        getActivityStatus();
    });
</script>
</body>
</html>
