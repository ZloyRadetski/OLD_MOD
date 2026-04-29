package net.portalmod.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class DataUtil {
   public static byte[] readInputStream(InputStream is) throws IOException {
      byte[] data = new byte[1024];
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

      int readCount;
      while((readCount = is.read(data, 0, data.length)) != -1) {
         buffer.write(data, 0, readCount);
      }

      return buffer.toByteArray();
   }

   public static String computeChecksum(byte[] data) throws IOException {
      return Hex.encodeHexString(DigestUtils.md5(data));
   }

   public static File tryCreateFolder(File folder) throws IOException {
      if (!folder.exists() && !folder.mkdirs()) {
         throw new IOException("Failed to create folder: " + folder.getName());
      } else {
         return folder;
      }
   }

   public static File tryCreateFolderAndGetFile(File folder, String filename) throws IOException {
      return new File(tryCreateFolder(folder), filename);
   }

   public static byte[] loadFile(File file) throws IOException {
      FileInputStream fis = new FileInputStream(file);
      Throwable var3 = null;

      byte[] data;
      try {
         data = readInputStream(fis);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (fis != null) {
            if (var3 != null) {
               try {
                  fis.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               fis.close();
            }
         }

      }

      return data;
   }

   public static String loadTextFile(File file) throws IOException {
      return new String(loadFile(file), StandardCharsets.UTF_8);
   }

   public static void writeFile(File file, byte[] data) throws IOException {
      FileOutputStream fos = new FileOutputStream(file);
      Throwable var3 = null;

      try {
         fos.write(data);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (fos != null) {
            if (var3 != null) {
               try {
                  fos.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               fos.close();
            }
         }

      }

   }

   public static void writeTextFile(File file, String data) throws IOException {
      writeFile(file, data.getBytes(StandardCharsets.UTF_8));
   }

   public static byte[] makeRequest(String url) throws IOException {
      HttpURLConnection connection = (HttpURLConnection)(new URL(url)).openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(10000);
      InputStream is = null;

      byte[] data;
      try {
         connection.connect();
         if (connection.getResponseCode() != 200) {
            throw new HttpRetryException(connection.getResponseMessage(), connection.getResponseCode());
         }

         is = connection.getInputStream();
         data = readInputStream(is);
      } finally {
         if (is != null) {
            is.close();
         }

      }

      return data;
   }

   public static String makeTextRequest(String url) throws IOException {
      return new String(makeRequest(url), StandardCharsets.UTF_8);
   }
}
