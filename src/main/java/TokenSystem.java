import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;



public class TokenSystem {

    private String region;
    private String arn ;
    private String roleSession;
    private String bucket;

    public TokenSystem(String clientRegion, String applicationArn, String clientRole, String applicationBucket) {
        this.region = clientRegion;
        this.arn = applicationArn;
        this.roleSession = clientRole;
        this.bucket = applicationBucket;
    }

    public BasicSessionCredentials getCredentials() {
        AWSSecurityTokenService tokenProvider = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(region)
                .build();

        AssumeRoleRequest giveRole = new AssumeRoleRequest().withRoleArn(region).withRoleSessionName(roleSession);
        AssumeRoleResult getRole = tokenProvider.assumeRole(giveRole);
        Credentials sessionCred = getRole.getCredentials();


        BasicSessionCredentials AWSCreds = new BasicSessionCredentials(
                sessionCred.getAccessKeyId(),
                sessionCred.getSecretAccessKey(),
                sessionCred.getSessionToken());

        return AWSCreds;
    }

}
