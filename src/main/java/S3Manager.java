import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import com.amazonaws.AmazonServiceException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class S3Manager {

    private S3Client s3;

    private final Region DEFAULT_REGION = Region.US_WEST_2;

    private S3Client getClient() {
        s3 = S3Client.builder()
                //this credentials provider lets us use AWS secret key and secret access key from the specific bucket
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                //default region is given above
                .region(DEFAULT_REGION)
                .build();
        return s3;
    }

    public String addAudioFile(String bucket, String objName, File file) {
        s3 = getClient();
        TransferManager transferManager = TransferManagerBuilder.standard().build();
        try {
            Upload transfer = transferManager.upload(bucket, objName, file);
            transfer.waitForCompletion();
            transferManager.shutdownNow();
            return "Successfully added new audio file";
        } catch (AmazonServiceException s) {
            System.err.println("Amazon service error: " + s.getMessage());
            System.exit(1);
        } catch (AmazonClientException c) {
            System.err.println("Amazon client error: " + c.getMessage());
            System.exit(1);
        } catch (InterruptedException i) {
            System.err.println("Transfer interrupted: " + i.getMessage());
            System.exit(1);
        }
        return "No new audio file was added";
    }


    /*
    Experimental to deal w/ non transfer manager method
    private static byte[] audioFileToBytes(S3Client s3, String bucket, String objName) {
        try {
            //A request specifying bucket and key to download object as bytes
            GetObjectRequest objRequest = GetObjectRequest
                    .builder()
                    .key(objName)
                    .bucket(bucket)
                    .build();

            ResponseBytes<GetObjectResponse> objBytes = s3.getObjectAsBytes(objRequest);
            return objBytes.asByteArray();
        } catch(AmazonServiceException s) { //Both errors here to isolate and know where it occurs
            System.out.println("Error : " + s.getMessage());
            System.exit(1);
        } catch(S3Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }
     */

    public String listAllBucketObjNames(String bucket) {
        String allObjNames;
        try {
            List<S3Object> listOfObj = listBucketObj(bucket);
            allObjNames = "Audio Files In Bucket : ";
            for (S3Object val : listOfObj) { //better for each loop
                allObjNames = allObjNames + val.key() + ",";
            }
            return allObjNames.substring(0 , allObjNames.length()-1);
        } catch(AmazonServiceException s) {
            System.out.println("Error : " + s.getMessage());
            System.exit(1);
        } catch(S3Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return "";
    }

    private List listBucketObj(String bucket) {
        s3 = getClient();
        try {
            ListObjectsRequest listOfObj = ListObjectsRequest
                    .builder()
                    .bucket(bucket)
                    .build();

            ListObjectsResponse objResponse = s3.listObjects(listOfObj);
            return objResponse.contents();
        } catch(AmazonServiceException s) {
            System.out.println("Error : " + s.getMessage());
            System.exit(1);
        } catch(S3Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public String deleteBucketObj(String bucket, String obj) {
        s3 = getClient();
        ArrayList<ObjectIdentifier> deleteObj = new ArrayList<ObjectIdentifier>();
        deleteObj.add(ObjectIdentifier.builder().key(obj).build());

        try {
            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(Delete.builder().objects(deleteObj).build())
                    .build();
            s3.deleteObjects(deleteRequest);
            return obj + " deleted successfully from bucket " + bucket;
        } catch(AmazonServiceException s) {
            System.out.println("Error : " + s.getMessage());
            System.exit(1);
        } catch(S3Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return "No objects were deleted";
    }

    public static void presignedUrlUpload(String bucket, String objKey) {
        S3Presigner presigner = S3Presigner.create();
        try {
            PutObjectRequest objRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objKey)
                    .contentType("audio/mpeg")
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(objRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            URL presignedURL = presignedRequest.url();

            HttpURLConnection httpConnection = (HttpURLConnection) presignedURL.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestProperty("Content-Type", "audio/mpeg");
            httpConnection.setRequestMethod("PUT");
            OutputStreamWriter output = new OutputStreamWriter(httpConnection.getOutputStream());
            output.write("Uploaded audio file as an object via presigned URL");
            output.close();

            presigner.close();
        } catch(IOException i) {
            System.out.println("Error : " + i.getMessage());
            System.exit(1);
        } catch(S3Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
    }

}
