;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ClientExitEventHandler() {
        this.code = ClientEventCodes.CODE_CLIENT_EXIT;
    }

    Utils.extend(ClientExitEventHandler, Handler);

    ClientExitEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);
        var role = obj.exitClientId == client.getClientId() ? "You" : obj.exitClientNickname;

        panel.append(role + " left the room. Room disbanded!\n");

        client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, data: null, info: null});
        window.imClient.leave()
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ClientExitEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
