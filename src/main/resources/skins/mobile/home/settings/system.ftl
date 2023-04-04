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
    <div id="systemTip" class="tip"></div><br/>
    <div class="module-header">
        自定义
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
        <div class="module-panel form fn-clear">
            <label>在线时间显示单位</label>
            <select id="onlineTimeUnit" onchange="Settings.update('system', '${csrfToken}')">
                <option value="m" <#if 'm' == onlineTimeUnit>selected</#if>>分钟</option>
                <option value="h" <#if 'h' == onlineTimeUnit>selected</#if>>小时</option>
                <option value="d" <#if 'd' == onlineTimeUnit>selected</#if>>天</option>
            </select>
        </div>
    </div>

    <div class="module">
        <div class="module-header">社区广告</div>
        <div class="module-panel form fn-clear">
            <label>
                摸鱼派社区❤️用爱发电；如果你喜欢这里的氛围，可以通过开启社区广告来支持我们。<br>
                广告收入将全部用于社区项目维护支出。
            </label>
            <div class="fn-clear settings-secret">
                <div>
                    <label>
                        <input id="showSideAd" type="checkbox" <#if showSideAd>checked="checked"</#if>>
                        显示侧栏广告
                    </label>
                </div>
                <div>
                    <label>
                        <input id="showTopAd" type="checkbox" <#if showTopAd>checked="checked"</#if>>
                        显示主页广告
                    </label>
                </div>
            </div>
            <button class="fn-right" onclick="Settings.update('system', '${csrfToken}')">${saveLabel}</button>
        </div>
    </div>

    <div class="module">
        <div class="module-header" style="margin-bottom: 15px;">
            个人卡片背景
        </div>
        <div class="module-panel form fn-clear">
            <input id="userCardSettings" type="text" value="">
            <div class="fn__clear" id="cardBgUploadButtons" style="margin-top: 15px;">
                <form id="cardBgUpload" method="POST" enctype="multipart/form-data">
                    <label class="btn green label__upload" style="height: 37px;margin: 0;">
                        ${uploadLabel}<input type="file" name="file">
                    </label>
                </form>
            </div>
            <button class="fn-right" style="height: 37px;" onclick="$('#userCardSettings').attr('bgUrl', '');Settings.update('system', '${csrfToken}');">恢复默认</button>
        </div>
    </div>
</@home>
<script src="${staticServePath}/js/lib/jquery/file-upload-9.10.1/jquery.fileupload.min.js"></script>
<script>
    Settings.initUploadAvatar({
        id: 'cardBgUpload',
        userId: '${currentUser.oId}',
        maxSize: '${imgMaxSize?c}'
    }, function (data) {
        let imgUrl = data.result.key;
        $("#userCardSettings").val(imgUrl);
        $("#userCardSettings").attr("bgUrl", imgUrl);
        Settings.update('system', '${csrfToken}');
    });

    let currentCardBg = "${cardBg}";
    $("#userCardSettings").val(currentCardBg);
    $("#userCardSettings").attr("bgUrl", currentCardBg);
</script>
