package net.minecraftforge.fml.common.registry;

import net.minecraft.util.ResourceLocation;

public interface RegistryDelegate<T> {
   T f();

   ResourceLocation S();

   Class<T> D();
}
