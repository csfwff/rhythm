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
    <div class="module">
        <div class="module-header">
            <h2>自定义网站标题</h2>
        </div>
        <div class="module-panel form fn-clear">
            <label>当前社区标题</label>
            <input value="<#if hasSystemTitle>${systemTitle}<#else>${symphonyLabel}</#if>" type="text" readonly />

            <label>新的社区标题</label>
            <input id="newSystemTitle" type="text" value="<#if hasSystemTitle>${systemTitle}<#else>${symphonyLabel}</#if>"/><br/><br/>

            <div id="systemTip" class="tip"></div><br/>
            <button class="fn-right" onclick="Settings.update('system', '${csrfToken}')">${saveLabel}</button>
        </div>
    </div>
</@home>
