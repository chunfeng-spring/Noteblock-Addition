package com.chunfeng.noteadd.mixin;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.server.command.PlaySoundCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlaySoundCommand.class)
public abstract class PlaySoundCommandMixin {

    @Redirect(
            method = "makeArgumentsForCategory",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/arguments/FloatArgumentType;floatArg(FF)Lcom/mojang/brigadier/arguments/FloatArgumentType;"
            )
    )
    private static FloatArgumentType removePitchLimits(float min, float max) {
        // 移除上限限制
        if (min == 0.0F && max == 2.0F) {
            return FloatArgumentType.floatArg(min); // 仅保留最小值
        }
        return FloatArgumentType.floatArg(min, max);
    }
}