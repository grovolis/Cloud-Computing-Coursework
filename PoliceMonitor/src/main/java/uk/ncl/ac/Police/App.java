package uk.ncl.ac.Police;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveSubscriptionMessageResult;

import java.net.URI;


public class App 
{
	public static final String storageConnectionString = //azure storage connection string
            "DefaultEndpointsProtocol=http;" +
                    "AccountName=mycloudstorage2;" +
                    "AccountKey=sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==";

    public static Configuration config = //service bus connection string
    		ServiceBusConfiguration.configureWithSASAuthentication(
                    "cloudcoursework",
                    "RootManageSharedAccessKey",
                    "FX8CmhYTAi6kxNay+X/KV2oE72cNc6mis4w6kWQ5KXk=",
                    ".servicebus.windows.net"
            );

    public static ServiceBusContract service = ServiceBusService.create(config);


    public static void main(String args[]) throws ServiceException {

        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(storageConnectionString);
            URI uri = URI.create("http://mycloudstorage2.table.core.windows.net/vehicledata");
            StorageCredentials storageCredentials = new StorageCredentialsAccountAndKey("mycloudstorage2", "sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==");

            //creates the cloud table if it doesn't exist
            CloudTable cloudTable = new CloudTable(uri, storageCredentials);
            cloudTable.createIfNotExists();
        } catch (Exception e) {
            //prints the stack trace.
            e.printStackTrace();
        }


        try {
            ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
            opts.setReceiveMode(ReceiveMode.PEEK_LOCK); //starts receive mode

            while (true) {
                ReceiveSubscriptionMessageResult resultSubMsg = //receive subscription message result
                        service.receiveSubscriptionMessage("CamerasTopic", "Speeding", opts);
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
                    //prints entity information

                    System.out.println("Registration: " +
                            message.getProperty("regPlate"));
                    System.out.println("Type: " +
                            message.getProperty("type"));
                    System.out.println("Speed Limit: " +
                            message.getProperty("speedLimit"));
                    System.out.println("Speed: " +
                            message.getProperty("speed"));
                    double speed = Double.parseDouble(message.getProperty("speed").toString());
                    double limit = Double.parseDouble(message.getProperty("speedLimit").toString());
                    if(speed > (1.1*limit)) {
                        System.out.println("PRIORITY");
                    }
                    System.out.println();
                    System.out.println("* * *");
                    System.out.println();
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

    }}
