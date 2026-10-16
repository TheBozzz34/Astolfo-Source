package bypass.loader.check;

public interface VerdictProcessor {
   Verdict process(CheckRequest checkRequest, Verdict verdict);
}
