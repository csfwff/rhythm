;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes, Poker) {
    'use strict';

    function GameStartingEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_STARTING;
    }

    Utils.extend(GameStartingEventHandler, Handler);

    GameStartingEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        panel.append("Game starting!");
        panel.append("");
        panel.append("Your cards are");
        var obj = JSON.parse(clientTransferData.data);
        panel.append(Poker.toString(obj.pokers));

        window.imClient.ratelRoomId = obj.roomId + ''
        window.imClient.roomList()
        client.dispatch({code: ClientEventCodes.CODE_GAME_LANDLORD_ELECT, data: clientTransferData.data, info: null});
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GameStartingEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes, this.Poker));
