var Chat = {
    init: function () {
        let toUser = getQueryVariable("toUser");
        if (toUser === "") {
            // 未选定用户，获取消息列表
            $("#chatStatus").html('请在左侧列表选择最近聊天的成员，或直接发起聊天。');
        } else {
            // 已选定用户，获取消息列表并获取第一页聊天信息
            // 状态
            $("#chatStatus").html('和 ' +
                '<a href="' + Label.servePath + '/member/' + toUser + '">' + toUser + '</a> ' +
                '聊天中');
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
                ]
            })
            // 显示按钮
            $("#buttons").show();
            // 显示翻页
            $(".pagination__chat").show();
        }
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

    addTargetMsg() {
        $("#chats").prepend('' +
            '');
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
