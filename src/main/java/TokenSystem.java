import com.amazonaws.AmazonClientException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;


public class TokenSystem {

    private final String region = "";
    private final String arn = "arn:aws:s3:::disability-aid-us-west2";
    private String roleSession;

    public TokenSystem(String clientRole, String applicationBucket) {
        this.roleSession = clientRole;
    }

    public BasicSessionCredentials getCredentials() {
        try {
            AWSSecurityTokenService tokenProvider = AWSSecurityTokenServiceClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(region)
                    .build();

            GetSessionTokenRequest tokenRequest = new GetSessionTokenRequest();
            tokenRequest.setDurationSeconds(3600);

            AssumeRoleRequest giveRole = new AssumeRoleRequest().withRoleArn(arn).withRoleSessionName(roleSession);

            AssumeRoleResult getRole = tokenProvider.assumeRole(giveRole);

            Credentials tokenCredentials = getRole.getCredentials();

            BasicSessionCredentials AWSCreds = new BasicSessionCredentials(
                    tokenCredentials.getAccessKeyId(),
                    tokenCredentials.getSecretAccessKey(),
                    tokenCredentials.getSessionToken());

            return AWSCreds;
        } catch (AmazonClientException e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

}
