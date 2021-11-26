;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function PveDifficultyNotSupportEventHandler() {
        this.code = null;
    }

    Utils.extend(PveDifficultyNotSupportEventHandler, Handler);

    PveDifficultyNotSupportEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        console.log(clientTransferData.code, clientTransferData.data, clientTransferData.info);
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new PveDifficultyNotSupportEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
