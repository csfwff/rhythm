;(function(window) {
	'use strict';

	function Logger(handlers) {
		this.handlerChain = [];
		this.handlerChain.push(Logger.defaultHandler);

		if (Array.isArray(handlers) && handlers.length > 0) {
			handlers.forEach(handler => this.handlerChain.push(handler));
		}
	}

	Logger.prototype.println = function(level, msg) {
		this.handlerChain.forEach(handler => {
			try {
				handler.call(null, level, msg);
			} catch (e) {
				console.warn("logger handle error", e);
			}
		});
	};

	Logger.prototype.debug = function() {
		this.println(Logger.LEVEL.DEBUG, arguments);
	};

	Logger.prototype.info = function() {
		this.println(Logger.LEVEL.INFO, arguments);
	};

	Logger.prototype.warn = function() {
		this.println(Logger.LEVEL.WARN, arguments);
	};

	Logger.prototype.error = function() {
		this.println(Logger.LEVEL.ERROR, arguments);
	};

	Logger.LEVEL = {
		DEBUG: "DEBUG",
		INFO: "INFO",
		WARN: "WARN",
		ERROR: "ERROR"
	};

	Logger.defaultHandler = function(level, args) {
		var msg = format.apply(null, args);

		if (level == 'DEBUG') console.debug.call(null, msg);
		if (level == 'INFO') console.info.call(null, msg);
		if (level == 'WARN') console.warn.call(null, msg);
		if (level == 'ERROR') {
			console.error.call(null, msg);

			for (var i in args) {
				if (args[i] instanceof Error) {
					console.error(args[i]);
				}
			}
		}
	};

	function format() {
		var args = Array.prototype.slice.call(arguments, 1);
		var i = 0;

		return arguments[0].replace(/\{\}/g, function(match, number) {
			var arg = args[i++];
			var replaceStr = typeof arg === 'object' ? JSON.stringify(arg) : arg;
			return typeof replaceStr != 'undefined' ? replaceStr : match;
		});
	}

	// ----------------------------------------------------------------

	function HandlerLoader() {
		this.handlers = [];
	}

	HandlerLoader.prototype.load = function(handlerPaths) {
		if (Array.isArray(handlerPaths)) {
			var shouldLoadScriptCount = handlerPaths.length;

			return new Promise(((resolve, reject) => {
				handlerPaths.forEach(handlerPath => {
					asyncLoadScript(handlerPath)
						.then(() => {
							if(--shouldLoadScriptCount == 0) resolve();
						})
						.catch((e) => {
							if(--shouldLoadScriptCount == 0) resolve();
							console.error("Error load script", e);
						});
				});
			}))
		}
	};

	HandlerLoader.prototype.getHandlers = function() {
		this.handlers = window._handlers_;
		delete window._handlers_;

		return this.handlers;
	}

	function asyncLoadScript(url) {
	    var script = document.createElement("script");
	    script.type = "text/javascript";
	    script.src = url;

	    return new Promise((resolve, reject) => {
			try {
				document.body.appendChild(script);
				script.onload = () => resolve();
				script.onerror = (e) => reject(e.target.outerHTML);
			} catch (e) {
				reject(e);
			}
	    });
	}

	// ----------------------------------------------------------------

	function extend(sub, sup) {
		function F() {}

		F.prototype = sup.prototype;
		var subProto = sub.prototype;
		sub.prototype = new F();

		Object.assign(sub.prototype, subProto);

		sub.prototype.constructor = sub;
		sub.prototype.sup = sup.prototype;

		if (sup.prototype.constructor !== sup) {
			sup.prototype.constructor = sup;
		}
	}

	function isEmpty(str) {
		if (typeof str === 'undefined' || str == null) {
			return true;
		}

		if (typeof str === 'string') {
			return str == "" ? true : str.trim() == "";
		}
	}

	var timeoutPromise = (timeoutMillis) => {
		return new Promise((resolve, reject) => {
			var num = setTimeout(() => {
				clearTimeout(num);
				reject("Timeout");
			}, timeoutMillis);
		})
	};

	function timeout(asyncFn, millis) {
		return Promise.race([asyncFn, timeoutPromise(millis)]);
	}

	var Utils = {
		"Logger": Logger,
		"HandlerLoader": HandlerLoader,
		"extend": extend,
		"isEmpty": isEmpty,
		"format": format,
		"timeout": timeout
	};

	window.Utils = Utils;
} (this));
