package net.elise1886.globalcoins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.*;




public class TransactionSystem{

    String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Coins" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE;

    SQLConnection connection;
    CommandSender sender;
    double TransactionRate;
    String username;
    Economy Eco;

    public TransactionSystem(SQLConnection connection, CommandSender sender, double TransactionRate, String username, Economy economy) {
        this.connection = connection;
        this.sender = sender;
        this.TransactionRate = TransactionRate;
        this.username = username;
        this.Eco = economy;

    }

    public int giveCoins(int amountToGive, int supply){
        if(amountToGive < 0){
            sender.sendMessage(prefix + "Please enter a positive number!");
        }
        else if(amountToGive > supply){
            sender.sendMessage(prefix + "Sorry but " + amountToGive + "exceeds the currenty supply. Please try a lower number!");
        }
        else{
            connection.updateBuy(username, amountToGive);
            return supply - amountToGive;
        }
        return supply;
    }
    public int takeCoins(int amountToTake, int supply){
        if(amountToTake < 0 || amountToTake > supply){
            sender.sendMessage(prefix + "Sorry what you entered is an error, please check to make sure your input is a positive number and does not exceed the current supply!");
        }
        else{
            connection.updateSell(username, amountToTake);
            return supply + amountToTake;
        }
        return  supply;
    }



    public int buyCoins(int amountOfCoinsToBuy, int supply){
        Player player = (Player) sender;
        if(amountOfCoinsToBuy < 0){
            sender.sendMessage(prefix + "Please enter a positive number!");
            return 0;
        }
        else{
            if(amountOfCoinsToBuy > supply){
                sender.sendMessage(prefix + "Sorry but " + amountOfCoinsToBuy + " exceeds the current supply. Please try a lower number!");
            }
            else{
                double price = TransactionRate * amountOfCoinsToBuy;
                    if(Eco.getBalance(player) < price){
                        sender.sendMessage(prefix + "You do not have enough money to buy this amount of coins!");
                    }
                    else{
                        TransactionRate += (0.0005 * price);
                        Eco.withdrawPlayer(player, price);
                        connection.updateBuy(username, amountOfCoinsToBuy);
                        sender.sendMessage(prefix + "You just bought " + ChatColor.GREEN + amountOfCoinsToBuy + ChatColor.WHITE +" for " + ChatColor.RED + price);
                        return  supply - amountOfCoinsToBuy;
                    }
            }
            return supply;
        }
    }

    public int sellCoins(int amountOfCoinsToSell, int supply){
        int userBalance = connection.getBalance(username);
            if(userBalance >= 0 ){
                sender.sendMessage(prefix + "You currently do not have any coins to sell!");
            }
            else{
                Player player = (Player) sender;
                double price = TransactionRate * amountOfCoinsToSell;
                TransactionRate -= (0.0005 * price);
                Eco.depositPlayer(player, price);
                int updatedBalance = userBalance - amountOfCoinsToSell;
                connection.updateSell(username, updatedBalance);
                sender.sendMessage(prefix + "You just sold " + ChatColor.GREEN + amountOfCoinsToSell + ChatColor.WHITE +" for " + ChatColor.RED + price);
                return  supply + amountOfCoinsToSell;
            }
            return 0;
    }

    public void playerBalance(){
        int playerBal =  connection.getBalance(username);
        sender.sendMessage(prefix + "Your current balance is " + ChatColor.GREEN + playerBal);
    }





}
