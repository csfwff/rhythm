var IdleTalk = {
    editor: undefined,

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
                    alert(result.msg);
                } else {
                    alert("已成功交给送信人，请静候回信！")
                    window.location.reload();
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
            counter: 4096,
            placeholder: '信柬内容（最长20480个字）',
            ctrlEnter: function () {
                Comment.add(Label.articleOId, Label.csrfToken,
                    document.getElementById('articleCommentBtn'))
            },
            esc: function () {
                $('.editor-hide').click()
            },
        })
    },

    getContent: function () {
        return IdleTalk.editor.getValue()
    }
}

$(document).ready(function () {
    IdleTalk.init();
});
