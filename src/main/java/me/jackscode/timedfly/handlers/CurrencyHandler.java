package me.jackscode.timedfly.handlers;

import me.jackscode.timedfly.api.Currency;
import me.jackscode.timedfly.exceptions.CurrencyException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CurrencyHandler {

    private final List<Currency> currencies;

    public CurrencyHandler() {
        this.currencies = new ArrayList<>();
    }

    /**
     * Add a new currency to be used by the plugin when players buy fly time.
     *
     * @param currency {@link Currency} to add and enable
     * @throws CurrencyException If there is a currency with the same name
     */
    public void addCurrency(@NotNull Currency currency) throws CurrencyException {
        if (currencies.stream().anyMatch(curr -> curr.name().equals(currency.name()))) {
            throw new CurrencyException("There is a currency with that name already");
        }
        currencies.add(currency);
    }

    /**
     * Removes the given currency from the plugin so it can't be used
     * to buy fly time.
     *
     * @param currency {@link Currency} to be removed and disabled
     * @throws CurrencyException If the currency does not exist, or is not enabled.
     */
    public void removeCurrency(@NotNull Currency currency) throws CurrencyException {
        if (!currencies.removeIf(curr -> curr.name().equals(currency.name()))) {
            throw new CurrencyException(String.format(
                    "Could not find the given currency, %s, to remove.",
                    currency.name()
            ));
        }
    }

    /**
     * Get a currency with a given name, if no currency found the returned value will be null.
     *
     * @param name Name of the currency to get.
     * @return The currency class found by the name provided
     */
    public Currency getCurrency(String name) {
        return currencies
                .stream()
                .filter(currency -> currency.name().equals(name))
                .findFirst()
                .orElse(null);
    }
}
