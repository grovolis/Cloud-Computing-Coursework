package uk.ac.ncl.nosqlconsumer;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.table.*;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.*;

import java.net.URI;

public class App { //azure storage string
    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=http;" +
                    "AccountName=mycloudstorage2;" +
                    "AccountKey=sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==";

    public static Configuration config = //service bus connection
    		ServiceBusConfiguration.configureWithSASAuthentication(
                    "cloudcoursework",
                    "RootManageSharedAccessKey",
                    "FX8CmhYTAi6kxNay+X/KV2oE72cNc6mis4w6kWQ5KXk=",
                    ".servicebus.windows.net"
            );

    public static ServiceBusContract service = ServiceBusService.create(config);


    public static void main(String args[]) throws ServiceException {

        try {
            //finds storage account from the connection string
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(storageConnectionString);
            URI uri = URI.create("http://mycloudstorage2.table.core.windows.net/cameradata");
            StorageCredentials storageCredentials = new StorageCredentialsAccountAndKey("mycloudstorage2", "sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==");

//creates the cloud table using the url and credentials
            CloudTable cloudTable = new CloudTable(uri, storageCredentials);
            cloudTable.createIfNotExists();
        } catch (Exception e) {
            //prints the stack trace.
            e.printStackTrace();
        }

        try {
        	//finds storage account from the connection string
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(storageConnectionString);
            URI uri = URI.create("http://mycloudstorage2.table.core.windows.net/vehicledata");
            StorageCredentials storageCredentials = new StorageCredentialsAccountAndKey("mycloudstorage2", "sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==");

          //creates the cloud table using the url and credentials
            CloudTable cloudTable = new CloudTable(uri, storageCredentials);
            cloudTable.createIfNotExists();
        } catch (Exception e) {
            //prints the stack trace
            e.printStackTrace();
        }


        try { //receives messages
            ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
            opts.setReceiveMode(ReceiveMode.PEEK_LOCK);

            while (true) {
                ReceiveSubscriptionMessageResult resultSubMsg =
                        service.receiveSubscriptionMessage("CamerasTopic", "AllInfo", opts); //receives all sub messages
                BrokeredMessage message = resultSubMsg.getValue();
                if (message != null && message.getMessageId() != null) { //if the message is not empty print message id
                    System.out.println("MessageID: " + message.getMessageId());
                    //print the topic message
                    System.out.print("From topic: ");
                    byte[] b = new byte[200];
                    String s = null;
                    int numRead = message.getBody().read(b); //get message body
                    while (-1 != numRead) {
                        s = new String(b);
                        s = s.trim();
                        System.out.print(s);
                        numRead = message.getBody().read(b);
                    }
                    System.out.println();
                    System.out.println("messageType: " +
                            message.getProperty("messageType")); //print message type property

                    if(message.getProperty("messageType").equals("camera")) {
                        try {
                        	//finds storage account from the connection string
                            CloudStorageAccount storageAccount =
                                    CloudStorageAccount.parse(storageConnectionString);

                            //Creates client table
                            CloudTableClient tableClient = storageAccount.createCloudTableClient();

                            //Creates a cloud table object
                            CloudTable cloudTable = tableClient.getTableReference("cameradata");

                            //Creates a new entity.
                            CameraEntity camera = new CameraEntity(message);

                            //Creates the operation to add a new client to the client table
                            TableOperation insertCamera = TableOperation.insertOrReplace(camera);

                            //submit the operation to the table
                            cloudTable.execute(insertCamera);
                            System.out.println("Sending the message to table...");
                        } catch (Exception e) {
                            //prints the stack trace
                            e.printStackTrace();
                        }
                    }
                    else if(message.getProperty("messageType").equals("vehicle")) { //checks if mesage types is the same as the vehicle
                        try {
                        	//finds storage account from the connection string
                            CloudStorageAccount storageAccount =
                                    CloudStorageAccount.parse(storageConnectionString);

                          //Creates client table
                            CloudTableClient tableClient = storageAccount.createCloudTableClient();

                          //Creates a cloud table object
                            CloudTable cloudTable = tableClient.getTableReference("vehicledata");

                          //Creates a new entity.
                            VehicleEntity vehicle = new VehicleEntity(message);

                          //Creates the operation to add a new client to the client table
                            TableOperation insertVehicle = TableOperation.insertOrReplace(vehicle);

                          //submit the operation to the table
                            cloudTable.execute(insertVehicle);
                            System.out.println("Sending the message to table...");
                        } catch (Exception e) {
                            // Output the stack trace.
                            e.printStackTrace();
                        }
                    }

                    System.out.println("Deleting message...");
                    service.deleteMessage(message); //deletes message
                }
            }
        } catch (ServiceException e) { //exceptions
            System.out.print("ServiceException encountered: ");
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            System.out.print("Generic exception encountered: ");
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }



}
