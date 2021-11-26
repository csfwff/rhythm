;(function(window, Utils, Handler, ClientEventCodes) {
    'use strict';

    function ShowOptionsEventHandler() {
        this.code = ClientEventCodes.CODE_SHOW_OPTIONS;
    }

    Utils.extend(ShowOptionsEventHandler, Handler);

    var tips = "游戏菜单 \n" +
                "1. 真人匹配\n" +
                "2. 人机\n" +
                "3. 设置\n" +
                "请输入选项编号 (输入 exit 或 e 退出服务器)";

    ShowOptionsEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        if (!Utils.isEmpty(clientTransferData.data)) {
            var obj = JSON.parse(clientTransferData.data);
            if ("clientId" in obj) {
                client.setClientId(obj.clientId);
            }
        }

        panel.append(tips);
        panel.waitInput().then((s) => inputResolve(client, panel, s));
    };

    function inputResolve(client, panel, s) {
        var i = s.toLowerCase();

        if (i == "exit" || i == "e") {
            client.close();
            return;
        }

        try {
            i = parseInt(s);
            if (Number.isNaN(i)) {
                throw new Error(s + " is not a number.")
            }
        } catch (e) {
            panel.append("Invalid option, please choose again：");
            panel.waitInput().then((s) => inputResolve(client, panel, s));
        }

        switch (i) {
            case 1:
                client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS_PVP, data: null, info: null});
                break;
            case 2:
                client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS_PVE, data: null, info: null});
                break;
            case 3:
                client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS_SETTING, data: null, info: null});
                break;
            default:
                panel.append("Invalid option, please choose again：");
                panel.waitInput().then((s) => inputResolve(client, panel, s));
        }
    }

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ShowOptionsEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes));
