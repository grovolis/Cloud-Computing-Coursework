package uk.ac.ncl.Queries;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.table.*;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.*;


public class App 
{
	public static final String storageConnectionString = //azure storage connection string
            "DefaultEndpointsProtocol=http;" +
                    "AccountName=mycloudstorage2;" +
                    "AccountKey=sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==";


    public static void main( String[] args )
    {
        try
        {
            //constraints for filters
            final String PARTITION_KEY = "PartitionKey";
            final String ROW_KEY = "RowKey";
            final String TIMESTAMP = "Timestamp";

          //finds storage account from the connection string
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(storageConnectionString);

          //Creates client table
            CloudTableClient tableClient = storageAccount.createCloudTableClient();

          //Creates a cloud table object
            CloudTable cloudTable = tableClient.getTableReference("cameradata");

            //creates a filter with the partition key being "georgios"
            String partitionFilter = TableQuery.generateFilterCondition(
                   PARTITION_KEY,
                   TableQuery.QueryComparisons.EQUAL,
                   "Georgios");

            //define a partition query, using "Georgios" as the partition key
            TableQuery<CameraEntity> query =
                    TableQuery.from(CameraEntity.class);

            //diplays information abou the entity
            System.out.println("uniqueId" +
                    "\t" + "dateTime" +
                    "\t" + "streetName" +
                    "\t" + "town" +
                    "\t" + "maxSpeed");
            for (CameraEntity entity : cloudTable.execute(query)) {
                System.out.println(entity.getPartitionKey() +
                        " " + entity.getRowKey() +
                        " " + entity.getStreetName() +
                        "\t" + entity.getTown() +
                        "\t" + entity.getMaxSpeed());
            }
        }
        catch (Exception e)
        {
            //prints
            e.printStackTrace();
        }    }
}
