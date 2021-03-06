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
<!DOCTYPE html>
<html>
    <head>
        <@head title="${activity1A0001Label} - ${activityLabel} - ${symphonyLabel}">
        </@head>
    </head>
    <body>
        <#include "../header.ftl">
        <div class="main">
            <div class="wrapper">
                <div class="content activity">
                    <div class="fn-hr10"></div>
                    <div class="vditor-reset">
                    ${activity1A0001TitleLabel}
                    ${activity1A0001GuideLabel}

                    <#if !closed && !closed1A0001 && !end && !collected && !participated>
                    <div id="betDiv">
                        <div>
                            ${activity1A0001BetSelectLabel}
                            <label><input name="smallOrLarge" type="radio" value="1" checked="checked" /> ${activity1A0001BetLargeLabel}</label>
                            <label><input name="smallOrLarge" type="radio" value="0" /> ${activity1A0001BetSmallLabel}</label>
                        </div>

                        <div>
                            ${activity1A0001BetAmountLabel}
                            <label><input name="amount" type="radio" value="200" checked="checked" /> 200</label>
                            <label><input name="amount" type="radio" value="300" /> 300</label>
                            <label><input name="amount" type="radio" value="400" /> 400</label>
                            <label><input name="amount" type="radio" value="500" /> 500</label>
                        </div>
                    </div>
                    </#if>
                    </div>
                    <#if participated || closed || closed1A0001 || collected || end>
                        <div id="tip" class="tip succ"><ul><li>${msg}</li></ul></div>
                        <div class="fn-hr10"></div>
                        <#if participated && hour?? && hour gt 15>
                        <div class="fn-clear">
                            <button id="collectBtn" class="red fn-right" onclick="Activity.collect1A0001()">${activityCollectLabel}</button>
                        </div>
                        <div class="fn-hr10"></div>
                        </#if>
                    <#else>
                    <div id="tip" class="tip"></div>
                    <div class="fn-hr10"></div>
                    <div class="fn-clear">
                        <button id="betBtn" class="red fn-right" onclick="Activity.bet1A0001('${csrfToken}')">${activityBetLabel}</button>
                    </div>
                    <div class="fn-hr10"></div>
                    </#if>
                </div>
                <div class="side">
                    <#include "../side.ftl">
                </div>
            </div>
        </div>
        <#include "../footer.ftl">
        <script src="${staticServePath}/js/activity${miniPostfix}.js?${staticResourceVersion}"></script>
    </body>
</html>