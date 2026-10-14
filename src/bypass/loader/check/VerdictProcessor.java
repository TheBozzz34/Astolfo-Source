package bypass.loader.check;

public interface VerdictProcessor {
   Verdict K(CheckRequest checkRequest, Verdict verdict);
}
