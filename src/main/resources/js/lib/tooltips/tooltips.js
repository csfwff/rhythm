/*!
 * @name $.tooltips 信息提示
 * @author M.J
 * @date 2017-6-23
 *
 * $.tooltips('tooltips')
 * $.tooltips('tooltips', 5000);
 * $.tooltips('tooltips', {
 *     type: 'danger',
 *     duration: 3000,
 *     callback: function() {}
 * });
 *
 */
(function($) {

    if (!$) {
        window.console && window.console.info('$.tooltips 暂时无法为您工作，请引入 jQuery 或 Zepto 库后重试');
        return;
    }

    /**
     * 构造函数
     * @param  {[type]} body    [description]
     * @param  {[type]} options [description]
     * @return {[type]}         [description]
     */
    var ToolTips = function(body, options) {
        var defaults = {
            type: 'success',
            duration: 3000,
            callback: function() {}
        };

        this.config = $.extend({}, defaults, $.isNumeric(options) ? {duration: options} : (options || {}));
        this.$elem = null;
        this.timeout = null;
        this.body = body;

        return this.init();
    };

    ToolTips.prototype = {
        /**
         * 默认 tpl
         * @type {String}
         */
        tpl: '<div class="ui-tooltips ui-{type}"><span>{body}</span></div>',

        /**
         * 渲染处理
         * @return {[type]} [description]
         */
        render: function() {
            return $(this.tpl.replace(/{type}/g, this.config.type).replace(/{body}/, this.body));
        },

        /**
         * 添加到 body 中
         * @return {[type]} [description]
         */
        append: function() {
            this.$elem.appendTo('body');
            return this;
        },

        /**
         * 显示
         */
        show: function() {
            var _this = this;

            setTimeout(function() {
                _this.$elem.addClass('is-show');
            }, 100);

            return this;
        },

        /**
         * 隐藏
         * @return {[type]} [description]
         */
        hide: function() {
            var _this = this;

            this.$elem.removeClass('is-show').one('webkitTransitionEnd oTransitionEnd transitionend', function() {
                _this.$elem.remove();
                _this.config.callback && _this.config.callback.apply(_this);
            });

            return this;
        },

        /**
         * 绑定
         * @return {[type]} [description]
         */
        bind: function() {
            var _this = this;

            clearTimeout(this.timeout);
            this.timeout = setTimeout(function() {
                _this.hide();
            }, this.config.duration);

            return this;
        },

        /**
         * 初始化
         * @return {[type]} [description]
         */
        init: function() {
            this.$elem = this.render();
            this.append().show().bind();
            return this;
        }
    };

    /**
     * $.tooltips('tooltips')
     * $.tooltips('asdbassdf', 5000);
     * $.tooltips('asdfsadfdsaf', {
     *     type: 'danger',
     *     duration: 3000,
     *     callback: function() {}
     * });
     */
    $.tooltips = function(body, options) {
        return new ToolTips(body, options);
    }

}(window.jQuery || window.Zepto));
