package bypass.auth.net.scramble;

public final class ScramblerRange {
   public final byte r;
   public final byte X;
   public final Scrambler m;

   public ScramblerRange(byte b, byte b2, Scrambler scrambler) {
      this.r = b;
      this.X = b2;
      this.m = scrambler;
   }
}
