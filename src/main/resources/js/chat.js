var Chat = {
    init: function () {
        Chat.editor = Util.newVditor({
            id: 'messageContent',
            cache: true,
            preview: {
                mode: 'editor',
            },
            resize: {
                enable: true,
                position: 'bottom',
            },
            height: 150,
            placeholder: '说点什么吧，友善第一哦。',
            toolbar: [
                'emoji',
                'link',
                'upload',
                'edit-mode',
                {
                    name: 'more',
                    toolbar: [
                        'insert-after',
                        'fullscreen',
                        'preview',
                        'info',
                        'help',
                    ],
                },
            ]
        })
    }
}

$(document).ready(function () {
    Chat.init();
});
