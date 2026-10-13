package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class DerivedWorldInfo extends WorldInfo {
   private final WorldInfo theWorldInfo;

   public DerivedWorldInfo(WorldInfo var1) {
      this.theWorldInfo = var1;
   }

   @Override
   public NBTTagCompound getNBTTagCompound() {
      return this.theWorldInfo.getNBTTagCompound();
   }

   @Override
   public NBTTagCompound cloneNBTCompound(NBTTagCompound var1) {
      return this.theWorldInfo.cloneNBTCompound(var1);
   }

   @Override
   public long getSeed() {
      return this.theWorldInfo.getSeed();
   }

   @Override
   public int getSpawnX() {
      return this.theWorldInfo.getSpawnX();
   }

   @Override
   public int getSpawnY() {
      return this.theWorldInfo.getSpawnY();
   }

   @Override
   public int getSpawnZ() {
      return this.theWorldInfo.getSpawnZ();
   }

   @Override
   public long getWorldTotalTime() {
      return this.theWorldInfo.getWorldTotalTime();
   }

   @Override
   public long getWorldTime() {
      return this.theWorldInfo.getWorldTime();
   }

   @Override
   public long getSizeOnDisk() {
      return this.theWorldInfo.getSizeOnDisk();
   }

   @Override
   public NBTTagCompound getPlayerNBTTagCompound() {
      return this.theWorldInfo.getPlayerNBTTagCompound();
   }

   @Override
   public String getWorldName() {
      return this.theWorldInfo.getWorldName();
   }

   @Override
   public int getSaveVersion() {
      return this.theWorldInfo.getSaveVersion();
   }

   @Override
   public long getLastTimePlayed() {
      return this.theWorldInfo.getLastTimePlayed();
   }

   @Override
   public boolean isThundering() {
      return this.theWorldInfo.isThundering();
   }

   @Override
   public int getThunderTime() {
      return this.theWorldInfo.getThunderTime();
   }

   @Override
   public boolean isRaining() {
      return this.theWorldInfo.isRaining();
   }

   @Override
   public int getRainTime() {
      return this.theWorldInfo.getRainTime();
   }

   @Override
   public WorldSettings.GameType getGameType() {
      return this.theWorldInfo.getGameType();
   }

   @Override
   public void setSpawnX(int var1) {
   }

   @Override
   public void setSpawnY(int var1) {
   }

   @Override
   public void setSpawnZ(int var1) {
   }

   @Override
   public void setWorldTotalTime(long var1) {
   }

   @Override
   public void setWorldTime(long var1) {
   }

   @Override
   public void setSpawn(BlockPos var1) {
   }

   @Override
   public void setWorldName(String var1) {
   }

   @Override
   public void setSaveVersion(int var1) {
   }

   @Override
   public void setThundering(boolean var1) {
   }

   @Override
   public void setThunderTime(int var1) {
   }

   @Override
   public void setRaining(boolean var1) {
   }

   @Override
   public void setRainTime(int var1) {
   }

   @Override
   public boolean isMapFeaturesEnabled() {
      return this.theWorldInfo.isMapFeaturesEnabled();
   }

   @Override
   public boolean isHardcoreModeEnabled() {
      return this.theWorldInfo.isHardcoreModeEnabled();
   }

   @Override
   public WorldType getTerrainType() {
      return this.theWorldInfo.getTerrainType();
   }

   @Override
   public void setTerrainType(WorldType var1) {
   }

   @Override
   public boolean areCommandsAllowed() {
      return this.theWorldInfo.areCommandsAllowed();
   }

   @Override
   public void setAllowCommands(boolean var1) {
   }

   @Override
   public boolean isInitialized() {
      return this.theWorldInfo.isInitialized();
   }

   @Override
   public void setServerInitialized(boolean var1) {
   }

   @Override
   public GameRules getGameRulesInstance() {
      return this.theWorldInfo.getGameRulesInstance();
   }

   @Override
   public EnumDifficulty getDifficulty() {
      return this.theWorldInfo.getDifficulty();
   }

   @Override
   public void setDifficulty(EnumDifficulty var1) {
   }

   @Override
   public boolean isDifficultyLocked() {
      return this.theWorldInfo.isDifficultyLocked();
   }

   @Override
   public void setDifficultyLocked(boolean var1) {
   }
}
