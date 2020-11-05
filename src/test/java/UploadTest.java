import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.time.Clock;

import static org.junit.Assert.*;

public final class UploadTest {

    private final S3Manager bucketManager = new S3Manager();

    private final String bucket = "arn:aws:s3:::disability-aid-us-west2";

    private final File mp3File = new File("C:\\Users\\Tuan Thai\\Documents\\Transcriber Aid\\AudioFiles\\test.mp3");

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    private String generateKey() {
        Clock clock = Clock.systemUTC();
        String instant = clock.instant().toString();

        return "Job : " + instant + " (UTC-07:00)";
    }

    @Test
    public void addFile() {
        String status = bucketManager.addAudioFile(bucket, generateKey(), mp3File);
        assertEquals("Successfully added new audio file", status);
    }

    @Test
    public void addAndDeleteFile() {
        String status = bucketManager.addAudioFile(bucket, generateKey(), mp3File);
    }
}
