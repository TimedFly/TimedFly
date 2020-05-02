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
                player.addTime(Integer.parseInt(args[1]));
                break;
            case "set":
                if (args.length < 2) return;
                player.setTimeLeft(Integer.parseInt(args[1]));
                break;
        }
    }
}
