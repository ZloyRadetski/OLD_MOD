package net.portalmod.core.init;

import net.minecraft.command.arguments.ArgumentTypes;
import net.portalmod.common.commands.LowercaseEnumArgument;

public class ArgumentTypeInit {
   public static void registerAll() {
      ArgumentTypes.func_218136_a("portalmodlowercase_enum", LowercaseEnumArgument.class, new LowercaseEnumArgument.Serializer());
   }
}
