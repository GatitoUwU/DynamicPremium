package im.thatneko.dynamicpremium.commons.database;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
/**
 * Represents the login state.
 */
public enum LoginTristate {
    NOTHING,
    // This is when the player just used /premium or joined by first time.
    PHASE_1,
    // This is set when the player joined the server with tristate set as "PHASE_1".
    // If the player joins with this state, this means that the player wasn't verified.
    PHASE_2;

    public boolean isOnVerification() {
        return this == PHASE_2;
    }

    public boolean isPendingVerification() {
        return this == PHASE_1;
    }
}
