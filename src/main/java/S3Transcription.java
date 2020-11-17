import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeAsyncClient;
import com.amazonaws.services.transcribe.model.AmazonTranscribeException;
import com.amazonaws.services.transcribe.model.GetTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.GetTranscriptionJobResult;
import com.amazonaws.services.transcribe.model.ListTranscriptionJobsRequest;
import com.amazonaws.services.transcribe.model.ListTranscriptionJobsResult;
import com.amazonaws.services.transcribe.model.Media;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobRequest;
import com.amazonaws.services.transcribe.model.TranscriptionJob;
import com.amazonaws.services.transcribe.model.TranscriptionJobSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3Transcription {

    private final Regions DEFAULT_REGION_AmazonS3 = Regions.US_WEST_2;

    private final String BUCKET = "disability-aid-transcription-us-west2";


    public AmazonTranscribe getClient() {
        return AmazonTranscribeAsyncClient.builder()
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withRegion(DEFAULT_REGION_AmazonS3)
                .build();
    } 

    public String getTranscript(String obj, String bucketURI) {
        AmazonTranscribe client = getClient();

        try {
            //Set up media object
            Media media = new Media();
            media.setMediaFileUri(bucketURI);

            //Set up transcription job request
            StartTranscriptionJobRequest jobRequest = new StartTranscriptionJobRequest();
            jobRequest.setLanguageCode("en-US");
            jobRequest.setMedia(media);
            jobRequest.setTranscriptionJobName(obj);
            jobRequest.setOutputBucketName(BUCKET);
            client.startTranscriptionJob(jobRequest);

            return "Successfully Transcribed Files";
        } catch(AmazonTranscribeException t) {
            System.out.println("Error : " + t.getMessage());
            System.exit(1);
        }  catch(S3Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }



    public String createJobItems(String obj, S3Client s3) {
        AmazonTranscribe client = getClient();

        try {
            //Set up get request for transcript
            GetTranscriptionJobRequest transcriptRequest = new GetTranscriptionJobRequest();
            transcriptRequest.setTranscriptionJobName(obj);

            //Set up get result for transcript
            GetTranscriptionJobResult transcriptResult = new GetTranscriptionJobResult();
            transcriptResult = client.getTranscriptionJob(transcriptRequest);

            //Store request for transcript into transcriptionJob object
            TranscriptionJob transcriptionJob = new TranscriptionJob();
            transcriptionJob = transcriptResult.getTranscriptionJob();
            String transcriptURI = transcriptionJob.getTranscript().getTranscriptFileUri();

            AmazonS3URI objectURI = new AmazonS3URI(transcriptURI);

            GetObjectRequest objRequest = GetObjectRequest.builder()
                                            .bucket(objectURI.getBucket())
                                            .key(objectURI.getKey())
                                            .build();

            ResponseInputStream<GetObjectResponse> s3InputStream = s3.getObject(objRequest, ResponseTransformer.toInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(s3InputStream));
            Gson gson = new Gson();
            JobItem jobItem = gson.fromJson(reader, JobItem.class);

            String output = (jobItem.getResults().getTranscripts())[0].getTranscript();
            return output;
        } catch(AmazonTranscribeException t) {
            System.out.println("Error : " + t.getMessage());
            System.exit(1);
        }  catch(S3Exception e) {
            System.out.println("Error : " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private JobItem deserializeJSONWithObjectMapper(String uri) {
        try {
            //Apache httpclient get json
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse response = httpClient.execute(httpGet);

            //deserialize and turn json to JobItem object
            ObjectMapper objectMapper = new ObjectMapper();
            JobItem item = objectMapper.readValue(response.getEntity().getContent(), JobItem.class);
            return item;
        } catch (IOException i) {
            System.out.println("Error : " + i.getMessage());
            System.exit(1);
        }
        return null;
    }

    private ArrayList<JobItem> getStoredTranscripts() {
        AmazonTranscribe client = getClient();
        try {
            //Sets up request syntax
            ListTranscriptionJobsRequest request = new ListTranscriptionJobsRequest();
            request.setStatus("COMPLETED");
            request.setMaxResults(100);

            ListTranscriptionJobsResult listJobs = client.listTranscriptionJobs(request);
            ArrayList<JobItem> list = new ArrayList<JobItem>();

            //add all objects in s3 bucket
            for(TranscriptionJobSummary summary : listJobs.getTranscriptionJobSummaries()) {
                    String URI = summary.getOutputLocationType();
                    String status = summary.getTranscriptionJobStatus();
                    if(status.equals("COMPLETED")) {
                        JobItem object = deserializeJSONWithObjectMapper(URI);
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
