package me.jackscode.timedfly.handlers;

import me.jackscode.timedfly.api.Currency;
import me.jackscode.timedfly.exceptions.CurrencyException;

import java.util.HashMap;
import java.util.Map;

public class CurrencyHandler {

    private static final Map<String, Currency> currencies;

    static {
        currencies = new HashMap<>();
    }

    public static void addCurrency(Currency currency) throws CurrencyException {
        if (currencies.containsKey(currency.name())) {
            throw new CurrencyException("There is a currency with that name already");
        }
        currencies.put(currency.name(), currency);
    }

}
