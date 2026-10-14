package net.minecraft.src;

public interface IFileUploadListener {
   void fileUploadFinished(String string, byte[] bytes, Throwable throwable);
}
