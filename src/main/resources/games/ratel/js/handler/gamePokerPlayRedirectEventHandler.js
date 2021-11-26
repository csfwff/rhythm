;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function GamePokerPlayRedirectEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_POKER_PLAY_REDIRECT;
    }

    Utils.extend(GamePokerPlayRedirectEventHandler, Handler);

    GamePokerPlayRedirectEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);
        var sellClientId = obj.sellClientId;
        var clientInfos = obj.clientInfos;

        for (var i = 0; i < 2; i++) {
            for (var clientInfo of clientInfos) {
                var position = clientInfo.position.toUpperCase();
                if (position == (i ? "DOWN" : "UP")) {
                    panel.append(Utils.format("{} {}  surplus {} {}", clientInfo.position, clientInfo.clientNickname, clientInfo.surplus, clientInfo.type));
                }
            }
        }
        panel.append("");

        if (sellClientId == client.getClientId()) {
            client.dispatch({code: ClientEventCodes.CODE_GAME_POKER_PLAY, data: clientTransferData.data, info: null});
        } else {
            panel.append("It is " + obj.sellClinetNickname + "'s turn. Please wait for him to play his cards.");
        }
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GamePokerPlayRedirectEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
