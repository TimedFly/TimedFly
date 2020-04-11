package me.jackscode.timedfly.handlers;

import me.jackscode.timedfly.api.TimedFlyCurrency;
import me.jackscode.timedfly.exceptions.CurrencyException;

import java.util.HashMap;
import java.util.Map;

public class CurrencyHandler {

    private static final Map<String, TimedFlyCurrency> currencies;

    static {
        currencies = new HashMap<>();
    }

    public static void addCurrency(TimedFlyCurrency currency) throws CurrencyException {
        if (currencies.containsKey(currency.name())) {
            throw new CurrencyException("There is a currency with that name already");
        }
        currencies.put(currency.name(), currency);
    }

}
