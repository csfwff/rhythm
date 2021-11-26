;(function (window) {
    'use strict';

    function EventWrapper() {
        this.eventHandlers = {};
    }

    EventWrapper.prototype.addEventListener = function(node, event, handler, capture) {
        if (typeof capture === 'undefined') capture = false;

        if (!(event in this.eventHandlers)) {
            this.eventHandlers[event] = []
        }

        this.eventHandlers[event].push({ node: node, handler: handler, capture: capture })
        node.addEventListener(event, handler, capture);
    };

    EventWrapper.prototype.removeEventListener = function(targetNode, event) {
        this.eventHandlers[event]
            .filter(obj => obj.node === targetNode)
            .forEach(obj => obj.node.removeEventListener(event, obj.handler, obj.capture));

        this.eventHandlers[event] = this.eventHandlers[event].filter(obj => obj.node !== targetNode);
    };

    window.EventWrapper = EventWrapper;
} (this));
