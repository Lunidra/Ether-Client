package luni.ether.feature.account.minecraft;

import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.java.JavaAuthManager;
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode;
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService;

import java.util.function.Consumer;


public class MsLoginFlow {

    public static MsAccount login() throws Exception {

        // Create HTTP client
        HttpClient httpClient = MinecraftAuth.createHttpClient();

        // Build authentication manager
        JavaAuthManager.Builder authBuilder = JavaAuthManager.create(httpClient);

        // Start device code login
        JavaAuthManager authManager = authBuilder.login(
                DeviceCodeMsaAuthService::new,
                new Consumer<MsaDeviceCode>() {
                    @Override
                    public void accept(MsaDeviceCode code) {

                        System.out.println("=== MICROSOFT LOGIN ===");
                        System.out.println("Open: " + code.getVerificationUri());
                        System.out.println("Code: " + code.getUserCode());
                        System.out.println("Direct link: " + code.getDirectVerificationUri());
                        System.out.println("=======================");

                    }
                }
        );

        // Build MsAccount from returned data
        MsAccount account = new MsAccount();

        account.username = authManager.getMinecraftProfile().getUpToDate().getName();
        account.uuid = authManager.getMinecraftProfile().getUpToDate().getId().toString();
        account.accessToken = authManager.getMinecraftToken().getUpToDate().getToken();
        account.refreshToken = authManager.getMsaToken().getUpToDate().getRefreshToken();

        return account;
    }
}