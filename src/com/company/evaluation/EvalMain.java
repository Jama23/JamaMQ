package com.company.evaluation;

import com.company.logging.LoggerEval;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jan Marti on 27.10.2014.
 */
public class EvalMain {

    private String _host;            /** Host address of messaging service */
    private int _port;               /** Port of messaging service */
    private int _producerCount;      /** Number of producers, i.e. number of writing client threads */
    private int _putPerProdCount;    /** Number of puts per writing client thread */
    private int _consumerCount;      /** Number of consumers, i.e. number of reading client threads */
    private int _getPerConsCount;    /** Number of gets per reading client thread */
    private boolean _dbPopulated;    /** True if db has initial load (messages), False for empty db */
    private int _populationSize;     /** Number of messages already in db if dbpopulated = True */
    private int _messageSize;        /** Number of characters per message */
    private int _instanceNo;         /** Needed if multiple clients connect to different messaging services*/

    public static void main(String[] args) {

        if(args.length != 10) {
            System.out.println("Arguments needed: host (string), port (int), number of producers (int), puts per producer (int), number of consumers (int), gets per consumer (int), populated or empty db (boolean), population size (int), message size (int), instance number (int)");
        }
        else {
            System.out.print(   "*************************************\n" +
                                "*********** JamaMQ * 2014 ***********\n" +
                                "*************************************\n" );

            String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date(System.currentTimeMillis()));
            String logPath = "log/clientlog-" + date + ".csv";
            LoggerEval.initLogger1(logPath);

            EvalMain evalMain = new EvalMain(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Boolean.parseBoolean(args[6]), Integer.parseInt(args[7]), Integer.parseInt(args[8]), Integer.parseInt(args[9]));
        }

    }

    public EvalMain(String host, int port, int producerCount, int putPerProdCount, int consumerCount, int getPerConsCount, boolean dbPopulated, int populationSize, int messageSize, int instanceNo) {
        _host = host;
        _port = port;
        _producerCount = producerCount;
        _putPerProdCount = putPerProdCount;
        _consumerCount = consumerCount;
        _getPerConsCount = getPerConsCount;
        _dbPopulated = dbPopulated;
        _populationSize = populationSize;
        _messageSize = messageSize;
        _instanceNo = instanceNo;

        init();

    }

    public void init() {
        ClientMain clientMain = new ClientMain(_host, _port, _producerCount, _putPerProdCount, _consumerCount, _getPerConsCount, _dbPopulated, _populationSize, _messageSize, _instanceNo);
        new Thread(clientMain).start();
    }

}
