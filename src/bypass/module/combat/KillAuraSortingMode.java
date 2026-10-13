package bypass.module.combat;

public enum KillAuraSortingMode {
   DISTANCE,
   HEALTH,
   AIM,
   ARMOR,
   DANGEROUS;

   private static final KillAuraSortingMode[] W = new KillAuraSortingMode[]{DISTANCE, HEALTH, AIM, ARMOR, DANGEROUS};
}
