package uk.ac.ncl.Queries;

import com.microsoft.azure.storage.table.TableServiceEntity;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;


public class VehicleEntity extends TableServiceEntity {
    public VehicleEntity(BrokeredMessage message) //converts properties to string

    {
        this.partitionKey = message.getProperty("cameraId").toString();
        this.rowKey = message.getProperty("regPlate").toString();
        this.type = message.getProperty("type").toString();
        this.speedLimit = message.getProperty("speedLimit").toString();
        this.speed = message.getProperty("speed").toString();
    }

    public VehicleEntity() { } //creates vehicle entity

    String type;
    String speed;
    String speedLimit;
  //setters and getters of the entity
   public String getType() {
        return this.type;
    }

    public String getSpeed() {
        return this.speed;
    }

    public String getSpeedLimit() {
        return this.speedLimit;
    }

    public void setType(String type) {
        this.type=type;
    }

    public void setSpeed(String speed) {
        this.speed=speed;
    }

    public void setSpeedLimit(String speedLimit) {
        this.speedLimit=speedLimit;
    }

}
