package luni.ether.mixin;

import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBrandRetriever.class)
public class ClientBrandMixin {

    @Inject(method = "getClientModName", at = @At("HEAD"), cancellable = true)
    private static void Ether$Brand(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("Ether");
    }
}
