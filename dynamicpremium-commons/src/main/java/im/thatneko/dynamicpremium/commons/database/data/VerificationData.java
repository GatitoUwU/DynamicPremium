package im.thatneko.dynamicpremium.commons.database.data;

import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.commons.database.LoginTristate;
import im.thatneko.dynamicpremium.commons.utils.FileIO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.File;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class VerificationData {
    private final String username;
    private long epoch = System.currentTimeMillis();
    private LoginTristate loginTristate = LoginTristate.NOTHING;

    public static VerificationData fromFile(File verificationFile) {
        String content = FileIO.readFile(verificationFile);
        return BaseDynamicPremium.getInstance().getFancyGson().fromJson(content, VerificationData.class);
    }

    public void flushToFile(File verificationFile) {
        String content = BaseDynamicPremium.getInstance().getFancyGson().toJson(this);
        FileIO.writeFile(verificationFile, content);
    }
}
