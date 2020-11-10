import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class uploadTrigger implements RequestHandler<S3Event, String> {
    private static final Logger logger = LoggerFactory.getLogger(uploadTrigger.class);

    @Override
    public String handleRequest(S3Event event, Context context) {
        try {
            logger.info("S3Bucket Event Received: " + event);

            S3EventNotification.S3EventNotificationRecord eventRecords = event.getRecords().get(0);
            String s3Bucket = eventRecords.getS3().getBucket().getName();
            String s3Key = eventRecords.getS3().getObject().getUrlDecodedKey();

            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(s3Bucket, s3Key));

            logger.info("STUFF STUFF STUFF " + s3Object.toString());

        } catch (AmazonServiceException e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
