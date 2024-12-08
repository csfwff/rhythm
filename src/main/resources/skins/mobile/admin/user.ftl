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
<@admin "users">
<div class="wrapper">
    <div class="module">
        <div class="module-header">
            <h2>${unmodifiableLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <label>Id</label>
            <input type="text" value="${user.oId}" readonly="readonly" />

            <label>${userNameLabel}</label>
            <input type="text" value="${user.userName}" readonly="readonly" />

            <label>${userNoLabel}</label>
            <input type="text" value="${user.userNo?c}" readonly="readonly" />

            <label>${userEmailLabel}</label>
            <input type="text" value="${user.userEmail}" readonly="readonly" />

            <label>${articleCountLabel}</label>
            <input type="text" value="${user.userArticleCount}" readonly="readonly" />

            <label>${commentCountLabel}</label>
            <input type="text" value="${user.userCommentCount}" readonly="readonly" />

            <label>${tagCountLabel}</label>
            <input type="text" value="${user.userTagCount}" readonly="readonly" />

            <label>${pointLabel}</label>
            <input type="text" value="${user.userPoint?c}" readonly="readonly" />

            <label>${countryLabel}</label>
            <input type="text" value="${user.userCountry}" readonly="readonly" />

            <label>${provinceLabel}</label>
            <input type="text" value="${user.userProvince}" readonly="readonly" />

            <label>${cityLabel}</label>
            <input type="text" value="${user.userCity}" readonly="readonly" />

            <label>IP</label>
            <input type="text" value="${user.userLatestLoginIP}" readonly="readonly" />

            <label>${registerTimeLabel}</label>
            <input type="text" value="${user.oId?number?number_to_datetime}" readonly="readonly" />

            <label>${loginTimeLabel}</label>
            <input type="text" value="${user.userLatestLoginTime?number_to_datetime}" readonly="readonly" />

            <label>${commentTimeLabel}</label>
            <input type="text" value="${user.userLatestCmtTime?number_to_datetime}" readonly="readonly" />

            <label>${articleTimeLabel}</label>
            <input type="text" value="${user.userLatestArticleTime?number_to_datetime}" readonly="readonly" />

            <label>${checkinStreakLabel}</label>
            <input type="text" value="${user.userCurrentCheckinStreak}" readonly="readonly" />

            <label>${checkinStreakPart0Label}</label>
            <input type="text" value="${user.userLongestCheckinStreak}" readonly="readonly" />
        </div>
    </div>

    <#if permissions["userUpdateUserBasic"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${modifiableLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/admin/user/${user.oId}" method="POST">
                <label for="userPassword">${passwordLabel}</label>
                <input type="text" id="userPassword" name="userPassword" value="${user.userPassword}" />

                <label for="userNickname">${nicknameLabel}</label>
                <input type="text" id="userNickname" name="userNickname" value="${user.userNickname}" />

                <label for="userTags">${selfTagLabel}</label>
                <input type="text" id="userTags" name="userTags" value="${user.userTags}" />

                <label for="userURL">URL</label>
                <input type="text" id="userURL" name="userURL" value="${user.userURL}" />

                <#--
                <label for="userQQ">QQ</label>
                <input type="text" id="userQQ" name="userQQ" value="${user.userQQ}" />
                -->

                <label for="userIntro">${userIntroLabel}</label>
                <input type="text" id="userIntro" name="userIntro" value="${user.userIntro}" />

                <label for="userIntro">${avatarURLLabel}</label>
                <input type="text" id="userAvatarURL" name="userAvatarURL" value="${user.userAvatarURL}" />

                <label for="userListPageSize">${userListPageSizeLabel}</label>
                <input type="number" id="userListPageSize" name="userListPageSize" value="${user.userListPageSize}" />

                <label>${cmtViewModeLabel}</label>
                <select id="userCommentViewMode" name="userCommentViewMode">
                    <option value="0"<#if 0 == user.userCommentViewMode> selected</#if>>${traditionLabel}</option>
                    <option value="1"<#if 1 == user.userCommentViewMode> selected</#if>>${realTimeLabel}</option>
                </select>

                <label>${avatarViewModeLabel}</label>
                <select id="userAvatarViewMode" name="userAvatarViewMode">
                    <option value="0"<#if 0 == user.userAvatarViewMode> selected</#if>>${orgImgLabel}</option>
                    <option value="1"<#if 1 == user.userAvatarViewMode> selected</#if>>${staticImgLabel}</option>
                </select>

                <label>${useNotifyLabel}</label>
                <select id="userNotifyStatus" name="userNotifyStatus">
                    <option value="0"<#if 0 == user.userNotifyStatus> selected</#if>>${yesLabel}</option>
                    <option value="1"<#if 1 == user.userNotifyStatus> selected</#if>>${noLabel}</option>
                </select>

                <label>${subMailLabel}</label>
                <select id="userSubMailStatus" name="userSubMailStatus">
                    <option value="0"<#if 0 == user.userSubMailStatus> selected</#if>>${yesLabel}</option>
                    <option value="1"<#if 1 == user.userSubMailStatus> selected</#if>>${noLabel}</option>
                </select>

                <label>${enableKbdLabel}</label>
                <select id="userKeyboardShortcutsStatus" name="userKeyboardShortcutsStatus">
                    <option value="0"<#if 0 == user.userKeyboardShortcutsStatus> selected</#if>>${yesLabel}</option>
                    <option value="1"<#if 1 == user.userKeyboardShortcutsStatus> selected</#if>>${noLabel}</option>
                </select>

                <label>${geoLabel}</label>
                <select id="userGeoStatus" name="userGeoStatus">
                    <option value="0"<#if 0 == user.userGeoStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userGeoStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userArticleStatusLabel}</label>
                <select id="userArticleStatus" name="userArticleStatus">
                    <option value="0"<#if 0 == user.userArticleStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userArticleStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userCommentStatusLabel}</label>
                <select id="userCommentStatus" name="userCommentStatus">
                    <option value="0"<#if 0 == user.userCommentStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userCommentStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userFollowingUserStatusLabel}</label>
                <select id="userFollowingUserStatus" name="userFollowingUserStatus">
                    <option value="0"<#if 0 == user.userFollowingUserStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userFollowingUserStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userFollowingTagStatusLabel}</label>
                <select id="userFollowingTagStatus" name="userFollowingTagStatus">
                    <option value="0"<#if 0 == user.userFollowingTagStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userFollowingTagStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userFollowingArticleStatusLabel}</label>
                <select id="userFollowingArticleStatus" name="userFollowingArticleStatus">
                    <option value="0"<#if 0 == user.userFollowingArticleStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userFollowingArticleStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userFollowerStatusLabel}</label>
                <select id="userFollowerStatus" name="userFollowerStatus">
                    <option value="0"<#if 0 == user.userFollowerStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userFollowerStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userPointStatusLabel}</label>
                <select id="userPointStatus" name="userPointStatus">
                    <option value="0"<#if 0 == user.userPointStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userPointStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userOnlineStatusLabel}</label>
                <select id="userOnlineStatus" name="userOnlineStatus">
                    <option value="0"<#if 0 == user.userOnlineStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userOnlineStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${displayUALabel}</label>
                <select id="userUAStatus" name="userUAStatus">
                    <option value="0"<#if 0 == user.userUAStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userUAStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${joinBalanceRankLabel}</label>
                <select id="userJoinPointRank" name="userJoinPointRank">
                    <option value="0"<#if 0 == user.userJoinPointRank> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userJoinPointRank> selected</#if>>${privateLabel}</option>
                </select>

                <label>${joinCosumptionRankLabel}</label>
                <select id="userJoinUsedPointRank" name="userJoinUsedPointRank">
                    <option value="0"<#if 0 == user.userJoinUsedPointRank> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userJoinUsedPointRank> selected</#if>>${privateLabel}</option>
                </select>

                <label>${userBreezemoonStatusLabel}</label>
                <select id="userBreezemoonStatus" name="userBreezemoonStatus">
                    <option value="0"<#if 0 == user.userBreezemoonStatus> selected</#if>>${publicLabel}</option>
                    <option value="1"<#if 1 == user.userBreezemoonStatus> selected</#if>>${privateLabel}</option>
                </select>

                <label>${roleLabel}</label>
                <select id="userRole" name="userRole">
                    <#list roles as role>
                        <option value=${role.oId}<#if role.oId == user.userRole> selected</#if>>${role.roleName}</option>
                    </#list>
                </select>

                <label>${appRoleLabel}</label>
                <select id="userAppRole" name="userAppRole">
                    <option value="0"<#if 0 == user.userAppRole> selected</#if>>${hackerLabel}</option>
                    <option value="1"<#if 1 == user.userAppRole> selected</#if>>${painterLabel}</option>
                </select>

                <label>${userStatusLabel}</label>
                <select id="userStatus" name="userStatus">
                    <option value="0"<#if 0 == user.userStatus> selected</#if>>${validLabel}</option>
                    <option value="1"<#if 1 == user.userStatus> selected</#if>>${banLabel}</option>
                    <option value="2"<#if 2 == user.userStatus> selected</#if>>${notVerifiedLabel}</option>
                    <option value="3"<#if 3 == user.userStatus> selected</#if>>${invalidLoginLabel}</option>
                    <option value="3"<#if 4 == user.userStatus> selected</#if>>${deactivateAccountLabel}</option>
                </select>

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["userUpdateUserAdvanced"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${advancedUpdateLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/admin/user/${user.oId}/phone" method="POST">
                <label for="userPhone">手机号码</label>
                <input type="text" id="userPhone" name="userPhone" value="${user.userPhone}"/>

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
            <br/>

            <form action="${servePath}/admin/user/${user.oId}/email" method="POST">
                <label for="userEmail">${userEmailLabel}</label>
                <input type="text" id="userEmail" name="userEmail" value="${user.userEmail}" />

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
            <br/>

            <form action="${servePath}/admin/user/${user.oId}/username" method="POST">
                <label for="userName">${userNameLabel}</label>
                <input type="text" name="userName" value="${user.userName}" />

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>

            <form action="${servePath}/admin/user/${user.oId}/cardBg" method="POST">
                <label for="cardBg">卡片背景</label>
                <input type="text" name="cardBg" value="${userCardBg}" />

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["userAddPoint"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${chargePointLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/admin/user/${user.oId}/charge-point" method="POST">
                <label>${userNameLabel}</label>
                <input type="text" name="userName" value="${user.userName}" readonly="readonly" />

                <label>${pointLabel}</label>
                <input type="text" name="point" value="" />

                <label>${memoLabel}</label>
                <input type="text" name="memo" value="" placeholder="${chargePointPlaceholderLabel}" />

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["userExchangePoint"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${exchangePointLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/admin/user/${user.oId}/exchange-point" method="POST">
                <label>${userNameLabel}</label>
                <input type="text" name="userName" value="${user.userName}" readonly="readonly" />

                <label>${pointLabel}</label>
                <input type="text" name="point" value="" />

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["userDeductPoint"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${abusePointLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/admin/user/${user.oId}/abuse-point" method="POST">
                <label>${userNameLabel}</label>
                <input type="text" name="userName" value="${user.userName}" readonly="readonly" />

                <label>${pointLabel}</label>
                <input type="text" name="point" value="" />

                <label>${memoLabel}</label>
                <input type="text" name="memo" value="" />

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <div class="module">
        <div class="module-header">
            <h2>${compensateInitPointLabel}</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/admin/user/${user.oId}/init-point" method="POST">
                <label>${userNameLabel}</label>
                <input type="text" name="userName" value="${user.userName}" readonly="readonly" />

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>

    <#if permissions["userAdjustBag"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>物品发放</h2>
        </div>
        <div class="module-panel form fn-clear">
            <form action="${servePath}/admin/user/${user.oId}/adjust-bag" method="POST">
                <label>${userNameLabel}</label>
                <input type="text" name="userName" value="${user.userName}" readonly class="input--admin-readonly"/>
                <label>物品名称</label>
                <input type="text" name="item" value=""/>
                <label>数量</label>
                <input type="text" name="sum" value=""/>

                <br/><br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
            <div style="float: left;font-size: 12px;color: rgba(0,0,0,0.38);word-break: break-all">
                当前用户背包数据：<br>
                ${sysBag}<br>
                可用物品名称：<br>
                checkin1day (1日免签卡)<br>
                checkin2days (2日免签卡)<br>
                patchCheckinCard (补签卡)<br>
                nameCard（改名卡）<br>
                数量为正数时，增加；数量为负数时，减少。
            </div>
        </div>
    </div>
    </#if>

    <#if permissions["userGiveMetal"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>勋章发放</h2>
        </div>
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}/give-metal" method="POST">
                <div class="fn__flex">
                    <label>
                        <div>${userNameLabel}</div>
                        <input type="text" name="userName" value="${user.userName}" readonly class="input--admin-readonly"/>
                    </label>
                </div>
                <div class="fn__flex">
                    <label class="mid">
                        <div>勋章名称</div>
                        <input id="metal-name" type="text" name="name" value=""/>
                    </label>
                    <label>
                        <div>描述</div>
                        <input id="metal-desc" type="text" name="description" value=""/>
                    </label>
                </div>
                <div class="fn__flex">
                    <label class="mid">
                        <div>属性</div>
                        <input id="metal-attr" type="text" name="attr" value=""/>
                    </label>
                    <label>
                        <div>数据</div>
                        <input id="metal-data" type="text" name="data" value=""/>
                    </label>
                </div>
                <br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
            <div style="float: left;font-size: 12px;color: rgba(0,0,0,0.38);">
                当前用户勋章数据：<br>
                ${sysMetal}<br>
                属性示例：<br>
                url=[图标URL]&backcolor=0000ff&fontcolor=ffffff<br>
                数据示例：<br>
                暂时无需填写，留空即可<br><br>
                快捷勋章：<br>
                <button class="btn" onclick="
                    $('#metal-name').val('纪律委员');
                    $('#metal-desc').val('摸鱼派管理组成员');
                    $('#metal-attr').val('url=https://file.fishpi.cn/2021/12/011shield-46ce360b.jpg&backcolor=2568ff&fontcolor=ffffff');
                ">纪律委员</button>
                <button class="btn" onclick="
                    $('#metal-name').val('开发');
                    $('#metal-desc').val('摸鱼派官方开发组成员');
                    $('#metal-attr').val('url=https://file.fishpi.cn/2021/12/metaldev-db507262.png&backcolor=483d8b&fontcolor=f8f8ff');
                ">开发</button>
                <button class="btn" onclick="
                    $('#metal-name').val('Operator');
                    $('#metal-desc').val('摸鱼派管理组成员');
                    $('#metal-attr').val('url=https://file.fishpi.cn/2023/08/op3-db4c5d4c.png&backcolor=b91c22&fontcolor=ffffff');
                ">Operator</button>
                <button class="btn" onclick="
                    $('#metal-name').val('超级会员');
                    $('#metal-desc').val('摸鱼派超级会籍成员');
                    $('#metal-attr').val('url=https://file.fishpi.cn/2021/12/vip-aff3ea5d.png&backcolor=696969&fontcolor=ffd700');
                ">超级会员</button>
                <button class="btn" onclick="
                    $('#metal-name').val('礼仪委员');
                    $('#metal-desc').val('摸鱼派文明标兵，有什么不懂的请问我哦');
                    $('#metal-attr').val('url=https://file.fishpi.cn/2022/06/013happy-6e078931.png&backcolor=f0e68c&fontcolor=ff0000');
                ">礼仪委员</button>
                <button class="btn" onclick="
                    $('#metal-name').val('小姐姐认证 ');
                    $('#metal-desc').val('摸鱼派官方认证，她是小姐姐哟');
                    $('#metal-attr').val('url=https://file.fishpi.cn/2022/07/girl-73f46e57.jpg&backcolor=ffb6c1&fontcolor=ff1493');
                ">小姐姐认证</button>
                <button class="btn" onclick="
                    $('#metal-name').val('LGBT ');
                    $('#metal-desc').val('摸鱼派最可爱的群体 🏳️‍🌈');
                    $('#metal-attr').val('url=https://file.fishpi.cn/2022/07/截屏20220707234552-4c0711d1.png&backcolor=b0c4de&fontcolor=f0f8ff');
                ">LGBT</button>
                <button class="btn" onclick="
                    $('#metal-name').val('00后');
                    $('#metal-desc').val('这片江山已经是00后的天下了');
                    $('#metal-attr').val('url=https://file.fishpi.cn/2022/07/啤酒-8227499a.jpg&backcolor=ffffff&fontcolor=ffa500');
                ">00后</button>
            </div>
        </div>
    </div>
    </#if>

    <#if permissions["userRemoveMetal"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>勋章移除</h2>
        </div>
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}/remove-metal" method="POST">
                <div class="fn__flex">
                    <label>
                        <div>${userNameLabel}</div>
                        <input type="text" name="userName" value="${user.userName}" readonly class="input--admin-readonly"/>
                    </label>
                    <label class="mid">
                        <div>勋章名称</div>
                        <input id="remove-metal-name" type="text" name="name" value=""/>
                    </label>
                </div>
                <br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
            <div style="float: left;font-size: 12px;color: rgba(0,0,0,0.38);">
                快速移除勋章：<br>
                <div id="remove-metal-panel">
                </div>
                <script>
                    let html = '';
                    let sysMetal = ${sysMetal};
                    let list = sysMetal.list;
                    if (list !== undefined && list.length !== 0) {
                        for (let i = 0; i < list.length; i++) {
                            let m = list[i];
                            html += '' +
                                '<button class="btn" onclick="$(\'#remove-metal-name\').val(\'' + m.name + '\')">' + m.name + '</button>';
                        }
                    }
                    document.getElementById('remove-metal-panel').innerHTML = html;
                </script>
            </div>
        </div>
    </div>
    </#if>

    <#if permissions["userRemoveMFA"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>两步认证令牌移除</h2>
        </div>
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}/remove-mfa" method="POST">
                <div class="fn__flex">
                    <label>
                        <div>${userNameLabel}</div>
                        <input type="text" name="userName" value="${user.userName}" readonly class="input--admin-readonly"/>
                    </label>
                </div>
                <br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>
</div>
</@admin>
