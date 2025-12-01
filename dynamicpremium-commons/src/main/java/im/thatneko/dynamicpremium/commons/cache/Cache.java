package im.thatneko.dynamicpremium.commons.cache;

import im.thatneko.dynamicpremium.commons.database.LoginTristate;
import im.thatneko.dynamicpremium.commons.database.data.VerificationData;
import im.thatneko.dynamicpremium.commons.utils.UUIDUtils;
import lombok.Data;

import java.util.UUID;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Data
public class Cache {
    private final String username;
    private long lastConnection;
    private UUID uuid;

    private VerificationData verificationData;
    private boolean premium;
    private boolean notifyCannotBePremium;
    private boolean geyserUser;

    public void updateUsage() {
        this.lastConnection = System.currentTimeMillis();
    }

    public boolean shouldPurge() {
        return System.currentTimeMillis() - this.lastConnection > 60000L;
    }

    public void setUuid(String stringUUID) {
        if (stringUUID.contains("-")) {
            this.uuid = UUID.fromString(stringUUID);
        } else {
            this.uuid = UUID.fromString(UUIDUtils.insertDashUUID(stringUUID));
        }
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}