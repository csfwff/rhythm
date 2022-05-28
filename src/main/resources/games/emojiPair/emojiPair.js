var cw, ch, ratio = 0.546875;
var game = document.getElementById('game'),
    btnArea = document.getElementById('btnArea'),
    hintBtn = document.getElementById('hintBtn'),
    replayBtn = document.getElementById('replayBtn'),
    timeLimit = 60*1000, // in millisec
    timeLeft,
    timeInt,
    lastBtn,
    hint,
    msTilHint,
    art = ['😄','🤣','🙂','🙃','😉','😇','😍','🤥','😘','😚','😛','😜','😋','🤗','🤔','🤐','😶','🤑','😏','🙄','😳','😬','😴','🤕','🤠','🤧','😢','😵','😎','🤓','😡','🤢','😭','😫','😠'],
    found = 0;


// Make 16 btn <divs>
for (var i=1; i<=16; i++){
    var b;
    if (i==1) b = document.getElementById('b1');
    else {
        b = document.getElementById('b1').cloneNode(true);
        b.id = 'b'+(i);
        btnArea.appendChild(b);
    }
    b.onclick = b.ontouchend = btnClick;
}



// Initial states...
new TimelineMax({onStart:populate})
    .set(game, 			{userSelect:'none'})
    .set('.btn',		{width:90, height:90, borderRadius:'50%', border:'3px solid transparent', textAlign:'center', fontSize:72, lineHeight:'86px', cursor:'pointer'})
    .set(hintBtn,		{right:15, bottom:15, width:75, fontSize:25, textAlign:'center', cursor:'pointer', autoAlpha:0})
    .set('.foundTxt',	{left:0, top:30, width:'100%', fontSize:35, textAlign:'center', textContent:'Find the matching pair...', fontWeight:300, letterSpacing:0.25})
    .set('.timeTxt',	{left:30, top:20, fontSize:50})
    .set('.timePlus',	{left:58, top:75, fontSize:25, alpha:0})
    .set('.end',		{width:'100%', height:'100%', background:'rgba(255,255,255,0.1)', autoAlpha:0, cursor:'pointer'})
    .set('.rewardTxt',	{textAlign:'center', top:257, width:'100%', fontSize:25, fontWeight:300})
    .set('.endTxt',		{textAlign:'center', top:257, width:'100%', fontSize:25, fontWeight:300})
    .set(replayBtn,		{left:400, top:275, scale:0.6, transformOrigin:'120px 130px'})
    .to('#container', 0.2, {alpha:1, ease:Power2.easeIn}, 0)

hintBtn.onclick = hintBtn.ontouchend = function(e){
    if (e.type=='touchend') hintBtn.onclick = undefined;
    TweenMax.set(hintBtn, {textContent:hint, fontSize:35})
}

function populate() {

    lastBtn = undefined;
    msTilHint = 5000;
    TweenMax.set(hintBtn, {autoAlpha:0, textContent:"Hint?", fontSize:25});
    TweenMax.staggerFromTo('.btn', 0.3, {scale:0.2, alpha:0, rotation:1}, {rotation:0, alpha:1, scale:1, ease:Back.easeOut.config(4), stagger:{ grid:[4,4], from:"center", amount:0.2} });

    var btns = [];

    for (var i=0; i<15; i++) makeNewNum();

    function makeNewNum(){
        var n = art[Math.ceil(rand(0, art.length-1))],
            valExists = false;

        for (var i=0; i<btns.length; i++) if (n==btns[i]) valExists = true;

        (valExists) ? makeNewNum() : btns.push(n);
    }

    hint = btns[14];

    btns.push(btns[14]);
    shuffleArray(btns);

    for (var b=1; b<=16; b++) window['b'+b].textContent = btns[b-1];
}


function btnClick(e){
    if (e.type=='touchend') e.currentTarget.onclick = undefined;

    if (timeInt==undefined) {
        timeLeft = timeLimit;
        timeInt = setInterval(updateTime, 10);
    }

    var b = e.currentTarget;
    TweenMax.to(b, 0.05, {scale:0.95, yoyo:true, repeat:1});

    if (lastBtn!=undefined && lastBtn!=b) {
        if (b.textContent==lastBtn.textContent) { //matched
            found++;
            timeLeft+=5000; //+time
            new TimelineMax({onComplete:populate})
                .set('.foundTxt', {textContent:'Found: ' + found, fontWeight:500}, 0)
                .to('.timePlus', 		0.1, {alpha:1, yoyo:true, repeat:1, repeatDelay:0.4}, 0)
                .fromTo('.timePlus', 	0.3, {scale:0, rotation:0.1}, {scale:1, rotation:0}, 0)
                .to([b,lastBtn], 		0.1, {border:'3px solid #08c04d'}, 0)
                .to(b, 		 			0.3, {rotation:1, scale:0.8, ease:Back.easeIn.config(7), yoyo:true, repeat:1}, 0)
                .to(lastBtn, 			0.3, {rotation:1, scale:0.8, ease:Back.easeIn.config(7), yoyo:true, repeat:1}, 0)
                .to('.btn',	 			0.1, {border:'3px solid transparent'}, 0.5)
            return;
        }
        else { //not matched
            TweenMax.to(lastBtn, 0.1, {border:'3px solid transparent'})
        }
    }
    TweenMax.to(b, 0.1, {border:'3px solid #006da6'});
    lastBtn=e.currentTarget;
}


function updateTime(){
    if (timeLeft>0){
        timeLeft-=10;
        var mil = Math.floor(timeLeft%1000/10);
        var sec = Math.floor(timeLeft/1000);
        if (mil<10) mil = "0"+mil;
        if (sec<10) sec = "0"+sec;
        var t = sec + ":" + mil;
        TweenMax.set('.timeTxt', {textContent:t});
        (msTilHint<1) ? TweenMax.to(hintBtn, 0.5, {autoAlpha:1}) : msTilHint-=10;
    }

    else { // Game over

        clearInterval(timeInt);
        timeInt = undefined;
        if (!found) {
            TweenMax.set('.foundTxt', {textContent:'Found: 0', fontWeight:500});
            TweenMax.set('.rewardTxt', {textContent:''});
        } else {
            $.ajax({
                url: '/api/games/emojiPair/score',
                type: 'POST',
                data: JSON.stringify({"founds": found}),
                cache: false,
                headers: {'csrfToken': Label.csrfToken},
                success: function (result, textStatus) {
                    if (0 === result.code) {
                        if (found > 30) {
                            TweenMax.set('.rewardTxt', {textContent: hint + " 30积分奖励已到账 " + hint, fontSize: 35});
                        } else {
                            TweenMax.set('.rewardTxt', {textContent: hint + " " + found + "积分奖励已到账 " + hint, fontSize: 35});
                        }
                    } else {
                        TweenMax.set('.rewardTxt', {textContent: ''});
                    }
                }
            });
        }
        TweenMax.set('.endTxt', {textContent: hint + " Try again? " + hint, fontSize: 25});

        new TimelineMax()
            .to('.timeTxt',			0.3, {autoAlpha:0}, 0)
            .to(hintBtn,			0.3, {autoAlpha:0}, 0)
            .to(btnArea,	 		0.3, {autoAlpha:0}, 0)
            .to('.btn',				0.1, {border:'3px solid transparent'}, 0.3)
            .fromTo('.foundTxt', 	0.4, {rotation:0.1}, {rotation:0, scale:2.5, y:130, ease:Power2.easeInOut}, 0.2)
            .to('.end', 	 		0.6, {autoAlpha:1, ease:Power2.easeInOut}, 0)
            .fromTo(replayBtn, 		1.0, {rotation:360, scale:0}, {scale:0.6, rotation:0, ease:Power1.easeInOut}, 0.5)
    }
}


replayBtn.onclick = replayBtn.ontouchend = function(){

    new TimelineMax()
        .to(replayBtn, 				0.5, {rotation:-180, scale:0, ease:Back.easeIn.config(3)}, 0)
        .fromTo('.foundTxt',		0.4, {rotation:0.1}, {rotation:0, scale:1, y:0, ease:Power1.easeInOut}, 0.4)
        .to('.end',					0.3, {autoAlpha:0}, 0.5)
        .to(['.timeTxt',btnArea], 	0.3, {autoAlpha:1}, 0.8)

        .call(function(){ // Reset game...

            TweenMax.set('.foundTxt', {textContent:'Found: 0'});
            TweenMax.set('.timeTxt', {textContent:'60:00'}); //gameover tip
            found = 0;
            lastBtn = hint = undefined;
            populate();

        }, null, null, 0.8)
}



/* Browser resize */
function doResize() {
    cw = container.clientWidth;
    ch = container.clientHeight;

    if (cw>1024){ cw=1024; ch=560; } //max stage dimensions

    TweenMax.set('.fs', {transformOrigin:'0 0', scale:(cw*ratio)/560});

    new TimelineMax()
        .set('#b1',  {left:320, top:100})
        .set('#b2',  {left:420, top:100})
        .set('#b3',  {left:520, top:100})
        .set('#b4',  {left:620, top:100})
        .set('#b5',  {left:320, top:200})
        .set('#b6',  {left:420, top:200})
        .set('#b7',  {left:520, top:200})
        .set('#b8',  {left:620, top:200})
        .set('#b9',  {left:320, top:300})
        .set('#b10', {left:420, top:300})
        .set('#b11', {left:520, top:300})
        .set('#b12', {left:620, top:300})
        .set('#b13', {left:320, top:400})
        .set('#b14', {left:420, top:400})
        .set('#b15', {left:520, top:400})
        .set('#b16', {left:620, top:400})
}

doResize();
window.addEventListener("resize", doResize);


/* Helper Functions */
function shuffleArray(array) {
    for (var i = array.length - 1; i > 0; i--) {
        var j = Math.floor(Math.random() * (i + 1));
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

function rand(min, max) {
    min = min || 0;
    max = max || 1;
    return min + (max-min)*Math.random();
}
