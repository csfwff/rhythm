;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function GameOverEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_OVER;
    }

    Utils.extend(GameOverEventHandler, Handler);

    GameOverEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);

        panel.append(Utils.format("\nPlayer {}[{}] won the game", obj.winnerNickname, obj.winnerType));
        panel.append("Game over, friendship first, competition second\n");
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GameOverEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
