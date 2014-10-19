package com.company.evaluation;

import com.company.client.MessageFactory;
import com.company.client.MessageService;
import com.company.client_backend.Queue;
import com.company.exception.*;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Jan Marti on 09.10.2014.
 */
public class ProducerMain {

    private static Logger _LOGGER = Logger.getLogger(ProducerMain.class.getCanonicalName());

    public static void main(String[] args) {
        MessageService messageService = new MessageService(args[0], Integer.parseInt(args[1]));
        try {
            messageService.register(1);
            Queue q = messageService.createQueue(1);
            for (int i = 0; i < 10; i++) {
                q.enqueueMessage(MessageFactory.createMessage("Hello JamaMQ."));
            }
            messageService.deregister();
        } catch (ClientAlreadyExistsException e) {
            e.printStackTrace();
        } catch (ClientRegisterFailureException e) {
            e.printStackTrace();
        } catch (ClientDoesNotExistException e) {
            e.printStackTrace();
        } catch (ClientDeregisterFailureException e) {
            e.printStackTrace();
        } catch (MessageEnqueueQueueDoesNotExistException e) {
            e.printStackTrace();
        } catch (MessageEnqueueException e) {
            e.printStackTrace();
        } catch (QueueAlreadyExistsException e) {
            e.printStackTrace();
        } catch (QueueCreateException e) {
            e.printStackTrace();
        } catch (MessageEnqueueSenderDoesNotExistException e) {
            e.printStackTrace();
        } catch (ClientNotRegisteredException e) {
            e.printStackTrace();
        }
    }
}
