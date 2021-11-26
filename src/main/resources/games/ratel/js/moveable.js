;(function (window) {
    'use strict';

    function Moveable(dragEl, wrapperEl) {
        this.drageElement = dragEl;
        this.wrapperElement = wrapperEl;
        this.drageable = false;
        this.registerEvent();
    }

    Moveable.prototype.updateWrapperPosition = function(positionX, positionY) {
        this.wrapperElement.style.left = positionX + "px";
        this.wrapperElement.style.top = positionY + "px";
    };

    Moveable.prototype.getWrapperPosition = function() {
        return [this.wrapperElement.offsetLeft, this.wrapperElement.offsetTop];
    };

    Moveable.prototype.registerEvent = function() {
        var initX, initY,
            coordination = this.getWrapperPosition(),
            wrapperLeft = coordination[0],
            wrapperTop = coordination[1];

        this.drageElement.addEventListener("mousedown", (e) => {
            this.drageable = true;

            initX = e.clientX;
            initY = e.clientY;
        }, false);

        this.drageElement.addEventListener("mouseup", (e) => {
            this.drageable = false;

            var coordination = this.getWrapperPosition();
            wrapperLeft = coordination[0];
            wrapperTop = coordination[1];
        }, false);

        document.body.addEventListener("mousemove", (e) => {
            if (this.drageable) {
                var currentX = e.clientX,
                    currentY = e.clientY,
                    distanceX = currentX - initX,
                    distanceY = currentY - initY;
                this.updateWrapperPosition(wrapperLeft + distanceX, wrapperTop + distanceY);
            }
        }, false);
    };

    window.Moveable = Moveable;
} (this));
