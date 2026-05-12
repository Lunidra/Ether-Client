package luni.ether.feature.account;

public class EtherAuthClient {

    public static EtherAccount login(String username, String password) {

        // HTTP request to Ether API
        // POST /auth/login

        EtherAccount acc = new EtherAccount();
        acc.username = username;
        acc.token = "api_token_here";

        return acc;
    }

}