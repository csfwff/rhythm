$(function () {
    var uploadedImageURL;

    // init
    $image.attr('src',originalImageURL).cropper(options);


    // rotate
    $rotate.on('click', function(){
        $image.cropper('rotate', 90);
    });

    // zoomOut
    $zoomOut.on('click',function(){
        $image.cropper('zoom', -0.1);
    });

    // zoomIn
    $zoomIn.on('click',function(){
        $image.cropper('zoom', 0.1);
    });

    // Move
    /*$move.on('click',function(){
     $image.cropper('setDragMode');
     });*/

    // reUpload
    $reUpload.on('click',function(){
        $image.cropper('destroy').attr('src', originalImageURL).cropper(options);
        if (uploadedImageURL) {
            URL.revokeObjectURL(uploadedImageURL);
            uploadedImageURL = '';
        }
    });

    // Keyboard
    $(document.body).on('keydown', function (e) {

        if (!$image.data('cropper') || this.scrollTop > 300) {
            return;
        }

        switch (e.which) {
            case 37:
                e.preventDefault();
                $image.cropper('move', -1, 0);
                break;

            case 38:
                e.preventDefault();
                $image.cropper('move', 0, -1);
                break;

            case 39:
                e.preventDefault();
                $image.cropper('move', 1, 0);
                break;

            case 40:
                e.preventDefault();
                $image.cropper('move', 0, 1);
                break;
        }

    });

    // save and upload cropped Img
    $save.on('click',function(){
        $('#image').cropper('getCroppedCanvas').toBlob(function (blob) {
            var formData = new FormData();
            formData.append('file[]', blob);
            $.ajax({
                url: Label.servePath + '/upload',
                method: "POST",
                data: formData,
                processData: false,
                contentType: false,
                mimeType: "multipart/form-data",
                success: function (res) {
                    var result = {
                        result: {
                            key: JSON.parse(res).data.succMap[Object.keys(JSON.parse(res).data.succMap)[0]]
                        }
                    };
                    updateAvatarByData(result);
                },
                error: function () {
                    alert('头像上传失败!');
                }
            });
        });
    })

    // Import Image
    var $inputImage = $('#inputImage');
    if (URL) {
        $inputImage.change(function () {
            var files = this.files;
            var file;

            if (!$image.data('cropper')) {
                return;
            }

            if (files && files.length) {
                file = files[0];

                if (/^image\/\w+$/.test(file.type)) {
                    if (uploadedImageURL) {
                        URL.revokeObjectURL(uploadedImageURL);
                    }

                    uploadedImageURL = URL.createObjectURL(file);
                    $image.cropper('destroy').attr('src', uploadedImageURL).cropper(options);
                    $inputImage.val('');
                } else {
                    alert('仅支持图片！')
                }
            }
        });
    } else {
        $inputImage.prop('disabled', true).parent().addClass('disabled');
    }
});
