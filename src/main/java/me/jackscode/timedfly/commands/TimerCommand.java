package me.jackscode.timedfly.commands;

import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.entity.TFPlayer;
import me.jackscode.timedfly.enums.CommandType;

public class TimerCommand extends Command {

    public TimerCommand() {
        super("timer", CommandType.TFLY, "TIMER COMMAND", null);
    }

    @Override public void execute(Messenger messenger, String[] args) {
        if (messenger.isConsole()) {
            System.out.println("Only players!");
            return;
        }

        if (args.length < 1) return;

        TFPlayer player = messenger.getTfPlayer();
        switch (args[0]) {
            case "start":
                player.startTimer();
                break;
            case "stop":
                player.stopTimer();
                break;
            case "add":
                if (args.length < 2) return;
                if (player.hasPermission("fly.add")) {
                    player.sendMessage("You have added time to your self, new time: {time_left}");
                    player.addTime(Integer.parseInt(args[1]));
                    player.startTimer();
                } else {
                    player.sendMessage("You dont have permissions for this");
                }
                break;
            case "set":
                if (args.length < 2) return;
                if (player.hasPermission("fly.set")) {
                    player.sendMessage("You have set your time to: {time_left}");
                    player.setTimeLeft(Integer.parseInt(args[1]));
                    player.startTimer();
                } else {
                    player.sendMessage("You dont have permissions for this");
                }
                break;
        }
    }
}
