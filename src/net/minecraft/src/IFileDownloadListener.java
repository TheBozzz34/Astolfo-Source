package net.minecraft.src;

public interface IFileDownloadListener {
   void fileDownloadFinished(String string, byte[] bytes, Throwable throwable);
}
