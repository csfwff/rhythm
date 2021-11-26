;(function(window, Utils, Handler, ClientEventCodes, ServerEventCodes, Poker) {
    'use strict';

    function GamePokerPlayEventHandler() {
        this.code = ClientEventCodes.CODE_GAME_POKER_PLAY;
    }

    Utils.extend(GamePokerPlayEventHandler, Handler);

    GamePokerPlayEventHandler.prototype.handle = function(client, panel, clientTransferData) {
        var obj = JSON.parse(clientTransferData.data);

        panel.append("It's your turn to play, your cards are as follows: ");
        panel.append(Poker.toString(obj.pokers));
        panel.append("Please enter the combination you came up with (enter [exit|e] to exit current room, enter [pass|p] to jump current round, enter [view|v] to show all valid combinations.)");

        panel.waitInput()
            .then(s => inputResolve(client, panel, obj, s));
    };

    function inputResolve(client, panel, obj, s) {
        if (Utils.isEmpty(s)) {
            panel.waitInput()
                .then(s => inputResolve(client, panel, obj, s));
        } else {
            s = s.toLowerCase();
            // 跳过
            if (s == "pass" || s == "p") {
                client.send(ServerEventCodes.CODE_GAME_POKER_PLAY_PASS, null, null);
            }
            // 退出游戏
            else if (s == "exit" || s == "e") {
                client.send(ServerEventCodes.CODE_CLIENT_EXIT, null, null);
            }
            // 出牌推荐
            else if (s == "view" || s == "v") {
                panel.append("Unsupport this feature.");
                panel.waitInput()
                    .then(s => inputResolve(client, panel, obj, s));
                return;
            }
            // 出牌
            else {
                var splits = s.split("");
                var pokerAliases = [];
                var access = true;

                for (var str of splits) {
                    if (str != "    " || str != "\t") {
                        if (!Poker.isVaildAlias(str)) {
                            access = false;
                            break;
                        }

                        pokerAliases.push(str);
                    }
                }

                if (access) {
                    client.send(ServerEventCodes.CODE_GAME_POKER_PLAY, JSON.stringify(pokerAliases), null);
                } else {
                    panel.append("Invalid enter");

                    if (client.getLastPokers() != null) {
                        panel.append(client.getLastSellClientNickname() + "[" + client.getLastSellClientType() + "] played: ");
                        panel.append(Poker.toString(client.getLastPokers()));
                    }

                    panel.waitInput()
                        .then(s => inputResolve(client, panel, obj, s));
                }
            }
        }
    }

    if (!window._handlers_) {
        window._handlers_ = [];
    }
    window._handlers_.push(new GamePokerPlayEventHandler());
} (this, this.Utils, this.Handler, this.ClientEventCodes, this.ServerEventCodes, this.Poker));
