<!DOCTYPE html>
<html itemscope itemtype="https://schema.org/CreativeWork">
<head>
	<meta charset="UTF-8"/>
	<!--  
		A Dark Room (v1.4)
		==================
		
		A minimalist text adventure by Michael Townsend and all his friends.
		Inspired by Candy Box (http://candies.aniwey.net/)
		Contribute on GitHub! (https://github.com/doublespeakgames/adarkroom/)
	-->
	<title>A Dark Room</title>
	<meta itemprop="description" name="description" property="og:description" content="A minimalist text adventure">
	<meta itemprop="image" property="og:image" content="img/adr.png" />
	<meta itemprop="name" property="og:title" content="A Dark Room" />
	<link rel="shortcut icon" href="favicon.ico" />
	<link rel="image_src" href="img/adr.png" />
	<script src="${servePath}/games/adarkroom/lib/jquery.min.js"></script>
	<script src="lib/jquery.color-2.1.2.min.js"></script>
	<script src="lib/jquery.event.move.js"></script>
	<script src="lib/jquery.event.swipe.js"></script>
	<script src="lib/base64.js"></script>
	<script src="lib/translate.js"></script>
	
	<script src="lang/langs.js"></script>
	
	<script>
		// try to read "lang" param's from url
		var lang = decodeURIComponent((new RegExp('[?|&]lang=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
		// if no language requested, try to read it from local storage
		if(!lang){
			try {
				lang = localStorage.lang;
			} catch(e) {}
		}
		// if a language different than english requested, load all translations
		if(lang && lang != 'en'){
			document.write('<script src="lang/'+lang+'/strings.js"><\/script>');
			document.write('<link rel="stylesheet" type="text/css" href="lang/'+lang+'/main.css" \/>');
		}
	</script>
	
	<script src="script/Button.js"></script>
	<script src="script/audioLibrary.js"></script>
	<script src="script/audio.js"></script>
	<script src="script/engine.js"></script>
	<script src="script/state_manager.js"></script>
	<script src="script/header.js"></script>
	<script src="script/notifications.js"></script>
	<script src="script/events.js"></script>
	<script src="script/room.js"></script>
	<script src="script/outside.js"></script>
	<script src="script/world.js"></script>
	<script src="script/path.js"></script>
	<script src="script/ship.js"></script>
	<script src="script/space.js"></script>
	<script src="script/prestige.js"></script>
	<script src="script/scoring.js"></script>
	<!-- Event modules -->
	<script src="script/events/global.js"></script>
	<script src="script/events/room.js"></script>
	<script src="script/events/outside.js"></script>
	<script src="script/events/encounters.js"></script>
  <script src="script/events/setpieces.js"></script>
  <script src="script/events/marketing.js"></script>
	
	<script type='text/javascript'>
		var oldIE = false;
	</script>
	<!--[if lt IE 9]> 
		<script type="text/javascript">oldIE = true;</script> 
	<![endif]-->
	
	<link rel="stylesheet" type="text/css" href="css/main.css" />
	<link rel="stylesheet" type="text/css" href="css/room.css" />
	<link rel="stylesheet" type="text/css" href="css/outside.css" />
	<link rel="stylesheet" type="text/css" href="css/path.css" />
	<link rel="stylesheet" type="text/css" href="css/world.css" />
	<link rel="stylesheet" type="text/css" href="css/ship.css" />
	<link rel="stylesheet" type="text/css" href="css/space.css" />
	
	<script src="script/localization.js"></script>
</head>
<body>
	<div id="wrapper">
		<div id="saveNotify"><script>document.write(_("saved."));</script></div>
		<div id="content">
			<div id="outerSlider">
				<div id="main">
					<div id="header"></div>
				</div>
			</div>
		</div>
  </div>
  <a class="logo" href="https://pwl.icu" alt="摸鱼派社区小游戏-作者DoubleSpeakGames.com" target="_blank">
    <svg xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:cc="http://creativecommons.org/ns#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:svg="http://www.w3.org/2000/svg" xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="0 0 32 32" class="logo-icon">
		<image id="image0" width="32" height="32" x="0" y="0" href="https://pwl.stackoverflow.wiki/mplogo_128.png"/>
	</svg>
  </a>
	<script src="${staticServePath}/js/lib/reconnecting-websocket.min.js?${staticResourceVersion}"></script>
	<script src="${staticServePath}/js/symbol-defs${miniPostfix}.js?${staticResourceVersion}"></script>
	<script src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}"></script>
	<script>
		var Label = {
			commentEditorPlaceholderLabel: '${commentEditorPlaceholderLabel}',
			langLabel: '${langLabel}',
			luteAvailable: ${luteAvailable?c},
			reportSuccLabel: '${reportSuccLabel}',
			breezemoonLabel: '${breezemoonLabel}',
			confirmRemoveLabel: "${confirmRemoveLabel}",
			reloginLabel: "${reloginLabel}",
			invalidPasswordLabel: "${invalidPasswordLabel}",
			loginNameErrorLabel: "${loginNameErrorLabel}",
			followLabel: "${followLabel}",
			unfollowLabel: "${unfollowLabel}",
			symphonyLabel: "${symphonyLabel}",
			visionLabel: "${visionLabel}",
			cmtLabel: "${cmtLabel}",
			collectLabel: "${collectLabel}",
			uncollectLabel: "${uncollectLabel}",
			desktopNotificationTemplateLabel: "${desktopNotificationTemplateLabel}",
			servePath: "${servePath}",
			staticServePath: "${staticServePath}",
			isLoggedIn: ${isLoggedIn?c},
			funNeedLoginLabel: '${funNeedLoginLabel}',
			notificationCommentedLabel: '${notificationCommentedLabel}',
			notificationReplyLabel: '${notificationReplyLabel}',
			notificationAtLabel: '${notificationAtLabel}',
			notificationFollowingLabel: '${notificationFollowingLabel}',
			pointLabel: '${pointLabel}',
			sameCityLabel: '${sameCityLabel}',
			systemLabel: '${systemLabel}',
			newFollowerLabel: '${newFollowerLabel}',
			makeAsReadLabel: '${makeAsReadLabel}',
			imgMaxSize: ${imgMaxSize?c},
			fileMaxSize: ${fileMaxSize?c},
			<#if isLoggedIn>
			currentUserName: '${currentUser.userName}',
			</#if>
			<#if csrfToken??>
			csrfToken: '${csrfToken}'
			</#if>
		}

		<#if isLoggedIn>
		Label.userKeyboardShortcutsStatus = '${currentUser.userKeyboardShortcutsStatus}'
		</#if>

		<#if isLoggedIn>
		// Init [User] channel
		Util.initUserChannel("${wsScheme}://${serverHost}:${serverPort}${contextPath}/user-channel")
		</#if>
	</script>
	<script>
		!function(p){"use strict";!function(t){var s=window,e=document,i=p,c="".concat("https:"===e.location.protocol?"https://":"http://","sdk.51.la/js-sdk-pro.min.js"),n=e.createElement("script"),r=e.getElementsByTagName("script")[0];n.type="text/javascript",n.setAttribute("charset","UTF-8"),n.async=!0,n.src=c,n.id="LA_COLLECT",i.d=n;var o=function(){s.LA.ids.push(i)};s.LA?s.LA.ids&&o():(s.LA=p,s.LA.ids=[],o()),r.parentNode.insertBefore(n,r)}()}({id:"JRkLwpTk0DlpkOHh",ck:"JRkLwpTk0DlpkOHh"});
	</script>
</body>
</html>
