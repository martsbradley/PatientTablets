package martinbradley.aws.s3;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
   
/** 
 * package names
 *  version 1 of the sdk is com.amazonaws ...
 *  version 2 of the sdk is software.amazon... 
 */

public class Uploader 
{
    private static final Logger logger = LoggerFactory.getLogger(Uploader.class);
    public void upload(byte[] data, String bucketName,String objectKey) throws IOException 
    {
        logger.warn("s3.Uploader");
         Region region = Region.EU_WEST_1;

         //Make a List Buckets Request, autoclose allows the program to exit correctly.
         try (S3Client s3 = S3Client.builder().region(region).build();) {
             

             PutObjectRequest putRequest = 
                            PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();
             
             ByteBuffer byteBuffer = ByteBuffer.allocate(120000);
             byteBuffer.put(data);
             
             // Put Object
             PutObjectResponse resp = s3.putObject(putRequest,
                                              RequestBody.fromByteBuffer(byteBuffer));

             logger.warn("eTag " + resp.eTag());
             logger.warn("-----------------------");

             logger.warn("eTag " + resp);
         }
    }

  //private void list() {
  //         // List buckets
  //         ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
  //         ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
  //         listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));
  //         Bucket gorticrumBucket = listBucketsResponse.buckets().get(0);

  //         System.out.println("Bucket get(0) " + gorticrumBucket);
  //         System.out.println("Done");
  //}

    private static byte[] getByteBuffer() throws IOException {
        String fileName = "/home/martin/Desktop/CarTax.png";

        byte result[] = null;

        try (FileInputStream fis = new FileInputStream(fileName);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ByteArrayOutputStream bao = new ByteArrayOutputStream();){

            int n = 0;
            byte data[]  = new byte[200];

            while ((n = bis.read(data)) > 0) {
                System.out.printf("%d%n", n);
                bao.write(data, 0, n);
            }
            result = bao.toByteArray();

        }
        return result;
    }
}
