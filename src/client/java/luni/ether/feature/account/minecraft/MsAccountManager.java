package luni.ether.feature.account.minecraft;

import net.lenni0451.commons.httpclient.HttpClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.java.JavaAuthManager;
import net.raphimc.minecraftauth.java.model.MinecraftPlayerCertificates;
import net.raphimc.minecraftauth.java.request.MinecraftPlayerCertificatesRequest;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MsAccountManager {
    private static final List<MsAccount> accounts =
            MsAccountStorage.load();

    public static List<MsAccount> getAccounts() {
        return accounts;
    }

    private static MsAccount activeAccount;

    public static void add(MsAccount account) {

        accounts.add(account);

        MsAccountStorage.save(accounts);

    }

    public static void remove(MsAccount account) {

        accounts.remove(account);

        MsAccountStorage.save(accounts);

    }

    private static void refreshAccount(MsAccount account) {

        if (account.refreshToken == null) return;

        try {

            HttpClient httpClient = MinecraftAuth.createHttpClient();

            JavaAuthManager.Builder builder = JavaAuthManager.create(httpClient);



            JavaAuthManager authManager = builder.login(
                    account.refreshToken
            );

            var mcToken = authManager.getMinecraftToken().getUpToDate();



            account.accessToken =
                    authManager.getMinecraftToken().getUpToDate().getToken();

            account.refreshToken =
                    authManager.getMsaToken().getUpToDate().getRefreshToken();

            MinecraftPlayerCertificatesRequest request =
                    new MinecraftPlayerCertificatesRequest(mcToken);

            MinecraftPlayerCertificates certs =
                    httpClient.execute(request, request);

            account.privateKey = certs.getKeyPair().getPrivate().getEncoded();
            account.publicKey = certs.getKeyPair().getPublic().getEncoded();
            account.publicKeySignature = certs.getPublicKeySignature();
            account.keyExpiry = certs.getExpireTimeMs();

            MsAccountStorage.save(accounts);

            System.out.println("[Ether] Refreshed account: " + account.username);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void switchAccount(MsAccount account) {
//        refreshAccount(account);
//
//        Minecraft mc = Minecraft.getInstance();
//
//
//
//        User newUser = new User(
//                account.username,
//                UUID.fromString(account.uuid),
//                account.accessToken,
//                Optional.empty(),
//                Optional.empty()
//        );
//
//        try {
//
//            Field userField = Minecraft.class.getDeclaredField("user");
//            userField.setAccessible(true);
//            userField.set(mc, newUser);
//
//
//
//            System.out.println(Minecraft.getInstance().getUser().getName());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    public static void switchAccount(MsAccount account) {
        refreshAccount(account);
        activeAccount = account;


        User newUser = new User(
                account.username,
                UUID.fromString(account.uuid),
                account.accessToken,
                Optional.empty(),
                Optional.empty()
        );
        Minecraft mc = Minecraft.getInstance();



        try {
            Field userField = Minecraft.class.getDeclaredField("user");
            userField.setAccessible(true);
            userField.set(mc, newUser);

            System.out.println(mc.getUser().getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Field field = Minecraft.class.getDeclaredField("ether$account");
            field.setAccessible(true);
            field.set(mc, account);
        } catch (NoSuchFieldException e) {
            // field doesn't exist yet → create via Unsafe fallback not needed, we’ll use mixin
        } catch (Exception e) {
            e.printStackTrace();
        }

        ProfileKeyPairCache.reset();

    }

    public static MsAccount getActiveAccount() {
        return activeAccount;
    }
}