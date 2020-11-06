import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.time.Clock;
import java.util.ArrayList;

import static org.junit.Assert.*;

public final class UnitTests {

    private final S3Manager bucketManager = new S3Manager();

    private final S3Transcription transcriptionManager = new S3Transcription();

    private final String bucket = "disability-aid-us-west2";

    private final String uriBucket = "s3://disability-aid-us-west2/";

    private final File mp3File = new File("C:\\Users\\tuant\\OneDrive\\Documents\\EduScribe testing\\TestAudio.mp3");

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    private String generateKey() {
        Clock clock = Clock.systemUTC();
        String instant = clock.instant().toString();

        return "Job:" + instant.substring(0,19);
    }

    @Test
    public void s3Client() {
        S3Client client = bucketManager.getClient();
    }

    @Test
    public void addFile() {
        String status = bucketManager.addAudioFile(bucket, generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);
    }

    @Test
    public void listFile() {
        String nameOfFile = generateKey();
        String status = bucketManager.addAudioFile(bucket, nameOfFile, mp3File);
        assertEquals("Successfully added new audio file", status);

        System.out.println(bucketManager.listBucketObj(bucket).toString());
    }

    @Test
    public void addAndDeleteFile() {
        String file = generateKey();

        String status = bucketManager.addAudioFile(bucket, file, mp3File);
        assertEquals("Successfully added new audio file", status);

        String status2 = bucketManager.deleteAudioFile(bucket, file);
        assertEquals(file + " deleted successfully from bucket " + bucket, status2);
    }

    @Test
    public void addAndDeleteMultipleFiles() {
        String file = generateKey();

        String status = bucketManager.addAudioFile(bucket, file, mp3File);
        assertEquals("Successfully added new audio file", status);

        String status3 = bucketManager.addAudioFile(bucket, generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);

        String status2 = bucketManager.deleteAudioFile(bucket, file);
        assertEquals(file + " deleted successfully from bucket " + bucket, status2);
    }

    @Test
    public void listMultipleFiles() {
        String status = bucketManager.addAudioFile(bucket, generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);

        String status2 = bucketManager.addAudioFile(bucket, generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);

        String status3 = bucketManager.addAudioFile(bucket, generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);

        System.out.println(bucketManager.listAllBucketObjNames(bucket));
    }


    @Test
    public void transcription() {
        String file = generateKey();
        String status = bucketManager.addAudioFile(bucket, file, mp3File);
        assertEquals("Successfully added new audio file", status);

        String uriForBucket = uriBucket + file;
        ArrayList<JobItem> list = transcriptionManager.getTranscript("Transcription" + 7, uriForBucket, bucket);
        System.out.println(list.toString());
    }

}
