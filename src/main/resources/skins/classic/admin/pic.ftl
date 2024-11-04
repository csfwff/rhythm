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
<@admin "pic">
    <div class="content">
        <div class="module">
            <div class="module-header">
                <h2>图片审核</h2>
            </div>

            <div class="module-panel form fn-clear form--admin">
                <div>
                    <h3>审前必读</h3>
                    <p>1. 您的审核将被记录</p>
                    <p>2. 临时图片：未出现在帖子、帖子回复下的临时图片：例如截图是最常见的。这一类图片可以从图床删除以节约摸鱼派的存储资源，删除此类图片您将获得8积分奖励，但不处罚、不通知用户</p>
                    <p>3. 违规图片：如涉及政治、色情、违法等违规图片，请及时删除，删除后您将获得128积分奖励，同时用户将被除以500积分的处罚，并通知用户</p>
                </div>
                <br>
                <@pagination url="${servePath}/admin/pic"/>
                <div class="file__items fn__clear">
                    <#list files as file>
                        <div class="item" id="${file.oId}">
                            <#if file.type == "mp4">
                                <video class="item__img" autoplay muted loop playsinline>
                                    <source src="${file.path}" type="video/mp4">
                                    您的浏览器不支持 video 标签。
                                </video>
                            <#else>
                                <div class="item__img" style="background-image: url(${file.path});"></div>
                            </#if>
                            <div class="item__info">
                                上传时间：${file.time}
                                <br>
                                类型：${file.type}
                                <br>
                                上传者：<a href="${servePath}/member/${file.userName}" target="_blank">${file.userName}</a>
                                <#if file.public == true>
                                    <span style="position: absolute; left: 5px; bottom: 5px">
                                        <button class="btn red" onclick="mark('${file.oId}', 'temp')">标记为临时图片</button><br>
                                        <button class="btn red" onclick="mark('${file.oId}', 'illegal')">标记为违规图片</button>
                                    </span>
                                <#else>
                                    <span style="position: absolute; left: 5px; bottom: 5px">
                                        <button disabled class="btn" onclick="javascript:void(0)">该图片已被审核过</button><br>
                                    </span>
                                </#if>
                                <span style="position: absolute; right: 5px; bottom: 5px">
                                <button class="btn green" onclick="window.open('${file.path}')">查看原图</button>
                                </span>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </div>
    </div>
    <script>
        function mark(oId, type) {
            $.ajax({
                url: "${servePath}/admin/pic",
                type: "POST",
                data: {
                    oId: oId,
                    type: type
                },
                success: function (result) {
                    Util.notice("success", 1500, result.msg);
                    $("#" + oId).remove();
                }
            });
        }
    </script>
</@admin>
