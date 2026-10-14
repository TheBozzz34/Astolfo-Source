package net.minecraft.client.gui.spectator.categories;

import com.google.common.base.Objects;
import java.util.List;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;

public class SpectatorDetails {
   private final ISpectatorMenuView field_178684_a;
   private final List<ISpectatorMenuObject> field_178682_b;
   private final int field_178683_c;

   public SpectatorDetails(ISpectatorMenuView iSpectatorMenuView, List<ISpectatorMenuObject> list, int i) {
      this.field_178684_a = iSpectatorMenuView;
      this.field_178682_b = list;
      this.field_178683_c = i;
   }

   public ISpectatorMenuObject func_178680_a(int i) {
      return i >= 0 && i < this.field_178682_b.size()
         ? Objects.firstNonNull(this.field_178682_b.get(i), SpectatorMenu.field_178657_a)
         : SpectatorMenu.field_178657_a;
   }

   public int func_178681_b() {
      return this.field_178683_c;
   }
}
