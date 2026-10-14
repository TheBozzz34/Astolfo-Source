package bypass.relation;

import bypass.manager.JsonFileManager;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import net.minecraft.entity.Entity;

public class RelationManager extends JsonFileManager<Relation, JsonObject> {
   private final HashMap<String, Relation> u = new HashMap<>();
   private static final long c = 27750111532342L;

   public RelationManager(File file) {
      super(file, "relations.json");
   }

   public void t(String string, JsonObject jsonObject) {
      this.getList().add(new Relation(string, jsonObject.get("nickname").getAsString(), jsonObject.get("protected").getAsBoolean()));
   }

   @Override
   public void I() {
   }

   public boolean z(Entity entity) {
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
      return this.u.containsKey(string) && this.u.get(string).z();
   }

   public boolean A(String string) {
      return this.u.containsKey(string) && this.u.get(string).z();
   }

   public boolean D(Entity entity) {
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
      return this.u.containsKey(string) && !this.u.get(string).z();
   }

   public boolean U(String string) {
      return this.u.containsKey(string) && !this.u.get(string).z();
   }

   public boolean canAdd(Relation relation) {
      this.u.put(relation.i(), relation);
      return true;
   }

   public HashMap<String, Relation> Z() {
      return this.u;
   }

   @Override
   public boolean canRemove(Object object) {
      return this.u.remove(object) != null;
   }

   public HashMap<String, Relation> n() {
      return this.u;
   }
}
