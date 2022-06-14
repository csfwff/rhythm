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
var Chat = {
    page: 1,
    toUser: '',
    init: function (to) {
        let toUser;
        if (to !== undefined) {
            // 打扫战场
            Chat.page = 1;
            try {
                Chat.ws.close();
            } catch (e) {
            }
            $("#chats").html("");
            $("#chatTo" + Chat.toUser).css("background-color", "");
            Chat.toUser = '';
            Chat.noMore = false;
            $("#chatTo" + to).css("background-color", "#f1f1f1");
            toUser = to;
        } else {
            toUser = getQueryVariable("toUser");
        }
        Chat.toUser = toUser;
        // 加载最近聊天列表
        setTimeout(function () {
            $.ajax({
                url: Label.servePath + '/chat/get-list?apiKey=' + apiKey,
                type: 'GET',
                async: 'false',
                success: function (result) {
                    if (0 === result.result) {
                        let data = result.data;
                        data.forEach((userData) => {
                            if (userData.receiverUserName === '文件传输助手') {
                                $("#fileTransferMsg").html(userData.preview);
                            } else {
                                if (userData.senderUserName != Label.currentUserName) {
                                    if ($("#chatTo" + userData.senderUserName).length <= 0) {
                                        Chat.addToMessageList(userData.senderUserName, userData.senderAvatar, userData.preview);
                                    }
                                } else {
                                    if ($("#chatTo" + userData.receiverUserName).length <= 0) {
                                        Chat.addToMessageList(userData.receiverUserName, userData.receiverAvatar, userData.preview);
                                    }
                                }
                            }
                        });
                    }
                }
            });

            if (toUser !== '') {
                // 用户已读
                $.ajax({
                    url: Label.servePath + "/chat/mark-as-read?apiKey=" + apiKey + "&fromUser=" + Chat.toUser,
                    type: "GET"
                });
                // 加载用户
                if (toUser !== 'FileTransfer') {
                    $.ajax({
                        url: Label.servePath + "/user/" + toUser,
                        type: "GET",
                        success: function (result) {
                            if (result.code === -1) {
                                alert('指定的用户名不存在，请检查后重试！');
                                location.href = Label.servePath + '/chat';
                            }
                            if ($("#chatTo" + result.userName).length <= 0) {
                                Chat.addToMessageList(result.userName, result.userAvatarURL, "&nbsp;");
                            }
                        }
                    });
                }
            }
        }, 100);

        $(function () {
            // 加载未读
            setTimeout(function () {
                $.ajax({
                    url: Label.servePath + '/chat/has-unread?apiKey=' + apiKey,
                    type: 'GET',
                    success: function (result) {
                        let count = result.result;
                        let list = result.data;
                        list.forEach((data) => {
                            $("#chatTo" + data.senderUserName).css("background-color", "#fff4eb");
                        });
                    }
                });
            }, 500);
        });

        if (toUser === "") {
            // 未选定用户
            $("#chatStatus").html('请在左侧列表选择最近聊天的成员，或直接发起聊天。<br><br>' +
                '<input class="form" id="chatWithInput" placeholder="输入用户名">&nbsp;<button onclick="Chat.startAChat()">发起聊天</button>');
            // 监听回车
            $("#chatWithInput").keypress(function (e) {
                if (e.which == 13) {
                    Chat.startAChat();
                }
            });
        } else {
            // 已选定用户，获取第一页聊天信息
            // 状态
            $("#chatStatus").html('和 ' +
                '<a href="' + Label.servePath + '/member/' + toUser + '">' + toUser + '</a> ' +
                '聊天中');
            // 显示按钮
            $("#buttons").show();
            // 显示翻页
            $(".pagination__chat").show();
            // 加载编辑器
            Chat.editor = Util.newVditor({
                id: 'messageContent',
                cache: true,
                preview: {
                    mode: 'editor',
                },
                resize: {
                    enable: true,
                    position: 'bottom',
                },
                height: 150,
                placeholder: '说点什么吧，友善第一哦。',
                toolbar: [
                    'emoji',
                    'link',
                    'upload',
                    'edit-mode',
                    {
                        name: 'more',
                        toolbar: [
                            'insert-after',
                            'fullscreen',
                            'preview',
                            'info',
                            'help',
                        ],
                    },
                ],
                ctrlEnter: function () {
                    Chat.send();
                }
            });
            // 连接WS
            Chat.ws = new WebSocket(chatChannelURL + toUser);
            Chat.ws.onopen = function () {
                console.log("Connected to chat channel websocket.")
            }
            Chat.ws.onmessage = function (evt) {
                var data = JSON.parse(evt.data)
                if (data.code === -1) {
                    $('#chatContentTip').
                    addClass('error').
                    html('<ul><li>' + data.msg + '</li></ul>');
                } else {
                    $('#chatContentTip').removeClass('error succ').html('')
                    let oId = data.oId;
                    let toId = data.toId;
                    let fromId = data.fromId;
                    let time = data.time;
                    let content = data.content;
                    let preview = data.preview.substring(0, 10);
                    let dot = data.preview.length > 10 ? "..." : "";
                    let user_session = data.user_session;
                    let senderUserName = data.senderUserName;
                    let senderAvatar = data.senderAvatar;
                    let receiverUserName = data.receiverUserName;
                    let receiverAvatar = data.receiverAvatar;
                    // 添加消息
                    if (senderUserName === Label.currentUserName) {
                        // 我发送的
                        Chat.addSelfMsg(oId, senderUserName, senderAvatar, content, time, false);
                        $("#chatTo" + receiverUserName).find("span").html(preview + dot);
                        // 新消息置顶
                        let html = $("#chatTo" + receiverUserName).prop('outerHTML');
                        $("#chatTo" + receiverUserName).remove();
                        $("#chatMessageList").prepend(html);
                        // 文件传输助手特殊管理
                        if (toId === '1000000000086') {
                            $("#chatToFileTransfer").find("span").html(preview + dot);
                        }
                    } else {
                        // 他发给我的
                        Chat.addTargetMsg(oId, senderUserName, senderAvatar, content, time, false);
                        $("#chatTo" + senderUserName).find("span").html(preview + dot);
                        // 新消息置顶
                        let html = $("#chatTo" + senderUserName).prop('outerHTML');
                        $("#chatTo" + senderUserName).remove();
                        $("#chatMessageList").prepend(html);
                    }
                }
            }
            Chat.ws.onclose = function () {
                console.log("Disconnected to chat channel websocket.")
            }
            Chat.ws.onerror = function (err) {
                console.log('ERROR', err)
            }
            $(function () {
                // 对话中的用户深色
                setTimeout(function () {
                    $("#chatTo" + toUser).css("background-color", "#f1f1f1");
                }, 220);
            });
            Chat.loadMore();
            // 监听滑动
            $(window).scroll(
                function() {
                    var scrollTop = $(this).scrollTop();
                    var scrollHeight = $(document).height();
                    var windowHeight = $(this).height();
                    if (scrollTop + windowHeight + 500 >= scrollHeight) {
                        Chat.loadMore();
                    }
                }
            );
        }
    },

    startAChat() {
        let input = $("#chatWithInput").val();
        if (input !== '') {
            Chat.init(input);
        }
    },

    noMore: false,
    moreLock: false,
    loadMore() {
        if (!Chat.moreLock) {
            Chat.moreLock = true;
            if (!Chat.noMore) {
                $.ajax({
                    url: Label.servePath + "/chat/get-message?apiKey=" + apiKey + "&toUser=" + Chat.toUser + "&page=" + Chat.page + "&pageSize=20",
                    type: "GET",
                    async: "false",
                    success: function (result) {
                        try {
                            result.data.length;
                        } catch (e) {
                            Chat.noMore = true;
                        }
                        if (!Chat.noMore) {
                            Chat.page++;
                            result.data.forEach((data) => {
                                let oId = data.oId;
                                let toId = data.toId;
                                let fromId = data.fromId;
                                let time = data.time;
                                let content = data.content;
                                let preview = data.preview.substring(0, 10);
                                let dot = data.preview.length > 10 ? "..." : "";
                                let user_session = data.user_session;
                                let senderUserName = data.senderUserName;
                                let senderAvatar = data.senderAvatar;
                                let receiverUserName = data.receiverUserName;
                                let receiverAvatar = data.receiverAvatar;
                                // 添加消息
                                if (senderUserName === Label.currentUserName) {
                                    // 我发送的
                                    Chat.addSelfMsg(oId, senderUserName, senderAvatar, content, time, true);
                                } else {
                                    // 他发给我的
                                    Chat.addTargetMsg(oId, senderUserName, senderAvatar, content, time, true);
                                }
                            });
                        }
                    }
                });
            }
            Chat.moreLock = false;
        }
    },

    send() {
        let content = Chat.editor.getValue();
        if (content.length > 1024) {
            $('#chatContentTip').
            addClass('error').
            html('<ul><li>发送失败：超过 1024 字符，请修改后重试。</li></ul>');
        } else {
            Chat.ws.send(content);
            Chat.editor.setValue('');
        }
    },

    addToMessageList(userName, avatarURL, preview) {
        let dot = preview.length > 10 ? "..." : "";
        $("#chatMessageList").append('' +
            '<div class="module-panel" id="chatTo' + userName + '" style="padding: 10px 15px;cursor: pointer" onclick="Chat.init(\'' + userName + '\')"\n' +
            '    <nav class="home-menu">\n' +
            '        <div class="avatar"\n' +
            '             style="display: inline-block; background-image:url(' + avatarURL + ')">\n' +
            '        </div>\n' +
            '        <div style="display: inline-block; vertical-align: -12px;">\n' +
            '            ' + userName + '<br>\n' +
            '            <span style="color: #868888">' + preview.substring(0, 10) + dot + '</span>\n' +
            '        </div>\n' +
            '    </nav>\n' +
            '</div>');
    },

    addSelfMsg(oId, userName, avatarURL, content, time, reverse) {
        if (reverse === true) {
            $("#chats").append('' +
                '<div id="chat' + oId + '" class="fn__flex chats__item chats__item--me">\n' +
                '    <a href="' + Label.servePath + '/member/' + userName + '" style="height: 38px">\n' +
                '        <div class="avatar tooltipped__user" aria-label="' + userName + '"\n' +
                '             style="background-image: url(' + avatarURL + ');"></div>\n' +
                '    </a>\n' +
                '    <div class="chats__content">\n' +
                '        <div class="chats__arrow"></div>\n' +
                '        <div style="margin-top: 4px" class="vditor-reset ft__smaller ">\n' +
                '            ' + content + '\n' +
                '        </div>\n' +
                '        <div class="ft__smaller ft__fade fn__right date-bar">\n' +
                '            ' + time + '\n' +
                '        </div>\n' +
                '    </div>\n' +
                '</div>');
        } else {
            $("#chats").prepend('' +
                '<div id="chat' + oId + '" class="fn__flex chats__item chats__item--me">\n' +
                '    <a href="' + Label.servePath + '/member/' + userName + '" style="height: 38px">\n' +
                '        <div class="avatar tooltipped__user" aria-label="' + userName + '"\n' +
                '             style="background-image: url(' + avatarURL + ');"></div>\n' +
                '    </a>\n' +
                '    <div class="chats__content">\n' +
                '        <div class="chats__arrow"></div>\n' +
                '        <div style="margin-top: 4px" class="vditor-reset ft__smaller ">\n' +
                '            ' + content + '\n' +
                '        </div>\n' +
                '        <div class="ft__smaller ft__fade fn__right date-bar">\n' +
                '            ' + time + '\n' +
                '        </div>\n' +
                '    </div>\n' +
                '</div>');
        }
        Util.listenUserCard();
    },

    addTargetMsg(oId, userName, avatarURL, content, time, reverse) {
        if (reverse === true) {
            $("#chats").append('' +
                '<div id="chat' + oId + '" class="fn__flex chats__item">\n' +
                '    <a href="' + Label.servePath + '/member/' + userName + '" style="height: 38px">\n' +
                '        <div class="avatar tooltipped__user" aria-label="' + userName + '"\n' +
                '             style="background-image: url(' + avatarURL + ');"></div>\n' +
                '    </a>\n' +
                '    <div class="chats__content">\n' +
                '        <div class="chats__arrow"></div>\n' +
                '        <div style="margin-top: 4px" class="vditor-reset ft__smaller ">\n' +
                '            ' + content + '\n' +
                '        </div>\n' +
                '        <div class="ft__smaller ft__fade fn__right date-bar">\n' +
                '            ' + time + '\n' +
                '        </div>\n' +
                '    </div>\n' +
                '</div>');
        } else {
            $("#chats").prepend('' +
                '<div id="chat' + oId + '" class="fn__flex chats__item">\n' +
                '    <a href="' + Label.servePath + '/member/' + userName + '" style="height: 38px">\n' +
                '        <div class="avatar tooltipped__user" aria-label="' + userName + '"\n' +
                '             style="background-image: url(' + avatarURL + ');"></div>\n' +
                '    </a>\n' +
                '    <div class="chats__content">\n' +
                '        <div class="chats__arrow"></div>\n' +
                '        <div style="margin-top: 4px" class="vditor-reset ft__smaller ">\n' +
                '            ' + content + '\n' +
                '        </div>\n' +
                '        <div class="ft__smaller ft__fade fn__right date-bar">\n' +
                '            ' + time + '\n' +
                '        </div>\n' +
                '    </div>\n' +
                '</div>');
        }
        Util.listenUserCard();
    }
}

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return "";
}

$(document).ready(function () {
    Chat.init();
    // 监听按钮
    $("#sendChatBtn").on('click', function () {
        Chat.send();
    });
});
