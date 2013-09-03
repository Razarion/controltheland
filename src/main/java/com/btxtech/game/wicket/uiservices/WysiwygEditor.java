package com.btxtech.game.wicket.uiservices;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: beat
 * Date: 11.05.12
 * Time: 12:15
 */
public class WysiwygEditor extends Panel implements IHeaderContributor {
    public WysiwygEditor(String id, IModel<String> iModel) {
        super(id, iModel);
        TextArea textArea = new TextArea<>("editor", new IModel<String>() {

            @Override
            public String getObject() {
                return (String) getDefaultModelObject();
            }

            @Override
            public void setObject(String html) {
                if (html != null && !html.isEmpty()) {
                    setDefaultModelObject(Jsoup.clean(html, Whitelist.basicWithImages().addTags("hr")));
                } else {
                    setDefaultModelObject(null);
                }
            }

            @Override
            public void detach() {
            }
        });
        add(textArea);
        String htmlEditorId = UUID.randomUUID().toString().toUpperCase();
        textArea.add(new AttributeModifier("id", htmlEditorId));
        PackageTextTemplate jsTemplate = new PackageTextTemplate(WysiwygEditor.class, "WysiwygEditor.js");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("editorId", htmlEditorId);
        Label myScript = new Label("editorScript", new JavaScriptTemplate(jsTemplate).asString(parameters));
        myScript.setEscapeModelStrings(false);
        add(myScript);
    }

    public WysiwygEditor(String id) {
        this(id, null);
    }

    @Override
    public void renderHead(IHeaderResponse iHeaderResponse) {
        iHeaderResponse.render(JavaScriptHeaderItem.forUrl("/ckeditor/ckeditor.js"));
    }

}
