package me.jackscode.timedfly.commands;

import java.util.Arrays;

import me.jackscode.timedfly.api.Argument;
import me.jackscode.timedfly.api.ArgumentType;
import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.entity.TFPlayer;
import me.jackscode.timedfly.enums.CommandType;
import net.md_5.bungee.api.chat.ClickEvent;

public class TimerCommand extends Command {

    public TimerCommand() {
        super("timer", CommandType.TFLY, "TIMER COMMAND");
        this.addArgs("start", Arrays.asList(
                new Argument("player", ArgumentType.OPTIONAL,
                        "Starts the timer to a specified player, or yourself.",
                        "By_Jack")));
        this.addArgs("stop",
                Arrays.asList(new Argument("player", ArgumentType.OPTIONAL,
                        "Stops the timer to a specified player, or yourself.",
                        "mister_jairo")));
        this.addArgs("add", Arrays.asList(
                new Argument("time", ArgumentType.REQUIRED,
                        "Time to to add to specified player, or yourself.",
                        "100 seconds"),
                new Argument("player", ArgumentType.OPTIONAL,
                        "Player to to add the time to.",
                        "Elprimero")));
        this.addArgs("set", Arrays.asList(
                new Argument("time", ArgumentType.REQUIRED,
                        "Time to to set to specified player, or yourself.",
                        "5m"),
                new Argument("player", ArgumentType.OPTIONAL,
                        "Player to to set the time to.",
                        "BrayanPS")));
    }

    @Override
    public void execute(Messenger messenger, String[] args) {
        if (messenger.isConsole()) {
            messenger.sendMessage("Only players can use this command!");
            return;
        }

        if (this.invalidArgsLength(messenger, args, 1))
            return;

        TFPlayer player = messenger.getTfPlayer();
        switch (args[0]) {
            case "help":
                this.sendHelpMessage(messenger, true);
                break;
            case "start":
                player.startTimer();
                break;
            case "stop":
                player.stopTimer();
                break;
            case "add":
                if (args.length < 2) {
                    player.sendMessage("&cUsage: &7/tfly timer add <time> [player]");
                    return;
                }
                if (player.hasPermission("fly.add")) {
                    player.addTime(Integer.parseInt(args[1]));
                    player.startTimer();
                    player.sendMessage("You have added time to your self, new time: &3{time_left}");
                } else {
                    player.sendMessage("&cYou dont have permissions for this.");
                }
                break;
            case "set":
                if (args.length < 2)
                    return;
                if (player.hasPermission("fly.set")) {
                    player.setTimeLeft(Integer.parseInt(args[1]));
                    player.startTimer();
                    player.sendMessage("You have set your time to: {time_left}");
                } else {
                    player.sendMessage("&cYou dont have permissions for this.");
                }
                break;
            default:
                player.sendHoverableMessage(
                        String.format("&cCommand with name &3%s &cwas not found. Try &6/tfly timer help", args[0]),
                        new Messenger.OnClick(ClickEvent.Action.RUN_COMMAND, "/tfly timer help"),
                        "Click to display help.");
        }
    }
}
