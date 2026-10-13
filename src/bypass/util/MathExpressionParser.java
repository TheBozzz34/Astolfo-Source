package bypass.util;

public final class MathExpressionParser {
   public int pos;
   public int ch;
   public final String expression;
   private static final long a = 11678934009726L;

   public MathExpressionParser(String var1) {
      this.expression = var1;
      this.pos = -1;
   }

   public void nextChar() {
      this.ch = ++this.pos < this.expression.length() ? this.expression.charAt(this.pos) : -1;
   }

   public boolean eat(int var1) {
      while (this.ch == 32) {
         this.nextChar();
      }

      if (this.ch == var1) {
         this.nextChar();
         return true;
      } else {
         return false;
      }
   }

   public double parse() {
      this.nextChar();
      double var3 = this.parseExpression();
      if (this.pos < this.expression.length()) {
         throw new RuntimeException("Unexpected: " + (char)this.ch);
      } else {
         return var3;
      }
   }

   public double parseExpression() {
      double var4 = this.parseTerm();

      while (true) {
         while (!this.eat(43)) {
            if (!this.eat(45)) {
               return var4;
            }

            var4 -= this.parseTerm();
         }

         var4 += this.parseTerm();
      }
   }

   public double parseTerm() {
      double var4 = this.parseFactor();

      while (true) {
         while (!this.eat(42)) {
            if (!this.eat(47)) {
               return var4;
            }

            var4 /= this.parseFactor();
         }

         var4 *= this.parseFactor();
      }
   }

   public double parseFactor() {
      if (this.eat(43)) {
         return this.parseFactor();
      }

      if (this.eat(45)) {
         return -this.parseFactor();
      }

      int var6 = this.pos;
      double var4;
      if (this.eat(40)) {
         var4 = this.parseExpression();
         this.eat(41);
      } else if ((this.ch < 48 || this.ch > 57) && this.ch != 46) {
         if (this.ch < 97 || this.ch > 122) {
            throw new RuntimeException("Unexpected: " + (char)this.ch);
         }

         while (this.ch >= 97 && this.ch <= 122) {
            this.nextChar();
         }

         String var7 = this.expression.substring(var6, this.pos);
         var4 = this.parseFactor();
         switch (var7) {
            case "sqrt":
               var4 = Math.sqrt(var4);
               break;
            case "sin":
               var4 = Math.sin(Math.toRadians(var4));
               break;
            case "cos":
               var4 = Math.cos(Math.toRadians(var4));
               break;
            case "tan":
               var4 = Math.tan(Math.toRadians(var4));
               break;
            default:
               throw new RuntimeException("Unknown function: " + var7);
         }
      } else {
         while (this.ch >= 48 && this.ch <= 57 || this.ch == 46) {
            this.nextChar();
         }

         var4 = Double.parseDouble(this.expression.substring(var6, this.pos));
      }

      if (this.eat(94)) {
         var4 = Math.pow(var4, this.parseFactor());
      }

      return var4;
   }
}
