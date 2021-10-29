import { global } from './vars';
import { messageQueue } from './functions.js';

PlayFab.settings.titleId = 'CE351';
export var playFabId = -1;

export var playFabStats = {
    isLogin : false,
    loginName: '',
    loginStat : '未登录',
    lastSaveTime: '',
    playFabSaveTime: ''
};


window.autoSaveToPlayFab = function () {
    var saveString = LZString.compressToBase64(JSON.stringify(global));
    saveToPlayFab(saveString);
}
setInterval(autoSaveToPlayFab, 30 * 60 * 1000);

window.loginPlayFab =  function(username, pass) {
    var error = $("#playfab-error");
    var saveLogin = true;
    if (!username || !pass) {
        var nameElem = $("#playfab-username");
        var passElem = $("#playfab-password");
            
        if (nameElem == null || passElem == null) {
            error.html("error");
            return;
        }
        else {
            username = nameElem.val();
            pass = passElem.val();
        }
    }
    var requestData = {
        Username: username,
        Password: pass
    }
    try {
        storeTempPlayFabInfo(username, pass);
        PlayFab.ClientApi.LoginWithPlayFab(requestData, playFabLoginCallback).catch(function (error) { 
            var err = Object.values(error.errorDetails).join('<br>');
            $('#playfab-error').html(err);
        });
    }
    catch (e) {
        console.log(e);
        error.html("连接不上PlayFab服务");
    }
}
window.registerPlayFabUser = function () {
    var error = $("#playfab-reg-error");

    var nameElem = $("#playfab-reg-username");
    var passElem = $("#playfab-reg-password");
    var confirmPasswordElem = $("#playfab-reg-confirm-password");
    if (nameElem == null || passElem == null || confirmPasswordElem == null) {
        //Elements required to register are missing, rebuild login screen
        error.html('请填写完整');
        return;
    }
    if (confirmPasswordElem.val() != passElem.val()) {
        error.html("两次密码不一致");
        return;
    }
    var requestData = {
        Username: nameElem.val(),
        Password: passElem.val(),
        RequireBothUsernameAndEmail: false
    }
    storeTempPlayFabInfo(nameElem.val(), passElem.val());
    PlayFab.ClientApi.RegisterPlayFabUser(requestData, playFabLoginCallback).catch(function (error) { 
        var err = Object.values(error.errorDetails).join('<br>');
        $('#playfab-reg-error').html(err);
    });
}


export function tryPlayFabAutoLogin() {
    var type = global.settings.onlineSave;
    //-1 = not set, 1 = Kongregate, 2 = PlayFab
    if (type == false) {
        return false;
    }
    var info = readPlayFabInfo();
    if (!info) return false;
    loginPlayFab(info[0], info[1]);
    return true;
}

function playFabLoginCallback(data, error) {
    if (error) {
        var errorElem = $("#playfab-error");
        if (errorElem != null && error.errorMessage) {
            errorElem.html(error.errorMessage);
        }
        return;
    }
    if (data) {
        playFabStats.isLogin = true;
        var tempuser = readTempPlayFabInfo();
        storePlayFabInfo(tempuser[0], tempuser[1]);
        playFabStats.loginName = tempuser[0];
        playFabStats.loginStat = '已登录(' + playFabStats.loginName + ')';
        playFabId = data.data.PlayFabId;
        $('.login-content input').val('');
        playFabSaveCheck();
    }
}

function cancelPlayFab() {
    playFabId = -1;
}

function playFabSaveCheck() {
    if (playFabId == -1) return false;
    var requestData = {
        Keys: ["gameTotalDays"],
        PlayFabId: playFabId
    }
    try {
        PlayFab.ClientApi.GetUserData(requestData, playFabSaveCheckCallback);
    }
    catch (e) { console.log(e); }
}

function playFabSaveCheckCallback(data, error) {
    if (error) {
        console.log("error checking existing PlayFab data");
        console.log(error);
        return;
    }
    if (data) {
        var gameTotalDays = (data.data.Data.gameTotalDays) ? parseInt(data.data.Data.gameTotalDays.Value) : 0;
        var saveString = data.data.Data.saveString ? data.data.Data.saveString : null;
        if (saveString != null) {
            playFabStats.playFabSaveTime = moment(saveString.LastUpdated).format("YYYY-MM-DD HH:mm:ss");
        }
        playFabFinishLogin(true);
    }
}

function playFabFinishLogin(downloadFirst) {
    if (downloadFirst) {
        //交由用户手动load
        loadFromPlayFab();
        return;
    }
}

function saveToPlayFab(saveString) {
    if (global.beta) return;
    if (!global.settings.onlineSave) return;
    if (!playFabId || typeof PlayFab === 'undefined' || typeof PlayFab.ClientApi === 'undefined') return false;
    var requestData = {
        Data: {
            saveString: saveString,
            gameTotalDays: global.stats.days + global.stats.tdays,
        }
    }
    try {
        PlayFab.ClientApi.UpdateUserData(requestData, saveToPlayFabCallback);
    }
    catch (e) { console.log(e); }
}

var playFabSaveErrors = 0;

function saveToPlayFabCallback(data, error) {
    if (error) {
        playFabSaveErrors++;
        messageQueue("连不上云存档了，检查一下你的网络链接，最好备份一下存档噢");
        console.log(error);
        if (playFabId != -1) {
            playFabAttemptReconnect();
        }
        return false;
    }
    if (data) {
        messageQueue("成功备份到云存档，下一次备份将在30分钟后",'info');
        playFabStats.lastSaveTime = moment().format("YYYY-MM-DD HH:mm:ss");
        playFabStats.playFabSaveTime = moment().format("YYYY-MM-DD HH:mm:ss");
        return true;
    }
}

function playFabAttemptReconnect(reconnected) {
    console.log((reconnected) ? "Reconnected" : "Attempting to reconnect");
    if (reconnected) {
        playFabSaveErrors = 0;
        return;
    }
    tryPlayFabAutoLogin();
}

window.importFromPlayFab = function () {
    importFromPlayFab();
}
window.syncNow = function () {
    autoSaveToPlayFab();
}

function importFromPlayFab() {
    if (!playFabId || typeof PlayFab === 'undefined' || typeof PlayFab.ClientApi === 'undefined') return false;
    var requestData = {
        Keys: ["saveString"],
        PlayFabId: playFabId
    }
    try {
        PlayFab.ClientApi.GetUserData(requestData, function (data,error) {
            if (error) {
                console.log(error);
                return;
            }
            if (data) {
                var saveString = data.data.Data.saveString ? data.data.Data.saveString : null;
                if (saveString != null) {
                    playFabStats.playFabSaveTime = moment(saveString.LastUpdated).format("YYYY-MM-DD HH:mm:ss");
                    if (!importGame(saveString.Value)) {
                        playFabId = -1;
                        $('#playfab-error').html('加载存档错误');
                        return;
                    }
                }
            }
        });
    }
    catch (e) { console.log(e); }

}

function loadFromPlayFab() {
    if (!playFabId || typeof PlayFab === 'undefined' || typeof PlayFab.ClientApi === 'undefined') return false;
    var requestData = {
        Keys: ["saveString"],
        PlayFabId: playFabId
    }
    try {
        PlayFab.ClientApi.GetUserData(requestData, loadFromPlayFabCallback);
    }
    catch (e) { console.log(e); }
}

function loadFromPlayFabCallback(data, error) {
    if (error) {
        console.log(error);
        return;
    }
    if (data) {
        var saveString = data.data.Data.saveString ? data.data.Data.saveString : null;
        if (saveString != null) {
            playFabStats.playFabSaveTime = moment(saveString.LastUpdated).format("YYYY-MM-DD HH:mm:ss");
        }
    }
}

function storePlayFabInfo(name, pass) {
    try {
        localStorage.setItem("playFabName", name);
        localStorage.setItem("playFabPass", pass);
    }
    catch (e) { console.log(e) }
    return false;
}
function storeTempPlayFabInfo(name, pass) {
    try {
        localStorage.setItem("playFabNameTemp", name);
        localStorage.setItem("playFabPassTemp", pass);
    }
    catch (e) { console.log(e) }
    return false;
}

function readPlayFabInfo() {
    var info = [false, false];
    try {
        info[0] = localStorage.getItem("playFabName");
        info[1] = localStorage.getItem("playFabPass");
    }
    catch (e) { console.log(e) }
    if (info[0] && info[1]) return info;
    return false;
}
function readTempPlayFabInfo() {
    var info = [false, false];
    try {
        info[0] = localStorage.getItem("playFabNameTemp");
        info[1] = localStorage.getItem("playFabPassTemp");
    }
    catch (e) { console.log(e) }
    if (info[0] && info[1]) return info;
    return false;
}
