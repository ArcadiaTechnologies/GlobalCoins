package net.elise1886.globalcoins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.*;

import java.io.File;



public class main extends JavaPlugin {
    double TransactionRate;
    int Supply;



    FileConfiguration config = this.getConfig();
    File configFile = new File(this.getDataFolder(), "config.yml");
    SQLConnection sqlConnection;
    private Economy econ;


    String host = "";
    int port = 3306;
    String username = "";
    String password = "";
    String database = "";








    //Creates the config file if none exists
    public void configCreation(){
        if (configFile.exists()){
            getLogger().info("Config file exists! Will be loading it shortly.");
        }
        else{
            getLogger().info("Configuration File doesn't exist, Creating it now!");
            config.addDefault("Database Host:", "localhost");
            config.addDefault("Database Port:", 3306);
            config.addDefault("Database Username:", "admin");
            config.addDefault("Database Password:","password");
            config.addDefault("Database Name:", "GlobalCoins");
            config.addDefault("Supply:", 100000);
            config.addDefault("Transaction Rate:", 25000);
            config.options().copyDefaults(true);
            saveConfig();
        }
    }

    //Loads the config file from memory
    public void configLoad(){

        host = config.getString("Database Host:");
        port = config.getInt("Database Port:");
        username = config.getString("Database Username:");
        password = config.getString("Database Password:");
        database = config.getString("Database Name:");
        Supply = config.getInt("Supply:");
        TransactionRate = config.getDouble("Transaction Rate:");
        getLogger().info("Config Loaded");

    }




    //Enables the plugin
    @Override
    public void onEnable() {
        configCreation();
        configLoad();
        sqlConnection = new SQLConnection(host, port, username, password, database);


        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }



    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        TransactionSystem transSystem = new TransactionSystem(sqlConnection, sender.toString(), TransactionRate );

        if(cmd.getName().equalsIgnoreCase("gcoins")){
            if(args[0] == "buy" || args[0] == "buycoins"){
                transSystem.buyCoins(Integer.parseInt(args[1]), Supply );
            }
            else if(args[0] == "sell" || args[0] == "sellcoins"){
                transSystem.sellCoins(Integer.parseInt(args[1]), Supply);
            }
            else if(args[0] == "bal" || args[0] == "balance"){
                transSystem.playerBalance();
            }
        }

        return  false;
    }




    @Override
    public void onDisable(){

    }







}
