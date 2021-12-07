define(
		[ 'backbone', 'model/symbols', 'model/symbol' ],
		function(Backbone, Symbols, Symbol) {

			var Tile = Backbone.Model
					.extend({

						defaults : {
							value : 0,
							x : 0,
							y : 0,
							edges : []
						},

						updateValue : function(isSilent) {
							this.set('value', this.get('symbols').sum(), {
								silent : isSilent
							});
						},

						empty : function() {
							this.set({
								'symbols' : new Symbols(),
								'edges' : [],
								'value' : 0
							}, {
								silent : true
							});
						},

						merge : function(oTile) {
							var origVal = this.get('value');
							if (origVal != 0) {
								var oSymbols = oTile.get('symbols'), symbols = this
										.get('symbols'), matchedSymbol;

								for (var index = 0, numOfSymbols = oSymbols.length; index < numOfSymbols; index += 1) {
									var oSymbol = oSymbols.at(index);
									matchedSymbol = symbols.byName(oSymbol
											.get('name'));
									if (matchedSymbol) {
										matchedSymbol.addValue(oSymbol
												.get('value'));
									} else {
										var newSymbol = new Symbol({
											name : oSymbol.get('name'),
											value : oSymbol.get('value')
										});
										symbols.add(newSymbol);
									}
								}
								oTile.trigger('translate', {
									x : this.get('x'),
									y : this.get('y')
								});
								this.updateValue(true);
								this.set('state', 'merged', {silent:true});
								oTile.empty();
							} else {
								var oX = oTile.get('x');
								var oY = oTile.get('y');
								oTile.set({
									x : this.get('x'),
									y : this.get('y'),
								}, {
									silent : true
								});
								oTile.trigger('translate', {x : this.get('x'), y: this.get('y')});
								this.set({
									x : oX,
									y : oY
								});
								this.trigger('change');
							}
							var _this = this;
							setTimeout(function(){
								oTile.trigger('change');
								origVal != 0 ?_this.trigger('change') : null;
							},210);
						},

						canMerge : function(oTile) {
							return this.get('value') == oTile.get('value')
									&& this.containSymbol(oTile);
						},

						containSymbol : function(oTile) {
							var oSymbols = oTile.get('symbols').pluck('name'), symbols = this
									.get('symbols').pluck('name');
							return _.intersection(symbols, oSymbols).length > 0;

						},

						toString : function() {
							return this.get('x') + ' ' + this.get('y') + ' '
									+ this.get('value');
						}

					});

			return Tile;
		});