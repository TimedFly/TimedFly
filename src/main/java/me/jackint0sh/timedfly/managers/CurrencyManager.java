package me.jackint0sh.timedfly.managers;

import me.jackint0sh.timedfly.hooks.currencies.NoCurrency;
import me.jackint0sh.timedfly.utilities.Currency;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CurrencyManager {

    private static Map<String, Currency> currencies = new HashMap<>();

    public static void addCurrency(Currency currency) {
        CurrencyManager.currencies.put(currency.name(), currency);
    }

    public static Currency getCurrency(String currency) {
       return CurrencyManager.currencies.get(currency);
    }

    public static Collection<Currency> getCurrencies() {
       return CurrencyManager.currencies.values();
    }

    private static Currency getDefaultCurrency() {
       return CurrencyManager.currencies.getOrDefault("vault", new NoCurrency()); // TODO: Get from config
    }

    public static boolean withdraw(Player player, int amount, Currency currency) {
        if (currency == null) currency = CurrencyManager.getDefaultCurrency();
        return currency.withdraw(player, amount);
    }

    public static boolean deposit(Player player, int amount, Currency currency) {
        if (currency == null) currency = CurrencyManager.getDefaultCurrency();
        return currency.deposit(player, amount);
    }

    public static int balance(Player player, Currency currency) {
        if (currency == null) currency = CurrencyManager.getDefaultCurrency();
        return currency.balance(player);
    }

    public static boolean has(Player player, int amount, Currency currency) {
        if (currency == null) currency = CurrencyManager.getDefaultCurrency();
        return currency.has(player, amount);
    }
}
