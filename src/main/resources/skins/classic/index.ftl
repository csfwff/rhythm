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
<#include "common/title-icon.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="${symphonyLabel}">
        <meta name="description" content="${symDescriptionLabel}"/>
    </@head>
    <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}"/>
    <link rel="canonical" href="${servePath}">
</head>
<body class="index">
${HeaderBannerLabel}
<#include "header.ftl">
<div class="main">
    <div class="wrapper" style="padding-bottom: 20px">
        <div class="index-recent fn-flex-1">
            <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">最新</div>
                <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/recent">更多</a></div>
                <div style="clear:both;"></div>
            </div>
            <div class="module-panel">
                <ul class="module-list">
                    <style>
                        .cb-stick {
                            position: absolute;
                            top: 0;
                            left: 0;
                            border-width: 10px 10px 13px 10px;
                            border-color: #999 transparent transparent #999;
                            border-style: solid;
                            margin-left: 5px;
                            z-index: 100;
                        }

                        .icon-pin {
                            position: absolute;
                            top: -9px;
                            left: -11px;
                            color: #FFF;
                        }

                        .icon-pin-rank {
                            position: absolute;
                            top: -11px;
                            left: -9px;
                            color: #FFF;
                        }

                        .rank {
                            padding: 7px 15px 8px 15px !important;
                        }
                    </style>
                    <#list recentArticles as article>
                        <li class="fn-flex">
                            <#if article.articleStick != 0>
                                <span class="cb-stick" aria-label="管理置顶"><svg class="icon-pin"><use
                                                xlink:href="#pin"></use></svg></span>
                            </#if>
                            <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL210}')"></span>
                            </a>
                            <a rel="nofollow" class="title fn-ellipsis fn-flex-1"
                               href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                            <a class="fn-right count ft-gray ft-smaller"
                               href="${servePath}${article.articlePermalink}"><#if article.articleViewCount < 1000>
                                    ${article.articleViewCount}<#else>${article.articleViewCntDisplayFormat}</#if></a>
                        </li>
                    </#list>
                </ul>
            </div>

        </div>
        <div class="index-recent fn-flex-1">
            <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">随便看看</div>
                <div style="float:right;font-size:13px;margin:5px 0 0 0;">
                    <a onclick="randomArticles()" style="cursor: pointer; color: #c8383a; text-decoration: none;">
                        <svg id="randomArticlesRefreshSvg">
                            <use xlink:href="#refreshQ"></use>
                        </svg>
                        换点别的
                    </a>
                </div>
                <div style="clear:both;"></div>
            </div>
            <div class="module-panel">
                <ul class="module-list" id="randomArticles">
                    <#if indexRandomArticles??>
                        <#list indexRandomArticles as article>
                            <li class="fn-flex">
                                <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL210}')"></span>
                                </a>
                                <a rel="nofollow" class="title fn-ellipsis fn-flex-1"
                                   href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                <a class="fn-right count ft-gray ft-smaller"
                                   href="${servePath}${article.articlePermalink}"><#if article.articleViewCount < 1000>
                                        ${article.articleViewCount}<#else>${article.articleViewCntDisplayFormat}</#if></a>
                            </li>
                        </#list>
                    </#if>
                </ul>
            </div>
        </div>
        <div class="index-recent fn-flex-1">
            <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">活跃成员</div>
                <div style="clear:both;"></div>
            </div>
            <div class="module-panel">
                <div class="index-user">
                    <#list niceUsers as user>
                        <a class="niceUsersElement fn-hidden" rel="nofollow"
                           href="${servePath}/member/${user.userName}">
                                    <span class="avatar-middle slogan"
                                          aria-label="${user.userName}"
                                          style="background-image:url('${user.userAvatarURL48}');height:30px;width:30px;margin: 0px 10px 10px 0px"></span>
                        </a>
                    </#list>
                </div>
            </div>

            <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">签到排行</div>
                <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/top/checkin">更多</a>
                </div>
                <div style="clear:both;"></div>
            </div>
            <div class="module-panel">
                <ul class="module-list">
                    <#list topCheckinUsers as user>
                        <#if user_index < 5>
                            <li class="fn-flex rank topCheckInUsersElement fn-hidden">
                                <#if user_index == 0 || user_index == 1 || user_index == 2>
                                <span
                                        <#if user_index == 0>
                                            style="border-color: #ffab10 transparent transparent #ffab10;"
                                        <#elseif user_index == 1>
                                            style="border-color: #c0c0c0 transparent transparent #c0c0c0;"
                                        <#elseif user_index == 2>
                                            style="border-color: #d9822b transparent transparent #d9822b;"
                                        </#if>
                                        class="cb-stick" aria-label="第${user_index + 1}名">
                                    <span class="icon-pin-rank">${user_index + 1}</span>
                                    </#if>
                                </span>
                                <a rel="nofollow" href="${servePath}/member/${user.userName}">
                                    <span class="avatar-small slogan"
                                          aria-label="${user.userName}"
                                          style="background-image:url('${user.userAvatarURL}')"></span>
                                </a>
                                <a rel="nofollow" class="title fn-flex-1"
                                   aria-label="${pointLabel} ${user.userPoint?c}"
                                   href="${servePath}/member/${user.userName}">${user.userName}</a>
                                <a class="fn-right count ft-gray ft-smaller"
                                   aria-label="${checkinStreakPart0Label}${user.userLongestCheckinStreak}${checkinStreakPart1Label}${user.userCurrentCheckinStreak}${checkinStreakPart2Label}"
                                   href="${servePath}/top/checkin">${user.userCurrentCheckinStreak}/<span
                                            class="ft-red">${user.userLongestCheckinStreak}</span></a>
                            </li>
                        </#if>
                    </#list>
                </ul>
            </div>

            <div style="border-bottom: 1px solid #eee;margin:10px 10px 0 10px;">
                <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">在线时间排行</div>
                <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/top/online">更多</a>
                </div>
                <div style="clear:both;"></div>
            </div>
            <div class="module-panel">
                <ul class="module-list">
                    <#list onlineTopUsers as user>
                        <#if user_index < 5>
                            <li class="fn-flex rank topCheckInUsersElement fn-hidden">
                                <#if user_index == 0 || user_index == 1 || user_index == 2>
                                <span
                                        <#if user_index == 0>
                                            style="border-color: #ffab10 transparent transparent #ffab10;"
                                        <#elseif user_index == 1>
                                            style="border-color: #c0c0c0 transparent transparent #c0c0c0;"
                                        <#elseif user_index == 2>
                                            style="border-color: #d9822b transparent transparent #d9822b;"
                                        </#if>
                                        class="cb-stick" aria-label="第${user_index + 1}名">
                                    <span class="icon-pin-rank">${user_index + 1}</span>
                                    </#if>
                                </span>
                                <a rel="nofollow" href="${servePath}/member/${user.userName}">
                                    <span class="avatar-small slogan"
                                          aria-label="${user.userName}"
                                          style="background-image:url('${user.userAvatarURL}')"></span>
                                </a>
                                <a rel="nofollow" class="title fn-flex-1"
                                   aria-label="${pointLabel} ${user.userPoint?c}"
                                   href="${servePath}/member/${user.userName}">${user.userName}</a>
                                <a class="fn-right count ft-gray ft-smaller"
                                   aria-label="在线时长共计 ${user.onlineMinute} 分钟"
                                   href="${servePath}/top/online">${user.onlineMinute} 分钟</a>
                            </li>
                        </#if>
                    </#list>
                </ul>
            </div>

        </div>
    </div>

    <div style="background-color:#f6f8fa">
        <div class="wrapper">
            <div class="fn-flex-1">
                <div class="metro-line fn-flex">
                    <div class="metro-item">
                        <div style="padding-top: 8%">
                            <div style="font-size: 13px; color: rgba(101,101,104,0.91)" id="vLine1">距离放假还有 🎉</div>
                            <div style="font-size: 80px; font-weight: bold; color: #fc7a15" id="vLine2"><span
                                        id="vDay">?</span><span style="font-size: 30px"> 天</span></div>
                            <div style="font-size: 10px; color: rgba(161,163,163,0.91)" id="vLine3">我还在编......</div>
                        </div>
                    </div>
                    <div class="metro-item" style="cursor: pointer">
                        <a class="preview" id="yesterday" onclick="yesterday()">
                            <img id="yesterdayImg" src="https://pwl.stackoverflow.wiki/2021/09/红包-(1)-6e07f7a0.png"
                                 alt="领取昨日活跃奖励"><b>领取昨日活跃奖励</b>
                        </a>
                    </div>
                    <div class="metro-item" style="cursor: pointer">
                        <a class="preview" id="checkIn" onclick="checkIn()">
                            <img id="checkInImg" src="https://pwl.stackoverflow.wiki/2021/09/签到-(1)-fa104128.png"
                                 alt="每日签到"><b>每日签到</b>
                        </a>
                    </div>
                    <div class="metro-item">
                        <a class="preview" href="${servePath}/cr">
                            <img src="https://pwl.stackoverflow.wiki/2021/09/多人在线聊天，聊天，群聊-2b7e898f.png" alt="聊天室">
                            <b>聊天室</b>
                        </a>
                    </div>
                    <div class="metro-item">
                        <a class="preview" href="${servePath}/activities">
                            <img src="https://pwl.stackoverflow.wiki/2021/09/围棋-9195fb7f.png" alt="五子棋">
                            <b>好玩</b>
                        </a>
                    </div>
                </div>

                <div class="metro-border fn-flex">
                    <div></div>
                    <div class="green"></div>
                    <div class="yellow"></div>
                    <div class="red"></div>
                    <div class="purple"></div>
                </div>
            </div>
        </div>
    </div>

    <div style="background-color:#f6f8fa;padding-top:40px;">
        <div class="wrapper">
            <div class="index-recent fn-flex-1">
                <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                    <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">聊天室</div>
                    <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/cr">进入完整版聊天室</a>
                    </div>
                    <div style="clear:both;"></div>
                </div>
                <div class="module-panel">
                    <div class="module-header form">
                        <input id="chatRoomInput"
                               type="text"
                               class="comment__text breezemoon__input"
                               placeholder="简单聊聊 (高级功能请访问完整版聊天室哦)"/>
                        <div id="chatUsernameSelectedPanel" class="completed-panel"
                             style="height:170px;display:none;left:auto;top:auto;cursor:pointer;"></div>
                        <span id="chatRoomPostBtn" class="btn breezemoon__btn" data-csrf="${csrfToken}"
                              onclick="sendChat()">Biu~</span>
                    </div>
                    <div class="module-panel">
                        <ul class="module-list" id="chatRoomIndex">
                            <#if messages?size != 0>
                                <#list messages as msg>
                                    <#if msg_index <= 9>
                                        <li class="fn-flex" id="chatindex${msg.oId}"
                                            style='border-bottom: 1px solid #eee;'>
                                            <a rel="nofollow" href="${servePath}/member/${msg.userName}">
                                                <div class="avatar"
                                                     aria-label="${msg.userName}"
                                                     style="background-image:url('${msg.userAvatarURL}')"></div>
                                            </a>
                                            <div class="fn-flex-1">
                                                <div class="ft-smaller">
                                                    <a rel="nofollow" href="${servePath}/member/${msg.userName}">
                                                        <span class="ft-gray">${msg.userName}</span>
                                                    </a>
                                                </div>
                                                <div class="vditor-reset comment<#if 0 == chatRoomPictureStatus> blur</#if>">
                                                    ${msg.content}
                                                </div>
                                            </div>
                                        </li>
                                    </#if>
                                </#list>
                            <#else>
                                <li class="ft-center ft-gray" id="emptyChatRoom">${chickenEggLabel}</li>
                            </#if>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="index-recent fn-flex-1">
                <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                    <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">标签</div>
                    <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/tags">更多</a>
                    </div>
                    <div style="clear:both;"></div>
                </div>
                <div class="module-panel">
                    <div class="index-user">
                        <#list tags as tag>
                            <#if tag_index <= 20>
                                <div class="tag-metro-item">
                                    <a class="preview" href="${servePath}/tag/${tag.tagURI}">
                                        <img src="${tag.tagIconPath}" alt="${tag.tagTitle}">
                                        <span style="white-space: nowrap;">
                                        <#if tag.tagTitle?length gt 3>
                                            <marquee width="100%" height="100%" scrollamount="1" scrolldelay="100"
                                                     truespeed>
                                                    ${tag.tagTitle}
                                                </marquee>
                                        <#else>
                                            ${tag.tagTitle}
                                        </#if>
                                        </span>
                                    </a>
                                </div>
                            </#if>
                        </#list>
                    </div>
                </div>

                <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                    <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">问答</div>
                    <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/qna">更多</a>
                    </div>
                    <div style="clear:both;"></div>
                </div>
                <div class="module-panel">
                    <ul class="module-list">
                        <#list qna as article>
                            <#if article_index <= 8>
                                <li class="fn-flex">
                                    <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL210}')"></span>
                                    </a>
                                    <a rel="nofollow" class="title fn-ellipsis fn-flex-1"
                                       href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                    <a class="fn-right count ft-gray ft-smaller"
                                       href="${servePath}${article.articlePermalink}"><#if article.articleViewCount < 1000>
                                            ${article.articleViewCount}<#else>${article.articleViewCntDisplayFormat}</#if></a>
                                </li>
                            </#if>
                        </#list>
                    </ul>
                </div>
            </div>

            <div class="index-recent fn-flex-1">
                <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                    <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">清风明月</div>
                    <a href="${servePath}/article/1630938317106" title="清风明月是什么？"
                       style="float: right; margin: 5px 0 10px 0">
                        <svg>
                            <use xlink:href="#iconQuestion"></use>
                        </svg>
                    </a>
                    <div style="clear:both;"></div>
                </div>
                <div class="module-panel">
                    <div class="module-header form">
                        <input id="breezemoonInput"
                               type="text"
                               class="comment__text breezemoon__input"
                               placeholder="${breezemoonLabel}"/>
                        <span id="breezemoonPostBtn" class="btn breezemoon__btn"
                              data-csrf="${csrfToken}">${postLabel}</span>
                    </div>
                    <div class="module-panel">
                        <ul class="module-list">
                            <#list sideBreezemoons as item>
                                <#if item_index <= 13>
                                    <li>
                                        <a href="${servePath}/member/${item.breezemoonAuthorName}">
                    <span class="avatar-small slogan" aria-label="${item.breezemoonAuthorName}"
                          style="background-image: url(${item.breezemoonAuthorThumbnailURL48})"></span>
                                        </a>
                                        <a href="${servePath}/member/${item.breezemoonAuthorName}/breezemoons/${item.oId}"
                                           class="title">${item.breezemoonContent}</a>
                                    </li>
                                </#if>
                            </#list>
                            <#if sideBreezemoons?size == 0>
                                <li class="ft-center ft-gray">${chickenEggLabel}</li>
                            </#if>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<#include "footer.ftl">
<script>
    Label.chatRoomPictureStatus = "<#if 0 == chatRoomPictureStatus> blur</#if>";
</script>
<script src="${staticServePath}/js/channel${miniPostfix}.js?${staticResourceVersion}"></script>
<script type="text/javascript">
    // tag click
    $('.preview, .index-tabs > span').click(function (event) {
        var $it = $(this),
            maxLen = Math.max($it.width(), $it.height());
        $it.prepend('<span class="ripple" style="top: ' + (event.offsetY - $it.height() / 2)
            + 'px;left:' + (event.offsetX - $it.width() / 2) + 'px;height:' + maxLen + 'px;width:' + maxLen + 'px"></span>');

        setTimeout(function () {
            $it.find('.ripple').remove();
        }, 800);
    });

    // 聊天室发送讯息
    $('#chatRoomInput').bind('keydown', function (event) {
        if (event.keyCode == "13") {
            $("#chatUsernameSelectedPanel").hide();
            sendChat();
        }
    });

    $("#chatRoomInput").on('input', function () {
        $("#chatUsernameSelectedPanel").html("");

        let value = $("#chatRoomInput").val()
        let users;
        if (value == '@') {
            $("#chatUsernameSelectedPanel").show();
            users = Util.getAtUsers('');
        } else if (value.startsWith('@')) {
            $("#chatUsernameSelectedPanel").show();
            value = value.substring(1)
            users = Util.getAtUsers(value);
        } else {
            $("#chatUsernameSelectedPanel").hide();
        }
        if (users.length === 0 || $("#chatRoomInput").val() === "") {
            $("#chatUsernameSelectedPanel").hide();
        } else {
            for (let i = 0; i < users.length; i++) {
                let user = users[i];
                $("#chatUsernameSelectedPanel").append("<a onclick=\"fillUsername('" + user.username + "')\"><img src='" + user.avatar + "' style='height:20px;width:20px;'> " + user.username + "</a>");
            }
        }
    });


    function fillUsername(username) {
        $("#chatRoomInput").val('@' + username + ' ');
        $("#chatUsernameSelectedPanel").html("");
        $("#chatUsernameSelectedPanel").hide();
    }

    function sendChat() {
        <#if isLoggedIn>
        var content = $("#chatRoomInput").val();
        var requestJSONObject = {
            content: content,
        };
        $.ajax({
            url: Label.servePath + '/chat-room/send',
            type: 'POST',
            cache: false,
            data: JSON.stringify(requestJSONObject),
            beforeSend: function () {
                $("#chatRoomInput").val("")
            },
            success: function (result) {
            }
        });
        <#else>
        window.location.href = "${servePath}/login";
        </#if>
    }

    ChatRoomChannel.init("${wsScheme}://${serverHost}:${serverPort}${contextPath}/chat-room-channel?type=index");

    var chatRoomPictureStatus = "<#if 0 == chatRoomPictureStatus> blur</#if>";
</script>
<script>
    // 随机一句话
    function randomPoem() {
        let maxNum = 5;
        let num = parseInt(Math.random() * (maxNum - 1 + 1) + 1, 10)
        switch (num) {
            case 1:
                return "都放假了，有逛摸鱼派的时间，出去玩一玩，它不香吗？";
                break;
            case 2:
                return "是不是打算睡个大懒觉，结果发现熬夜根本停不下来？";
                break;
            case 3:
                return "如果你觉得无聊，就去谈个恋爱吧~"
                break;
            case 4:
                return "虽然放假，但是你还是要敲代码呀，卷起来。"
                break;
            case 5:
                return "上学的时候，放假想上学；上班的时候：不可能，我死在家里。"
                break;
        }
    }

    // 放假倒计时
    $.ajax({
        url: "${servePath}/api/vocation",
        type: "GET",
        cache: false,
        success: function (result) {
            let dayName = result.dayName;
            let type = result.type;
            if (type === 0 || type === 3) {
                let vName = result.vName;
                let vRest = result.vRest;
                if (type === 3) {
                    $("#vLine1").html("调休不摸🐟，天理难容！<br>距离" + vName + "还有");
                } else {
                    $("#vLine1").html("摸 🐟 加油！<br>距离" + vName + "还有");
                }
                $("#vDay").html(vRest);
                $.ajax({
                    url: "https://v1.hitokoto.cn/",
                    type: "GET",
                    cache: false,
                    success: function (result) {
                        $("#vLine3").html(result.hitokoto);
                    }
                });
            } else if (type === 1 || type === 2) {
                let wRest = result.wRest;
                if (wRest === 1) {
                    $("#vLine1").html("😰 今天是" + dayName + "<br><b>假期余额严重不足❗❗❗️</b>");
                    $("#vLine2").html("<span style='font-size:45px;color:#c9320c;'>明天<br>上班</span>");
                    $("#vLine2").css("line-height", "30px");
                    $("#vLine3").html("明天，你就可以见到久违的老板和可爱的同事们了！<b>你开心吗？</b>");
                    $("#vLine3").css("padding-top", "15px");
                } else {
                    $("#vLine1").html("" + dayName + "快乐 🏖️<br>假期余额还有<b>" + wRest + "</b>天！");
                    $("#vLine2").html("<span style='font-size:60px;color:#63bf8a;'>放假</span>");
                    $("#vLine3").html(randomPoem());
                }
            }
        }
    });

    var fishingPiVersion = "${fishingPiVersion}";

    $(function () {
        var collectedYesterdayLivenessReward = ${collectedYesterdayLivenessReward};
        var checkedIn = ${checkedIn};
        if (collectedYesterdayLivenessReward === 0) {
            $("#yesterdayImg").addClass("cake");
        }
        if (checkedIn === 0) {
            $("#checkInImg").addClass("cake");
        }
    });

    function yesterday() {
        $("#yesterday").fadeOut(500, function () {
            $.ajax({
                url: "${servePath}/activity/yesterday-liveness-reward-api",
                type: "GET",
                cache: false,
                async: false,
                headers: {'csrfToken': '${csrfToken}'},
                success: function (result) {
                    if (result.sum === -1) {
                        $("#yesterday").html("<img src='https://pwl.stackoverflow.wiki/2021/09/embarrassed-4112bd37.png'><b>暂时没有昨日奖励可领取呦！明天再来试试吧</b>");
                        setTimeout(function () {
                            $("#yesterday").fadeOut(500, function () {
                                $("#yesterday").html('<img src="https://pwl.stackoverflow.wiki/2021/09/红包-(1)-6e07f7a0.png" alt="领取昨日活跃奖励"><b>领取昨日活跃奖励</b>');
                                $("#yesterday").fadeIn(500);
                            });
                        }, 2000);
                    } else {
                        $("#yesterday").html("<img src='https://pwl.stackoverflow.wiki/2021/09/correct-1f5e3258.png'><b>昨日奖励已领取！积分 +" + result.sum + "</b>");
                        setTimeout(function () {
                            $("#yesterday").fadeOut(500, function () {
                                $("#yesterday").html('<img src="https://pwl.stackoverflow.wiki/2021/09/红包-(1)-6e07f7a0.png" alt="领取昨日活跃奖励"><b>领取昨日活跃奖励</b>');
                                $("#yesterday").fadeIn(500);
                            });
                        }, 2000);
                    }
                    $("#yesterday").fadeIn(500);
                }
            });
        });
    }

    function checkIn() {
        $("#checkIn").fadeOut(500, function () {
            $.ajax({
                url: "${servePath}/activity/daily-checkin-api",
                type: "GET",
                cache: false,
                async: false,
                headers: {'csrfToken': '${csrfToken}'},
                success: function (result) {
                    if (result.sum === -1) {
                        $("#checkIn").html("<img src='https://pwl.stackoverflow.wiki/2021/09/embarrassed-4112bd37.png'><b>你已经签到过了哦！</b>");
                        setTimeout(function () {
                            $("#checkIn").fadeOut(500, function () {
                                $("#checkIn").html('<img id="checkInImg" src="https://pwl.stackoverflow.wiki/2021/09/签到-(1)-fa104128.png" alt="每日签到"><b>每日签到</b>');
                                $("#checkIn").fadeIn(500);
                            });
                        }, 2000);
                    } else {
                        $("#checkIn").html("<img src='https://pwl.stackoverflow.wiki/2021/09/correct-1f5e3258.png'><b>签到成功～ 积分 +" + result.sum + "</b>");
                        setTimeout(function () {
                            $("#checkIn").fadeOut(500, function () {
                                $("#checkIn").html('<img id="checkInImg" src="https://pwl.stackoverflow.wiki/2021/09/签到-(1)-fa104128.png" alt="每日签到"><b>每日签到</b>');
                                $("#checkIn").fadeIn(500);
                            });
                        }, 2000);
                    }
                    $("#checkIn").fadeIn(500);
                }
            });
        });
    }

    var loading = false;
    var rotate = new Rotate("randomArticlesRefreshSvg");

    function randomArticles() {
        if (!loading) {
            loading = true;
            rotate.submit();
            $("#randomArticles").fadeOut(100);
            $.ajax({
                url: "${servePath}/article/random/12",
                method: "GET",
                cache: false,
                async: true,
                success: function (result) {
                    rotate.stop();
                    loading = false;
                    $("#randomArticles").html('');
                    for (let articleCur in result.articles) {
                        let article = result.articles[articleCur];
                        let viewCount = article.articleViewCount;
                        if (viewCount >= 1000) {
                            viewCount = article.articleViewCntDisplayFormat;
                        }
                        $("#randomArticles").append('<li class="fn-flex">' +
                            '<a rel="nofollow" href="${servePath}/member/' + article.articleAuthorName + '">' +
                            '<span class="avatar-small slogan" aria-label="' + article.articleAuthorName + '" style="background-image:url(\'' + article.articleAuthorThumbnailURL210 + '\')"></span></a>' +
                            '<a rel="nofollow" class="title fn-ellipsis fn-flex-1" href="${servePath}' + article.articlePermalink + '">' + article.articleTitleEmoj + '</a>' +
                            '<a class="fn-right count ft-gray ft-smaller" href="${servePath}' + article.articlePermalink + '">' + viewCount + '</a>' +
                            '</li>');
                    }
                    $("#randomArticles").fadeIn(500);
                }
            });
        }
    }
</script>
<script>
    // img preview
    var fixDblclick = null
    $('#chatRoomIndex').on('dblclick', '.vditor-reset img', function () {
        clearTimeout(fixDblclick)
        if ($(this).hasClass('emoji')) {
            return
        }
        window.open($(this).attr('src'))
    }).on('click', '.vditor-reset img', function (event) {
        clearTimeout(fixDblclick)
        if ($(this).hasClass('emoji')) {
            return
        }
        var $it = $(this),
            it = this
        fixDblclick = setTimeout(function () {
            var top = it.offsetTop,
                left = it.offsetLeft

            $('body').append('<div class="img-preview" onclick="$(this).remove()"><img style="transform: translate3d(' +
                Math.max(0, left) + 'px, ' +
                Math.max(0, (top - $(window).scrollTop())) + 'px, 0)" src="' +
                ($it.attr('src').split('?imageView2')[0]) +
                '"></div>')

            $('.img-preview').css({
                'background-color': '#fff',
                'position': 'fixed',
            })
        }, 100)
    })
</script>
<script>
    // 渐变输出
    function elementFadeOut(element, speed) {
        let fadePicList = $(element);
        for (i = 0; i < fadePicList.length; i++) {
            let element = $(fadePicList[i]);
            setTimeout(function () {
                element.css("display", "none");
                element.removeClass("fn-hidden");
                element.fadeIn(500);
            }, speed * (i + 1));
        }
    }

    elementFadeOut(".niceUsersElement", 20);
    elementFadeOut(".topCheckInUsersElement", 90);
</script>
</body>
</html>
