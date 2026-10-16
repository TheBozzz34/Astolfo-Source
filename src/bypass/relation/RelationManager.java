package bypass.relation;

import bypass.manager.JsonFileManager;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import net.minecraft.entity.Entity;

public class RelationManager extends JsonFileManager<Relation, JsonObject> {
   private final HashMap<String, Relation> relationMap = new HashMap<>();

   public RelationManager(File file) {
      super(file, "relations.json");
   }

   public void loadEntry(String string, JsonObject jsonObject) {
      this.getList().add(new Relation(string, jsonObject.get("nickname").getAsString(), jsonObject.get("protected").getAsBoolean()));
   }

   @Override
   public void loadDefaults() {
   }

   public boolean isFriend(Entity entity) {
      if (entity.getCommandSenderEntity() == null) {
         return false;
      }

      if (entity == mc.thePlayer) {
         return false;
      }

      if (entity.getEntityId() < 0) {
         return true;
      }

      String string = entity.getCommandSenderEntity().getName();
      return this.relationMap.containsKey(string) && this.relationMap.get(string).isFriend();
   }

   public boolean isFriendName(String string) {
      return this.relationMap.containsKey(string) && this.relationMap.get(string).isFriend();
   }

   public boolean isEnemy(Entity entity) {
      if (entity.getCommandSenderEntity() == null) {
         return false;
      }

      if (entity == mc.thePlayer) {
         return false;
      }

      if (entity.getEntityId() < 0) {
         return true;
      }

      String string = entity.getCommandSenderEntity().getName();
      return this.relationMap.containsKey(string) && !this.relationMap.get(string).isFriend();
   }

   public boolean isEnemyName(String string) {
      return this.relationMap.containsKey(string) && !this.relationMap.get(string).isFriend();
   }

   public boolean canAdd(Relation relation) {
      this.relationMap.put(relation.getName(), relation);
      return true;
   }

   public HashMap<String, Relation> getRelationMap() {
      return this.relationMap;
   }

   @Override
   public boolean canRemove(Object object) {
      return this.relationMap.remove(object) != null;
   }

   public HashMap<String, Relation> getRelations() {
      return this.relationMap;
   }
}
