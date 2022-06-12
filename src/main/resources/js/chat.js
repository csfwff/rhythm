var Chat = {
    init: function () {
        // 获取消息列表


        let toUser = getQueryVariable("toUser");
        if (toUser === "") {
            // 未选定用户
            $("#chatStatus").html('请在左侧列表选择最近聊天的成员，或直接发起聊天。');
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
            // 监听按钮
            $("#sendChatBtn").on('click', function () {
                Chat.send();
            });
            // 加载最近聊天列表

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
                    let user_session = data.user_session;
                    let senderUserName = data.senderUserName;
                    let senderAvatar = data.senderAvatar;
                    let receiverUserName = data.receiverUserName;
                    let receiverAvatar = data.receiverAvatar;
                    if (senderUserName === Label.currentUserName) {
                        // 我发送的
                        Chat.addSelfMsg(oId, senderUserName, senderAvatar, content, time);
                    } else {
                        // 他发给我的
                        Chat.addTargetMsg(oId, senderUserName, senderAvatar, content, time);
                    }
                }
            }
            Chat.ws.onclose = function () {
                console.log("Disconnected to chat channel websocket.")
                setInterval(function () {
                    $.ajax({
                        url: Label.servePath + "/shop",
                        method: "get",
                        success: function() {
                            location.reload();
                        }
                    })
                }, 10000);
            }
            Chat.ws.onerror = function (err) {
                console.log('ERROR', err)
                setInterval(function () {
                    $.ajax({
                        url: Label.servePath + "/shop",
                        method: "get",
                        success: function() {
                            location.reload();
                        }
                    })
                }, 10000);
            }
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

    loadMessageList() {
        $("#chatMessageList").append('' +
            '<div class="module-panel" style="padding: 10px 15px">\n' +
            '    <nav class="home-menu">\n' +
            '        <div class="avatar"\n' +
            '             style="display: inline-block; background-image:url(https://pwl.stackoverflow.wiki/2021/09/1552202503861_1562141298909-67b099fd.jpeg)">\n' +
            '        </div>\n' +
            '        <div style="display: inline-block; vertical-align: -12px;">\n' +
            '            adlered<br>\n' +
            '            <span style="color: #868888">最近一条消息</span>\n' +
            '        </div>\n' +
            '    </nav>\n' +
            '</div>');
    },

    addSelfMsg(oId, userName, avatarURL, content, time) {
        $("#chats").prepend('' +
            '<div id="chat' + oId + '" class="fn__flex chats__item chats__item--me">\n' +
            '    <a href="' + Label.servePath + '/member/' + userName + '" style="height: 38px">\n' +
            '        <div class="avatar tooltipped__user" aria-label="' + userName + '"\n' +
            '             style="background-image: url(' + avatarURL + ');"></div>\n' +
            '    </a>\n' +
            '    <div class="chats__content">\n' +
            '        <div class="chats__arrow"></div>\n' +
            '        <div style="margin-top: 4px" class="vditor-reset ft__smaller ">\n' +
            '            <p>' + content + '</p>\n' +
            '        </div>\n' +
            '        <div class="ft__smaller ft__fade fn__right date-bar">\n' +
            '            ' + time + '\n' +
            '        </div>\n' +
            '    </div>\n' +
            '</div>');
        Util.listenUserCard();
    },

    addTargetMsg(oId, userName, avatarURL, content, time) {
        $("#chats").prepend('' +
            '<div id="chat' + oId + '" class="fn__flex chats__item">\n' +
            '    <a href="' + Label.servePath + '/member/' + userName + '" style="height: 38px">\n' +
            '        <div class="avatar tooltipped__user" aria-label="' + userName + '"\n' +
            '             style="background-image: url(' + avatarURL + ');"></div>\n' +
            '    </a>\n' +
            '    <div class="chats__content">\n' +
            '        <div class="chats__arrow"></div>\n' +
            '        <div style="margin-top: 4px" class="vditor-reset ft__smaller ">\n' +
            '            <p>' + content + '</p>\n' +
            '        </div>\n' +
            '        <div class="ft__smaller ft__fade fn__right date-bar">\n' +
            '            ' + time + '\n' +
            '        </div>\n' +
            '    </div>\n' +
            '</div>');
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
});
