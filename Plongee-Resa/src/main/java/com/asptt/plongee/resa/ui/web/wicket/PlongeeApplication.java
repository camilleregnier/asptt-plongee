package com.asptt.plongee.resa.ui.web.wicket;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.asptt.plongee.resa.ui.web.wicket.page.AccueilPage;
import com.asptt.plongee.resa.ui.web.wicket.page.LoginPage;

public class PlongeeApplication extends AuthenticatedWebApplication {

	private ApplicationContext ctx;
	
	protected void init() {
		super.init();

		// démarrage du contexte Spring (injection des dépendances)
		ctx = new ClassPathXmlApplicationContext("/spring/spring-service-impl.xml", "/spring/spring-dao-jdbc.xml", "/spring/spring-datasource.xml");
		
		// setting page that Wicket will display if user has no rights to access
		// a page
		getApplicationSettings().setAccessDeniedPage(LoginPage.class);
		// setting authorization strategy (you can use any strategy you like)
		// getSecuritySettings().setAuthorizationStrategy( new
		// RoleAuthorizationStrategy( new MyRoleCheckingStrategy() ) );
		// mounting login page so that it can be referred to in the security
		// constraint
		mountBookmarkablePage("/login", LoginPage.class);		
	}

	public Session newSession(Request request, Response response) {
		return new ResaSession(request, ctx);
	}

	/**
	 * Overriding newWebRequest so that to store take user information from
	 * servletRequest and put it into wicket session.
	 */
	@Override
	/*protected WebRequest newWebRequest(final HttpServletRequest servletRequest) {
		final WebRequest webRequest = super.newWebRequest(servletRequest);
		final Session session = getSessionStore().lookup(webRequest);
		if (session != null) {
			 Save user info into session.
			 ( ( PlongeeSession )session ).takeUserFromRequest( servletRequest
			 );
		}
		return webRequest;
	}*/

	public Class<? extends WebPage> getHomePage() {
		return AccueilPage.class;
	}

	@Override
	protected Class<? extends SignInPage> getSignInPageClass() {
		return LoginPage.class;
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return ResaSession.class;
	}
}
