package io.github.shygiants.philips_hue.console;

import com.philips.lighting.model.PHLight;
import io.github.shygiants.philips_hue.hue.Controller;
import io.github.shygiants.philips_hue.util.Resources;

import java.io.Console;
import java.util.List;

/**
 * @auther Sanghoon Yoon (iDBLab, shygiants@gmail.com)
 * @date 2016. 11. 25.
 * @see
 */
public final class Shell implements Runnable {

    private static final Shell singleton = new Shell();

    public static final Shell getInstance() {
        return singleton;
    }

    private final Console console;
    private boolean isStarted;
    private Controller controller;

    private Shell() {
        isStarted = false;
        console = System.console();


    }

    public void start(Controller controller) {
        if (isStarted) throw new IllegalStateException();
        this.controller = controller;
        isStarted = true;
        println(Resources.strings("consoleWelcome"));
        Thread shell = new Thread(this);
        shell.start();
        try {
            shell.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void println(String line) {
        System.out.println(line);
    }

    private void print(String str) {
        System.out.print(str);
    }

    @Override
    public void run() {
        while (true) {
            print("> ");
            String command = console.readLine();
            command = command.trim();
            if (command.isEmpty()) continue;
            String[] tokens = command.split(" ");

            if (tokens.length > 3) tooManyArgs();

            String cmd = tokens[0];
            switch (cmd) {
                case "list":
                    if (tokens.length > 1) tooManyArgs();
                    else {
                        List<PHLight> lights = controller.getAllLights();
                        for (PHLight light : lights) {
                            console.printf("Id: %s, isOn: %b%n", light.getIdentifier(), light.getLastKnownLightState().isOn());
                        }
                    }
                    break;
                case "turn":

                    if (tokens.length < 3) tooFewArgs();

                    else {
                        String onoff = tokens[2];
                        if (onoff.equals("on") || onoff.equals("off"))
                            controller.turnOnOff(tokens[1], onoff.equals("on"));
                        else
                            invalidArgs();
                    }
                    break;
                case "color":
                    if (tokens.length < 3)
                        tooFewArgs();
                    else if (tokens[2].equals("white"))
                        controller.color(tokens[1]);
                    else
                        invalidArgs();
                    break;
                case "exit":
                    if (tokens.length > 1) tooManyArgs();
                    else {
                        println("Exiting...");
                        return;
                    }
                    break;
                default:
                    // Wrong command
                    invalidArgs();
            }
        }
    }

    private void tooManyArgs() {
        println(Resources.strings("consoleTooManyArgs"));
    }

    private void tooFewArgs() {
        println(Resources.strings("consoleTooFewArgs"));
    }

    private void invalidArgs() {
        println(Resources.strings("consoleInvalidArgs"));
    }
}
