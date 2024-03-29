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
<#include "macro-settings.ftl">
<@home "profile">
<div class="module">
    <div class="module-header fn-clear">
        <a rel="nofollow" href="${servePath}/member/${currentUser.userName}" target="_blank">${currentUser.userName}</a>
        <h2>${profilesLabel}</h2>
    </div>
    <div class="module-panel form fn-clear">
        <label>${nicknameLabel}</label><br/>
        <input id="userNickname" type="text" value="${currentUser.userNickname}" placeholder="${selfNicknameLabel}"/>

        <label>${selfTagLabel}</label><br/>
        <input id="userTags" type="text" value="${currentUser.userTags}" placeholder="${selfDescriptionLabel}"/>

        <label>URL</label><br/>
        <input id="userURL" type="text" value="${currentUser.userURL}" placeholder="${selfURLLabel}"/>

        <label>${userIntroLabel}</label><br/>
        <textarea id="userIntro" placeholder="${selfIntroLabel}">${currentUser.userIntro}</textarea>

        <label>MBTI</label><br/>
        <textarea id="userMbti" placeholder="请输入你的MBTI">${currentUser.mbti}</textarea>
        <label>填写格式错误会导致保存后无显示，请使用英文横杠（只写主要MBTI请忽略），不区分大小写<br>正确示例：<b>ENTP ENFP-A ENTP-T ISTJ ISFJ-A ISTJ-T</b><br>
        如果不知道你的MBTI或者不知道MBTI是什么，请<a href="https://www.16personalities.com/ch" target="_blank">点击这里</a></label>
        <br><br>
        <br><br>
        <br><br>
        <div class="tip" id="profilesTip"></div>
        <br>
        <button class="fn-right" onclick="Settings.update('profiles', '${csrfToken}')">${saveLabel}</button>
    </div>
</div>
</@home>
