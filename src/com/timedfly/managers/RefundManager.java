package com.timedfly.managers;

import com.timedfly.configurations.ConfigCache;
import com.timedfly.hooks.PlayerPointsHook;
import com.timedfly.hooks.TokenManager;
import com.timedfly.hooks.Vault;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.TimeFormat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

public class RefundManager {

    private PlayerManager playerManager;
    private Set<PaymentType> paymentTypes;
    private int amountPayed;
    private int timeBought;
    private PlayerPointsHook playerPointsHook;
    private TokenManager tokensManager;
    private int refundsToday = 0;
    private Vault vault;

    RefundManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.playerPointsHook = new PlayerPointsHook();
        this.tokensManager = new TokenManager();
        this.paymentTypes = new HashSet<>();
        this.vault = new Vault();
    }


    private RefundError refundError() {
        if (!ConfigCache.isRefundsEnabled()) return RefundError.DISABLED;
        if (getAmountPayed() <= 0) return RefundError.REFUND_NOT_VALID;

        Player player = playerManager.getPlayerFromUUID();

        if (refundsToday >= ConfigCache.getRefundsPerDay()) return RefundError.MAX_REFUNDS_TODAY;

        int timePassed = playerManager.getInitialTime() - playerManager.getTimeLeft();
        if (timePassed > TimeFormat.timeToSeconds(ConfigCache.getRefundTime())) return RefundError.TIME_LIMIT;

        for (PaymentType payment : paymentTypes) {
            if (payment == PaymentType.VAULT) vault.addMoney(player, amountPayed);
            if (payment == PaymentType.PLAYER_POINTS) playerPointsHook.addPoints(player.getUniqueId(), amountPayed);
            if (payment == PaymentType.TOKENS_MANAGER) tokensManager.addTokens(player, amountPayed);
            if (payment == PaymentType.LEVELS) player.setLevel(player.getLevel() + amountPayed);
            if (payment == PaymentType.EXP) player.setTotalExperience(player.getTotalExperience() - amountPayed);
            if (payment == PaymentType.CUSTOM) return RefundError.REFUND_NOT_VALID;
        }

        if (playerManager.getTimeLeft() > timeBought)
            playerManager.setTime(playerManager.getTimeLeft() - timeBought);
        else playerManager.setTime(0).setTimePaused(false).stopTimedFly(true, false);

        refundsToday++;
        paymentTypes.clear();
        return RefundError.NONE;
    }

    public void refundExec(Player player, FileConfiguration languageConfig) {
        switch (refundError()) {
            case TIME_LIMIT:
                Message.sendMessage(player, languageConfig.getString("Other.Refund.TimeLimit")
                        .replace("%refund_limit%", ConfigCache.getRefundTime()));
                break;
            case MAX_REFUNDS_TODAY:
                if (CooldownManager.isInCooldown(player.getUniqueId(), "refunds")) {
                    int timeLeft = CooldownManager.getTimeLeft(player.getUniqueId(), "refunds");
                    Message.sendMessage(player, languageConfig.getString("Other.Refund.MaxRefunds")
                            .replace("%refund_cooldown%", TimeFormat.formatShort((long) timeLeft, true)));
                    return;
                }

                new CooldownManager(player.getUniqueId(), "refunds",
                        TimeFormat.timeToSeconds("24h").intValue()).start();
                this.resetRefunds();
                refundExec(player, languageConfig);
                break;
            case REFUND_NOT_VALID:
                Message.sendMessage(player, languageConfig.getString("Other.Refund.NotValid"));
                break;
            case NONE:
                Message.sendMessage(player, languageConfig.getString("Other.Refund.Refunded")
                        .replace("%refund_amount%", NumberFormat.getIntegerInstance().format(this.getAmountPayed())));

                this.setAmountPayed(0).setTimeBought(0);
                break;
            case DISABLED:
                Message.sendMessage(player, languageConfig.getString("Other.Refund.Disabled"));
                break;
        }
    }

    void addPaymentType(PaymentType paymentType) {
        this.paymentTypes.add(paymentType);
    }

    RefundManager setAmountPayed(int amountPayed) {
        this.amountPayed = amountPayed;
        return this;
    }

    private void resetRefunds() {
        this.refundsToday = 0;
    }

    private int getAmountPayed() {
        return amountPayed;
    }

    void setTimeBought(int timeBought) {
        this.timeBought = timeBought;
    }

    public enum PaymentType {
        VAULT, PLAYER_POINTS, TOKENS_MANAGER, LEVELS, EXP, CUSTOM
    }

    public enum RefundError {
        TIME_LIMIT, MAX_REFUNDS_TODAY, NONE, DISABLED, REFUND_NOT_VALID
    }

}
