package bypass.http;

public enum ContentType {
   ATOM_FEEDS(MediaType.APPLICATION, "atom+xml"),
   ECMASCRIPT(MediaType.APPLICATION, "ecmascript"),
   EDI_X12(MediaType.APPLICATION, "EDI-X12"),
   EDIFACT(MediaType.APPLICATION, "EDIFACT"),
   JSON(MediaType.APPLICATION, "json"),
   JAVASCRIPT(MediaType.APPLICATION, "javascript"),
   OCTET_STREAM(MediaType.APPLICATION, "octet-stream"),
   OGG_APPLICATION(MediaType.APPLICATION, "ogg"),
   PDF(MediaType.APPLICATION, "pdf"),
   POSTSCRIPT(MediaType.APPLICATION, "postscript"),
   RDF(MediaType.APPLICATION, "rdf+xml"),
   RSS(MediaType.APPLICATION, "rss+xml"),
   SOAP(MediaType.APPLICATION, "soap+xml"),
   FONT_WOFF(MediaType.APPLICATION, "font-woff"),
   XHTML(MediaType.APPLICATION, "xhtml+xml"),
   XML_APPLICATION(MediaType.APPLICATION, "xml"),
   DTD(MediaType.APPLICATION, "xml-dtd"),
   XOP(MediaType.APPLICATION, "xop+xml"),
   ZIP(MediaType.APPLICATION, "zip"),
   GZIP(MediaType.APPLICATION, "gzip"),
   APPLICATION_EXAMPLE(MediaType.APPLICATION, "example"),
   NACL(MediaType.APPLICATION, "x-nacl"),
   URL_ENCODED(MediaType.APPLICATION, "x-www-form-urlencoded"),
   BASIC_AUDIO(MediaType.AUDIO, "basic"),
   L24(MediaType.AUDIO, "L24"),
   MP4_AUDIO(MediaType.AUDIO, "mp4"),
   MPEG_AUDIO(MediaType.AUDIO, "mpeg"),
   OGG_AUDIO(MediaType.AUDIO, "ogg"),
   OPUS(MediaType.AUDIO, "opus"),
   VORBIS(MediaType.AUDIO, "vorbis"),
   REALAUDIO(MediaType.AUDIO, "vnd.rn-realaudio"),
   WAV(MediaType.AUDIO, "vnd.wave"),
   WEBM_AUDIO(MediaType.AUDIO, "webm"),
   AUDIO_EXAMPLE(MediaType.AUDIO, "example"),
   EXAMPLE(MediaType.EXAMPLE, ""),
   GIF(MediaType.IMAGE, "gif"),
   JPEG(MediaType.IMAGE, "jpeg"),
   PJPEG(MediaType.IMAGE, "pjpeg"),
   X_JPS(MediaType.IMAGE, "x-jps"),
   PNG(MediaType.IMAGE, "png"),
   X_QUICKTIME(MediaType.IMAGE, "x-quicktime"),
   SVG(MediaType.IMAGE, "svg+xml"),
   IMAGE_EXAMPLE(MediaType.IMAGE, "example"),
   HTTP(MediaType.MESSAGE, "http"),
   IMDN(MediaType.MESSAGE, "imdn+xml"),
   PARTIAL(MediaType.MESSAGE, "partial"),
   EML(MediaType.MESSAGE, "rfc882"),
   MESSAGE_EXAMPLE(MediaType.MESSAGE, "example"),
   IGES(MediaType.MODEL, "iges"),
   IGS(MediaType.MODEL, "iges"),
   MESH(MediaType.MODEL, "mesh"),
   VRML(MediaType.MODEL, "vrml"),
   X3D(MediaType.MODEL, "x3d"),
   X3D_BINARY(MediaType.MODEL, "x3d+binary"),
   X3D_FAST_INFOSET(MediaType.MODEL, "x3d+fastinfoset"),
   X3D_VRML(MediaType.MODEL, "x3d-vrml"),
   X3D_XML(MediaType.MODEL, "x3d+xml"),
   MODEL_EXAMPLE(MediaType.MODEL, "example"),
   MIXED(MediaType.MULTIPART, "mixed"),
   ALTERNATIVE(MediaType.MULTIPART, "alternative"),
   RELATED(MediaType.MULTIPART, "related"),
   FORM_DATA(MediaType.MULTIPART, "form-data"),
   SIGNED(MediaType.MULTIPART, "signed"),
   X_ZIP(MediaType.MULTIPART, "x-zip"),
   ENCRYPTED(MediaType.MULTIPART, "encrypted"),
   MULTIPART_EXAMPLE(MediaType.MULTIPART, "example"),
   COMMAND(MediaType.TEXT, "cmd"),
   CSS(MediaType.TEXT, "css"),
   CSV(MediaType.TEXT, "csv"),
   PLAIN(MediaType.TEXT, "plain"),
   X_C(MediaType.TEXT, "x-c"),
   TEXT_EXAMPLE(MediaType.TEXT, "example"),
   X_SCRIPT_PHYTON(MediaType.TEXT, "x-script.phyton"),
   HTML(MediaType.TEXT, "html"),
   JAVASCRIPT_DEPRECATED(MediaType.TEXT, "javascript"),
   PLAIN_TEXT(MediaType.TEXT, "plain"),
   RTF(MediaType.TEXT, "rtf"),
   VCARD(MediaType.TEXT, "vcard"),
   ABC_MUSIC(MediaType.TEXT, "vnd.abc"),
   XML_TEXT(MediaType.TEXT, "xml"),
   AVI(MediaType.VIDEO, "avi"),
   VIDEO_EXAMPLE(MediaType.VIDEO, "example"),
   MPEG_1_VIDEO(MediaType.VIDEO, "mpeg"),
   MPEG_VIDEO(MediaType.VIDEO, "mpeg"),
   MP4_VIDEO(MediaType.VIDEO, "mp4"),
   OGG_VIDEO(MediaType.VIDEO, "ogg"),
   QUICKTIME(MediaType.VIDEO, "quicktime"),
   WEBM_VIDEO(MediaType.VIDEO, "webm"),
   MATROSKA(MediaType.VIDEO, "x-matroska"),
   MSVIDEO(MediaType.VIDEO, "msvideo"),
   X_MSVIDEO(MediaType.VIDEO, "video/x-msvideo"),
   AVS(MediaType.VIDEO, "video/avs-video"),
   WMV(MediaType.VIDEO, "x-ms-wmv"),
   FLV(MediaType.VIDEO, "x-flv");

   private final String u;
   private final String a;
   private final MediaType p;
   private static final long b = 18057567985023L;

   ContentType(MediaType var3, String var4) {
      this.u = var4;
      this.p = var3;
      this.a = this.T().e().concat(!var4.isEmpty() ? "/" : "").concat(var4);
   }

   public static ContentType d(String var0) {
      String var4 = var0.replace(";", "");
      if (var0.contains(":")) {
         var4 = var0.substring(var0.indexOf(58));
      }

      for (ContentType var8 : values()) {
         if (var8.S().trim().equalsIgnoreCase(var0.trim()) || var8.S().trim().equalsIgnoreCase(var4.trim())) {
            return var8;
         }
      }

      return null;
   }

   public MediaType T() {
      return this.p;
   }

   public String N() {
      return this.u;
   }

   public String S() {
      return this.a;
   }

   @Override
   public String toString() {
      return this.S();
   }
}
