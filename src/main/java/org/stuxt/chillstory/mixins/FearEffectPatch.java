package org.stuxt.chillstory.mixins;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.yezon.theabyss.eventhandlers.FearPotionStartedappliedEventHandlers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FearPotionStartedappliedEventHandlers.class)
public class FearEffectPatch {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap=false)
    private static void onExecute(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        ci.cancel();
    }
}