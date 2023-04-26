package im.thatneko.dynamicpremium.commons.utils;

import lombok.experimental.UtilityClass;

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
@UtilityClass
public class UUIDUtils {
    public String getOnlineUUID(String name) {
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

    public String insertDashUUID(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }
}