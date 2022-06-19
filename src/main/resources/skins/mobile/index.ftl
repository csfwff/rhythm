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
<#include "common/index-nav.ftl">
<!DOCTYPE html>
<html>
<head>
    <@head title="${symphonyLabel}">
        <meta name="description" content="${symDescriptionLabel}"/>
    </@head>
</head>
<body class="index" style="background-color: #f6f6f6;">
<#if showTopAd>
    ${HeaderBannerLabel}
</#if>
<div class="mobile-head">
    <#include "header.ftl">
    <@indexNav ''/>
</div>
<div style="height: 74px;width: 1px;" ></div>

<div class="main" >

    <ul>
        <#if recentArticles??>
        <#list recentArticles as article>
            <#include "common/list-item.ftl">
        </#list>
        </#if>
    </ul>
</div>
<#if tags?size != 0>
    <div class="module_new">
        <h2 class="module__title ft__fade fn__clear">
            推荐标签
            <div class="fn__right">
                <a class="ft__gray" href="${servePath}/domains">领域</a>
                &nbsp;•&nbsp;
                <a class="ft__gray" href="${servePath}/tags">标签</a>
            </div>
        </h2>
    </div>
    <div class="tags fn__clear">
        <#list tags as tag>
            <a class="fn__clear tag" href="${servePath}/tag/${tag.tagURI}">
                <#if tag.tagIconPath!="">
                    <img src="${tag.tagIconPath}" alt="${tag.tagTitle}"/>
                </#if>
                <span class="fn__left"> ${tag.tagTitle}</span>
            </a>
        </#list>

    </div>
</#if>
<div class="fn-hr10"></div>

<#if  niceUsers?size!=0>
    <div class="module_new">
        <h2 class="module__title ft__fade fn__clear">
            最新注册
        </h2>
    </div>
    <div class="module__body">
        <#list recentRegUsers as user>
            <a  rel="nofollow"
               href="${servePath}/member/${user.userName}">
                <img class="avatar avatar--index" src="${user.userAvatarURL48}" style="background-image: none; background-color: transparent;" >
            </a>
        </#list>
    </div>

    <div class="fn-hr10"></div>
</#if>





<div class="module_new">
    <h2 class="module__title ft__fade fn__clear">
        <div class="module__title ft__fade fn__clear">

            <a class="ft__gray" href="${servePath}/pre-post">${postArticleLabel}</a>
        </div>
    </h2>
</div>


<ul class="menu">
    <#if TGIF == '0'>
        <li  class="menu__item">
            <a class="title" onclick="window.location.href=Label.servePath+'/post?type=0&tags=摸鱼周报&title=摸鱼周报 ${yyyyMMdd}'">
                <b>每周五的摸鱼周报时间到了！</b>
                <br>
                今天还没有人写摸鱼周报哦，抢在第一名写摸鱼周报，获得 <b style="color:orange">1000 积分</b> 奖励！
            </a>
        </li>
    <#elseif TGIF == '-1'>
    <#else>
        <li class="menu__item">
            <a class="title" href="${TGIF}">
                <b>每周五的摸鱼周报时间到了！</b>
                <br>
                今天已经有人写了摸鱼周报哦，快来看看吧~
            </a>
        </li>
    </#if>
    <li class="menu__item"><a class="title" style="text-decoration: none" id="livenessToday">
        </a>
    </li>
    <li class="menu__item"><a class="title" style="text-decoration: none" id="checkIn">
        </a>
    </li >
    <li class="menu__item"><a class="title" style="text-decoration: none" id="yesterday" onclick="yesterday()">领取昨日活跃奖励</a>
    </li>
    <!--                    <li><a class="title" href="${servePath}/activity/1A0001">${activity1A0001Label}</a></li>-->
    <li class="menu__item"><a class="title" href="${servePath}/cr">聊天室</a></li>
    <li class="menu__item"><a class="title" href="${servePath}/activity/character">${characterLabel}</a></li>
    <li class="menu__item"><a class="title" href="${servePath}/charge/point"><span
                    class="ft-red">❤</span>️ ${chargePointLabel}</a></li>
    <li class="menu__item"><a class="title" href="${servePath}/top">排行榜</a></li>
    <li class="menu__item"><a class="title" href="${servePath}/shop">系统商店</a></li>
</ul>


<div class="main">
    <div class="wrapper">
        <#if showSideAd>
        <#if ADLabel != ''>
            <div class="module">
                <div class="module-header" style="background-color: #7ea5c8">
                    ${sponsorLabel}
                </div>
                <div class="ad module-panel fn-clear">
                    ${ADLabel}
                </div>
            </div>
        </#if>
        </#if>
    </div>
</div>

<div class="slogan">
    摸鱼派 - 鱼油专属摸鱼社区&nbsp;
    <a href="https://github.com/csfwff/rhythm" target="_blank">
        <svg>
            <use xlink:href="#github"></use>
        </svg>
    </a>
    <div class="TGIF__item" style="margin-top: 20px">
        <div style="float: left">
            <svg style="width: 35px; height: 35px;"><use xlink:href="#logo"></use></svg>
        </div>
        <button class="green fn-right" style="margin-left: 5px" onclick="window.location.href=Label.servePath+'/download'">下载</button>
        <div style="padding-left:40px">
            <b>随时随地摸鱼？</b>
            <br>
            下载摸鱼派客户端，想摸就摸！
        </div>
    </div>
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
    $(function(){
        let today = new Date();
        if(today.getMonth() == 11 && today.getDate() == 13){
        $('html').css("filter","grayscale(100%)")
         $('html').css("-webkit-filter","grayscale(100%)")
     }
    });
</script>
<script>
    var liveness = ${liveness};
    var checkedIn = <#if checkedIn == 1>true<#else>false</#if>;
    function getCheckedInStatus() {
        $.ajax({
            url: Label.servePath + "/user/checkedIn",
            method: "get",
            cache: false,
            async: false,
            success: function (result) {
                checkedIn = result.checkedIn;
            }
        });
    }
    function getActivityStatus() {
        $.ajax({
            url: Label.servePath + "/user/liveness",
            method: "get",
            cache: false,
            async: false,
            success: function (result) {
                liveness = result.liveness;
            }
        });
    }
    function refreshActivities() {
        <#if isLoggedIn>
        getCheckedInStatus();
        getActivityStatus();
        </#if>
        $("#livenessToday").html("今日活跃度：" + liveness + "%");
        if (liveness < 10 && !checkedIn) {
            $("#checkIn").html("今日活跃度到达 10% 后<br>系统将自动签到");
        } else if (liveness < 10 && checkedIn) {
            $("#checkIn").html("您的免签卡今日已生效");
        } else if (liveness >= 10 && !checkedIn) {
            $("#checkIn").html("已提交自动签到至系统<br>请稍候查看签到状态");
        } else if (liveness < 100) {
            $("#checkIn").html("今日活跃度到达 100% 后<br>可获得神秘礼物及明日天降红包资格");
        } else {
            $("#checkIn").html("礼物已放入背包，并获得明日天降红包资格！<br>明天在线时如有新人注册，将获得天降红包");
        }
    }
    refreshActivities();
    <#if isLoggedIn>
    setInterval(refreshActivities, 30000);
    </#if>

    <#if userPhone == "">
    Util.alert("为了确保账号的安全及正常使用，依照相关法规政策要求：<br>您需要绑定手机号后方可正常访问摸鱼派。<br><br><button onclick='location.href=\"${servePath}/settings/account#bind-phone\"'>点击这里前往设置</button>")
    </#if>

    <#if need2fa == "yes">
    Util.alert("摸鱼派管理组成员，您好！<br>作为管理组的成员，您的账号需要更高的安全性，以确保社区的稳定运行。<br>请您收到此通知后，立即在个人设置-账户中启用两步验证，感谢你对社区的贡献！<br><br><button onclick='location.href=\"${servePath}/settings/account#mfaCode\"'>点击这里前往设置</button>", "致管理组成员的重要通知️")
    </#if>
</script>
</html>
