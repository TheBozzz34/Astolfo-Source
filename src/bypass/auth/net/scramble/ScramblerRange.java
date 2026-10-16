package bypass.auth.net.scramble;

public final class ScramblerRange {
   public final byte minId;
   public final byte maxId;
   public final Scrambler scrambler;

   public ScramblerRange(byte b, byte b2, Scrambler scrambler) {
      this.minId = b;
      this.maxId = b2;
      this.scrambler = scrambler;
   }
}
