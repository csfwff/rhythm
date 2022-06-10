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
<!DOCTYPE html>
<html>
<head>
    <@head title="私信 - ${symphonyLabel}">
    </@head>
    <link rel="stylesheet" href="${staticServePath}/css/home.css?${staticResourceVersion}" />
</head>
<body>
<#include "header.ftl">
<div class="main">
    <div class="wrapper">
        <div class="side">
            <div class="module person-info">
                <div class="module-panel" style="padding: 0">
                    <nav class="home-menu">
                        <a href="http://localhost:8080/notifications/reply">
                            <span>收到的回复</span>
                        </a>
                        <a href="http://localhost:8080/notifications/reply">
                            <span>收到的回复</span>
                        </a>
                    </nav>
                </div>
            </div>
        </div>
        <div class="content">

        </div>
    </div>
</div>
<#include "footer.ftl">
</body>
</html>
<script src="${staticServePath}/js/channel${miniPostfix}.js?${staticResourceVersion}"></script>
