package oshi.jna.platform.unix;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.ptr.PointerByReference;

public interface CLibrary extends LibCAPI, Library {
   int AI_CANONNAME = 2;

   int getpid();

   int getaddrinfo(String string, String string2, CLibrary.Addrinfo addrinfo, PointerByReference pointerByReference);

   void freeaddrinfo(Pointer pointer);

   String gai_strerror(int i);

   @FieldOrder({"ai_flags", "ai_family", "ai_socktype", "ai_protocol", "ai_addrlen", "ai_addr", "ai_canonname", "ai_next"})
   class Addrinfo extends Structure {
      public int ai_flags;
      public int ai_family;
      public int ai_socktype;
      public int ai_protocol;
      public int ai_addrlen;
      public CLibrary.SockaddrByReference ai_addr;
      public String ai_canonname;
      public CLibrary.Addrinfo.ByReference ai_next;

      public Addrinfo() {
      }

      public Addrinfo(Pointer p) {
         super(p);
         this.read();
      }

      public static class ByReference extends CLibrary.Addrinfo implements com.sun.jna.Structure.ByReference {
      }
   }

   @FieldOrder({"sa_family", "sa_data"})
   class Sockaddr extends Structure {
      public short sa_family;
      public byte[] sa_data = new byte[14];
   }

   class SockaddrByReference extends CLibrary.Sockaddr implements com.sun.jna.Structure.ByReference {
   }

   @FieldOrder({"tv_sec", "tv_usec"})
   class Timeval extends Structure {
      public long tv_sec;
      public long tv_usec;
   }
}
