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
<div class="content">
    <div class="module">
        <div class="module-header">
            <h2>${unmodifiableLabel}</h2>
        </div>
        <div class="module-panel form form--admin fn-clear">
            <div class="fn__flex">
                <label>
                    <div>Id</div>
                    <input onfocus="this.select()" type="text" value="${user.oId}" readonly="readonly"/>
                </label>
                <label>
                    <div>${userNameLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userName}" readonly="readonly"/>
                </label>
                <label>
                    <div>${userNoLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userNo?c}" readonly="readonly"/>
                </label>
                <label>
                    <div>${userEmailLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userEmail}" readonly="readonly"/>
                </label>
            </div>
            <div class="fn__flex">
                <label>
                    <div>${articleCountLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userArticleCount?c}" readonly="readonly"/>
                </label>
                <label>
                    <div>${commentCountLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userCommentCount?c}" readonly="readonly"/>
                </label>
                <label>
                    <div>${tagCountLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userTagCount}" readonly="readonly"/>
                </label>
                <label>
                    <div>${pointLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userPoint?c}" readonly="readonly"/>
                </label>
            </div>
            <div class="fn__flex">
                <label>
                    <div>${countryLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userCountry}" readonly="readonly"/>
                </label>
                <label>
                    <div>${provinceLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userProvince}" readonly="readonly"/>
                </label>
                <label>
                    <div>${cityLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userCity}" readonly="readonly"/>
                </label>
                <label>
                    <div>IP</div>
                    <input onfocus="this.select()" type="text" value="${user.userLatestLoginIP}" readonly="readonly"/>
                </label>
            </div>
            <div class="fn__flex">
                <label>
                    <div>${registerTimeLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.oId?number?number_to_datetime}"
                           readonly="readonly"/>
                </label>
                <label>
                    <div>${loginTimeLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userLatestLoginTime?number_to_datetime}"
                           readonly="readonly"/>
                </label>
                <label>
                    <div>${commentTimeLabel}</div>
                    <input onfocus="this.select()" type="text"
                           value="<#if user.userLatestCmtTime != 0>${user.userLatestCmtTime?number_to_datetime}</#if>"
                           readonly="readonly"/>
                </label>
                <label>
                    <div>${articleTimeLabel}</div>
                    <input onfocus="this.select()" type="text"
                           value="<#if user.userLatestArticleTime != 0>${user.userLatestArticleTime?number_to_datetime}</#if>"
                           readonly="readonly"/>
                </label>
            </div>
            <div class="fn__flex">
                <label>
                    <div>${checkinStreakLabel}</div>
                    <input onfocus="this.select()" type="text" value="${user.userCurrentCheckinStreak}"
                           readonly="readonly"/>
                </label>
                <label>
                    <div>${checkinStreakPart0Label}</div>
                    <input onfocus="this.select()" type="text" value="${user.userLongestCheckinStreak}"
                           readonly="readonly"/>
                </label>
                <label></label>
                <label></label>
            </div>
        </div>
    </div>

    <#if permissions["userUpdateUserBasic"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${modifiableLabel}</h2>
        </div>
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}" method="POST">
                <div class="fn__flex">
                    <label>
                        <div>${passwordLabel}</div>
                        <input type="text" id="userPassword" name="userPassword" value="${user.userPassword}"/>
                    </label>
                    <label class="mid">
                        <div>${nicknameLabel}</div>
                        <input type="text" id="userNickname" name="userNickname" value="${user.userNickname}"/>
                    </label>
                    <label>
                        <div>${avatarViewModeLabel}</div>
                        <select id="userAvatarViewMode" name="userAvatarViewMode">
                            <option value="0"<#if 0 == user.userAvatarViewMode> selected</#if>>${orgImgLabel}</option>
                            <option value="1"<#if 1 == user.userAvatarViewMode>
                                    selected</#if>>${staticImgLabel}</option>
                        </select>
                    </label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${selfTagLabel}</div>
                        <input type="text" id="userTags" name="userTags" value="${user.userTags}"/>
                    </label>
                    <label class="mid">
                        <div>${userIntroLabel}</div>
                        <input type="text" id="userIntro" name="userIntro" value="${user.userIntro}"/>
                    </label>
                    <label>
                        <div>URL</div>
                        <input type="text" id="userURL" name="userURL" value="${user.userURL}"/>
                    </label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${avatarURLLabel}</div>
                        <input type="text" id="userAvatarURL" name="userAvatarURL" value="${user.userAvatarURL}"/>
                    </label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${userListPageSizeLabel}</div>
                        <input type="number" id="userListPageSize" name="userListPageSize"
                               value="${user.userListPageSize}"/>
                    </label>
                    <label class="mid">
                        <div>${cmtViewModeLabel}</div>
                        <select id="userCommentViewMode" name="userCommentViewMode">
                            <option value="0"<#if 0 == user.userCommentViewMode>
                                    selected</#if>>${traditionLabel}</option>
                            <option value="1"<#if 1 == user.userCommentViewMode>
                                    selected</#if>>${realTimeLabel}</option>
                        </select>
                    </label>
                    <label></label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${useNotifyLabel}</div>
                        <select id="userNotifyStatus" name="userNotifyStatus">
                            <option value="0"<#if 0 == user.userNotifyStatus> selected</#if>>${yesLabel}</option>
                            <option value="1"<#if 1 == user.userNotifyStatus> selected</#if>>${noLabel}</option>
                        </select>
                    </label>
                    <label class="mid">
                        <div>${subMailLabel}</div>
                        <select id="userSubMailStatus" name="userSubMailStatus">
                            <option value="0"<#if 0 == user.userSubMailStatus> selected</#if>>${yesLabel}</option>
                            <option value="1"<#if 1 == user.userSubMailStatus> selected</#if>>${noLabel}</option>
                        </select>
                    </label>
                    <label>
                        <div>${enableKbdLabel}</div>
                        <select id="userKeyboardShortcutsStatus" name="userKeyboardShortcutsStatus">
                            <option value="0"<#if 0 == user.userKeyboardShortcutsStatus>
                                    selected</#if>>${yesLabel}</option>
                            <option value="1"<#if 1 == user.userKeyboardShortcutsStatus>
                                    selected</#if>>${noLabel}</option>
                        </select>
                    </label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${userCommentStatusLabel}</div>
                        <select id="userCommentStatus" name="userCommentStatus">
                            <option value="0"<#if 0 == user.userCommentStatus> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userCommentStatus> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label class="mid">
                        <div>${userFollowingUserStatusLabel}</div>
                        <select id="userFollowingUserStatus" name="userFollowingUserStatus">
                            <option value="0"<#if 0 == user.userFollowingUserStatus>
                                    selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userFollowingUserStatus>
                                    selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label>
                        <div>${userFollowingTagStatusLabel}</div>
                        <select id="userFollowingTagStatus" name="userFollowingTagStatus">
                            <option value="0"<#if 0 == user.userFollowingTagStatus>
                                    selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userFollowingTagStatus>
                                    selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${userFollowingArticleStatusLabel}</div>
                        <select id="userFollowingArticleStatus" name="userFollowingArticleStatus">
                            <option value="0"<#if 0 == user.userFollowingArticleStatus>
                                selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userFollowingArticleStatus>
                                selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label class="mid">
                        <div>${userWatchingArticleStatusLabel}</div>
                        <select id="userWatchingArticleStatus" name="userWatchingArticleStatus">
                            <option value="0"<#if 0 == user.userWatchingArticleStatus>
                                selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userWatchingArticleStatus>
                                selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label>
                        <div>${userFollowerStatusLabel}</div>
                        <select id="userFollowerStatus" name="userFollowerStatus">
                            <option value="0"<#if 0 == user.userFollowerStatus> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userFollowerStatus> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${userPointStatusLabel}</div>
                        <select id="userPointStatus" name="userPointStatus">
                            <option value="0"<#if 0 == user.userPointStatus> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userPointStatus> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label class="mid">
                        <div>${userOnlineStatusLabel}</div>
                        <select id="userOnlineStatus" name="userOnlineStatus">
                            <option value="0"<#if 0 == user.userOnlineStatus> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userOnlineStatus> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label>
                        <div>${displayUALabel}</div>
                        <select id="userUAStatus" name="userUAStatus">
                            <option value="0"<#if 0 == user.userUAStatus> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userUAStatus> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${userArticleStatusLabel}</div>
                        <select id="userArticleStatus" name="userArticleStatus">
                            <option value="0"<#if 0 == user.userArticleStatus> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userArticleStatus> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label class="mid">
                        <div>${geoLabel}</div>
                        <select id="userGeoStatus" name="userGeoStatus">
                            <option value="0"<#if 0 == user.userGeoStatus> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userGeoStatus> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label>
                        <div>${userBreezemoonStatusLabel}</div>
                        <select id="userBreezemoonStatus" name="userBreezemoonStatus">
                            <option value="0"<#if 0 == user.userBreezemoonStatus> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userBreezemoonStatus> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${joinBalanceRankLabel}</div>
                        <select id="userJoinPointRank" name="userJoinPointRank">
                            <option value="0"<#if 0 == user.userJoinPointRank> selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userJoinPointRank> selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label class="mid">
                        <div>${joinCosumptionRankLabel}</div>
                        <select id="userJoinUsedPointRank" name="userJoinUsedPointRank">
                            <option value="0"<#if 0 == user.userJoinUsedPointRank>
                                    selected</#if>>${publicLabel}</option>
                            <option value="1"<#if 1 == user.userJoinUsedPointRank>
                                    selected</#if>>${privateLabel}</option>
                        </select>
                    </label>
                    <label></label>
                </div>
                <div class="fn__flex">
                    <label>
                        <div>${roleLabel}</div>
                        <select id="userRole" name="userRole">
                    <#list roles as role>
                        <option value=${role.oId}<#if role.oId == user.userRole> selected</#if>>${role.roleName}</option>
                    </#list>
                        </select>
                    </label>
                    <label class="mid">
                        <div>${appRoleLabel}</div>
                        <select id="userAppRole" name="userAppRole">
                            <option value="0"<#if 0 == user.userAppRole> selected</#if>>${hackerLabel}</option>
                            <option value="1"<#if 1 == user.userAppRole> selected</#if>>${painterLabel}</option>
                        </select>
                    </label>
                    <label>
                        <div>${userStatusLabel}</div>
                        <select id="userStatus" name="userStatus">
                            <option value="0"<#if 0 == user.userStatus> selected</#if>>${validLabel}</option>
                            <option value="1"<#if 1 == user.userStatus> selected</#if>>${banLabel}</option>
                            <option value="2"<#if 2 == user.userStatus> selected</#if>>${notVerifiedLabel}</option>
                            <option value="3"<#if 3 == user.userStatus> selected</#if>>${invalidLoginLabel}</option>
                            <option value="3"<#if 4 == user.userStatus> selected</#if>>${deactivateAccountLabel}</option>
                        </select>
                    </label>
                </div>
                <br/>
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
        <div class="module-panel form fn-clear form--admin">
            <form class="fn__flex" action="${servePath}/admin/user/${user.oId}/phone" method="POST">
                <label>
                    <div>手机号码</div>
                    <input type="text" id="userPhone" name="userPhone" value="${user.userPhone}"/>
                </label>
                <div>
                    &nbsp; &nbsp;
                    <button type="submit" class="green fn-right btn--admin">${submitLabel}</button>
                </div>
            </form>
            <form class="fn__flex" action="${servePath}/admin/user/${user.oId}/email" method="POST">
                <label>
                    <div>${userEmailLabel}</div>
                    <input type="text" id="userEmail" name="userEmail" value="${user.userEmail}"/>
                </label>
                <div>
                    &nbsp; &nbsp;
                    <button type="submit" class="green fn-right btn--admin">${submitLabel}</button>
                </div>
            </form>
            <form action="${servePath}/admin/user/${user.oId}/username" method="POST" class="fn__flex">
                <label>
                    <div>${userNameLabel}</div>
                    <input type="text" name="userName" value="${user.userName}"/>
                </label>
                <div>
                    &nbsp; &nbsp;
                    <button type="submit" class="green fn-right btn--admin">${submitLabel}</button>
                </div>
            </form>
            <form action="${servePath}/admin/user/${user.oId}/cardBg" method="POST" class="fn__flex">
                <label>
                    <div>卡片背景</div>
                    <input type="text" name="cardBg" value="${userCardBg}"/>
                </label>
                <div>
                    &nbsp; &nbsp;
                    <button type="submit" class="green fn-right btn--admin">${submitLabel}</button>
                </div>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["userAddPoint"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${chargePointLabel}</h2>
        </div>
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}/charge-point" method="POST">
                <div class="fn__flex">
                    <label>
                        <div>${userNameLabel}</div>
                        <input onfocus="this.select()" type="text" name="userName" value="${user.userName}"
                               readonly />
                    </label>
                    <label class="mid">
                        <div>${pointLabel}</div>
                        <input type="text" name="point" value=""/>
                    </label>
                    <label>
                        <div>${memoLabel}</div>
                        <input type="text" name="memo" value="" placeholder="${chargePointPlaceholderLabel}"/>
                    </label>
                </div>
                <br/>
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
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}/exchange-point" method="POST" class="fn__flex">
                <label>
                    <div>${userNameLabel}</div>
                    <input type="text" name="userName" value="${user.userName}" readonly class="input--admin-readonly"/>
                </label>
                <label class="mid">
                    <div>${pointLabel}</div>
                    <input type="text" name="point" value=""/>
                </label>
                <div class="fn__flex-1">
                    <button type="submit" class="green fn-right btn--admin">${submitLabel}</button>
                </div>
            </form>
        </div>
    </div>
    </#if>

    <#if permissions["userDeductPoint"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>${abusePointLabel}</h2>
        </div>
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}/abuse-point" method="POST">
                <div class="fn__flex">
                    <label>
                        <div>${userNameLabel}</div>
                        <input type="text" name="userName" value="${user.userName}" readonly class="input--admin-readonly"/>
                    </label>
                    <label class="mid">
                        <div>${pointLabel}</div>
                        <input type="text" name="point" value=""/>
                    </label>
                    <label>
                        <div>${memoLabel}</div>
                        <input type="text" name="memo" value=""/>
                    </label>
                </div>
                <br/>
                <button type="submit" class="green fn-right">${submitLabel}</button>
            </form>
        </div>
    </div>
    </#if>

    <div class="module">
        <div class="module-header">
            <h2>${compensateInitPointLabel}</h2>
        </div>
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}/init-point" class="fn__flex" method="POST">
                <label>
                    <div>${userNameLabel}</div>
                    <input type="text" name="userName" value="${user.userName}" readonly class="input--admin-readonly"/>
                </label>
                <div>
                    &nbsp; &nbsp;
                    <button type="submit" class="green fn-right btn--admin">${submitLabel}</button>
                </div>
            </form>
        </div>
    </div>

    <#if permissions["userAdjustBag"].permissionGrant>
    <div class="module">
        <div class="module-header">
            <h2>物品发放</h2>
        </div>
        <div class="module-panel form fn-clear form--admin">
            <form action="${servePath}/admin/user/${user.oId}/adjust-bag" method="POST">
                <div class="fn__flex">
                    <label>
                        <div>${userNameLabel}</div>
                        <input type="text" name="userName" value="${user.userName}" readonly class="input--admin-readonly"/>
                    </label>
                    <label class="mid">
                        <div>物品名称</div>
                        <input type="text" name="item" value=""/>
                    </label>
                    <label>
                        <div>数量</div>
                        <input type="text" name="sum" value=""/>
                    </label>
                </div>
                <br/>
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
