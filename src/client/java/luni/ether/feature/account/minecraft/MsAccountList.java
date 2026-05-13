package luni.ether.feature.account.minecraft;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MsAccountList extends ObjectSelectionList<MsAccountList.Entry> {

    public MsAccountList(Minecraft mc, int width, int height, int top, int bottom) {
        super(mc, width, height, top, 20);

        reloadAccounts();
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {

        private final MsAccount account;
        private Identifier headTexture;
        private boolean headRequested = false;

        public Entry(MsAccount account) {
            this.account = account;
        }

        public MsAccount getAccount() {
            return account;
        }

        private void requestHead() {

            if (headRequested) return;

            headRequested = true;

            new Thread(() -> {

                try {

                    String username = account.username;

                    String url = "https://api.mcheads.org/head/" + username + "/16";

                    NativeImage image = NativeImage.read(new java.net.URL(url).openStream());

                    Identifier id = Identifier.fromNamespaceAndPath("ether", "heads/" + username.toLowerCase());

                    Minecraft.getInstance().execute(() -> {

                        Minecraft.getInstance().getTextureManager().register(
                                id,
                                new DynamicTexture(() -> "ether_head_" + username, image)
                        );

                        headTexture = id;

                    });

                } catch (Exception e) {

                    headTexture = DefaultPlayerSkin.getDefaultTexture();

                }

            }).start();
        }



        @Override
        public void extractContent(
                GuiGraphicsExtractor graphics,
                int mouseX,
                int mouseY,
                boolean hovered,
                float delta
        ) {
            requestHead();


            Minecraft mc = Minecraft.getInstance();
            int x = this.getContentX();
            int y = this.getContentY();


            Minecraft.getInstance().execute(() -> {});

            Identifier tex = headTexture != null
                    ? headTexture
                    : DefaultPlayerSkin.getDefaultTexture();

            int size = 16;
            int headY = y + (20 - size) / 2;

            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    tex,
                    x + 4,
                    y,
                    size,
                    size,
                    16,
                    16,
                    16,
                    16,
                    16,
                    16
            );




            if (hovered) {
                graphics.fill(
                        this.getX(),
                        this.getY(),
                        this.getX() + this.getWidth(),
                        this.getY() + this.getHeight(),
                        0x33FFFFFF
                );
            }

            boolean active =
                    Minecraft.getInstance().getUser().getName().equals(account.username);

            int textColor = active ? 0xFF55FF55 : 0xFFFFFFFF;

            graphics.text(
                    mc.font,
                    account.username,
                    this.getContentX() + 26,
                    this.getContentY() + 4,
                    textColor,
                    true
            );


            //graphics.drawString(mc.font, account.username, this.getContentX() + 20, this.getContentY(), 0xFFFFFFFF, true);

        }

        @Override
        public Component getNarration() {
            return Component.literal(account.username);
        }
    }

    public void reloadAccounts() {
        this.clearEntries();

        for (MsAccount account : MsAccountManager.getAccounts()) {
            this.addEntry(new Entry(account));
        }
    }

    public int getRowTop(Entry entry) {
        int index = this.children().indexOf(entry);
        return this.getY() + index * 20;
    }

    @Override
    public int getRowWidth() {
        return 200;
    }
}