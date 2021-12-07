define([ 'jquery', 'underscore', 'backbone', 'model/game' ], function($, _,
		Backbone, Game) {

	var ScoreView = Backbone.View.extend({

		el : '.control-panel',

		model : Game,

		initialize : function() {
			this.listenTo(this.model, 'change', this.render);
			this.render();
		},

		render : function() {
			var _this = this;
			this.$el.find('#game-score strong').html(this.model.get('score')).addClass(
					'animated pulse');
			this.$el.find('#best-score strong').html(this.model.get('best'));
			if(this.model.get('score') >= this.model.get('best')){
				this.$el.find('#best-score strong').addClass(
					'animated pulse');
			}
			setTimeout(function(){
				_this.$el.find('.animated').removeClass('animated pulse');
			}, 500);
		}

	});

	return ScoreView;
});