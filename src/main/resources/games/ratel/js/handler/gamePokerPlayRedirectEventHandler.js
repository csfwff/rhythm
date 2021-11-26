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
                    panel.append(Utils.format("{} {} 剩余 {} {}", clientInfo.position, clientInfo.clientNickname, clientInfo.surplus, clientInfo.type));
                }
            }
        }
        panel.append("");

        if (sellClientId == client.getClientId()) {
            client.dispatch({code: ClientEventCodes.CODE_GAME_POKER_PLAY, data: clientTransferData.data, info: null});
        } else {
            panel.append("轮到 " + obj.sellClientNickname + " 出牌...");
        }
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GamePokerPlayRedirectEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
