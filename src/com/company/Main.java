package com.company;

import org.postgresql.*;
/**
 * Created by Jan Marti on 27.09.2014.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World.");

        //Simple output
        System.out.println("Hello ASL.");

        //Read arguments
        assert(args.length >= 2);
        System.out.println("Arg0: " + args[0]);
        System.out.println("Arg1: " + args[1]);

        //Check if JDBC library is in classpath
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("JDBC Driver found");
        } catch(ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!!!");
        }
    }

}
