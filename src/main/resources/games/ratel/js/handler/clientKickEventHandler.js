;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ClientKickEventHandler() {
        this.code = ClientEventCodes.CODE_CLIENT_KICK;
    }

    Utils.extend(ClientKickEventHandler, Handler);

    ClientKickEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append("You have been kicked from the room for being idle.\n");
        client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, data: null, info: null});
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ClientKickEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
