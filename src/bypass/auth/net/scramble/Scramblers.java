package bypass.auth.net.scramble;

import java.util.ArrayList;
import java.util.List;

public enum Scramblers {
   INSTANCE;

   private final List<ScramblerRange> e = new ArrayList<>();
   private static final long a = 69050655182956L;

   Scramblers() {
      this.e.add(new ScramblerRange((byte)-120, (byte)-100, new SwapHalvesScrambler()));
      this.e.add(new ScramblerRange((byte)-99, (byte)-60, new SwapPairsScrambler()));
      this.e.add(new ScramblerRange((byte)-59, (byte)-10, new RecursiveSwapScrambler()));
      this.e.add(new ScramblerRange((byte)0, (byte)36, new FirstPairSwapScrambler()));
   }

   public Scrambler w(byte var1) {
      for (ScramblerRange var6 : this.e) {
         if (var1 >= var6.r && var1 <= var6.X) {
            return var6.m;
         }
      }

      return null;
   }

   public List<ScramblerRange> Q() {
      return this.e;
   }
}
