package luni.ether.feature.account.minecraft;

import net.minecraft.world.entity.player.ProfileKeyPair;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ProfileKeyPairCache {

    public static CompletableFuture<Optional<ProfileKeyPair>> cached;
    public static String cachedUuid;
    public static String cachedAccessToken;

    public static void reset() {
        cached = null;
        cachedUuid = null;
        cachedAccessToken = null;
    }
}