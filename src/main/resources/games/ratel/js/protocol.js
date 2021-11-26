;(function(window, Utils, protobuf) {
    'use strict';

    function Protocol() {
    }

    /**
     * 初始化方法
     * 不要在构造函数中初始化，初始化可能是异步的，需要返回Promise确保初始化完毕
     *
     * @returns {Promise<unknown>}
     */
    Protocol.prototype.init = function() {
        return new Promise((resolve, reject) => {
            resolve();
        });
    };
    Protocol.prototype.decode = function (s) {
        return new Promise((resolve, reject) => {
            resolve(s);
        });
    };
    Protocol.prototype.encode = function (o) {
        return new Promise((resolve, reject) => {
            resolve(o);
        });
    };

    // -------------------------------------------------------------

    function JsonProtocol() {}

    Utils.extend(Protocol, JsonProtocol);

    JsonProtocol.prototype.init = function() {
        if (!("JSON" in window)) {
            throw new Error("Browser version is too low, please upgrade the version.");
        }

        return Protocol.prototype.init.call(this);
    };

    JsonProtocol.prototype.decode = function(s) {
        return new Promise((resolve, reject) => {
            try {
                resolve(JSON.parse(s));
            } catch(e) {
                reject(e);
            }
        });
    };

    JsonProtocol.prototype.encode = function(o) {
        return new Promise((resolve, reject) => {
            try {
                resolve(JSON.stringify(o));
            } catch(e) {
                reject(e);
            }
        });
    };

    // -------------------------------------------------------------

    function ProtobufProtocol() {
        this.protocs = {};
    }

    Utils.extend(Protocol, ProtobufProtocol);

    ProtobufProtocol.prototype.init = function() {
        var loadProtocCount = 0;

        return new Promise(resolve => {
            protobuf.load("./protoc/ServerTransferDataProtoc.proto", (e, root) => {
                if (e) throw e;

                this.protocs.ServerTransferDataProtoc = root.lookup("ServerTransferDataProtoc");

                if (++loadProtocCount == 2) resolve();
            });

            protobuf.load("./protoc/ClientTransferDataProtoc.proto", (e, root) => {
                if (e) throw e;

                this.protocs.ClientTransferDataProtoc = root.lookup("ClientTransferDataProtoc");

                if (++loadProtocCount == 2) resolve();
            });
        });
    };

    ProtobufProtocol.prototype.decode = function(s) {
        var reader = new FileReader();
        reader.readAsArrayBuffer(s);

        return new Promise((resolve, reject) => {
            reader.onload = (e) => {
                var decodeValue = this.protocs.ClientTransferDataProtoc.decode(new Uint8Array(reader.result));
                resolve(decodeValue);
            };
            reader.onerror = (e) => reject(e);
        });
    };

    ProtobufProtocol.prototype.encode = function(o) {
        return new Promise((resolve, reject) => {
            try {
                resolve(this.protocs.ServerTransferDataProtoc.encode(this.protocs.ServerTransferDataProtoc.create(o)).finish());
            } catch(e) {
                reject(e);
            }
        });
    };

    var defaultProtocol = "JSON";
    window.Protocol = defaultProtocol == "JSON" ? JsonProtocol : ProtobufProtocol;
} (this, this.Utils, protobuf));
