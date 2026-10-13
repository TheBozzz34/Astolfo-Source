package bypass.via.providers;

import com.viaversion.viaversion.api.platform.providers.Provider;

public interface NativeVersionProvider extends Provider {
   int getServerProtocolVersion();
}
