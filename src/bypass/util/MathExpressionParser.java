package bypass.util;

public final class MathExpressionParser {
   public int pos;
   public int ch;
   public final String expression;

   public MathExpressionParser(String string) {
      this.expression = string;
      this.pos = -1;
   }

   public void nextChar() {
      this.ch = ++this.pos < this.expression.length() ? this.expression.charAt(this.pos) : -1;
   }

   public boolean eat(int i) {
      while (this.ch == 32) {
         this.nextChar();
      }

      if (this.ch == i) {
         this.nextChar();
         return true;
      } else {
         return false;
      }
   }

   public double parse() {
      this.nextChar();
      double d = this.parseExpression();
      if (this.pos < this.expression.length()) {
         throw new RuntimeException("Unexpected: " + (char)this.ch);
      } else {
         return d;
      }
   }

   public double parseExpression() {
      double d = this.parseTerm();

      while (true) {
         while (!this.eat(43)) {
            if (!this.eat(45)) {
               return d;
            }

            d -= this.parseTerm();
         }

         d += this.parseTerm();
      }
   }

   public double parseTerm() {
      double d = this.parseFactor();

      while (true) {
         while (!this.eat(42)) {
            if (!this.eat(47)) {
               return d;
            }

            d /= this.parseFactor();
         }

         d *= this.parseFactor();
      }
   }

   public double parseFactor() {
      if (this.eat(43)) {
         return this.parseFactor();
      }

      if (this.eat(45)) {
         return -this.parseFactor();
      }

      int i = this.pos;
      double d;
      if (this.eat(40)) {
         d = this.parseExpression();
         this.eat(41);
      } else if ((this.ch < 48 || this.ch > 57) && this.ch != 46) {
         if (this.ch < 97 || this.ch > 122) {
            throw new RuntimeException("Unexpected: " + (char)this.ch);
         }

         while (this.ch >= 97 && this.ch <= 122) {
            this.nextChar();
         }

         String string = this.expression.substring(i, this.pos);
         d = this.parseFactor();
         switch (string) {
            case "sqrt":
               d = Math.sqrt(d);
               break;
            case "sin":
               d = Math.sin(Math.toRadians(d));
               break;
            case "cos":
               d = Math.cos(Math.toRadians(d));
               break;
            case "tan":
               d = Math.tan(Math.toRadians(d));
               break;
            default:
               throw new RuntimeException("Unknown function: " + string);
         }
      } else {
         while (this.ch >= 48 && this.ch <= 57 || this.ch == 46) {
            this.nextChar();
         }

         d = Double.parseDouble(this.expression.substring(i, this.pos));
      }

      if (this.eat(94)) {
         d = Math.pow(d, this.parseFactor());
      }

      return d;
   }
}
