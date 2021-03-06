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
<#macro pagination url query>
<#if paginationPageCount?? && paginationPageCount!=0 && paginationPageCount!=1>
<div class="fn-clear">
    <div class="pagination">
        <#if paginationCurrentPageNum!=1>
        <a rel="prev" href="${url}?p=${paginationCurrentPageNum-1}&${query}"><</a>
        </#if>

        <select data-url="${url}" <#if query != ''>data-param="${query}"</#if>>
            <#list 1..paginationLastPageNum as nums>
            <option <#if nums == paginationCurrentPageNum> selected="selected"</#if>>${nums}</option>
            </#list>
        </select>

        <#if paginationLastPageNum gt paginationCurrentPageNum>
        <a rel="next" href="${url}?p=${paginationCurrentPageNum+1}&${query}">></a>
        </#if>
    </div>
</div>
<#else>
<div class="fn-hr10"></div>
</#if>
</#macro>