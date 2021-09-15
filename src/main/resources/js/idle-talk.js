var IdleTalk = {
    send: function () {
        $.ajax({
            url: Label.servePath + '/idle-talk/send',
            type: 'POST',
            headers: {'csrfToken': Label.csrfToken},
            cache: false,
            data: JSON.stringify({
                userName: "admin",
                theme: "test",
                content: "hello, <h1>this is a test</h1>",
            }),
            success: function (result) {
            }
        });
    }
}
