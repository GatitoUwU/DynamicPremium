package me.gatogamer.dynamicpremium.commons.database;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.commons.config.IConfigParser;
import me.gatogamer.dynamicpremium.commons.database.type.FlatfileDatabase;
import me.gatogamer.dynamicpremium.commons.database.type.MySQLDatabase;

import java.io.File;

@Getter
@Setter
public class DatabaseManager {
    private Database database;
    private final File dataFolder;

    public DatabaseManager(IConfigParser iConfigParser, File dataFolder) {
        this.dataFolder = dataFolder;

        String databaseType = iConfigParser.getString("DatabaseType");

        if (databaseType.equalsIgnoreCase("MYSQL")) {
            System.out.println("DynamicPremium > Loading MySQL database.");
            database = new MySQLDatabase();
        } else {
            System.out.println("DynamicPremium > Loading Flatfile database!");
            database = new FlatfileDatabase();
        }
    }
}