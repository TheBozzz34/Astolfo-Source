package net.minecraft.client.shader;

import java.io.IOException;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.JsonException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderLinkHelper {
   private static final Logger logger = LogManager.getLogger();
   private static ShaderLinkHelper staticShaderLinkHelper;
   private static final long a = 104423840118040L;
   private static final long e = 5278616030573264896L;

   public static void setNewStaticShaderLinkHelper() {
      staticShaderLinkHelper = new ShaderLinkHelper();
   }

   public static ShaderLinkHelper getStaticShaderLinkHelper() {
      return staticShaderLinkHelper;
   }

   public void deleteShader(ShaderManager var1) {
      var1.getFragmentShaderLoader().deleteShader(var1);
      var1.getVertexShaderLoader().deleteShader(var1);
      OpenGlHelper.glDeleteProgram(var1.getProgram());
   }

   public int createProgram() throws JsonException {
      int var4 = OpenGlHelper.glCreateProgram();
      if (var4 <= 0) {
         throw new JsonException("Could not create shader program (returned program ID " + var4 + ")");
      } else {
         return var4;
      }
   }

   public void linkProgram(ShaderManager var1) throws IOException {
      var1.getFragmentShaderLoader().attachShader(var1);
      var1.getVertexShaderLoader().attachShader(var1);
      OpenGlHelper.glLinkProgram(var1.getProgram());
      int var5 = OpenGlHelper.glGetProgrami(var1.getProgram(), OpenGlHelper.GL_LINK_STATUS);
      if (var5 == 0) {
         logger.warn(
            "Error encountered when linking program containing VS "
               + var1.getVertexShaderLoader().getShaderFilename()
               + " and FS "
               + var1.getFragmentShaderLoader().getShaderFilename()
               + ". Log output:"
         );
         logger.warn(OpenGlHelper.glGetProgramInfoLog(var1.getProgram(), (int)e));
      }
   }
}
