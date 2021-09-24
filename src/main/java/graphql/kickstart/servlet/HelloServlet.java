package graphql.kickstart.servlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "HelloServlet", urlPatterns = {"graphql/*"}, loadOnStartup = 1)
public class HelloServlet extends GraphQLHttpServlet {

  @Override
  protected GraphQLConfiguration getConfiguration() {
    return GraphQLConfigurationProvider.getInstance().getConfiguration();
  }
}
