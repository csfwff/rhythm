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
            <link rel="canonical" href="${servePath}/open/apply">
             <link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}" />
        </@head>
<#--        <link rel="stylesheet" href="${staticServePath}/css/theme/dark-index.css?${staticResourceVersion}" />-->
      <style>
        .tips {
          margin-top:10px;
          margin-left:16px;
        }
        .apply-form {
          padding: 10px;
          display:flex;
          flex-direction: column;
        }
        .apply-input-area {
          margin-top:10px;
          margin-left:6px;
        }
        .apply-title-area {
          margin-top:16px;
          margin-left:6px;
        }
        .apply-row-area {
          margin-top:10px;
          margin-left:6px;
          display:flex;
          align-items:center;
          flex-direction: row;
        }
        .apply-radio {
          margin:6px;
        }
      </style>
    </head>
    <body>
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper">
                <div class="content activity">
                    <h2 class="sub-head">申请ApiKey</h2>
                    <p class="tips">提示：申请需审核，审核很严格，木事莫申请，申请也不过。</p>
                    <div class="apply-form">
                        <div class="apply-input-area">
                            <input id="apiKeyDescription" type="text" placeholder="ApiKey描述" autocomplete="off" />
                        </div>
                        <div class="apply-row-area">用途：
                             <div class="apply-radio"><label><input type="radio" value="0" name="apiKeyType" checked>鱼游 </label></div>
                            <div class="apply-radio"><label><input type="radio" value="1" name="apiKeyType"> 应用 </label></div>
                            <div class="apply-radio"><label><input type="radio" value="2" name="apiKeyType"> 其他 </label></div>
                        </div>
                        <div id="applyTip" class="tip"></div>
                        <div class="apply-input-area">
                          <button class="green" onclick="Open.apply()">申请</button>
                          <#--  <input id="userId2" type="hidden" value="${user.oId}">  -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <#include "../footer.ftl">
        <script src="${staticServePath}/js/open${miniPostfix}.js?${staticResourceVersion}"></script>
         <script>
            Open.init();
        </script>
    </body>
</html>
