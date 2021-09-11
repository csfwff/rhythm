<#--

    Symphony - A modern community (forum/BBS/SNS/blog) platform written in Java.
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
    <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}" />
    <link rel="canonical" href="${servePath}">
</head>
<body class="index">
${HeaderBannerLabel}
<#include "header.ftl">
<div class="main ">
    <div class="wrapper">
        <div class="index-recent fn-flex-1">
            <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">最新</div>
                <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/recent">更多</a></div>
                <div style="clear:both;"></div>
            </div>
            <div class="module-panel">
                <ul class="module-list">
                    <#list recentArticles as article>
                        <li class="fn-flex">
                            <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL20}')"></span>
                            </a>
                            <a rel="nofollow" class="title fn-ellipsis fn-flex-1" href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                            <a class="fn-right count ft-gray ft-smaller" href="${servePath}${article.articlePermalink}"><#if article.articleViewCount < 1000>
                                    ${article.articleViewCount}<#else>${article.articleViewCntDisplayFormat}</#if></a>
                        </li>
                    </#list>
                </ul>
            </div>

        </div>
        <div class="index-recent fn-flex-1">
            <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">优选</div>
                <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/perfect">更多</a></div>
                <div style="clear:both;"></div>
            </div>
            <div class="module-panel">
                <ul class="module-list">
                    <#list perfectArticles as article>
                        <li class="fn-flex">
                            <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL20}')"></span>
                            </a>
                            <a rel="nofollow" class="title fn-ellipsis fn-flex-1" href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                            <a class="fn-right count ft-gray ft-smaller" href="${servePath}${article.articlePermalink}"><#if article.articleViewCount < 1000>
                                    ${article.articleViewCount}<#else>${article.articleViewCntDisplayFormat}</#if></a>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
        <div class="index-recent fn-flex-1">
            <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">活跃用户</div>
                <div style="clear:both;"></div>
            </div>
            <div class="module-panel">
                <div class="index-user">
                    <#list niceUsers as user>

                        <a rel="nofollow" href="${servePath}/member/${user.userName}">
                                    <span class="avatar-middle tooltipped tooltipped-se slogan"
                                          aria-label="${user.userName}"
                                          style="margin-bottom: 15px;background-image:url('${user.userAvatarURL48}')"></span>
                        </a>

                    </#list>
                </div>
            </div>
        </div>
    </div>
    <div style="background-color:#f6f8fa;padding-top:10px;">
        <div class="wrapper">
            <div class="index-recent fn-flex-1">
                <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                    <a href="${servePath}/cr" style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">实时聊天室</a>
                    <div style="clear:both;"></div>
                </div>
                <div class="module-panel">
                    <div class="module-header form">
                        <input id="chatRoomInput"
                               type="text"
                               class="comment__text breezemoon__input"
                               placeholder="${chatRoomLabel}"/>
                        <span id="chatRoomPostBtn" class="btn breezemoon__btn" data-csrf="${csrfToken}" onclick="sendChat()">Biu~</span>
                    </div>
                    <div class="module-panel">
                        <ul class="module-list" id="chatRoomIndex">
                            <#if messages?size != 0>
                                <#list messages as msg>
                                    <#if msg_index <= 9>
                                        <li class="fn-flex" style='border-bottom: 1px solid #eee;'>
                                            <a rel="nofollow" href="${servePath}/member/${msg.userName}">
                                                <div class="avatar tooltipped tooltipped-n"
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
                    <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/tags">更多</a></div>
                    <div style="clear:both;"></div>
                </div>
                <div class="module-panel">
                    <div class="index-user">
                        <#list tags as tag>
                            <#if tag_index <= 20>
                                <div class="tag-metro-item">
                                    <a class="preview" href="${servePath}/tag/${tag.tagURI}">
                                        <img src="${tag.tagIconPath}" alt="${tag.tagTitle}" >
                                        <b>${tag.tagTitle}</b>
                                    </a>
                                </div>
                            </#if>
                        </#list>
                    </div>
                </div>

                <div style="border-bottom: 1px solid #eee;margin:0px 10px ;">
                    <div style="float:left;font-size:13px;margin:5px 0 10px 0; font-weight:bold;">问答</div>
                    <div style="float:right;font-size:13px;margin:5px 0 0 0;"><a href="${servePath}/qna">更多</a></div>
                    <div style="clear:both;"></div>
                </div>
                <div class="module-panel">
                    <ul class="module-list">
                        <#list qna as article>
                            <#if article_index <= 8>
                                <li class="fn-flex">
                                    <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL20}')"></span>
                                    </a>
                                    <a rel="nofollow" class="title fn-ellipsis fn-flex-1" href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                                    <a class="fn-right count ft-gray ft-smaller" href="${servePath}${article.articlePermalink}"><#if article.articleViewCount < 1000>
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
                    <a href="${servePath}/article/1630938317106" title="清风明月是什么？" style="float: right; margin: 5px 0 10px 0">
                        <svg><use xlink:href="#iconQuestion"></use></svg>
                    </a>
                    <div style="clear:both;"></div>
                </div>
                <div class="module-panel">
                    <div class="module-header form">
                        <input id="breezemoonInput"
                               type="text"
                               class="comment__text breezemoon__input"
                               placeholder="${breezemoonLabel}"/>
                        <span id="breezemoonPostBtn" class="btn breezemoon__btn" data-csrf="${csrfToken}">${postLabel}</span>
                    </div>
                    <div class="module-panel">
                        <ul class="module-list">
                            <#list sideBreezemoons as item>
                                <#if item_index <= 12>
                                    <li>
                                        <a href="${servePath}/member/${item.breezemoonAuthorName}">
                    <span class="avatar-small slogan tooltipped tooltipped-se" aria-label="${item.breezemoonAuthorName}"
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

        <div class="wrapper">
            <div class="fn-flex-1">
                <div class="metro-line fn-flex">
                    <div class="metro-item">
                        <a class="preview" href="${servePath}/activity/daily-checkin">
                            <img src="https://pwl.stackoverflow.wiki/2021/09/签到-f95cfc2f.png" alt="每日签到">
                            <b>每日签到</b>
                        </a>
                    </div>
                    <div class="metro-item">
                        <a class="preview" href="${servePath}/activity/yesterday-liveness-reward">
                            <img src="https://pwl.stackoverflow.wiki/2021/09/Reward-401eafa5.png" alt="领取昨日活跃奖励">
                            <b>领取昨日活跃奖励</b>
                        </a>
                    </div>
                    <div class="metro-item">
                        <a class="preview" href="${servePath}/cr">
                            <img src="https://pwl.stackoverflow.wiki/2021/09/多人在线聊天，聊天，群聊-2b7e898f.png" alt="聊天室">
                            <b>聊天室</b>
                        </a>
                    </div>
                    <div class="metro-item">
                        <a class="preview" href="${servePath}/activity/eating-snake">
                            <img src="https://pwl.stackoverflow.wiki/2021/09/snake-b6dbc348.png" alt="贪吃蛇">
                            <b>贪吃蛇</b>
                        </a>
                    </div>
                    <div class="metro-item">
                        <a class="preview" href="${servePath}/activity/gobang">
                            <img src="https://pwl.stackoverflow.wiki/2021/09/围棋-9195fb7f.png" alt="五子棋">
                            <b>五子棋</b>
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
</div>
</div>
<#include "footer.ftl">
<script src="${staticServePath}/js/channel${miniPostfix}.js?${staticResourceVersion}"></script>
<script type="text/javascript">
    $('.metro-item').height($('.metro-item').width());

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
    $('#chatRoomInput').bind('keydown',function(event){
        if (event.keyCode == "13") {
            sendChat();
        }
    });

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

    ChatRoomChannel.init("${wsScheme}://${serverHost}:${serverPort}${contextPath}/chat-room-channel?type=index&user=" + Label.currentUserName);

    var chatRoomPictureStatus = "<#if 0 == chatRoomPictureStatus> blur</#if>";
</script>
</body>
</html>
