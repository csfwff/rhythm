define([ 'backbone', 'model/tile' ], function(Backbone, Tile) {

	var Tiles = Backbone.Collection.extend({
		model : Tile,

		mergeTile : function(tile, nonEmptyTile, emptyTile) {
			var isMerged = false;
			if (nonEmptyTile != null && nonEmptyTile.canMerge(tile)) {
				nonEmptyTile.merge(tile);
				isMerged = true;
				this.trigger('tile-merge', nonEmptyTile.get('value'));
			} else if (emptyTile != null) {
				emptyTile.merge(tile);
				isMerged = true;
			} else {
				// no matching tile to merge.
			}
			return isMerged;
		},

		moveToFarRight : function(tile) {
			var firstNonEmptyTile = null, farEmptyTile = null, j, i, nxtTile;
			i = tile.get('x');
			for (j = tile.get('y') + 1; j < 4; j++) {
				nxtTile = this.findWhere({
					x : i,
					y : j
				});
				if (nxtTile.get('value') != 0) {
					firstNonEmptyTile = nxtTile;
					break;
				}
			}

			for (j = firstNonEmptyTile != null ? firstNonEmptyTile.get('y') - 1
					: 3; j > tile.get('y'); j--) {
				nxtTile = this.findWhere({
					x : i,
					y : j
				});
				if (nxtTile.get('value') == 0) {
					farEmptyTile = nxtTile;
					break;
				}
			}
			return this.mergeTile(tile, firstNonEmptyTile, farEmptyTile);
		},

		moveToFarLeft : function(tile) {
			var firstNonEmptyTile = null, farEmptyTile = null, j, i, nxtTile;
			i = tile.get('x');
			for (j = tile.get('y') - 1; j >= 0; j--) {
				nxtTile = this.findWhere({
					x : i,
					y : j
				});
				if (nxtTile.get('value') != 0) {
					firstNonEmptyTile = nxtTile;
					break;
				}
			}

			for (j = firstNonEmptyTile != null ? firstNonEmptyTile.get('y') + 1
					: 0; j < tile.get('y'); j++) {
				nxtTile = this.findWhere({
					x : i,
					y : j
				});
				if (nxtTile.get('value') == 0) {
					farEmptyTile = nxtTile;
					break;
				}
			}

			return this.mergeTile(tile, firstNonEmptyTile, farEmptyTile);
		},

		moveToFarUp : function(tile) {
			var firstNonEmptyTile = null, farEmptyTile = null, j, i, nxtTile;
			j = tile.get('y');
			for (i = tile.get('x') - 1; i >= 0; i--) {
				nxtTile = this.findWhere({
					x : i,
					y : j
				});
				if (nxtTile.get('value') != 0) {
					firstNonEmptyTile = nxtTile;
					break;
				}
			}

			for (i = firstNonEmptyTile != null ? firstNonEmptyTile.get('x') + 1
					: 0; i < tile.get('x'); i++) {
				nxtTile = this.findWhere({
					x : i,
					y : j
				});
				if (nxtTile.get('value') == 0) {
					farEmptyTile = nxtTile;
					break;
				}
			}

			return this.mergeTile(tile, firstNonEmptyTile, farEmptyTile);
		},

		moveToFarDown : function(tile) {
			var firstNonEmptyTile = null, farEmptyTile = null, j, i, nxtTile;
			j = tile.get('y');
			for (i = tile.get('x') + 1; i < 4; i++) {
				nxtTile = this.findWhere({
					x : i,
					y : j
				});
				if (nxtTile.get('value') != 0) {
					firstNonEmptyTile = nxtTile;
					break;
				}
			}

			for (i = firstNonEmptyTile != null ? firstNonEmptyTile.get('x') - 1
					: 3; i > tile.get('x'); i--) {
				nxtTile = this.findWhere({
					x : i,
					y : j
				});
				if (nxtTile.get('value') == 0) {
					farEmptyTile = nxtTile;
					break;
				}
			}

			return this.mergeTile(tile, firstNonEmptyTile, farEmptyTile);
		},

		detectEdges : function(tile) {
			var edges = [];
			var rT = this.nonEmptyTileTowardsRight(tile);
			var lT = this.nonEmptyTileTowardsLeft(tile);
			var bT = this.nonEmptyTileTowardsBottom(tile);
			var uT = this.nonEmptyTileTowardsTop(tile);

			if (rT != null && rT.canMerge(tile)) {
				edges.push('right');
			}

			if (lT != null && lT.canMerge(tile)) {
				edges.push('left');
			}

			if (bT != null && bT.canMerge(tile)) {
				edges.push('bottom');
			}

			if (uT != null && uT.canMerge(tile)) {
				edges.push('top');
			}

			tile.set('edges', edges);
			return edges.length > 0 ? true : false;
		},

		nonEmptyTileTowardsRight : function(tile) {
			var row = tile.get('x'), col;
			for (col = tile.get('y') + 1; col < 4; col++) {
				var nxtTile = this.findWhere({
					x : row,
					y : col
				});
				if (nxtTile.get('value') != 0) {
					return nxtTile;
				}
			}
			return null;
		},

		nonEmptyTileTowardsLeft : function(tile) {
			var row = tile.get('x'), col;
			for (col = tile.get('y') - 1; col >= 0; col--) {
				var nxtTile = this.findWhere({
					x : row,
					y : col
				});
				if (nxtTile.get('value') != 0) {
					return nxtTile;
				}
			}
			return null;
		},

		nonEmptyTileTowardsTop : function(tile) {
			var col = tile.get('y'), row;
			for (row = tile.get('x') - 1; row >= 0; row--) {
				var nxtTile = this.findWhere({
					x : row,
					y : col
				});
				if (nxtTile.get('value') != 0) {
					return nxtTile;
				}
			}
			return null;
		},

		nonEmptyTileTowardsBottom : function(tile) {
			var col = tile.get('y'), row;
			for (row = tile.get('x') + 1; row < 4; row++) {
				var nxtTile = this.findWhere({
					x : row,
					y : col
				});
				if (nxtTile.get('value') != 0) {
					return nxtTile;
				}
			}
			return null;
		},

		emptyTiles : function() {
			return this.where({
				value : 0
			});
		},

		nonEmptyTiles : function() {
			return this.filter(function(tile) {
				return tile.get('value') != 0;
			});
		},

		nonEmptyTilesAtColumn : function(col) {
			return this.filter(function(tile) {
				return tile.get('y') == col && tile.get('value') != 0;
			});
		},

		nonEmptyTilesAtRow : function(row) {
			return this.filter(function(tile) {
				return tile.get('x') == row && tile.get('value') != 0;
			});
		},
		
		tile2048 : function(){
			return this.findWhere({
				value : 2048
			});
		}
	});

	return Tiles;
});