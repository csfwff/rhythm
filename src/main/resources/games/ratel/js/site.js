;(function(window) {
    'use strict';

    function Site(siteInfo) {
        this.url = siteInfo.url;
        this.title = siteInfo.title;
        this.icon = siteInfo.icon;
    }

    var iconPrefix = "./favicons/";

    Site.prototype.render = function() {
        document.querySelector("#site").src = this.url;
        document.querySelector("#headTitle").innerHTML = this.title;
        document.querySelector("link[rel*='icon']").href = iconPrefix + this.icon;
    };

    var siteMap = {
	"w3c": {url: "https://www.w3school.com.cn/", title: "W3C", icon: ""},
        "vue": {url: "https://vuejs.org/", title: "Vue.js", icon: "favicon-vue.png"},
        "bilibili": {url: "https://www.bilibili.com/", title: "哔哩哔哩 (゜-゜)つロ 干杯~-bilibili", icon: "favicon-bilibili.ico"}
    };

    var defaultSiteName = "w3c";

    document.getElementById("switchWebsite").addEventListener("keyup", function(event){
        event.preventDefault();
        if (event.keyCode === 13) {
            if(this.value){
                document.querySelector("#site").src = this.value;
            }
        }
    })


    window.defaultSite = new Site(siteMap[defaultSiteName]);
} (this));
