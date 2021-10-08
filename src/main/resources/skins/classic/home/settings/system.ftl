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
<@home "system">
    <div class="module-header">
        <h2>自定义</h2>
    </div>
    <div class="module">
        <div class="module-panel form fn-clear">
            <label>当前社区标题</label>
            <input value="<#if hasSystemTitle>${systemTitle}<#else>${symphonyLabel}</#if>" type="text" readonly />

            <label>新的社区标题</label>
            <input id="newSystemTitle" type="text" value="<#if hasSystemTitle>${systemTitle}<#else>${symphonyLabel}</#if>"/><br/><br/>

            <button class="fn-right" onclick="Settings.update('system', '${csrfToken}')">${saveLabel}</button>
        </div>
    </div>

    <div class="module">
        <div class="module-header" style="margin-bottom: 50px;">
            <h2>个人卡片背景</h2>
        </div>
        <div class="module-panel form fn-clear">
            <div style="position: absolute;z-index: 130;">
                <div class="user-card" id="userCardSettings">
                    <div>
                        <a href="${servePath}/member/${currentUser.userName}">
                            <div class="avatar-mid-card" style="background-image: url(${currentUser.userAvatarURL});"></div>
                        </a>
                        <div class="user-card__meta">
                            <div class="fn__ellipsis">
                                <a class="user-card__name" href="${servePath}/member/${currentUser.userName}"><b>${currentUser.userNickname}</b></a>
                                <a class="ft-gray ft-smaller" href="${servePath}/member/${currentUser.userName}"><b>${currentUser.userName}</b></a>
                            </div>
                            <div class="user-card__info vditor-reset">
                                ${currentUser.userIntro}
                            </div>
                            <div class="user-card__icons fn__flex">
                                <div class="fn__flex-1">
                                    <a href="https://pwl.icu/article/1630575841478">
                                        <img style="height: 20px;margin: 0px;" src="https://pwl.stackoverflow.wiki/vipRole.png">
                                    </a>
                                    <a href="${servePath}/member/${currentUser.userName}/points" class="tooltipped-new tooltipped__n" aria-label="${currentUser.userPoint?c} 积分">
                                        <svg>
                                            <use xlink:href="#iconPoints"></use>
                                        </svg>
                                    </a>
                                </div>
                                <div class="fn__shrink">
                                    <a class="green small btn" href="${servePath}/idle-talk?toUser=${currentUser.userName}" rel="nofollow">
                                        私信
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="fn__clear" id="cardBgUploadButtons" style="margin-top: 150px;">
                <form id="cardBgUpload" method="POST" enctype="multipart/form-data">
                    <label class="btn green label__upload" style="height: 37px;margin: 0;">
                        ${uploadLabel}<input type="file" name="file">
                    </label>
                </form>
            </div>
            <button class="fn-right" style="height: 37px;" onclick="$('#userCardSettings').attr('bgUrl', '');Settings.update('system', '${csrfToken}');location.reload();">恢复默认</button>
        </div>
    </div>

    <div id="systemTip" class="tip"></div><br/>
</@home>
<script src="${staticServePath}/js/lib/jquery/file-upload-9.10.1/jquery.fileupload.min.js"></script>
<script>
    Settings.initUploadAvatar({
        id: 'cardBgUpload',
        userId: '${currentUser.oId}',
        maxSize: '${imgMaxSize?c}'
    }, function (data) {
        let imgUrl = data.result.key;
        $("#cardBgUploadButtons").css("margin-top", "270px");
        $("#userCardSettings").addClass("user-card--bg");
        $("#userCardSettings").css("background-image", "url(" + imgUrl + ")");
        $("#userCardSettings > div").attr("style", "background-image: linear-gradient(90deg, rgba(214, 227, 235, 0.36), rgba(255, 255, 255, 0.76), rgba(255, 255, 255, 0.76));");
        $("#userCardSettings > div > a > div").css("width", "105px");
        $("#userCardSettings > div > a > div").css("height", "105px");
        $("#userCardSettings > div > a > div").css("top", "80px");
        $("#userCardSettings").attr("bgUrl", imgUrl);
        Settings.update('system', '${csrfToken}');
    });

    let currentCardBg = "${cardBg}";
    if (currentCardBg !== "") {
        $("#cardBgUploadButtons").css("margin-top", "270px");
        $("#userCardSettings").addClass("user-card--bg");
        $("#userCardSettings").css("background-image", "url(" + currentCardBg + ")");
        $("#userCardSettings > div").attr("style", "background-image: linear-gradient(90deg, rgba(214, 227, 235, 0.36), rgba(255, 255, 255, 0.76), rgba(255, 255, 255, 0.76));");
        $("#userCardSettings > div > a > div").css("width", "105px");
        $("#userCardSettings > div > a > div").css("height", "105px");
        $("#userCardSettings > div > a > div").css("top", "80px");
        $("#userCardSettings").attr("bgUrl", currentCardBg);
    }
</script>
