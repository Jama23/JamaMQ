package com.company.evaluation;

import com.company.client.MessageService;
import com.company.exception.ClientAlreadyExistsException;
import com.company.exception.ClientDeregisterFailureException;
import com.company.exception.ClientDoesNotExistException;
import com.company.exception.ClientRegisterFailureException;

/**
 * Created by Jan Marti on 08.10.2014.
 */
public class EvaluationMain {

    public static void main(String[] args) {
        MessageService messageService = new MessageService(args[0], Integer.parseInt(args[1]));
        try {
            messageService.register("Client1");
            messageService.deregister();
        } catch (ClientAlreadyExistsException e) {
            e.printStackTrace();
        } catch (ClientRegisterFailureException e) {
            e.printStackTrace();
        } catch (ClientDoesNotExistException e) {
            e.printStackTrace();
        } catch (ClientDeregisterFailureException e) {
            e.printStackTrace();
        }
    }
}
