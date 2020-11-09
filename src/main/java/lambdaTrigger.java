import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;

public class lambdaTrigger implements RequestHandler<S3Event, String> {


    @Override
    public String handleRequest(S3Event event, Context context) {
        try {
            LambdaLogger logger = context.getLogger();
            logger.log("S3Bucket Event Recevied: " + event);

            S3EventNotification.S3EventNotificationRecord eventRecords = event.getRecords().get(0);
            String s3Bucket = eventRecords.getS3().getBucket().getName();
            String s3Key = eventRecords.getS3().getObject().getUrlDecodedKey();



        } catch (AmazonServiceException e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
