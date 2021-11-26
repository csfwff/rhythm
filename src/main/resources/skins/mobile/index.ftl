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
<!DOCTYPE html>
<html>
<head>
    <@head title="${symphonyLabel}">
        <meta name="description" content="${symDescriptionLabel}"/>
    </@head>
</head>
<body class="index">
${HeaderBannerLabel}
<#include "header.ftl">
<@subNav '' ''/>
<div class="main">
    <div class="wrapper fn-clear">
        <div class="module">
            <div class="module-header" style="background-color: #97cf76;">
                <a href="${servePath}/recent">${latestLabel}</a>
            </div>
            <div class="module-panel">
                <style>
                    .cb-stick {
                        position: absolute;
                        top: 0;
                        left: 0;
                        border-width: 10px 10px 10px 10px;
                        border-color: #999 transparent transparent #999;
                        border-style: solid;
                    }

                    .icon-pin {
                        position: absolute;
                        top: -9px;
                        left: -11px;
                        color: #FFF;
                    }
                </style>
                <ul class="module-list">
                    <#list recentArticles as article>
                        <li<#if !article_has_next> class="last"</#if>>
                            <#if article.articleStick != 0>
                                <span class="cb-stick tooltipped tooltipped-e" aria-label="管理置顶"><svg class="icon-pin"><use
                                                xlink:href="#pin"></use></svg></span>
                            </#if>
                            <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL210}')"></span>
                            </a>
                            <a rel="nofollow" class="title fn-ellipsis"
                               href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                            <a class="fn-right count ft-gray ft-smaller"
                               href="${servePath}${article.articlePermalink}"><#if article.articleViewCount < 1000>
                                    ${article.articleViewCount}<#else>${article.articleViewCntDisplayFormat}</#if></a>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
        <div class="module-panel">
            <div class="module-header" style="background-color: #69b6df;">
                <a onclick="randomArticles()" style="text-decoration: none;">
                    <svg id="randomArticlesRefreshSvg">
                        <use xlink:href="#refresh"></use>
                    </svg>
                    换点别的&nbsp;&nbsp;
                    随便看看
                </a>
            </div>
            <ul class="module-list" id="randomArticles">
                <#if indexRandomArticles??>
                    <#list indexRandomArticles as article>
                        <li class="fn-flex">
                            <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
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
        <div class="module">
            <div class="module-header" style="background-color: #dfb169;">
                <a href="${servePath}/perfect">${perfectLabel}</a>
            </div>
            <div class="module-panel">
                <ul class="module-list">
                    <#list perfectArticles as article>
                        <li<#if !article_has_next> class="last"</#if>>
                            <a rel="nofollow" href="${servePath}/member/${article.articleAuthorName}">
                                    <span class="avatar-small tooltipped tooltipped-se slogan"
                                          aria-label="${article.articleAuthorName}"
                                          style="background-image:url('${article.articleAuthorThumbnailURL210}')"></span>
                            </a>
                            <a rel="nofollow" class="title fn-ellipsis"
                               href="${servePath}${article.articlePermalink}">${article.articleTitleEmoj}</a>
                            <a class="fn-right count ft-gray ft-smaller"
                               href="${servePath}${article.articlePermalink}">${article.articleViewCount}</a>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
        <div class="module">
            <div class="module-header" style="background-color: #69dfac;">
                <a href="${servePath}/cr">聊天室</a>
            </div>
            <div class="module-panel">
                <div style="margin: 15px 0 5px 0">
                    <a href="${servePath}/cr">点击加入聊天</a>
                </div>
                <ul class="module-list" id="chatRoomIndex">
                    <#if messages?size != 0>
                        <#list messages as msg>
                            <#if msg_index <= 9>
                                <li class="fn-flex" id="chatindex${msg.oId}">
                                    <a rel="nofollow" href="${servePath}/member/${msg.userName}">
                                        <div class="avatar tooltipped tooltipped-n"
                                             aria-label="${msg.userName}"
                                             style="background-image:url('${msg.userAvatarURL}')"></div>
                                    </a>
                                    <div class="fn-flex-1">
                                        <div class="ft-smaller">
                                            <a rel="nofollow" href="${servePath}/member/${msg.userName}">
                                                <#if msg.userNickname?? && msg.userNickname?length gt 1>
                                                    <span class="ft-gray">${msg.userNickname} ( ${msg.userName} )</span>
                                                <#else>
                                                    <span class="ft-gray">${msg.userName}</span>
                                                </#if>                                            </a>
                                        </div>
                                        <div class="vditor-reset comment<#if 0 == chatRoomPictureStatus> blur</#if>">
                                            <#assign text=msg.content>
                                            <#if text?contains("\"msgType\":\"redPacket\"")>
                                                [收到红包，请在完整版聊天室查看]
                                            <#else>
                                                ${text}
                                            </#if>
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
</div>
<#if tags?size != 0>
    <div class="index-wrap">
        <div class="wrapper">
            <ul class="tag-desc fn-clear">
                <#list tags as tag>
                    <li>
                        <a rel="nofollow" href="${servePath}/tag/${tag.tagURI}">
                            <#if tag.tagIconPath!="">
                                <img src="${tag.tagIconPath}" alt="${tag.tagTitle}"/>
                            </#if>
                            ${tag.tagTitle}
                        </a>
                    </li>
                </#list>
            </ul>
        </div>
    </div>
</#if>
<div class="fn-hr10"></div>
<div class="main">
    <div class="wrapper">
        <#if ADLabel != ''>
            <div class="module">
                <div class="module-header" style="background-color: #7ea5c8">
                    <a href="${servePath}/about">${sponsorLabel}</a>
                </div>
                <div class="ad module-panel fn-clear">
                    ${ADLabel}
                </div>
            </div>
        </#if>
        <div class="module">
            <div class="module-header" style="background-color: #9cd462">
                <a href="${servePath}/pre-post">${postArticleLabel}</a>
            </div>
            <div class="module-panel">
                <ul class="module-list">
                    <#if TGIF == '0'>
                        <li>
                            <a class="title" onclick="window.location.href=Label.servePath+'/post?type=0&tags=摸鱼周报&title=摸鱼周报 ${yyyyMMdd}'">
                                <b>每周五的摸鱼周报时间到了！</b>
                                <br>
                                今天还没有人写摸鱼周报哦，抢在第一名写摸鱼周报，获得 <b style="color:orange">1000 积分</b> 奖励！
                            </a>
                        </li>
                    <#elseif TGIF == '-1'>
                    <#else>
                        <li>
                            <a class="title" href="${TGIF}">
                                <b>每周五的摸鱼周报时间到了！</b>
                                <br>
                                今天已经有人写了摸鱼周报哦，快来看看吧~
                            </a>
                        </li>
                    </#if>
                    <li><a class="title" style="text-decoration: none">
                            今日活跃度：${liveness}%
                        </a>
                    </li>
                    <li><a class="title" style="text-decoration: none" id="checkIn">
                            <#if liveness < 10 && checkedIn == 0>
                                今日活跃度到达 10% 后<br>系统将自动签到
                            <#elseif liveness < 100>
                                已签到，今日活跃度到达 100% 后<br>可获得一张免签卡 (2天)
                            <#else>
                                成功获取免签卡 (2天) 一张<br>可在设置-账户中使用
                            </#if>
                        </a>
                    </li>
                    <li><a class="title" style="text-decoration: none" id="yesterday" onclick="yesterday()">领取昨日活跃奖励</a>
                    </li>
                    <li><a class="title" href="${servePath}/activity/1A0001">${activity1A0001Label}</a></li>
                    <li><a class="title" href="${servePath}/activity/character">${characterLabel}</a></li>
                    <li><a class="title" href="${servePath}/charge/point"><span
                                    class="ft-red">❤</span>️ ${chargePointLabel}</a></li>
                    <li><a class="title" href="${servePath}/top">排行榜</a></li>
                </ul>
            </div>
        </div>
    </div>
</div>

<div class="slogan">
    ${indexIntroLabel}&nbsp;
    <a href="https://github.com/88250/symphony" target="_blank">
        <svg>
            <use xlink:href="#github"></use>
        </svg>
    </a>
</div>
<#include "footer.ftl">
</body>
<script>
    var chatRoomPictureStatus = "<#if 0 == chatRoomPictureStatus> blur</#if>";

    var fishingPiVersion = "${fishingPiVersion}";

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
                            '<span class="avatar-small tooltipped tooltipped-se slogan" aria-label="' + article.articleAuthorName + '" style="background-image:url(\'' + article.articleAuthorThumbnailURL210 + '\')"></span></a>' +
                            '<a rel="nofollow" class="title fn-ellipsis fn-flex-1" href="${servePath}' + article.articlePermalink + '">' + article.articleTitleEmoj + '</a>' +
                            '<a class="fn-right count ft-gray ft-smaller" href="${servePath}' + article.articlePermalink + '">' + viewCount + '</a>' +
                            '</li>');
                    }
                    $("#randomArticles").fadeIn(500);
                }
            });
        }
    }

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
                        $("#yesterday").html("暂时没有昨日奖励可领取呦！明天再来试试吧");
                        setTimeout(function () {
                            $("#yesterday").fadeOut(500, function () {
                                $("#yesterday").html('领取昨日活跃奖励');
                                $("#yesterday").fadeIn(500);
                            });
                        }, 2000);
                    } else {
                        $("#yesterday").html("昨日奖励已领取！积分 +" + result.sum);
                        setTimeout(function () {
                            $("#yesterday").fadeOut(500, function () {
                                $("#yesterday").html('领取昨日活跃奖励');
                                $("#yesterday").fadeIn(500);
                            });
                        }, 2000);
                    }
                    $("#yesterday").fadeIn(500);
                },
                error: function () {
                    Util.goLogin();
                }
            });
        });
    }
</script>
<script>
    $('#chatRoomIndex').on('click', '.vditor-reset img', function () {
        if ($(this).hasClass('emoji')) {
            return;
        }
        window.open($(this).attr('src'));
    });
</script>
</html>
