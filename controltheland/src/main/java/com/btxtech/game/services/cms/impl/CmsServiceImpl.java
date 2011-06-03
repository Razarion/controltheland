/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.cms.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.HbHelper;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsHomeLayout;
import com.btxtech.game.services.cms.DbCmsHomeText;
import com.btxtech.game.services.cms.generated.cms.CmsFactory;
import com.btxtech.game.services.cms.generated.cms.CmsPackage;
import com.btxtech.game.services.cms.generated.cms.DbCmsImage;
import com.btxtech.game.services.cms.generated.cms.DbMenu;
import com.btxtech.game.services.cms.generated.cms.DbMenuItem;
import com.btxtech.game.services.cms.generated.cms.DbPage;
import com.btxtech.game.services.cms.generated.cms.DbPageStyle;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.utg.UserGuidanceService;

/**
 * User: beat Date: 06.07.2010 Time: 21:41:45
 */
@Component("cmsServiceImpl")
public class CmsServiceImpl implements CmsService {
	private static final String CSS = "html{height: 100%;} \nbody{margin: 0; height: 100%; background: url(cmsimg?id=4);} .menuTextClass{color: green}";
	private HibernateTemplate hibernateTemplate;
	private Log log = LogFactory.getLog(CmsServiceImpl.class);
	@Autowired
	private UserGuidanceService userGuidanceService;
	@Autowired
	private CrudRootServiceHelper<DbCmsHomeText> dbCmsHomeTextCrudRootServiceHelper;
	@Autowired
	private CrudRootServiceHelper<DbCmsHomeLayout> dbCmsHomeLayoutCrudRootServiceHelper;
	private DbCmsHomeText dbCmsHomeText;
	private DbCmsHomeLayout dbCmsHomeLayout;
	// @PersistenceContext
	// private EntityManager em;
	// @Resource(name = "entityManagerFactoryTeneo")
	// @Autowired
	// private EntityManagerFactory emf;
	@Autowired
	private SessionFactory teneoSessionFactory;

	// @PersistenceUnit(name = "entityManagerFactoryTeneo")
	// public void setEntityManagerFactory(EntityManagerFactory emf) {
	// this.emf = emf;
	// }

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	@PostConstruct
	public void init() {
		dbCmsHomeTextCrudRootServiceHelper.init(DbCmsHomeText.class);
		dbCmsHomeLayoutCrudRootServiceHelper.init(DbCmsHomeLayout.class);
		try {
			loadTeneo();
			activateHome();
		} catch (Throwable t) {
			log.error("", t);
		}
	}

	@Override
	public void activateHome() {
		@SuppressWarnings("unchecked")
		List<DbCmsHomeText> dbCmsHomeTexts = hibernateTemplate.executeFind(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(DbCmsHomeText.class);
				criteria.add(Restrictions.eq("isActive", true));
				return criteria.list();
			}
		});
		if (dbCmsHomeTexts.isEmpty()) {
			log.error("No active DbCmsHomeText found");
			return;
		}
		if (dbCmsHomeTexts.size() > 1) {
			log.info("More the one active DbCmsHomeText found. Take first one.");
		}
		dbCmsHomeText = dbCmsHomeTexts.get(0);

		@SuppressWarnings("unchecked")
		List<DbCmsHomeLayout> dbCmsHomeLayouts = hibernateTemplate.executeFind(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(DbCmsHomeLayout.class);
				criteria.add(Restrictions.eq("isActive", true));
				return criteria.list();
			}
		});
		if (dbCmsHomeLayouts.isEmpty()) {
			log.error("No active DbCmsHomeLayout found");
			return;
		}
		if (dbCmsHomeLayouts.size() > 1) {
			log.info("More the one active DbCmsHomeLayout found. Take first one.");
		}
		dbCmsHomeLayout = dbCmsHomeLayouts.get(0);
	}

	@Override
	public CrudRootServiceHelper<DbCmsHomeText> getCmsHomeTextCrudRootServiceHelper() {
		return dbCmsHomeTextCrudRootServiceHelper;
	}

	@Override
	public CrudRootServiceHelper<DbCmsHomeLayout> getCmsHomeLayoutCrudRootServiceHelper() {
		return dbCmsHomeLayoutCrudRootServiceHelper;
	}

	@Override
	public DbCmsHomeText getDbCmsHomeText() {
		return dbCmsHomeText;
	}

	@Override
	public DbCmsHomeLayout getDbCmsHomeLayout() {
		return dbCmsHomeLayout;
	}

	@Override
	public DbPage getPage(int pageId) {
		return getDefaultPage();
	}

	private void loadTeneo() {
		/*
		 * -> Unknown entity: hibernateTemplate.execute(new
		 * HibernateCallback<Void>() {
		 * 
		 * @Override public Void doInHibernate(Session session) throws
		 * HibernateException, SQLException { DbPage dbPage = getDefaultPage();
		 * session.save(dbPage); return null; } });
		 */

		try {
			Session session = teneoSessionFactory.openSession();
			Transaction transaction = session.beginTransaction();
			session.save(createDbStyle());
			transaction.commit();
			session.close();
		} catch (Exception e) {
			log.error("", e);
		}

		// EntityManager tmpEM = emf.createEntityManager();
		// EntityTransaction tx = tmpEM.getTransaction();
		/*
		 * try { tx.begin();
		 * 
		 * DbPage dbPage = getDefaultPage();
		 * 
		 * tmpEM.persist(dbPage); tx.commit(); } finally { if (null != tmpEM) {
		 * tmpEM.close(); } }
		 */
	}

	@Override
	public DbPage getDefaultPage() {
		// Style
		DbPageStyle dbPageStyle = CmsFactory.eINSTANCE.createDbPageStyle();
		dbPageStyle.setCss(CSS);
		dbPageStyle.setId(1);
		// Content
		DbPage page1 = CmsFactory.eINSTANCE.createDbPage();
		page1.setStyle(dbPageStyle);
		page1.setId(1);
		DbPage page2 = CmsFactory.eINSTANCE.createDbPage();
		page2.setStyle(dbPageStyle);
		page2.setId(2);
		DbPage page3 = CmsFactory.eINSTANCE.createDbPage();
		page3.setStyle(dbPageStyle);
		page3.setId(3);
		DbPage page4 = CmsFactory.eINSTANCE.createDbPage();
		page4.setStyle(dbPageStyle);
		page4.setId(4);
		// Menu
		DbMenu dbMenu = CmsFactory.eINSTANCE.createDbMenu();
		DbMenuItem dbMenuItem1 = CmsFactory.eINSTANCE.createDbMenuItem();
		dbMenuItem1.setPage(page1);
		dbMenuItem1.setName("page1");
		dbMenu.getMenuItems().add(dbMenuItem1);
		DbMenuItem dbMenuItem2 = CmsFactory.eINSTANCE.createDbMenuItem();
		dbMenuItem2.setPage(page2);
		dbMenuItem2.setName("page2");
		dbMenu.getMenuItems().add(dbMenuItem2);
		DbMenuItem dbMenuItem3 = CmsFactory.eINSTANCE.createDbMenuItem();
		dbMenuItem3.setPage(page3);
		dbMenuItem3.setName("page3");
		dbMenu.getMenuItems().add(dbMenuItem3);
		DbMenuItem dbMenuItem4 = CmsFactory.eINSTANCE.createDbMenuItem();
		dbMenuItem4.setPage(page4);
		dbMenuItem4.setName("page4");
		dbMenu.getMenuItems().add(dbMenuItem4);

		page1.setMenu(dbMenu);
		page2.setMenu(dbMenu);
		page3.setMenu(dbMenu);
		page4.setMenu(dbMenu);

		return page1;
	}

	@Override
	public DbPageStyle getStyle(int styleId) {
		return createDbStyle();
	}

	private DbPage createDbPage(DbPageStyle dbPageStyle) {
		DbPage page1 = CmsFactory.eINSTANCE.createDbPage();
		page1.setStyle(dbPageStyle);
		page1.setId(1);
		return page1;
	}

	private DbPageStyle createDbStyle() {
		DbPageStyle dbPageStyle = CmsFactory.eINSTANCE.createDbPageStyle();
		dbPageStyle.setCss(CSS);
		dbPageStyle.setId(1);
		return dbPageStyle;
	}

	@Override
	public DbCmsImage getDbCmsImage(int imgId) {
		DbCmsImage cmsImage = CmsFactory.eINSTANCE.createDbCmsImage();
		cmsImage.setContentType("image/gif");
		cmsImage.setData(new byte[] { -1, -40, -1, -32, 0, 16, 74, 70, 73, 70, 0, 1, 1, 1, 0, 72, 0, 72, 0, 0, -1, -37, 0, 67, 0, 5, 3, 4, 4, 4, 3, 5, 4, 4, 4, 5, 5, 5, 6, 7, 12, 8, 7, 7, 7, 7, 15,
		        11, 11, 9, 12, 17, 15, 18, 18, 17, 15, 17, 17, 19, 22, 28, 23, 19, 20, 26, 21, 17, 17, 24, 33, 24, 26, 29, 29, 31, 31, 31, 19, 23, 34, 36, 34, 30, 36, 28, 30, 31, 30, -1, -37, 0, 67,
		        1, 5, 5, 5, 7, 6, 7, 14, 8, 8, 14, 30, 20, 17, 20, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
		        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, -1, -64, 0, 17, 8, 0, 50, 0, -106, 3, 1, 34, 0, 2, 17, 1, 3, 17, 1, -1, -60, 0, 27, 0, 1, 1, 1, 1, 0, 3, 1, 0, 0,
		        0, 0, 0, 0, 0, 0, 0, 0, 5, 6, 4, 2, 3, 7, 8, -1, -60, 0, 56, 16, 0, 1, 3, 3, 0, 5, 8, 7, 9, 1, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 17, 6, 18, 19, 33, 49, 21, 34, 65, 84, 97, -110,
		        -95, -31, 22, 81, 82, 85, -111, -47, -16, 20, 35, 83, 100, 113, -127, -109, -79, -63, -94, -1, -60, 0, 26, 1, 1, 0, 2, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 3, 5, 6, 2, -1,
		        -60, 0, 48, 17, 0, 1, 3, 3, 1, 5, 5, 8, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 4, 5, 17, 33, 18, 49, 65, 81, 97, 6, -127, -95, -47, -16, 19, 20, 21, 34, 50, 66, 113, -63, 82, -111, -31,
		        -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, -4, 100, 0, 8, -128, 30, 112, 70, -23, -90, 108, 76, -58, -77, -105, 9, -98, 0, -100, 41, 107, 75, -120, 3, 122, -16, 5, 42, -37, 107,
		        105, -94, 71, 109, -111, -18, 115, -111, -83, 107, 119, -27, 79, 123, 108, 78, -43, 77, 106, -124, 69, -58, -12, 70, -7, -104, 77, 68, 96, 103, 43, 98, -37, 77, 91, -98, 88, 25, -88,
		        -22, 56, -88, -64, -75, -56, 75, -42, 83, -71, -26, 57, 9, 122, -54, 119, 60, -56, -9, -104, -71, -81, 127, 4, -82, -2, 30, 35, -51, 69, 5, 10, 107, 99, -25, -88, -107, -116, -107,
		        54, 113, -69, 85, 95, -114, 43, -40, -121, 79, 33, 59, -84, -89, 115, -52, -109, 60, 109, 56, 37, 99, -114, -45, 89, 43, 118, -104, -52, -113, -56, -13, 81, -127, 103, -112, -99, -42,
		        83, -71, -26, 120, -55, 101, -39, -58, -23, 31, 82, -120, -42, -90, 87, -103, -26, 71, -68, -59, -51, 123, 54, 106, -48, 50, 89, -30, 60, -44, -128, 82, -96, -75, 58, -86, 29, -78,
		        -53, -88, -59, 94, 110, 91, -67, 123, 78, -114, 66, -4, -57, -4, -7, -110, -22, -120, -38, 112, 74, -13, 21, -94, -78, 86, 7, -75, -102, 30, -93, -51, 69, 5, -66, 67, 78, -80, -67,
		        -45, -47, 95, 107, 109, 45, 51, -90, -37, -27, 83, 27, -107, 56, -112, 42, 35, 113, -64, 42, 100, -77, -42, 70, -62, -9, 55, 65, -82, -16, -91, -128, 12, -21, 88, -128, 0, -120, 0, 8,
		        -123, -83, 20, -95, -110, -90, -86, 73, -110, -97, 108, -40, -101, -116, 42, -31, 50, -65, 74, 69, 55, -102, 43, 111, -114, 11, 27, 38, -102, 41, 115, 46, 101, 85, 71, 97, 17, 58, 60,
		        19, 37, 58, -23, -67, -108, 93, 78, -117, -92, -20, -83, -72, -41, 87, -116, -115, 24, 54, -113, -21, -127, -29, -47, 77, -87, -92, 117, 85, -6, 42, 54, -47, -73, 86, 6, 109, 36, 98,
		        47, 28, -16, -54, -4, 14, 93, 41, -122, 58, 56, -94, -119, -76, -116, -126, 71, -82, -74, 81, -39, 92, 39, -41, -127, 103, 69, -87, 89, 84, -54, -85, -108, -112, -54, -87, 81, 42,
		        -20, -16, -18, 13, 78, 31, 93, -122, 99, 73, -89, 100, -9, -103, -10, 104, -87, 28, 107, -77, 110, 87, 60, 56, -8, -28, -83, 78, 75, -89, -40, -32, -47, -81, -25, -5, -26, -73, 87,
		        118, 71, 5, -80, -44, -111, -13, 76, -17, -105, 119, -45, -61, -19, 31, 104, 27, -120, -34, -89, 107, 59, -38, 95, -119, -84, -74, -38, -46, 43, 75, 42, 42, 104, 90, -2, 98, -56, -25,
		        -67, -40, -35, -57, -6, 51, 118, -86, 85, -83, -72, -63, 74, -120, -85, -76, 122, 34, -29, -114, 58, 124, 50, 110, 47, -76, -47, 108, 32, -95, -118, 41, 91, 37, 84, -119, 26, 101,
		        -39, -61, 83, 123, -105, 31, -94, 120, -103, 43, 101, -61, -101, 24, 59, -11, -18, 84, -5, 47, 111, -10, -112, -51, 86, -10, -28, 55, 65, -43, -57, -122, -96, -15, -64, -17, 81, -19,
		        -108, 49, -70, -115, -78, -66, -110, -107, -50, -109, 47, -25, 72, -12, 84, 69, -32, -104, 70, -81, 71, 105, 34, 91, 93, -51, -14, 57, -23, 76, -83, 69, 85, 84, 106, 61, -72, 78, -50,
		        38, -54, -7, 89, 79, 103, -94, -114, 69, -95, 107, -14, -28, 99, 89, -83, -85, -47, -5, -111, 61, 44, -121, -35, 12, -2, 117, -7, 24, 32, -102, 119, 101, -15, -73, 32, -11, -1, 0, 86,
		        -46, -23, 110, -75, 64, 89, 77, 87, 62, -61, -102, 56, 52, -15, -26, 67, 10, -115, -55, 55, 79, -64, 119, 125, -65, 51, -107, -16, -50, -38, -97, -77, 57, -82, -38, -85, -111, -70,
		        -88, -71, -34, -67, 6, -113, -46, -56, 125, -48, -49, -25, 95, -111, -63, -93, 17, -74, -69, 72, 90, -6, -116, -86, -86, -70, 77, 86, -82, 21, -50, -11, 39, -9, -5, 23, 27, 52, -51,
		        107, -99, 43, 112, 0, -11, -59, 115, -45, -37, -19, -46, -51, 12, 20, 51, 23, -105, -69, 7, 57, 24, 29, -19, 31, -75, 78, -29, 111, -114, -35, 105, 124, -110, 80, 70, -118, -58, 35,
		        81, -18, 118, 85, 92, -69, -77, -2, -103, 61, 103, 123, 75, -15, 62, -99, 83, 108, -93, -88, -115, 25, 61, 36, -81, 106, 46, 112, -81, -23, -8, 28, -2, -113, -38, 125, -36, -2, -15,
		        66, -98, -30, -56, -38, 118, -14, 79, -82, -85, -83, -69, -10, 58, -90, -82, 86, -102, 114, -42, -76, 12, 99, 92, -8, 52, 44, 110, -115, -47, -55, 93, 94, -65, 116, -77, 50, 54, -21,
		        57, -86, -72, 79, 82, 125, 118, 30, -19, 41, -118, 58, 105, 98, -90, 101, 43, 32, 126, 53, -35, -123, -54, -29, -126, 127, -90, -62, 59, 109, -74, -126, 41, 36, 109, 51, -32, -113,
		        25, 123, -106, 68, 68, -35, -21, 62, 127, 119, -87, 109, 93, -54, 121, -29, 69, 72, -36, -20, 49, 23, -114, -86, 110, 66, -59, 60, -58, -90, 98, -31, -12, -113, 92, -42, -110, -13,
		        109, 109, -106, -38, -40, 36, 32, -54, -13, -31, -45, 45, -49, 33, -65, -102, -28, 0, 27, 53, -60, 32, 0, 34, 0, 2, 32, 0, 34, 0, 2, 32, 0, 34, 0, 2, 32, 0, 34, 0, 2, 32, 0, 34, 0, 2,
		        32, 0, 34, 0, 2, 32, 0, 34, 0, 2, 32, 0, 34, 0, 2, 32, 0, 34, 0, 2, 32, 0, 34, 0, 2, 32, 0, 34, -1, -39 });
		return cmsImage;
	}

	private void loadFromDb() {

		// The hibernate properties can be set by having a hibernate.properties
		// file in the root of
		// the classpath.
		// Another approach is setting the properties in the HbDataStore.
		// For more information see section 3.1 of the Hibernate manual
		// final Properties props = new Properties();
		// props.setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");
		// props.setProperty(Environment.USER, "root");
		// props.setProperty(Environment.URL, "jdbc:mysql://127.0.0.1:3306/" +
		// dbName);
		// props.setProperty(Environment.PASS, "root");
		// props.setProperty(Environment.DIALECT,
		// org.hibernate.dialect.MySQLInnoDBDialect.class.getName());

		// props.setProperty(Environment.DRIVER, "org.hsqldb.jdbcDriver");
		// props.setProperty(Environment.USER, "sa");
		// props.setProperty(Environment.URL, "jdbc:hsqldb:mem:library");
		// props.setProperty(Environment.PASS, "");
		// props.setProperty(Environment.DIALECT,
		// org.hibernate.dialect.HSQLDialect.class.getName());

		// set a specific option
		// see this page
		// http://wiki.eclipse.org/Teneo/Hibernate/Configuration_Options
		// for all the available options
		// TODO
		// props.setProperty(PersistenceOptions.CASCADE_POLICY_ON_NON_CONTAINMENT,
		// "REFRESH,PERSIST,MERGE");

		// the name of the session factory
		String hbName = "Library";
		// create the HbDataStore using the name
		final HbDataStore hbds = HbHelper.INSTANCE.createRegisterDataStore(hbName);

		// set the properties
		// TODO hbds.setDataStoreProperties(props);

		// sets its epackages stored in this datastore
		// TODO hbds.setEPackages(new EPackage[] { ExtlibraryPackage.eINSTANCE
		// });
		hbds.setEPackages(new EPackage[] { CmsPackage.eINSTANCE });

		// initialize, also creates the database tables
		try {
			hbds.initialize();
		} finally {
			// print the generated mapping
			System.err.println(hbds.getMappingXML());
		}
		// TODO
		/*
		 * SessionFactory sessionFactory = hbds.getSessionFactory();
		 * 
		 * // Create a session and a transaction Session session =
		 * sessionFactory.openSession(); Transaction tx =
		 * session.getTransaction();
		 * 
		 * // Start a transaction, create a library and make it persistent
		 * tx.begin(); Library lib =
		 * ExtlibraryFactory.eINSTANCE.createLibrary();
		 * lib.setName("My Library"); session.save(lib);
		 * 
		 * // create a writer Writer writer =
		 * ExtlibraryFactory.eINSTANCE.createWriter();
		 * writer.setName("JRR Tolkien");
		 * 
		 * // and one of his books Book book =
		 * ExtlibraryFactory.eINSTANCE.createBook(); book.setAuthor(writer);
		 * book.setPages(305); book.setTitle("The Hobbit");
		 * book.setCategory(BookCategory.SCIENCE_FICTION); session.save(book);
		 * 
		 * // add the writer/book to the library. lib.getWriters().add(writer);
		 * lib.getBooks().add(book);
		 * 
		 * // at commit the objects will be present in the database tx.commit();
		 * // and close of, this should actually be done in a finally block
		 * session.close();
		 * 
		 * return hbds;
		 */
	}
}
