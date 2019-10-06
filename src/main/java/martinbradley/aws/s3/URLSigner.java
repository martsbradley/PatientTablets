package martinbradley.aws.s3;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4PresignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class URLSigner {

 public static URI generateUrl(AwsCredentialsProvider credentialsProvider, 
                               Region region, 
                               String bucket, 
                               String key) throws UnsupportedEncodingException {
    
        System.out.println(StandardCharsets.UTF_8.toString());
        String encodedBucket = URLEncoder.encode(bucket, StandardCharsets.UTF_8.toString().toLowerCase());
        
        String encodedKey = Stream.of(key.split("/"))
                                  .map((String part) -> {
                                            try {
                                                return URLEncoder.encode(part, StandardCharsets.UTF_8.toString().toLowerCase());
                                            } catch (UnsupportedEncodingException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                            }
                                            return "error...";
                        })
                        .collect(Collectors.joining("/"));
        
        String host = "s3." + region.id() + ".amazonaws.com";

        SdkHttpFullRequest httpRequest = 
                        SdkHttpFullRequest.builder()
                                          .method(SdkHttpMethod.GET)
                                          .protocol("https")
                                          .host(host)
                                          .encodedPath(encodedBucket + "/" + encodedKey)
                                          .build();

        Aws4PresignerParams presignRequest = 
             Aws4PresignerParams.builder()
                                .awsCredentials(credentialsProvider.resolveCredentials())
                                .signingName(S3Client.SERVICE_NAME)
                                .signingRegion(region)
                                .expirationTime(Instant.now().plus(25, ChronoUnit.SECONDS))
                                .doubleUrlEncode(false)
                                .build();

        return AwsS3V4Signer.create()
            .presign(httpRequest, presignRequest)
            .getUri();
    }

    public static void main(String[] args) throws Exception {

        AwsCredentialsProvider provider = EnvironmentVariableCredentialsProvider.create();
        
        Region region = Region.EU_WEST_1;
        String bucket = "gorticrumboxone";   
        String objectKey = "martykey";
        
        URI url = generateUrl(provider, 
                              region, 
                              bucket, 
                              objectKey);
        System.out.format("Download URL: %s\n", url);
    }

}
