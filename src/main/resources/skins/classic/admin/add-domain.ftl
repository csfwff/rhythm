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
<@admin "addDomain">
<div class="content">
    <#if permissions["domainAddDomain"].permissionGrant>
        <div class="module">
            <div class="module-header">
                <h2>${addDomainLabel}</h2>
            </div>
            <div class="module-panel form fn-clear form--admin">
                <form action="${servePath}/admin/add-domain" class="fn__flex" method="POST">
                    <label>
                        <div>${titleLabel}</div>
                        <input name="domainTitle" type="text" />
                    </label>
                    <div>
                        &nbsp; &nbsp;
                        <button type="submit" class="green fn-right btn--admin">${submitLabel}</button>
                    </div>
                </form>
            </div>
        </div>
    </#if>
</div>
</@admin>