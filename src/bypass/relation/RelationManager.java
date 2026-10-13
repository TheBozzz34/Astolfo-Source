package bypass.relation;

import bypass.manager.JsonFileManager;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import net.minecraft.entity.Entity;

public class RelationManager extends JsonFileManager<Relation, JsonObject> {
   private final HashMap<String, Relation> u = new HashMap<>();
   private static final long c = 27750111532342L;

   public RelationManager(File var1) {
      super(var1, "relations.json");
   }

   public void t(String var1, JsonObject var2) {
      this.getList().add(new Relation(var1, var2.get("nickname").getAsString(), var2.get("protected").getAsBoolean()));
   }

   @Override
   public void I() {
   }

   public boolean z(Entity var1) {
      if (var1.getCommandSenderEntity() == null) {
         return false;
      }

      if (var1 == mc.thePlayer) {
         return false;
      }

      if (var1.getEntityId() < 0) {
         return true;
      }

      String var5 = var1.getCommandSenderEntity().getName();
      return this.u.containsKey(var5) && this.u.get(var5).z();
   }

   public boolean A(String var1) {
      return this.u.containsKey(var1) && this.u.get(var1).z();
   }

   public boolean D(Entity var1) {
      if (var1.getCommandSenderEntity() == null) {
         return false;
      }

      if (var1 == mc.thePlayer) {
         return false;
      }

      if (var1.getEntityId() < 0) {
         return true;
      }

      String var5 = var1.getCommandSenderEntity().getName();
      return this.u.containsKey(var5) && !this.u.get(var5).z();
   }

   public boolean U(String var1) {
      return this.u.containsKey(var1) && !this.u.get(var1).z();
   }

   public boolean canAdd(Relation var1) {
      this.u.put(var1.i(), var1);
      return true;
   }

   public HashMap<String, Relation> Z() {
      return this.u;
   }

   @Override
   public boolean canRemove(Object var1) {
      return this.u.remove(var1) != null;
   }

   public HashMap<String, Relation> n() {
      return this.u;
   }
}
