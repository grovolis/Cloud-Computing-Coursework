package uk.ac.ncl.Queries;

import com.microsoft.azure.storage.table.TableServiceEntity;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;



public class CameraEntity extends TableServiceEntity {
    public CameraEntity(BrokeredMessage message) //converts properties to string

    {
        this.partitionKey = message.getProperty("uniqueId").toString();
        this.rowKey = message.getProperty("dateTime").toString();
        this.streetName = message.getProperty("streetName").toString();
        this.town = message.getProperty("town").toString();
        this.maxSpeed = message.getProperty("maxSpeed").toString();
    }

    public CameraEntity() { } //creates the camera entity

    String streetName;
    String town;
    String maxSpeed;
  //getters and setters for the properties 
    public String getStreetName() {
        return this.streetName;
    }

    public String getTown() {
        return this.town;
    }

    public String getMaxSpeed() {
        return this.maxSpeed;
    }

    public void setStreetName(String streetName) {
        this.streetName=streetName;
    }

    public void setTown(String town) {
        this.town=town;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed=maxSpeed;
    }

}
