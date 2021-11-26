;(function(window, Utils) {
    'use strict';

    function Level(level, symbol, aliases) {
        this.level = level;
        this.symbol = symbol;
        this.aliases = Array.from(Array.prototype.slice.call(arguments, 2));
    }

    Level.prototype.is = function(alias) {
        var arr = this.aliases.filter(a => a == alias);
        return arr != null && arr.length > 0;
    };

    function Type(type, symbol) {
        this.type = type;
        this.symbol = symbol;
    }

    var LEVELS = {
        LEVEL_3: new Level(3, "3", "3"),
        LEVEL_4: new Level(4, "4", "4"),
        LEVEL_5: new Level(5, "5", "5"),
        LEVEL_6: new Level(6, "6", "6"),
        LEVEL_7: new Level(7, "7", "7"),
        LEVEL_8: new Level(8, "8", "8"),
        LEVEL_9: new Level(9, "9", "9"),
        LEVEL_10: new Level(10, "10", "0", "T", "t"),
        LEVEL_J: new Level(11, "J", "J", "j"),
        LEVEL_Q: new Level(12, "Q", "Q", "q"),
        LEVEL_K: new Level(13, "K", "K", "k"),
        LEVEL_A: new Level(14, "A", "1", "A", "a"),
        LEVEL_2: new Level(15, "2", "2"),
        LEVEL_SMALL_KING: new Level(16, "S", "S", "s"),
        LEVEL_BIG_KING: new Level(17, "X", "X", "x")
    };

    var TYPES = {
        BLANK: new Type("BLANK", "_"),
        DIAMOND: new Type("DIAMOND", "_"),
        CLUB: new Type("CLUB", "_"),
        SPADE: new Type("SPADE", "_"),
        HEART: new Type("HEART", "_")
    };

    function Poker(type, level) {
        if (Utils.isEmpty(type) || Utils.isEmpty(level))
            throw new Error("Type or level must not null.");

        this.type = type;
        this.level = level;
    };

    Poker.prototype.toString = function() {
        var s = "<code>";
        s += "┌─┐\n";
        s += "│";
        s += LEVELS[this.level].symbol + LEVELS[this.level].symbol.length == 1 ? " " : "";
        s += "│\n│";
        s += TYPES[this.type].symbol;
        s += "│\n";
        s += "└─┘";
        s += "</code>";

        return s;
    };

    Poker.isVaildAlias = function(alias) {
        for (var a in LEVELS) {
            if (LEVELS[a].is(alias)) return true;
        }

        return false;
    };

    Poker.toString = function(pokers) {
        if (!Array.isArray(pokers)) {
            return new Poker(pokers).toString();
        }

        var s = "<code>";

        if(! window.pockerStyle || window.pockerStyle == 1){
            for (var i in pokers) {
            s += i == 0 ? "┌─┐" : "─┐";
            }
            s += "\n";

            for (var i in pokers) {
                if (i == 0) s += "│";
                s += LEVELS[pokers[i].level].symbol + (LEVELS[pokers[i].level].symbol.length == 1 ? " " : "") + "│";
            }
            s += "\n";

            for (var i in pokers) {
                if (i == 0) s += "│";
                s += TYPES[pokers[i].type].symbol + " │";
            }
            s += "\n";

            for (var i in pokers) {
                s += i == 0 ? "└─┘" : "─┘";
            }
        }else{
            for (var i in pokers) {
                s += LEVELS[pokers[i].level].symbol + " "
            }
        }
        s += "</code>";

        return s;
    };

    window.Poker = Poker;
} (this, this.Utils));
