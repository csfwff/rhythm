;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ShowOptionsPveEventHandler() {
        this.code = ClientEventCodes.CODE_SHOW_OPTIONS_PVE;
    }

    Utils.extend(ShowOptionsPveEventHandler, Handler);

    var tips = "PVE: \n" +
            "1. Easy Mode \n" +
            "2. Medium Mode\n" +
            "3. Hard Mode\n" +
            "Please select an option above (enter [back|b] to return to options list)";

    ShowOptionsPveEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append(tips);
        panel.waitInput().then((s) => inputResolve(client, panel, s));
    };

    function inputResolve(client, panel, s) {
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
            panel.append("Invalid option, please choose again：");
            panel.waitInput().then((s) => inputResolve(client, panel, s));
        }

        if (i < 4 && i > 0) {
            client.send(ServerEventCodes.CODE_ROOM_CREATE_PVE, s);
        } else {
            panel.append("Invalid option, please choose again：");
            panel.waitInput().then((s) => inputResolve(client, panel, s));
        }
    }

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ShowOptionsPveEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
