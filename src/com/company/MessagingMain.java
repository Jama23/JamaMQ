package com.company;

import com.company.logging.LoggerSingleton;
import com.company.messaging.Configuration;
import com.company.messaging.Acceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jan Marti on 27.09.2014.
 */
public class MessagingMain {

    private Acceptor _acceptor;
    private ExecutorService _executor;

    public static void main(String[] args) {

        System.out.print("*********** JamaMQ * 2014 ***********");

        String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date(System.currentTimeMillis()));
        String configFilePath = "var/config.prop";
        String logPath = "log/" + date + "-log.csv";

        if(args.length >= 1) {
            configFilePath = args[0];
        }

        Configuration.initConfig(configFilePath);
        Configuration.putProperty("log.perf.path", logPath);

        MessagingMain mm = new MessagingMain();
        mm.start();
    }

    public MessagingMain() {
        LoggerSingleton.initLogger(Configuration.getProperty("log.perf.path"));
        _executor = Executors.newFixedThreadPool(Integer.parseInt(Configuration.getProperty("ms.pool.max")));
        System.out.println("Starting executor threads: ");
        for(int i = 0; i < Integer.parseInt(Configuration.getProperty("ms.pool.max")); i++) {
            _executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.print(Thread.currentThread().getId() + " ");
                }
            });
        }
        System.out.print("\n");
        _acceptor = new Acceptor(Configuration.getProperty("net.interface.ip"), Integer.parseInt(Configuration.getProperty("net.interface.port")), _executor);
    }

    public void start() {
        new Thread(_acceptor).start();
    }
}
