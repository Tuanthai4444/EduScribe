import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.time.*;

import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import com.amazonaws.services.transcribe.AmazonTranscribeAsyncClient;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3Transcription {

    private final Region DEFAULT_REGION = Region.US_WEST_2;

    //Sets up a unique job ID as per Amazon time conventions
    private final String key = "Job : " + Clock.systemUTC().instant().toString() + "(UTC-07:00)";

    private AmazonTranscribe client = AmazonTranscribeAsyncClient.builder().build();

    public ArrayList getTranscript(String obj, URL url) {

        try {
            //Set up media object
            Media media = new Media();
            media.setMediaFileUri(url.toString());

            //Set up transcription job request
            StartTranscriptionJobRequest jobRequest = new StartTranscriptionJobRequest();
            jobRequest.setLanguageCode("en-US");
            jobRequest.setMedia(media);
            jobRequest.setTranscriptionJobName(key);
            jobRequest.setOutputBucketName("URL2");
            client.startTranscriptionJob(jobRequest);

            //Set up get request for transcript
            GetTranscriptionJobRequest transcriptRequest = new GetTranscriptionJobRequest();
            transcriptRequest.setTranscriptionJobName(key);

            //Set up get result for transcript
            GetTranscriptionJobResult transcriptResult = new GetTranscriptionJobResult();
            transcriptResult = client.getTranscriptionJob(transcriptRequest);

            //Store request for transcript into transcriptionJob object
            TranscriptionJob transcriptionJob = new TranscriptionJob();
            transcriptionJob = transcriptResult.getTranscriptionJob();
            String transcriptURI = transcriptionJob.getTranscript().getTranscriptFileUri();

            //add objects by deserializing json
            ArrayList list = new ArrayList<JobItem>();
            if(transcriptionJob.getTranscriptionJobStatus().equals("COMPLETED")) {
                JobItem object = deserializeJSON(transcriptURI);
                list.add(object);
            }
            return list;
        } catch(AmazonTranscribeException t) {
            System.out.println("Error : " + t.getMessage());
            System.exit(1);
        }  catch(S3Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private JobItem deserializeJSON(String uri) {
        try {
            //Apache httpclient get json
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse response = httpClient.execute(httpGet);

            //deserialize and turn json to jobitem object
            ObjectMapper objectMapper = new ObjectMapper();
            JobItem item = objectMapper.readValue(response.getEntity().getContent(), JobItem.class);
            return item;
        } catch (IOException i) {
            System.out.println("Error : " + i.getMessage());
            System.exit(1);
        }
        return null;
    }

    private ArrayList getStoredTranscripts(String obj, URL url) {
        try {
            //Sets up request syntax
            ListTranscriptionJobsRequest request = new ListTranscriptionJobsRequest();
            request.setStatus("COMPLETED");
            request.setMaxResults(100);

            ListTranscriptionJobsResult listJobs = client.listTranscriptionJobs(request);
            ArrayList list = new ArrayList<JobItem>();

            //add all objects in s3 bucket
            for(TranscriptionJobSummary summary : listJobs.getTranscriptionJobSummaries()) {
                    String URI = summary.getOutputLocationType();
                    String status = summary.getTranscriptionJobStatus();
                    if(status.equals("COMPLETED")) {
                        JobItem object = deserializeJSON(URI);
                        list.add(object);
                    }
            }
            return list;
        } catch(AmazonTranscribeException t) {
            System.out.println("Error : " + t.getMessage());
            System.exit(1);
        }  catch(S3Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }


}
