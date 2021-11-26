;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function ClientNickNameSetEventHandler() {
        this.code = ClientEventCodes.CODE_CLIENT_NICKNAME_SET;
    }

    Utils.extend(ClientNickNameSetEventHandler, Handler);

    var NICKNAME_MAX_LENGTH = 10;
    ClientNickNameSetEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        client.setUserName(Label.currentUserName);
        window.imClient.setNickname(Label.currentUserName)
        client.send(ServerEventCodes.CODE_CLIENT_NICKNAME_SET, Label.currentUserName);
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ClientNickNameSetEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
