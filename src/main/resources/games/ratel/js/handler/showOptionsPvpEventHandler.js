;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ShowOptionsPvpEventHandler() {
        this.code = ClientEventCodes.CODE_SHOW_OPTIONS_PVP;
    }

    Utils.extend(ShowOptionsPvpEventHandler, Handler);

    var tips = "真人匹配: \n" +
            "1. 创建房间\n" +
            "2. 房间列表\n" +
            "3. 加入房间\n" +
            "4. 旁观模式\n" +
            "请选择其中一个选项 (输入 back 或 b 返回列表)";

    ShowOptionsPvpEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append(tips);
        panel.waitInput()
            .then(s => optionChooseResolve(client, panel, s));
    };

    function optionChooseResolve(client, panel, s) {
        var i = s.toLowerCase();

        if (i == "back" || i == "b") {
            client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, data: null, info: null});
            return;
        }

        try {
            i = parseInt(s);
            if (Number.isNaN(i)) {
                throw new Error(s + " is not a number.")
            }
        } catch (e) {
            panel.append("输入错误，请重新输入：");
            panel.waitInput().then((s) => optionChooseResolve(client, panel, s));
        }

        if (i == 1) {
            client.send(ServerEventCodes.CODE_ROOM_CREATE, s);
        } else if (i == 2) {
            client.send(ServerEventCodes.CODE_GET_ROOMS, s);
        } else if (i == 3) {
            panel.append("请输入你想加入游戏的房间号 (输入 back 或 b 返回列表)");
            panel.waitInput()
                .then(str => joinRoomResolve(client, panel, str));
        } else if (i == 4) {
            panel.append("请输入你想旁观的房间号 (输入 back 返回列表)");
            panel.waitInput()
                .then(str => watchResolve(client, panel, str));
        }
    }

    function joinRoomResolve(client, panel, s) {
        try {
            var j = parseInt(s);
            if (Number.isNaN(j)) {
                throw new Error(s + " is not a number.")
            }
        } catch (ex) {
            panel.append("输入错误，请重新输入：");
            panel.waitInput().then((s) => joinRoomResolve(client, panel, s));
        }

        if (j < 1) {
            panel.append("输入错误，请重新输入：");
            panel.waitInput().then((s) => joinRoomResolve(client, panel, s));
        } else {
            client.send(ServerEventCodes.CODE_ROOM_JOIN, s);
        }
    }

    function watchResolve(client, panel, s) {
        try {
            var i = parseInt(s);
            if (Number.isNaN(i)) {
                throw new Error(s + " is not a number.")
            }
        } catch (e) {
            panel.append("输入错误，请重新输入：");
            panel.waitInput().then((s) => watchResolve(client, panel, s));
        }

        if (i < 1) {
            panel.append("输入错误，请重新输入：");
            panel.waitInput().then((s) => watchResolve(client, panel, s));
        } else {
            client.send(ServerEventCodes.CODE_GAME_WATCH, s);
        }
    }

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ShowOptionsPvpEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
