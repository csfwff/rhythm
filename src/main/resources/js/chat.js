var Chat = {
    init: function () {
        let toUser = getQueryVariable("toUser");
        if (toUser === "") {
            // 未选定用户，获取消息列表

        } else {
            // 已选定用户，获取消息列表并获取第一页聊天信息

        }

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
