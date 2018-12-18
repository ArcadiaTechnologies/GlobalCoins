package net.elise1886.globalcoins;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.*;



public class TransactionSystem extends JavaPlugin{
CommandSender msg;
String prefix = ChatColor.BLACK + "[" + ChatColor.GREEN + "Coins" + ChatColor.BLACK + "] ";

    SQLConnection connection;
    String username;
    double TransactionRate;
    private Economy economy;

    public TransactionSystem(SQLConnection connection, String username, double TransactionRate) {
        this.connection = connection;
        this.username = username;
        this.TransactionRate = TransactionRate;


    }

    public int buyCoins(int amountOfCoinsToBuy, int supply){
        Player player = Bukkit.getPlayer(username);
        if(amountOfCoinsToBuy > supply){
            msg.sendMessage(prefix + "Sorry but " + amountOfCoinsToBuy + " exceeds the current supply. Please try a lower number!");
        }
        else{
            double price = TransactionRate * amountOfCoinsToBuy;
            economy.withdrawPlayer(player, price);
            connection.update(username, amountOfCoinsToBuy);
            msg.sendMessage(prefix + "You just bought " + ChatColor.GREEN + amountOfCoinsToBuy + ChatColor.WHITE +" for " + ChatColor.RED + price);
            return  supply - amountOfCoinsToBuy;
        }
        return supply;
    }

    public int sellCoins(int amountOfCoinsToSell, int supply){
        Player player = Bukkit.getPlayer(username);
        double price = TransactionRate * amountOfCoinsToSell;
        economy.depositPlayer(player, price);
        int userBalance = connection.getBalance(username);
        int updatedBalance = userBalance - amountOfCoinsToSell;
        connection.update(username, updatedBalance);
        return  supply;
    }

    public void playerBalance(){
        int playerBal =  connection.getBalance(username);
        msg.sendMessage(prefix + "Your current balance is " + ChatColor.GREEN + playerBal);
    }





}
