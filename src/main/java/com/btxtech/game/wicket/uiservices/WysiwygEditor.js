CKEDITOR.replace('${editorId}',
        {
            skin:'v2',
            toolbar:[
                ['Undo', 'Redo'],
                ['NumberedList', 'BulletedList', '-', 'Blockquote'],
                ['Bold', 'Italic', 'Underline'],
                '/',
                ['Link', 'Unlink'],
                ['Smiley', 'SpecialChar'],
                ['Find', 'Replace', '-', 'SelectAll', 'RemoveFormat'],
                ['Maximize', '-', 'Source']
            ]
        });
