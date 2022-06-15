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
<#include "../macro-head.ftl">
<html>
    <head>
        <@head title="摸鱼派开放平台 - ${symphonyLabel}">
            <link rel="canonical" href="${servePath}/top/index">
        </@head>
<#--        <link rel="stylesheet" href="${staticServePath}/css/theme/dark-index.css?${staticResourceVersion}" />-->
    </head>
    <body>
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper">
                <div class="content activity">
                    <h2 class="sub-head">摸鱼派开放平台</h2>
                    <div class="list">
                        <ul>
                            <li class="fn__flex">
                                <div class="fn-flex-1">
                                    <h2>
                                        <a class="title"
                                           href="${servePath}/open/apply">申请API Key</a>
                                    </h2>
                                    <div class="ft-fade ft-smaller">申请一个ApiKey</div>
                                </div>
                            </li>
                            <li class="fn__flex">
                                 <div class="fn-flex-1">
                                        <h2>
                                            <a class="title"
                                              href="${servePath}/open/mine">我的API Key</a>
                                        </h2>
                                  <div class="ft-fade ft-smaller">查看我的ApiKey</div>
                            </li>
                        </ul>
                        <br/>
                    </div>
                </div>
            </div>
        </div>
        <#include "../footer.ftl">
        <script src="${staticServePath}/js/settings${miniPostfix}.js?${staticResourceVersion}"></script>
    </body>
</html>
