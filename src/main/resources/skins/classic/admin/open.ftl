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
<#include "macro-admin.ftl">
<#include "../macro-pagination.ftl">
<@admin "open">
<div class="content admin">
    <div class="module list">
        <ul class="notification">
            <#list open_api as item>
                <li class="">
                    <div class="fn-flex">
                        <div class="fn-flex-1">
                            ID：${item.oId}
                            申请人：${item.openApiApplyUserName}
                            API名称：${item.openApiName}
                            API描述：${item.openApiDescription}
                            API等级：${item.openApiLevel}
                            API类型：${item.openApiType}
                            <#--  ${reportLabel}
                            ${item.reportDataTypeStr}
                            ${item.reportData}
                            <div class="ft-smaller ft-gray">
                                ${item.reportTypeStr} •
                                ${item.reportTime?string('yyyy-MM-dd HH:mm')}
                                <#if item.reportHandled == 1>
                                • <span class="ft-green">${processLabel}</span>
                                <#elseif item.reportHandled == 2>
                                • <span class="ft-fade">${ignoreLabel}</span>
                                </#if>
                            </div>  -->
                        </div>
                <#--  "0:未处理,1:可用,-1:已拒绝,2:开发者禁用,3:已封禁"  -->
                <#if item.openApiAvailable == 0>
                <div>
                    <button class="mid green" onclick="AdminOpenApiApproved(this, '${item.oId}')">通过</button>
                    &nbsp;
                    <button class="mid" onclick="AdminOpenApiCancel(this, '${item.oId}')">拒绝</button>
                </div>
                </#if>
                   <#if item.openApiAvailable == 1>
                <div>
                   已通过
                </div>
                </#if>
                      <#if item.openApiAvailable == -1>
                <div>
                   已拒绝
                </div>
                </#if>
                    </div>
                    <#--  <div class="vditor-reset">
                        ${item.reportMemo}
                    </div>  -->
                </li>
            </#list>
        </ul>
        <@pagination url="${servePath}/admin/reports"/>
    </div>
</div>
</@admin>
