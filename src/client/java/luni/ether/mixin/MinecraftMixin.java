package luni.ether.mixin;

import luni.ether.core.EtherClient;
import luni.ether.core.window.TitleManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "updateTitle", at = @At("HEAD"), cancellable = true)
        private void Ether$UpdateTitle(CallbackInfo ci) {
        Minecraft mc = (Minecraft)(Object)this;
        mc.getWindow().setTitle(TitleManager.buildTitle());
        ci.cancel();
    }
}
