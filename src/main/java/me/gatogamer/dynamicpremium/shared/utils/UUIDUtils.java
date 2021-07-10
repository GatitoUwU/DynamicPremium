package me.gatogamer.dynamicpremium.shared.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class UUIDUtils {

    public static String getOnlineUUID(String name) {
        String onlineUUID = null;
        try {
            URLConnection connection = (new URL("https://api.mojang.com/users/profiles/minecraft/" + name)).openConnection();
            connection.setDoOutput(true);
            connection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null)
                response.append(inputLine).append("\n");
            bufferedReader.close();
            onlineUUID = response.toString();
        } catch (Exception exception) {
        }
        return onlineUUID;
    }

}
