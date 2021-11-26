;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ShowOptionsSettingEventHandler() {
        this.code = ClientEventCodes.CODE_SHOW_OPTIONS_SETTING;
    }

    Utils.extend(ShowOptionsSettingEventHandler, Handler);

    var tips = "Setting: \n" +
            "1. Card with shape edges (Default)\n" +
            "2. Card with rounded edges\n" +
            "3. Text Only with types\n" +
            "4. Text Only without types\n" +
            "5. Unicode Cards\n" +
            "Please select an option above (enter [BACK] to return to options list)";

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
