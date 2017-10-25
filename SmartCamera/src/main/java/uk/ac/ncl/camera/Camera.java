package uk.ac.ncl.camera;

import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.*;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Camera {
    private String uniqueId; //camera id
    @SuppressWarnings("unused")
	private String streetName; //camera street name
    @SuppressWarnings("unused")
	private String town; //town name 
    private int maxSpeed; //max speed allowed
    private Date dateTime; //date
    private Configuration config; //azure connection
    private ServiceBusContract service; //azure connection
    @SuppressWarnings("unused")
	private SubscriptionInfo subInfo; //sub info azure
    @SuppressWarnings("unused")
	private CreateTopicResult result; // topic result
    private TopicInfo topicInfo; //topic info
    Random r =new Random(); //date
    long unixTime = System.currentTimeMillis() + 1000L; //date
    

    public ArrayList<Vehicle> vehicles; //array of vehicles

    //camera constructor
    public Camera(String uniqueId, String streetName, String town, int maxSpeed, double rate) throws ServiceException {

        this.uniqueId = uniqueId;
        this.streetName = streetName;
        this.town = town;
        this.maxSpeed = maxSpeed;
        this.dateTime = new Date(unixTime);
//service bus connection string
        config =
                ServiceBusConfiguration.configureWithSASAuthentication(
                        "cloudcoursework",
                        "RootManageSharedAccessKey",
                        "FX8CmhYTAi6kxNay+X/KV2oE72cNc6mis4w6kWQ5KXk=",
                        ".servicebus.windows.net"
                );

        service = ServiceBusService.create(config);

        System.out.println(service.listTopics().getItems().isEmpty());

        if(service.listTopics().getItems().isEmpty())
        {
            topicInfo = new TopicInfo("CamerasTopic"); //camera topic
            try {
                result = service.createTopic(topicInfo);

                SubscriptionInfo subInfo = new SubscriptionInfo("AllInfo"); //all subs
                CreateSubscriptionResult result =
                        service.createSubscription("CamerasTopic", subInfo);
                System.out.println("Creating Subscription"); //create subscription

                SubscriptionInfo subInfo2 = new SubscriptionInfo("Speeding");
                CreateSubscriptionResult result2 =
                        service.createSubscription("CamerasTopic", subInfo2);
                System.out.println("Creating Subscription"); //speeding subscription

                RuleInfo ruleInfo = new RuleInfo("myRuleSpeeding");
                ruleInfo = ruleInfo.withSqlExpressionFilter("Speed > SpeedLimit");//check for speed limits
                CreateRuleResult ruleResult = //results of the speed check
                        service.createRule("CamerasTopic", "Speeding", ruleInfo);
                service.deleteRule("CamerasTopic", "Speeding", "$Default");

            } catch (ServiceException e) {
                System.out.print("ServiceException encountered: ");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }

        BrokeredMessage message = new BrokeredMessage("Camera Details"); //sends camera details
        message.setProperty("uniqueId", uniqueId);
        message.setProperty("streetName", streetName);
        message.setProperty("town", town);
        message.setProperty("maxSpeed", maxSpeed);
        message.setProperty("dateTime", dateTime);
        message.setProperty("messageType", "camera");


        service.sendTopicMessage("CamerasTopic", message); //sends message with the properties

        System.out.println("Camera info sent");
        System.out.println("ID :"+uniqueId+" Street Name: " + streetName + " Town: "+town+" Max Speed: "+ maxSpeed+" Date: "+dateTime);

    }

    public void simulateVehicles(double rate) throws ServiceException, InterruptedException {

        vehicles = new ArrayList<Vehicle>();

        while (true) {
            Vehicle v = new Vehicle(maxSpeed); //creates vehicles
            BrokeredMessage message = new BrokeredMessage("Vehicle Details");
            message.setProperty("regPlate", v.getRegPlate());
            message.setProperty("speed", v.getSpeed());
            message.setProperty("type", v.getType());
            message.setProperty("speedLimit", maxSpeed);
            message.setProperty("cameraId", uniqueId);
            message.setProperty("messageType", "vehicle");
//sets vehicle property details 
            service.sendTopicMessage("CamerasTopic", message); //sends the message
            System.out.println("Vehicle info sent");
            System.out.println("Registration Plate: "+ v.getRegPlate()+" Speed: "+v.getSpeed()+" Vehicle Type: "+v.getType()+" Max Speed: "+maxSpeed+" Camera ID: "+uniqueId);
            Thread.sleep((long) (60000 / 10)); //vehicle production rate
        }
    }
}

