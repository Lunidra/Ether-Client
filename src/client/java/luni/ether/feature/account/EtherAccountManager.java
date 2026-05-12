package luni.ether.feature.account;

public class EtherAccountManager {
    private static EtherAccount activeAccount;

    public static void login(String username, String password) {

        EtherAccount account = EtherAuthClient.login(username, password);

        activeAccount = account;
    }

    public static boolean isLoggedIn() {
        return activeAccount != null;
    }

    public static EtherAccount getAccount() {
        return activeAccount;
    }

}