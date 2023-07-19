package im.thatneko.dynamicpremium.commons.command;

import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.commons.command.impl.PremiumAdminCommand;
import im.thatneko.dynamicpremium.commons.command.impl.PremiumCommand;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
public class CommandManager {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public CommandManager(BaseDynamicPremium dynamicPremium) {
        Arrays.asList(
                new PremiumCommand(dynamicPremium),
                new PremiumAdminCommand(dynamicPremium)
        ).forEach(this::register);
    }

    public void register(Command command) {
        this.commands.put(command.getName(), command);
    }
}