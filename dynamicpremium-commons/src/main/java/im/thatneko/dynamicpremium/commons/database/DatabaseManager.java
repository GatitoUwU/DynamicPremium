package im.thatneko.dynamicpremium.commons.database;

import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.type.FlatfileDatabase;
import im.thatneko.dynamicpremium.commons.database.type.HikariMySQLDatabase;
import im.thatneko.dynamicpremium.commons.database.type.MySQLDatabase;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class DatabaseManager {
    private Database database;
    private final File dataFolder;

    public DatabaseManager(Config config, File dataFolder) {
        this.dataFolder = dataFolder;

        String databaseType = config.getString("database-type");

        switch (databaseType.toUpperCase()) {
            case "MYSQL": {
                System.out.println("DynamicPremium > Loading MySQL database based on MySQL Connector.");
                this.database = new MySQLDatabase();
                break;
            }
            case "MYSQL-HIKARI": {
                System.out.println("DynamicPremium > Loading MySQL database based on HikariCP.");
                this.database = new HikariMySQLDatabase();
                break;
            }
            default: {
                System.out.println("DynamicPremium > Loading Flatfile database!");
                this.database = new FlatfileDatabase();
                break;
            }
        }

        this.database.loadDatabase(config, this);
    }
}