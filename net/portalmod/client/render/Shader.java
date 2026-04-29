package net.portalmod.client.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.portalmod.PortalMod;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

public class Shader {
   private static final String BASE_PATH = "shaders/";
   private static final List<Shader> REGISTRY = new ArrayList();
   private final Map<Integer, String> sources;
   private int id;
   private final Map<String, Integer> UNIFORM_CACHE;
   private final FloatBuffer MATRIX_BUFFER;

   private Shader(int id, Map<Integer, String> sources) {
      this.UNIFORM_CACHE = new HashMap();
      this.MATRIX_BUFFER = MemoryUtil.memAllocFloat(16);
      this.id = id;
      this.sources = sources;
      REGISTRY.add(this);
   }

   public Shader bind() {
      GL20.glUseProgram(this.id);
      return this;
   }

   public void unbind() {
      GL20.glUseProgram(0);
   }

   public int getId() {
      return this.id;
   }

   public static void reloadAll() throws IOException {
      for(Shader shader : REGISTRY) {
         shader.reload();
      }

   }

   public void reload() throws IOException {
      Builder builder = new Builder();
      this.sources.forEach((type, path) -> builder.add(type, path));
      int newId = builder.makeShader(true);
      if (newId < 0) {
         PortalMod.LOGGER.error("Failed to reload shader [ID: " + this.id + "], skipping...");
      } else {
         GL20.glDeleteProgram(this.id);
         this.id = newId;
      }
   }

   private int getUniformLocation(String name) {
      if (!this.UNIFORM_CACHE.containsKey(name)) {
         this.UNIFORM_CACHE.put(name, GL20.glGetUniformLocation(this.id, name));
      }

      return (Integer)this.UNIFORM_CACHE.get(name);
   }

   public Shader setInt(String name, int x) {
      GL20.glUniform1i(this.getUniformLocation(name), x);
      return this;
   }

   public Shader setInt(String name, int x, int y) {
      GL20.glUniform2i(this.getUniformLocation(name), x, y);
      return this;
   }

   public Shader setInt(String name, int x, int y, int z) {
      GL20.glUniform3i(this.getUniformLocation(name), x, y, z);
      return this;
   }

   public Shader setInt(String name, int x, int y, int z, int w) {
      GL20.glUniform4i(this.getUniformLocation(name), x, y, z, w);
      return this;
   }

   public Shader setFloat(String name, float x) {
      GL20.glUniform1f(this.getUniformLocation(name), x);
      return this;
   }

   public Shader setFloat(String name, float x, float y) {
      GL20.glUniform2f(this.getUniformLocation(name), x, y);
      return this;
   }

   public Shader setFloat(String name, float x, float y, float z) {
      GL20.glUniform3f(this.getUniformLocation(name), x, y, z);
      return this;
   }

   public Shader setFloat(String name, float x, float y, float z, float w) {
      GL20.glUniform4f(this.getUniformLocation(name), x, y, z, w);
      return this;
   }

   public Shader setMatrix(String name, FloatBuffer matrix) {
      GL20.glUniformMatrix4fv(this.getUniformLocation(name), false, matrix);
      return this;
   }

   public Shader setMatrix(String name, Matrix4f matrix) {
      matrix.func_195879_b(this.MATRIX_BUFFER);
      this.setMatrix(name, this.MATRIX_BUFFER);
      return this;
   }

   public static class Builder {
      private final Map<Integer, String> sources = new HashMap();
      private final List<Integer> ids = new ArrayList();

      public Builder add(int type, String path) {
         if (this.sources.containsKey(type)) {
            PortalMod.LOGGER.error("Failed to attach shader of type " + type + " as it already exists in this program");
            return this;
         } else {
            int id = GL20.glCreateShader(type);
            GL20.glShaderSource(id, this.load(path));
            GL20.glCompileShader(id);
            if (GL20.glGetShaderi(id, 35713) == 0) {
               PortalMod.LOGGER.error("Failed to compile shader [ID: " + id + "]");
               PortalMod.LOGGER.error(GL20.glGetShaderInfoLog(id, GL20.glGetShaderi(id, 35716)));
               System.exit(1);
            }

            this.sources.put(type, path);
            this.ids.add(id);
            return this;
         }
      }

      private String load(String path) {
         try {
            StringBuilder sourceBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(Minecraft.func_71410_x().func_195551_G().func_199002_a(new ResourceLocation("portalmod", "shaders/" + path)).func_199027_b()));

            String line;
            while((line = reader.readLine()) != null) {
               sourceBuilder.append(line + "\n");
            }

            reader.close();
            return sourceBuilder.toString();
         } catch (Exception e) {
            PortalMod.LOGGER.error(e);
            System.exit(1);
            return "";
         }
      }

      private int makeShader(boolean skipIfFailed) {
         int programId = GL20.glCreateProgram();

         for(int id : this.ids) {
            GL20.glAttachShader(programId, id);
         }

         GL20.glLinkProgram(programId);
         GL20.glValidateProgram(programId);
         if (GL20.glGetProgrami(programId, 35714) == 0) {
            PortalMod.LOGGER.error("Failed to compile shader program [ID: " + programId + "]");
            PortalMod.LOGGER.error(GL20.glGetProgramInfoLog(programId, GL20.glGetProgrami(programId, 35716)));
            if (skipIfFailed) {
               return -1;
            }

            System.exit(1);
         }

         for(int id : this.ids) {
            GL20.glDeleteShader(id);
         }

         PortalMod.LOGGER.info("Loaded PortalMod shader [ID: " + programId + "]");
         return programId;
      }

      public Shader build() {
         return new Shader(this.makeShader(false), this.sources);
      }
   }
}
