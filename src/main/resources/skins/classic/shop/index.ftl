<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
    <#if hasSystemTitle>
        <title>${systemTitle}</title>
    <#else>
        <title>系统商店</title>
    </#if>
    <link rel="stylesheet" href="${staticServePath}/css/shop.css?${staticResourceVersion}" />
    <link rel="preconnect" href="${staticServePath}">
    <meta name="copyright" content="B3log" />
    <meta http-equiv="Window-target" content="_top" />
    <link rel="icon" type="image/png" href="${staticServePath}/images/favicon.png" />
    <link rel="apple-touch-icon" href="${staticServePath}/images/faviconH.png">
    <link rel="search" type="application/opensearchdescription+xml" title="Rym" href="/opensearch.xml">
    ${siteVisitStatCode}
</head>
<body>
<div class="main">

</div>
</body>
</html>
