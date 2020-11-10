import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.time.Clock;
import java.util.ArrayList;

import static org.junit.Assert.*;

public final class UnitTests {

    private final String uriBucket = "..";

    private final S3Manager bucketManager = new S3Manager();

    private final S3Transcription transcriptionManager = new S3Transcription();

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
        String status = bucketManager.addAudioFile(generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);
    }

    @Test
    public void listFile() {
        String nameOfFile = generateKey();
        String status = bucketManager.addAudioFile(nameOfFile, mp3File);
        assertEquals("Successfully added new audio file", status);

        System.out.println(bucketManager.listBucketObj().toString());
    }

    @Test
    public void addAndDeleteFile() {
        String file = generateKey();

        String status = bucketManager.addAudioFile(file, mp3File);
        assertEquals("Successfully added new audio file", status);

        String status2 = bucketManager.deleteAudioFile(file);
        assertEquals(file + " deleted successfully from bucket", status2);
    }

    @Test
    public void addAndDeleteMultipleFiles() {
        String file = generateKey();

        String status = bucketManager.addAudioFile(file, mp3File);
        assertEquals("Successfully added new audio file", status);

        String status3 = bucketManager.addAudioFile(generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);

        String status2 = bucketManager.deleteAudioFile(file);
        assertEquals(file + " deleted successfully from bucket", status2);
    }

    @Test
    public void listMultipleFiles() {
        String status = bucketManager.addAudioFile(generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);

        String status2 = bucketManager.addAudioFile(generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);

        String status3 = bucketManager.addAudioFile(generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);

        System.out.println(bucketManager.listAllBucketObjNames());
    }


    @Test
    public void transcription() {
        String file = generateKey();
        String status = bucketManager.addAudioFile(file, mp3File);
        assertEquals("Successfully added new audio file", status);

        String uriForBucket = uriBucket + file;
        String transcripts = transcriptionManager.getTranscript("bleh2", uriForBucket);
        System.out.println(transcripts);
    }

    @Test
    public void transcriptionDeserialize() {
        ArrayList<JobItem> jobItems = transcriptionManager.createJobItems("bleh2");
        System.out.println(jobItems);
    }

}
