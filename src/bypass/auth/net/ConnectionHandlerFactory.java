package bypass.auth.net;

public interface ConnectionHandlerFactory {
   ConnectionHandler createHandler(NioConnection nioConnection);
}
