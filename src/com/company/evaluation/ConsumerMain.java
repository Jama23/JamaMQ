package com.company.evaluation;

import com.company.client_backend.Message;
import com.company.client.MessageService;
import com.company.client_backend.Queue;
import com.company.exception.*;

/**
 * Created by Jan Marti on 09.10.2014.
 */
public class ConsumerMain {
    public static void main(String[] args) {
        MessageService messageService = new MessageService(args[0], Integer.parseInt(args[1]));
        try {
            messageService.register(2);
            Queue q = messageService.getQueue(1);
            Message m = q.dequeueMessage();
            System.out.println(m.getMessage());
            messageService.deregister();
        } catch (ClientAlreadyExistsException e) {
            e.printStackTrace();
        } catch (ClientRegisterFailureException e) {
            e.printStackTrace();
        } catch (QueueDoesNotExistException e) {
            e.printStackTrace();
        } catch (QueueGetException e) {
            e.printStackTrace();
        } catch (MessageDequeueNotIntendedReceiverException e) {
            e.printStackTrace();
        } catch (MessageDequeueException e) {
            e.printStackTrace();
        } catch (MessageDequeueQueueDoesNotExistException e) {
            e.printStackTrace();
        } catch (MessageDequeueEmptyQueueException e) {
            e.printStackTrace();
        } catch (ClientDoesNotExistException e) {
            e.printStackTrace();
        } catch (ClientDeregisterFailureException e) {
            e.printStackTrace();
        } catch (ClientNotRegisteredException e) {
            e.printStackTrace();
        }
    }
}
