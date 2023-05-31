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
    <@head title="老黄历 - ${activityLabel} - ${symphonyLabel}">
    <meta charset="UTF-8">
    <style>
        * {
            padding: 0;
            margin: 0
        }

        body {
            background-color: #eeeeee;
        }

        #catch-the-cat {
            width: 100%;
            margin-top: 32px;
            text-align: center;
        }
    </style>
</@head>
<link rel="stylesheet" href="${staticServePath}/css/index.css?${staticResourceVersion}" />
</head>
<body>
<#include "header.ftl">
<div class="main">
    <div class="wrapper">
        <div class="content">
            <div class="module">
                      <div class="wrapper" style="justify-content:center;padding: 15px;">
                          <div class="fn-flex old-almanac" >
                              <div class="tip metro-item" style="width: 100%;">
                                  <span class="date"></span>
                                  <div class="line-tip">
                                      <strong>座位朝向：</strong>面向<span class="direction_value"></span>写程序，BUG 最少。
                                  </div>
                                  <div class="line-tip">
                                      <strong>今日宜饮：</strong><span class="drink_value"></span>
                                  </div>
                                  <div class="line-tip">
                                      <strong>女神亲近指数：</strong><span class="goddes_value"></span>
                                  </div>
                                  <br/>
                              </div>
                              <div class="good metro-item" style="width: 100%;">
                                  <div class="title">
                                      <table>
                                          <tr>
                                              <td>宜</td>
                                          </tr>
                                      </table>
                                  </div>
                                  <div class="content">
                                      <ul></ul>
                                  </div>
                                  <div class="clear"></div>
                              </div>
                              <div class="bad metro-item" style="width: 100%;">
                                  <div class="title">
                                      <table>
                                          <tr>
                                              <td>不宜</td>
                                          </tr>
                                      </table>
                                  </div>
                                  <div class="content">
                                      <ul></ul>
                                  </div>
                                  <div class="clear"></div>
                              </div>
                          </div>
                      </div>

            </div>
        </div>
        <div class="side">
            <#include "side.ftl">
        </div>
    </div>
</div>
<#include "footer.ftl">
<script src="${staticServePath}/js/old-almanac${miniPostfix}.js?${staticResourceVersion}"></script>
</body>
</html>
