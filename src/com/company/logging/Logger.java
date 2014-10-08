package com.company.logging;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class Logger implements Runnable {

    private ConcurrentLinkedQueue<String> _logEntries = new ConcurrentLinkedQueue<String>();
    private PrintWriter _writer;

    public Logger(String fileName) {
        try {
            _writer = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void run() {
        String entry;
        while(true) {
            while((entry = _logEntries.poll()) != null) {
                _writer.println(entry);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void log(String message) {
        _logEntries.add(message);
    }
}
