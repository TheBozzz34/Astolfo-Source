package bypass.loader;

import bypass.auth.AuthHttpRequest;
import bypass.auth.AuthUtil;
import bypass.loader.check.StringListHolder;
import bypass.util.Holder;
import net.minecraft.entity.monster.EntitySkeleton;

public class IntegrityCheck {
   private final StringListHolder hashList;
   private boolean flag1;
   private boolean flag2 = true;
   private boolean flag3;
   private boolean flag4;
   private boolean flag5 = true;
   private boolean flag6;
   private boolean flag7;
   private boolean flag8;

   public IntegrityCheck(StringListHolder stringListHolder) {
      this.hashList = stringListHolder;
   }

   public Holder performIntegrityCheck() {
      this.setFlag5(true);
      Holder holder = new Holder();
      holder.value = new EntitySkeleton(3298775);
      this.setFlag1(true);
      AuthHttpRequest authHttpRequest = new AuthHttpRequest()
         .setUrl("https://security.astolfo.lgbt/api/md5")
         .setParamNames("hash")
         .setParamValues(this.hashList.getStringContainer().getOwnedContainer().m_Iterator().next())
         .setContentType("application/x-www-form-urlencoded")
         .setUserAgent("aa4hu3ib2");
      this.setFlag4(true);
      this.setFlag5(!this.isFlag4());
      if (!AuthUtil.httpPost(authHttpRequest).contains("Valid") && (holder.value = new EntitySkeleton(3298776)) != null) {
      }

      this.setFlag2(this.getFlag5());
      this.setFlag6(true);
      return holder;
   }

   public void setFlag1(boolean bl) {
      this.flag1 = bl;
   }

   public void setFlag2(boolean bl) {
      this.flag2 = bl;
   }

   public void setFlag3(boolean bl) {
      this.flag3 = bl;
   }

   public void setFlag4(boolean bl) {
      this.flag4 = bl;
   }

   public void setFlag5(boolean bl) {
      this.flag5 = bl;
   }

   public void setFlag6(boolean bl) {
      this.flag6 = bl;
   }

   public void setFlag7(boolean bl) {
      this.flag7 = bl;
   }

   public void setFlag8(boolean bl) {
      this.flag8 = bl;
   }

   public boolean isFlag1() {
      return this.flag1;
   }

   public boolean getFlag1() {
      return this.flag1;
   }

   public boolean isFlag6() {
      return this.flag6;
   }

   public boolean getFlag6() {
      return this.flag6;
   }

   public boolean isFlag5() {
      return this.flag5;
   }

   public boolean getFlag5() {
      return this.flag5;
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean isFlag7() {
      return this.flag7;
   }

   public boolean isFlag8() {
      return this.flag8;
   }
}
