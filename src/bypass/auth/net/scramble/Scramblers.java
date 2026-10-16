package bypass.auth.net.scramble;

import java.util.ArrayList;
import java.util.List;

public enum Scramblers {
   INSTANCE;

   private final List<ScramblerRange> ranges = new ArrayList<>();

   Scramblers() {
      this.ranges.add(new ScramblerRange((byte)-120, (byte)-100, new SwapHalvesScrambler()));
      this.ranges.add(new ScramblerRange((byte)-99, (byte)-60, new SwapPairsScrambler()));
      this.ranges.add(new ScramblerRange((byte)-59, (byte)-10, new RecursiveSwapScrambler()));
      this.ranges.add(new ScramblerRange((byte)0, (byte)36, new FirstPairSwapScrambler()));
   }

   public Scrambler getScrambler(byte b) {
      for (ScramblerRange scramblerRange : this.ranges) {
         if (b >= scramblerRange.minId && b <= scramblerRange.maxId) {
            return scramblerRange.scrambler;
         }
      }

      return null;
   }

   public List<ScramblerRange> getRanges() {
      return this.ranges;
   }
}
