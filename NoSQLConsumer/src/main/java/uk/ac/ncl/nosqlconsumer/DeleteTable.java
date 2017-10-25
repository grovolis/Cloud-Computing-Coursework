package uk.ac.ncl.nosqlconsumer;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;

import java.net.URI;


public class DeleteTable {

	public static final String storageConnectionString =
            "DefaultEndpointsProtocol=http;" +
                    "AccountName=mycloudstorage2;" +
                    "AccountKey=sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==";


    public static void main(String arge[])
    {

        try
        {
        	//finds storage account from the connection string
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse(storageConnectionString);

            //creates the client table
            CloudTableClient tableClient = storageAccount.createCloudTableClient();

            //deletes the table and the date in it.
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            URI uri = URI.create("http://mycloudstorage2.table.core.windows.net/vehicledata");
            StorageCredentials storageCredentials = new StorageCredentialsAccountAndKey("mycloudstorage2", "sNe/cStYr9vZCpHXhlYM9ylVxiuKsqXHDLMHVhys34Svcpm8d7bmpaB8hW9q1N9wRE7zav/oq/nV86dAJt2oYw==");

            //creates new table object and deletes it
            CloudTable cloudTable = new CloudTable(uri, storageCredentials);
            cloudTable.deleteIfExists();
            //creates url
            URI uri2 = URI.create("http://mycloudstorage2.table.core.windows.net/cameradata");

            //creates table object and deletes it.
            CloudTable cloudTable2 = new CloudTable(uri2, storageCredentials);
            cloudTable2.deleteIfExists();
        }
        catch (Exception e)
        {
            //prints the stack trace.
            e.printStackTrace();
        }
    }
}
