package im.thatneko.dynamicpremium.commons.command;

import im.thatneko.dynamicpremium.commons.player.DynamicPlayer;
import lombok.Getter;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
public abstract class Command {
    private final String name;
    private final String[] aliases;

    protected Command(String name) {
        this(name, null);
    }

    protected Command(String name, String[] aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void handle(DynamicPlayer dynamicPlayer, String[] args);
}