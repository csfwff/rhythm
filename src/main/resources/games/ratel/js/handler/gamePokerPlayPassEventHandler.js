;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function GamePokerPlayPassEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_POKER_PLAY_PASS;
    }

    Utils.extend(GamePokerPlayPassEventHandler, Handler);

    GamePokerPlayPassEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);

        panel.append(Utils.format("{} 不要，由 {} 出牌。", obj.clientNickname, obj.nextClientNickname));
        if(obj.nextClientId == client.getClientId()) {
            client.send(ServerEventCodes.CODE_GAME_POKER_PLAY_REDIRECT, null, null);
        }
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GamePokerPlayPassEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
