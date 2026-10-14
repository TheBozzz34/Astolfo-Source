package bypass.auth.net;

public interface ConnectionHandlerFactory {
   ConnectionHandler y(NioConnection nioConnection);
}
