<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="やり直すんだ。そして、次はうまくやる。"/>
    <meta name="keywords" content="人生重开模拟器 liferestart life restart remake 人生重来"/>
    <link id="themeLink" rel="stylesheet" href="../../../../../games/lifeRestart/view/light.css">
    <link rel="manifest" href="../../../../../games/lifeRestart/view/manifest.json">
    <title>Life Restart</title>
</head>
<body>
  <div class="banners-container">
    <div class="banners">
      <div class="banner error">
        <div class="banner-icon"><span class="iconfont">&#xe6a1;</span></div>
        <pre class="banner-message">Oops! Something went wrong!</pre>
        <div class="banner-close" onclick="hideBanners()"><span class="iconfont">&#xe6a8;</span></div>
      </div>
      <div class="banner success">
        <div class="banner-icon"><span class="iconfont">&#xe6a2;</span></div>
        <pre class="banner-message">Everything was fine!</pre>
        <div class="banner-close" onclick="hideBanners()"><span class="iconfont">&#xe6a8;</span></div>
      </div>
      <div class="banner info">
        <div class="banner-icon"><span class="iconfont">&#xe6a3;</span></div>
        <pre class="banner-message">Here is some useful information</pre>
        <div class="banner-close" onclick="hideBanners()"><span class="iconfont">&#xe6a8;</span></div>
      </div>
    </div>
  </div>
  <script src="../../../../../games/lifeRestart/lib/jquery-3.6.0.min.js"></script>
  <script src="../../../../../games/lifeRestart/lib/dom-to-image.min.js"></script>
  <script src="../../../../../games/lifeRestart/public/bundle.js"></script>
  <script>
    if ('serviceWorker' in navigator) {
      window.addEventListener('load', function () {
        navigator.serviceWorker.register('./sw.js', {scope: '.'})
          .then(function (registration) {
            console.log('ServiceWorker registration successful');
          })
          .catch(function (err) {
            console.log('ServiceWorker registration failed');
          });
      });
    }
  </script>
</body>
</html>
