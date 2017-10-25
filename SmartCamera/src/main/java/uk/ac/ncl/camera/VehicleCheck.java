package uk.ac.ncl.camera;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveSubscriptionMessageResult;

public class VehicleCheck {
	
	private Configuration config;
	private ServiceBusContract service;
	private String topic;
	public static final String storageConnectionString = "DefaultEndpointsProtocol=http;" +
            "AccountName=mycloudstorage2;" +
            "AccountKey=sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==";

	public VehicleCheck() {
		topic = "SpeedCamera";
		initiateConfigurations();
		recieveIncomingMessageFrom("vehicle_sub");
		recieveFromQueue();
	}
	
	private void initiateConfigurations() {
		config = ServiceBusConfiguration.configureWithSASAuthentication(
				"cloudcoursework",
                "RootManageSharedAccessKey",
                "FX8CmhYTAi6kxNay+X/KV2oE72cNc6mis4w6kWQ5KXk=",
                ".servicebus.windows.net"
        );
		service = ServiceBusService.create(config);
	}
	public void recieveFromQueue(){
		try
		{
			//finds storage account from the connection string
		    CloudStorageAccount storageAccount = 
		        CloudStorageAccount.parse(storageConnectionString);

		    //Creates the queue client
		    CloudQueueClient queueClient = storageAccount.createCloudQueueClient();

		    //retrives a reference to a queue.
		    CloudQueue queue = queueClient.getQueueReference("speedingcarqueue");
		    queue.downloadAttributes();

		    //Retrieve the latest cached message count
		    //Retrieves 10 messages from the queue with a visibility timeout of 200 seconds
		    for (CloudQueueMessage message : queue.retrieveMessages(10, 200, null, null)) {
		        //Does the processing for all messages in less than 5 minutes, 
		        //deletes each message after processing.		        
		    	queue.deleteMessage(message);
		    	System.out.println(message.getMessageContentAsString());
		    }
		}
		catch (Exception e)
		{
		    //prints the output stack trace
		    e.printStackTrace();
		}
	}
	public void recieveIncomingMessageFrom(String subscriber) {
		try {
			System.out.println("subscriber "+ subscriber); //prints subscriber
			// createTable(subscriber);
			ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
			opts.setReceiveMode(ReceiveMode.PEEK_LOCK); //starts receive mode
			while (true) {
				ReceiveSubscriptionMessageResult resultSubMsg = service.receiveSubscriptionMessage(topic, subscriber,
						opts); //receives all sub messages
				BrokeredMessage message = resultSubMsg.getValue();
				System.out.println("Custom Property: " + message.getProperty("regPlate")); //prints message properties for registration plates and camera id
				System.out.println("Custom Property: " + message.getProperty("cameraId"));
				if (message != null && message.getMessageId() != null) { //if the message is not empty print message id
					System.out.println("MessageID: " + message.getMessageId());
					//prints topic message
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
					System.out.println("Custom Property: " + message.getProperty("MessageNumber"));
					//Deletes the message.
					System.out.println("Deleting this message...");
					isVehicleStolen((String)message.getProperty("regPlate"));
					service.deleteMessage(message);
				} else {
					System.out.println("Finishing up - no more messages.");
					break;
					//Added to handle no more messages.
				}
			}
		} catch (ServiceException e) { //exceptions
			System.out.print("ServiceException encountered: ");
			System.out.println(e.getMessage());
			System.exit(-1);
		} catch (Exception e) {
			System.out.print("Generic exception encountered: ");
			System.out.println(e.getMessage());
		}
	}
	
	public static boolean isVehicleStolen(String vehicleRegistration) //specification
	{
	    try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return (Math.random() < 0.95);
	}
	
	public static void main(String[] args) {
		VehicleCheck vehicleCheck = new VehicleCheck(); //runs the method
	}
}
