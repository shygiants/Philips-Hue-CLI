package io.github.shygiants.philips_hue.util;

/**
 * @auther Sanghoon Yoon (iDBLab, shygiants@gmail.com)
 * @date 2016. 12. 8.
 * @see
 */
public final class Stop implements Runnable {

    private volatile Thread blinker;

    public void stop() {
        if (blinker != null) throw new IllegalStateException();
        blinker = new Thread(this);
        blinker.start();
        try {
            blinker.join();
        } catch (InterruptedException e) {
            /* DO NOTHING */
        }
    }

    public void go() {
        if (blinker == null) throw new IllegalStateException();
        blinker = null;
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        while (blinker == thisThread) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                /* DO NOTHING */
            }
        }
    }
}
