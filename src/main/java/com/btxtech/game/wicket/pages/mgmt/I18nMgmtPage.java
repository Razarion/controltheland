package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.db.DbI18nString;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * User: beat
 * Date: 09.01.13
 * Time: 12:03
 */
public class I18nMgmtPage extends MgmtWebPage {
    @SpringBean
    private SessionFactory sessionFactory;
    @SpringBean
    private PlatformTransactionManager transactionManager;

    public I18nMgmtPage() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        final DetachHashListProvider<DbI18nString> provider = new DetachHashListProvider<DbI18nString>() {
            @Override
            protected List<DbI18nString> createList() {
                List<DbI18nString> dbI18nStrings = HibernateUtil.loadAll(sessionFactory, DbI18nString.class);
                for (Iterator<DbI18nString> iterator = dbI18nStrings.iterator(); iterator.hasNext(); ) {
                    DbI18nString dbI18nString = iterator.next();
                    if (dbI18nString.isEmpty()) {
                        iterator.remove();
                    }
                }
                return dbI18nStrings;
            }
        };

        form.add(new DataView<DbI18nString>("table", provider) {
            @Override
            protected void populateItem(final Item<DbI18nString> item) {
                item.add(new Label("id", new Model<>(item.getModelObject().getId())));
                item.add(new TextArea<>("default", new IModel<String>() {

                    @Override
                    public String getObject() {
                        return item.getModelObject().getString();
                    }

                    @Override
                    public void setObject(String string) {
                        item.getModelObject().putString(string);
                    }

                    @Override
                    public void detach() {
                        // Ignore
                    }
                }));
                item.add(new TextArea<>("german", new IModel<String>() {

                    @Override
                    public String getObject() {
                        return item.getModelObject().getStringNoFallback(Locale.GERMAN);
                    }

                    @Override
                    public void setObject(String string) {
                        item.getModelObject().putString(Locale.GERMAN, string);
                    }

                    @Override
                    public void detach() {
                        // Ignore
                    }
                }));
            }
        });

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    public void doInTransactionWithoutResult(TransactionStatus status) {
                        HibernateUtil.saveOrUpdateAll(sessionFactory, provider.getList());
                    }
                });
            }
        });

        form.add(new Button("cancel") {
            @Override
            public void onSubmit() {
                setResponsePage(MgmtPage.class);
            }
        });
    }
}
