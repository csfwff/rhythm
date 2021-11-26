;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function GameLandlordCycleEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_LANDLORD_CYCLE;
    }

    Utils.extend(GameLandlordCycleEventHandler, Handler);

    GameLandlordCycleEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append("没人抢地主，重新发牌...");
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GameLandlordCycleEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
