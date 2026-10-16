package bypass.module.combat;

public enum VelocityMode {
   PACKET,
   PULL,
   LEGIT;

   private static final VelocityMode[] VALUES = new VelocityMode[]{PACKET, PULL, LEGIT};
}
