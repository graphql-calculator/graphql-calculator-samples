package graphql.kickstart.servlet;

public class SubscriptionEndpoint extends GraphQLWebsocketServlet {

  public SubscriptionEndpoint() {
    super(GraphQLConfigurationProvider.getInstance().getConfiguration());
  }
}
