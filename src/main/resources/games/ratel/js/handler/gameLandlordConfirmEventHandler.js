;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes, Poker) {
    'use strict';

    function GameLandlordConfirmEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_LANDLORD_CONFIRM;
    }

    Utils.extend(GameLandlordConfirmEventHandler, Handler);

    GameLandlordConfirmEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);

        panel.append(obj.landlordNickname + " has become the landlord and gotten three extra cards");
        panel.append(Poker.toString(obj.additionalPokers));

        client.send(ServerEventCodes.CODE_GAME_POKER_PLAY_REDIRECT, null, null);
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GameLandlordConfirmEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes, this.Poker));
