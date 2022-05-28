/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
var IdleTalk = {
    editor: undefined,

    destroy: function (mapId) {
        $("#" + mapId).remove();
        if ($("#received").find("li")[0] === undefined && $("#received").find(".nope")[0] === undefined) {
            $("#received").append('<div class="nope"><svg><use xlink:href="#nope"></use></svg> 没有收到任何来信</div>');
        }
    },

    revoke: function (mapId) {
        let c = confirm("确定要撤回这封信吗？对方将无法再打开这封信，同时你的积分将不会被退还。")
        if (c === true) {
            $.ajax({
                url: Label.servePath + '/idle-talk/revoke?mapId=' + mapId,
                type: 'GET',
                headers: {'csrfToken': Label.csrfToken},
                cache: false,
                success: function (result) {
                    if (result.code === -1) {
                        Util.notice("danger", 3000, result.msg);
                    } else {
                        $("#" + mapId).remove();
                        if ($("#sent").find("li")[0] === undefined && $("#sent").find(".nope")[0] === undefined) {
                            $("#sent").append('<div class="nope"><svg><use xlink:href="#nope"></use></svg> 没有未读的发信</div>');
                        }
                        Util.notice("success", 3000, "撤回成功。");
                    }
                }
            });
        }
    },

    reply: function (fromUserName, theme) {
        if ($("#sendMessageWindow").is(":hidden")) {
            $("#title").css("padding", "230px 0 20px 0");
            $("#sendMessageWindow").fadeIn(200);
        }
        $("#userForm").val(fromUserName);
        $("#themeForm").val("回复：" + theme);
        document.documentElement.scrollTop = 0;
    },

    seek: function (mapId, fromUserName, theme) {
        $.ajax({
            url: Label.servePath + '/idle-talk/seek?mapId=' + mapId,
            type: 'GET',
            headers: {'csrfToken': Label.csrfToken},
            cache: false,
            success: function (result) {
                if (result.code === -1) {
                    Util.notice("danger", 3000, result.msg);
                } else {
                    $("#" + mapId).append("<div id='seek-" + mapId + "' style='background-color: #F7F7F7;display:none;overflow: auto;padding: 20px;'>" + result.data + "<br><button class='btn' style='margin-right: 10px' onclick='IdleTalk.reply(\"" + fromUserName + "\", \"" + theme + "\")'>回复</button><button class='green' onclick='IdleTalk.destroy(" + mapId + ")'>已阅，可以销毁了</button></div>");
                    $("#seek-" + mapId).show(200);
                }
            }
        });
    },

    send: function () {
        $.ajax({
            url: Label.servePath + '/idle-talk/send',
            type: 'POST',
            headers: {'csrfToken': Label.csrfToken},
            cache: false,
            data: JSON.stringify({
                userName: $("#userForm").val(),
                theme: $("#themeForm").val(),
                content: IdleTalk.getContent()
            }),
            success: function (result) {
                if (result.code === -1) {
                    Util.notice("danger", 3000, result.msg);
                } else {
                    $("#userForm").val("");
                    $("#themeForm").val("");
                    IdleTalk.editor.setValue("");
                    Util.notice("success", 3000, "私信已发送，请静候佳音！");
                }
            }
        });
    },

    init: function () {
        IdleTalk.editor = Util.newVditor({
            id: 'messageContent',
            cache: true,
            preview: {
                mode: 'editor',
            },
            resize: {
                enable: true,
                position: 'top',
            },
            height: 200,
            placeholder: '私信内容（最长20480个字）'
        })

        let toUser = IdleTalk.getUrlPath("toUser");
        if (toUser !== false) {
            if ($("#sendMessageWindow").is(":hidden")) {
                $("#title").css("padding", "230px 0 20px 0");
                $("#sendMessageWindow").fadeIn(200);
            }
            $("#userForm").val(toUser);
        }

        $("#userForm").on('input', function () {
            $("#chatUsernameSelectedPanel").html("");
            $("#chatUsernameSelectedPanel").show();
            let users = Util.getAtUsers($("#userForm").val());
            if (users.length === 0 || $("#userForm").val() === "") {
                $("#chatUsernameSelectedPanel").hide();
            } else {
                for (let i = 0; i < users.length; i++) {
                    let user = users[i];
                    $("#chatUsernameSelectedPanel").append("<a onclick=\"IdleTalk.fillUsername('" + user.username + "')\"><img src='" + user.avatar + "' style='height:20px;width:20px;'> " + user.username + "</a>");
                }
            }
        });
    },

    getContent: function () {
        return IdleTalk.editor.getValue()
    },

    expand: function () {
        if ($("#sendMessageWindow").is(":hidden")) {
            $("#title").css("padding", "230px 0 20px 0");
            $("#sendMessageWindow").fadeIn(200);
        } else {
            $("#title").css("padding", "20px 0 20px 0");
            $("#sendMessageWindow").fadeOut(200);
        }
    },

    getUrlPath: function (variable) {
        var query = window.location.search.substring(1);
        var vars = query.split("&");
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split("=");
            if (pair[0] == variable) {
                return pair[1];
            }
        }
        return (false);
    },

    fillUsername: function (username) {
        $("#userForm").val(username);
        $("#chatUsernameSelectedPanel").html("");
        $("#chatUsernameSelectedPanel").hide();
    }
}

$(document).ready(function () {
    IdleTalk.init();
});
