﻿/*
 Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
 For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.dialog.add('anchor', function (a) {
    var b = function (d) {
        this._.selectedElement = d;
        var e = d.data('cke-saved-name');
        this.setValueOf('info', 'txtName', e || '');
    };

    function c(d, e) {
        return d.createFakeElement(e, 'cke_anchor', 'anchor');
    }

    ;
    return{title:a.lang.anchor.title, minWidth:300, minHeight:60, onOk:function () {
        var k = this;
        var d = k.getValueOf('info', 'txtName'), e = {name:d, 'data-cke-saved-name':d};
        if (k._.selectedElement) {
            if (k._.selectedElement.data('cke-realelement')) {
                var f = c(a, a.document.createElement('a', {attributes:e}));
                f.replace(k._.selectedElement);
            } else k._.selectedElement.setAttributes(e);
        } else {
            var g = a.getSelection(), h = g && g.getRanges()[0];
            if (h.collapsed) {
                if (CKEDITOR.plugins.link.synAnchorSelector)e['class'] = 'cke_anchor_empty';
                if (CKEDITOR.plugins.link.emptyAnchorFix) {
                    e.contenteditable = 'false';
                    e['data-cke-editable'] = 1;
                }
                var i = a.document.createElement('a', {attributes:e});
                if (CKEDITOR.plugins.link.fakeAnchor)i = c(a, i);
                h.insertNode(i);
            } else {
                if (CKEDITOR.env.ie && CKEDITOR.env.version < 9)e['class'] = 'cke_anchor';
                var j = new CKEDITOR.style({element:'a', attributes:e});
                j.type = CKEDITOR.STYLE_INLINE;
                j.apply(a.document);
            }
        }
    }, onHide:function () {
        delete this._.selectedElement;
    }, onShow:function () {
        var h = this;
        var d = a.getSelection(), e = d.getSelectedElement(), f;
        if (e) {
            if (CKEDITOR.plugins.link.fakeAnchor) {
                var g = CKEDITOR.plugins.link.tryRestoreFakeAnchor(a, e);
                g && b.call(h, g);
                h._.selectedElement = e;
            } else if (e.is('a') && e.hasAttribute('name'))b.call(h, e);
        } else {
            f = CKEDITOR.plugins.link.getSelectedLink(a);
            if (f) {
                b.call(h, f);
                d.selectElement(f);
            }
        }
        h.getContentElement('info', 'txtName').focus();
    }, contents:[
        {id:'info', label:a.lang.anchor.title, accessKey:'I', elements:[
            {type:'text', id:'txtName', label:a.lang.anchor.name, required:true, validate:function () {
                if (!this.getValue()) {
                    alert(a.lang.anchor.errorName);
                    return false;
                }
                return true;
            }}
        ]}
    ]};
});
