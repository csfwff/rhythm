<!DOCTYPE HTML>
<html>
<head>
    <#if hasSystemTitle>
        <title>${systemTitle}</title>
    <#else>
        <title>进化 - Evolve</title>
    </#if>
    <meta http-equiv="Content-type" content="text/html;charset=utf-8">
    <link rel="icon" href="../../../../games/evolve/evolved-light.ico" type="images/x-icon" media="(prefers-color-scheme:dark)">
    <link rel="icon" href="../../../../games/evolve/evolved.ico" type="images/x-icon" media="(prefers-color-scheme:light)">
    <link rel="icon" href="../../../../games/evolve/evolved.ico" type="images/x-icon" media="(prefers-color-scheme:no-preference)">
    <link href="../../../../games/evolve/lib/googlelato.css" rel="stylesheet">
    <link rel="stylesheet" href="../../../../games/evolve/lib/buefy.min.css">
    <link rel="stylesheet" type="text/css" href="../../../../games/evolve/lib/weather-icons.min.css">
    
    <script src="../../../../games/evolve/lib/jquery.min.js"></script>
    <script src="../../../../games/evolve/lib/vue.min.js"></script>
    <script src="../../../../games/evolve/lib/buefy.min.js"></script>
    <script src="../../../../games/evolve/lib/popper.min.js"></script>
    <script src="../../../../games/evolve/lib/Sortable.min.js"></script>
    <script src="../../../../games/evolve/lib/lodash.min.js"></script>
    <script src="../../../../games/evolve/lib/PlayFabClientApi.js"></script>
    <script src="../../../../games/evolve/lib/moment.js"></script>
<!--
    <script src="https://code.jquery.com/jquery-3.4.1.min.js" integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo=" crossorigin="anonymous"></script>
    <script src="https://unpkg.com/vue@2.6.11/dist/vue.min.js"></script>
    <script src="https://unpkg.com/buefy@0.9.3/dist/buefy.min.js"></script>
    <script src="https://unpkg.com/popper.js@1.16.1-lts/dist/umd/popper.min.js"></script>
    <script src="https://unpkg.com/sortablejs@1.10.2/Sortable.min.js"></script>
    <script src="https://unpkg.com/lodash@4.17.15/lodash.min.js"></script>
-->
    <link rel="stylesheet" type="text/css" href="../../../../games/evolve/evolve/evolve.css?r=20210605">
    <script src="../../../../games/evolve/lib/lz-string.min.js"></script>
    <script src="../../../../games/evolve/evolve/main.js?r=20210605" type="module"></script>
</head>
<body>
<!--          <script src="zh/core.js"></script> -->
    <style>
        .loading {
            text-align: center;
            margin-top: 10rem;
        }
        .lds-dual-ring {
            display: inline-block;
            width: 64px;
            height: 64px;
        }
        .lds-dual-ring:after {
            content: " ";
            display: block;
            width: 5rem;
            height: 5rem;
            margin: 1px;
            border-radius: 50%;
            border: 5px solid #fff;
            border-color: #fff transparent #fff transparent;
            animation: lds-dual-ring 1.2s linear infinite;
            }
            @keyframes lds-dual-ring {
            0% {
                transform: rotate(0deg);
            }
            100% {
                transform: rotate(360deg);
            }
        }
    </style>
    <div class="loading"><div class="lds-dual-ring"></div></div>
</body>
</html>
<script src="${staticServePath}/js/lib/reconnecting-websocket.min.js?${staticResourceVersion}"></script>
<script src="${staticServePath}/js/symbol-defs${miniPostfix}.js?${staticResourceVersion}"></script>
<script src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}"></script>
<script>
    var Label = {
        saveData: '',
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
