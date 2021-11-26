;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes, Poker) {
    'use strict';

    function ShowPokersEventHandler() {
        this.code = ClientEventCodes.CODE_SHOW_POKERS;
    }

    Utils.extend(ShowPokersEventHandler, Handler);

    ShowPokersEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);

        client.setLastPokers(obj.pokers);
        client.setLastSellClientType(obj.clientType);
        client.setLastSellClientNickname(obj.clientNickname);

        panel.append(obj.clientNickname + "[" + obj.clientType + "] played:");
        panel.append(Poker.toString(obj.pokers));

        if ("sellClinetNickname" in obj) {
            panel.append("Next player is " + obj.sellClinetNickname + ". Please wait for him to play his combination.");
        }
    };

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new ShowPokersEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes, this.Poker));
