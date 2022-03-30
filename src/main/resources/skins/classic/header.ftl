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
<div class="nav">
    <h1 aria-label="${symphonyLabel}" class="tooltipped tooltipped-s">
        <a href="${servePath}">
            <svg class="swimming"><use xlink:href="#logo-white"></use></svg>
        </a>
    </h1>
    <div class="nav-tabs">
        <a pjax-title="${latestLabel} - ${symphonyLabel}"
           href="${servePath}/recent"<#if selected?? && 'recent' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#refresh"></use>
            </svg> ${latestLabel}</a>
        <a pjax-title="${breezemoonLabel} - ${symphonyLabel}"
           href="${servePath}/breezemoons"<#if selected?? && 'breezemoons' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#refresh"></use>
            </svg> ${breezemoonLabel}</a>
        <a pjax-title="${qnaLabel} - ${symphonyLabel}"
           href="${servePath}/qna"<#if selected?? && 'qna' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#iconAsk"></use>
            </svg> ${qnaLabel}</a>
        <a href="${servePath}/perfect"<#if selected?? && 'perfect' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#perfect"></use>
            </svg> ${perfectLabel}</a>
        <#if isLoggedIn && "" != currentUser.userCity>
        <a href="${servePath}/city/my"<#if selected?? && 'city' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#local"></use>
            </svg> ${currentUser.userCity}</a>
        </#if>
        <a href="${servePath}/top"<#if selected?? && 'top' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#rank"></use>
            </svg> ${totalRankLabel}</a>
        <#if isLoggedIn>
        <a href="${servePath}/watch"<#if selected?? && 'watch' == selected> class="current"</#if>>
            <svg>
                <use xlink:href="#view"></use>
            </svg> ${followLabel}</a>
        </#if>
    </div>
    <#if esEnabled || algoliaEnabled>
    <form class="responsive-hide fn-left" target="_blank" action="/search">
        <input class="search" placeholder="搜索你感兴趣的内容" type="text" name="key" id="search"
               value="<#if key??>${key}</#if>">
        <input type="submit" class="fn-none" value=""/>
    </form>
    </#if>
    <div class="user-nav">
        <#if isLoggedIn>
            <a href="${servePath}/pre-post" class="tooltipped tooltipped-w" aria-label="${postArticleLabel}">
                <svg>
                    <use xlink:href="#addfile"></use>
                </svg>
            </a>
            <#if permissions["menuAdmin"].permissionGrant>
            <a href="${servePath}/admin" aria-label="${adminLabel}" class="tooltipped tooltipped-w">
                <svg>
                    <use xlink:href="#userrole"></use>
                </svg>
            </a>
            </#if>
            <a id="aNotifications"
               class="tooltipped tooltipped-w <#if unreadNotificationCount == 0>no-msg<#else>msg</#if>"
               href="${servePath}/notifications" aria-label="${messageLabel}">${unreadNotificationCount}
            </a>
            <a href="${servePath}/idle-talk" class="tooltipped tooltipped-w" aria-label="私信">
                <#if hasUnreadChatMessage?? && hasUnreadChatMessage>
                    <script>
                        setTimeout(function () {
                            Util.blingChat();
                        }, 2000);
                    </script>
                </#if>
                <svg id="idleTalkIconContainer">
                    <use xlink:href="#idleChat"></use>
                </svg>
            </a>
            <a href="${servePath}/activities" aria-label="${activityLabel}" class="tooltipped tooltipped-w">
                <svg>
                    <use xlink:href="#flag"></use>
                </svg>
            </a>
            <a href="javascript:void(0)" id="aPersonListPanel" class="tooltipped tooltipped-w"
               aria-label="${viewHomeAndProfileLabel}"
               data-url="${servePath}/member/${currentUser.userName}">
                <span class="avatar-small" style="background-image:url('${currentUser.userAvatarURL20}')"></span>
            </a>
            <div class="module person-list" id="personListPanel" style="right: 13px">
                <ul>
                    <li>
                        <a href="${servePath}/member/${currentUser.userName}">${goHomeLabel}</a>
                    </li>
                    <li>
                        <a href="${servePath}/settings">${settingsLabel}</a>
                    </li>
                    <li>
                        <a href="${servePath}/charge/point"><span class="ft-red">❤</span>️ ${chargePointLabel}</a>
                    </li>
                    <li>
                        <a href="javascript:void(0)" onclick="Count.settings()">⏰ 下班倒计时</a>
                    </li>
                    <li>
                        <a href="${servePath}/settings/help">${helpLabel}</a>
                    </li>
                    <li>
                        <a href="javascript:Util.logout()">${logoutLabel}</a>
                    </li>
                </ul>
            </div>
        <#else>
            <a href="javascript: Util.goLogin();" class="unlogin">${loginLabel}</a>
            <a href="javascript:Util.goRegister()" class="unlogin">${registerLabel}</a>
        </#if>
    </div>
</div>
