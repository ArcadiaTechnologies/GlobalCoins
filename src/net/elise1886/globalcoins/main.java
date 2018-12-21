package net.elise1886.globalcoins;

import net.elise1886.globalcoins.mysql.MySQL;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.*;

import java.io.File;
import java.sql.Connection;
import java.util.logging.Logger;


public class main extends JavaPlugin {
    double TransactionRate = 25000;
    int Supply = 1000000;



    FileConfiguration config = this.getConfig();
    File configFile = new File(this.getDataFolder(), "config.yml");
    SQLConnection sqlConnection;
    TransactionSystem transSystem;
    private Economy econ;
    private Permission perms;
    Connection c = null;
    String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Coins" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE;


    String host = "";
    String port = "3306";
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
            config.addDefault("Database Port:", "3306");
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
        port = config.getString("Database Port:");
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
        MySQL MySQL = new MySQL(host, port, database, username, password);
        try {
                c = MySQL.openConnection();

                if (MySQL.checkConnection() == false){
                    System.err.println("Error, the database connection is closes!");
                }
                else{
                    System.out.println("Database Connected");
                }
        }
        catch (Exception e)
        {
            System.err.println("Got an exception in OpenConnection!");
            System.err.println(e.getMessage());
        }
        sqlConnection = new SQLConnection(c);
        getCommand("gcoins").setExecutor(this);



        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.setupPermissions();
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

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public Permission getPermissions() {
        return perms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


            if (!(sender instanceof Player)){
                sender.sendMessage("Error, you are not a player!");
            }
            else{
                String username = sender.getName();
                transSystem = new TransactionSystem(sqlConnection, sender, TransactionRate, username, econ);

                if(sender.isOp()) {
                    try {
                        if (cmd.getName().equalsIgnoreCase("gcoins")) {
                            if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("buycoins")) {
                                Supply = transSystem.buyCoins(Integer.parseInt(args[1]), Supply);
                                TransactionRate = transSystem.TransactionRate;
                            } else if (args[0].equalsIgnoreCase("sell") || args[0].equalsIgnoreCase("sellcoins")) {
                                Supply = transSystem.sellCoins(Integer.parseInt(args[1]), Supply);
                                TransactionRate = transSystem.TransactionRate;
                            } else if (args[0].equalsIgnoreCase("bal") || args[0].equalsIgnoreCase("balance")) {
                                transSystem.playerBalance();
                            } else if (args[0].equalsIgnoreCase("supply")) {
                                sender.sendMessage(prefix + "The current supply is " + ChatColor.GREEN + Supply);
                            } else if (args[0].equalsIgnoreCase("transactionrate") || args[0].equalsIgnoreCase("rate")) {
                                sender.sendMessage(prefix + "The current Transaction Rate is " + ChatColor.GREEN + TransactionRate);
                            }
                        }
                    } catch (Exception e) {
                        sender.sendMessage(prefix + "You typed the command wrong, please use /gcoins buy or /gcoins sell or /gcoins bal");
                    }
                    return false;
                }
                else{
                    try {
                        if (cmd.getName().equalsIgnoreCase("gcoins")) {
                            if (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("buycoins")) {
                                if (sender.hasPermission("globalcoins.transaction.buy")) {
                                    Supply = transSystem.buyCoins(Integer.parseInt(args[1]), Supply);
                                    TransactionRate = transSystem.TransactionRate;
                                } else {
                                    sender.sendMessage(prefix + "You are missing the " + ChatColor.RED + "globalcoins.transaction.buy " + ChatColor.WHITE + "permission!");
                                }
                            } else if (args[0].equalsIgnoreCase("sell") || args[0].equalsIgnoreCase("sellcoins")) {
                                if (sender.hasPermission("globalcoins.transaction.sell")) {
                                    Supply = transSystem.sellCoins(Integer.parseInt(args[1]), Supply);
                                    TransactionRate = transSystem.TransactionRate;
                                } else {
                                    sender.sendMessage(prefix + "You are missing the " + ChatColor.RED + "globalcoins.transaction.sell " + ChatColor.WHITE + "permission!");
                                }
                            } else if (args[0].equalsIgnoreCase("bal") || args[0].equalsIgnoreCase("balance")) {
                                if (sender.hasPermission("globalcoins.transaction.balance")) {
                                    transSystem.playerBalance();
                                } else {
                                    sender.sendMessage(prefix + "You are missing the " + ChatColor.RED + "globalcoins.transaction.balance " + ChatColor.WHITE + "permission!");
                                }
                            } else if (args[0].equalsIgnoreCase("supply")) {
                                if (sender.hasPermission("globalcoins.admin.supply")) {
                                    sender.sendMessage(prefix + "The current supply is " + ChatColor.GREEN + Supply);
                                } else {
                                    sender.sendMessage(prefix + "You are missing the " + ChatColor.RED + "globalcoins.admin.supply " + ChatColor.WHITE + "permission!");
                                }
                            } else if (args[0].equalsIgnoreCase("transactionrate") || args[0].equalsIgnoreCase("rate")) {
                                if (sender.hasPermission("globalcoins.admin.rate")) {
                                    sender.sendMessage(prefix + "The current Transaction Rate is " + ChatColor.GREEN + TransactionRate);
                                } else {
                                    sender.sendMessage(prefix + "You are missing the " + ChatColor.RED + "globalcoins.admin.rate " + ChatColor.WHITE + "permission!");
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        sender.sendMessage(prefix + "You typed the command wrong, please use /gcoins buy or /gcoins sell or /gcoins bal");
                        }
                    return false;
                }
        }

    return false;
        }




    @Override
    public void onDisable(){
    config.set("Transaction Rate: ", TransactionRate);
    config.set("Supply: ", Supply);
    getLogger().info("Current supply is " + Supply + " with a current TransactionRate of " + TransactionRate);

}
}
