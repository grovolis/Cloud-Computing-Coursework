package uk.ac.ncl.camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.microsoft.windowsazure.exception.ServiceException;

public class CameraMaker {

    public static void main(String args[]) throws ServiceException, InterruptedException {

    	Random rID =new Random();
    	Random rStr =new Random();
    	Random rTown =new Random();
    	Random rMaxSpeed =new Random();
    	Random rRate =new Random();
    	int randomID = rID.nextInt((100 - 1) + 1) + 1;
    	int randomSpeed = rMaxSpeed.nextInt((80 - 20) + 1) + 20;
    	String uniqID = Integer.toString(randomID); 
    	String[] listStr = {"King's Road", "King's Cross", "Gallowgate", "Shiney Row", "Northumberland"};
    	String str = listStr[rStr.nextInt(listStr.length)];
    	String[] listTown = {"Newcastle", "London", "Manchester", "Sunderland"};
    	String lTown = listTown[rTown.nextInt(listTown.length)];
    	
    	Camera c = new Camera(uniqID, str, lTown, randomSpeed, 20);

        c.simulateVehicles(20);


    }
}
