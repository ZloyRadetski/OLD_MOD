package net.portalmod.common.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;

public class LowercaseEnumArgument<T extends Enum<T>> implements ArgumentType<T> {
   private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> new TranslationTextComponent("commands.forge.arguments.enum.invalid", new Object[]{constants, found}));
   private final Class<T> enumClass;
   private final Map<String, T> options;

   public static <R extends Enum<R>> LowercaseEnumArgument<R> enumArgument(Class<R> enumClass) {
      return new LowercaseEnumArgument<R>(enumClass);
   }

   private LowercaseEnumArgument(Class<T> enumClass) {
      this.enumClass = enumClass;
      this.options = new HashMap();
      this.options.putAll((Map)Arrays.stream(enumClass.getEnumConstants()).collect(Collectors.toMap((option) -> option.name().toLowerCase(), (option) -> option)));
   }

   public T parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readUnquotedString();
      if (this.options.containsKey(argument)) {
         return (T)(this.options.get(argument));
      } else {
         throw INVALID_ENUM.createWithContext(reader, argument, Arrays.toString(this.options.keySet().toArray()));
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return ISuggestionProvider.func_197005_b(this.options.keySet(), builder);
   }

   public Collection<String> getExamples() {
      return this.options.keySet();
   }

   public static class Serializer implements IArgumentSerializer<LowercaseEnumArgument<?>> {
      public void serializeToNetwork(LowercaseEnumArgument<?> argument, PacketBuffer buffer) {
         buffer.func_180714_a(argument.enumClass.getName());
      }

      public LowercaseEnumArgument<?> deserializeFromNetwork(PacketBuffer buffer) {
         try {
            String name = buffer.func_218666_n();
            return new LowercaseEnumArgument(Class.forName(name));
         } catch (ClassNotFoundException var3) {
            return null;
         }
      }

      public void serializeToJson(LowercaseEnumArgument<?> argument, JsonObject json) {
         json.addProperty("enum", argument.enumClass.getName());
      }
   }
}
