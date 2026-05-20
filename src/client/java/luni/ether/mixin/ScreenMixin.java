package luni.ether.mixin;

import luni.ether.ui.notification.NotificationRenderer;
import luni.ether.ui.render.UIRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void ether$renderNotifications(
            GuiGraphics context,
            int mouseX,
            int mouseY,
            float delta,
            CallbackInfo ci
    ) {

        UIRenderer renderer =
                new UIRenderer(context);

        NotificationRenderer.render(renderer);
    }
}