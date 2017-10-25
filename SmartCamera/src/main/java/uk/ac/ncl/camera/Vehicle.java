package uk.ac.ncl.camera;

import java.util.Random;

public class Vehicle { //vehicle properties
    private String regPlate;
    private String type;
    private double speed;
    private int maxSpeed;

    public Vehicle(int maxSpeed)
    {
        this.maxSpeed = maxSpeed;
        this.regPlate = simulateRegPlate();
        this.type = simulateType();
        this.speed = simulateSpeed(maxSpeed);

    }

    private double simulateSpeed(int maxSpeed) {
        Random r = new Random();
        double speed = r.nextGaussian()*3+maxSpeed -3; //generate speed
        return speed;
    }

    private String simulateType() {
        String[] types = {"Car", "Truck", "Motorcycle"}; //generate type
        int index = new Random().nextInt(types.length);
        String type = (types[index]);
        return type;
    }


    private String simulateRegPlate() { //generate registration plate

        StringBuilder sb = new StringBuilder();
        Random r = new Random();

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 2; i++) {
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        for (int i = 0; i < 2; i++) {
            sb.append(r.nextInt(10));
        }
        for (int i = 0; i < 3; i++) {
            sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return sb.toString();

    }

    public String getRegPlate()
    {
        return regPlate;
    }
    public String getType()
    {
        return type;
    }
    public double getSpeed()
    {
        return speed;
    }

}
