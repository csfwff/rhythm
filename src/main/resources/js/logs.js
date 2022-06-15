var Logs = {

    init: function () {
        Logs.connectWS();
    },

    connectWS: function () {
        // 连接WS
        Logs.ws = new WebSocket(logsChannelURL);
        Logs.ws.onopen = function () {
            console.log("Connected to logs channel websocket.");
        }
        Logs.ws.onmessage = function (evt) {
            var data = JSON.parse(evt.data);
            switch (data.type) {
                case 'simple':
                    Logs.appendLog(data.key1, data.key2, data.key3, data.data);
                    break;
            }
        }
        Logs.ws.onclose = function () {
            console.log("Disconnected to logs channel websocket.");
        }
        Logs.ws.onerror = function (err) {
            console.log('ERROR', err);
        }
    },

    appendLog: function (key1, key2, key3, data) {
        let result = '<div>';
        result += '<span style=\'color="#696969"\'>' + key1 + '</span> ';
        result += '<span style=\'color="#708090"\'>' + key2 + '</span> ';
        result += '<span style=\'color="#6A5ACD"\'>' + key3 + '</span> ';
        result += '<span style=\'color="#1E90FF"\'>' + data + '</span> ';
        result += '</div>';
        $("#logsContent").prepend(result);
    }

}

$(document).ready(function () {
    Logs.init();
});
