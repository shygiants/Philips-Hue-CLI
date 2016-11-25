package io.github.shygiants.philips_hue;

import io.github.shygiants.philips_hue.hue.Controller;
import io.github.shygiants.philips_hue.util.Resources;

/**
 * @auther Sanghoon Yoon (iDBLab, shygiants@gmail.com)
 * @date 2016. 11. 25.
 * @see
 */
public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(Resources.strings("consoleInvalidArgs"));
            return;
        }

        Controller controller = new Controller(args[0], Integer.parseInt(args[1]));
    }
}
