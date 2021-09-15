var IdleTalk = {
    editor: undefined,

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
                        alert(result.msg);
                    } else {
                        $("#" + mapId).remove();
                    }
                }
            });
        }
    },

    seek: function (mapId) {
        $.ajax({
            url: Label.servePath + '/idle-talk/seek?mapId=' + mapId,
            type: 'GET',
            headers: {'csrfToken': Label.csrfToken},
            cache: false,
            success: function (result) {
                if (result.code === -1) {
                    alert(result.msg);
                } else {
                    $("#" + mapId).append("<div id='seek-" + mapId + "' style='background-color: #F7F7F7;display:none;'>" + result.data + "<br><button class='btn' style='margin: 20px' onclick='$(\"#" + mapId + "\").remove();'>朕已阅，可以销毁了</button></div>");
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
                    alert(result.msg);
                } else {
                    $("#userForm").val("");
                    $("#themeForm").val("");
                    IdleTalk.editor.setValue("");

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
            placeholder: '信柬内容（最长20480个字）'
        })

        let toUser = IdleTalk.getUrlPath("toUser");
        if (toUser !== false) {
            IdleTalk.expand();
            $("#userForm").val(toUser);
        }
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
    }
}

$(document).ready(function () {
    IdleTalk.init();
});
