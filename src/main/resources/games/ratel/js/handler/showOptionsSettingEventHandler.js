;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ShowOptionsSettingEventHandler() {
        this.code = ClientEventCodes.CODE_SHOW_OPTIONS_SETTING;
    }

    Utils.extend(ShowOptionsSettingEventHandler, Handler);

    var tips = "设置: \n" +
            "1. 带形状边缘的卡片 (默认)\n" +
            "2. 圆角卡片\n" +
            "3. 仅带牌型的文本卡片\n" +
            "4. 纯文本卡片\n" +
            "5. Unicode 卡片\n" +
            "请选择其中一个选项 (输入 back 返回列表)";

    ShowOptionsSettingEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append(tips);
        panel.waitInput()
            .then(s => inputResolve(client, panel, s));
    };

    function inputResolve(client, panel, s) {
        var i = s.toLowerCase();

        if (i == "back") {
            client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, data: null, info: null});
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

        if (i < 1 || i > 5) {
            panel.append("Invalid setting, please choose again：");
            panel.waitInput().then((s) => inputResolve(client, panel, s));
        } else {
            window.pockerStyle = i
            // TODO 设置牌形状
            client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, data: null, info: null});
        }
    }

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ShowOptionsSettingEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
