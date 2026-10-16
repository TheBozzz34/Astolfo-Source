package bypass.manager;

import com.google.gson.JsonElement;

public interface JsonSerializable<T extends JsonElement> {
   String getKey();

   T toJson();
}
