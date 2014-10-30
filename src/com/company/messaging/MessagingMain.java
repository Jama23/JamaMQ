package com.company.messaging;

import com.company.logging.LoggerEval;

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

        System.out.print(   "*************************************\n" +
                            "*********** JamaMQ * 2014 ***********\n" +
                            "*************************************\n" );

        String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date(System.currentTimeMillis()));
        String configFilePath = "var/config.prop";
        String log1Path = "log/mslog-f-w-" + date + ".csv";
        String log2Path = "log/mslog-f-l-" + date + ".csv";
        String log3Path = "log/mslog-b-w-" + date + ".csv";
        String log4Path = "log/mslog-b-l-" + date + ".csv";
        String log5Path = "log/mslog-db-l-" + date + ".csv";

        LoggerEval.initLogger1(log1Path);
        LoggerEval.initLogger2(log2Path);
        LoggerEval.initLogger3(log3Path);
        LoggerEval.initLogger4(log4Path);
        LoggerEval.initLogger5(log5Path);

        Configuration.initConfig(configFilePath);

        MessagingMain mm = new MessagingMain();
        mm.start();
    }

    public MessagingMain() {
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

        _acceptor = new Acceptor(Configuration.getProperty("net.interface.ip"), Integer.parseInt(Configuration.getProperty("net.interface.port")), _executor);
    }

    public void start() {
        new Thread(_acceptor).start();
    }
}
