;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes) {
    'use strict';

    function GameWatchSuccessfulEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_WATCH_SUCCESSFUL;
    }

    Utils.extend(GameWatchSuccessfulEventHandler, Handler);

    var tips = "                                                 \n" +
        "+------------------------------------------------\n" +
        "| 你已经进入观战模式。      \n"         +
        "| 房主：{}. 当前房间状态：{}.\n"         +
        "+------------------------------------------------\n" +
        "(输入 exit 或 e 退出观战模式)                  \n" +
        "                                                   ";

    GameWatchSuccessfulEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        client.setWatching(true);

        var obj = JSON.parse(clientTransferData.data);
        panel.append(Utils.format(tips, obj.owner, obj.status));

        panel.waitInput()
            .then(s => inputResolve(client, panel, s));
    };

    function inputResolve(client, panel, s) {
        s = s.toLowerCase();

        if (s == "exit" || s == "e") {
            panel.append("Spectating ended. Bye.");
            panel.append("");
            panel.append("");

            client.setWatching(false);

            client.send(ServerEventCodes.CODE_GAME_WATCH_EXIT, null, null);
            client.dispatch({code: ClientEventCodes.CODE_SHOW_OPTIONS, data: null, info: null});
        } else {
            panel.waitInput()
                .then(s1 => inputResolve(client, panel, s1));
        }
    }

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GameWatchSuccessfulEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes));
