package bypass.module.combat;

public enum KillAuraSortingMode {
   DISTANCE,
   HEALTH,
   AIM,
   ARMOR,
   DANGEROUS;

   private static final KillAuraSortingMode[] VALUES = new KillAuraSortingMode[]{DISTANCE, HEALTH, AIM, ARMOR, DANGEROUS};
}
