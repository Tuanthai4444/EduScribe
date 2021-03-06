import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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


/**
 * Utilizes AWS Amazon Transcribe Client to transcribe Audio Files
 * within the S3 bucket 'disability-aid-us-west2'. Allows for
 * variety of different transcription options.
 * 
 * Audio files to be transcribed required to be within bucket - 
 * 'disability-aid-us-west2'
 * 
 * Finished transciptions are required to be within bucket -
 * 'disability-aid-transcription-us-west2'
 */

public class S3Transcription {


    private final Regions DEFAULT_REGION_AmazonS3 = Regions.US_WEST_2;

    private final String BUCKET = "disability-aid-transcription-us-west2";

    /**
     * Utilizes a builder to build Amazon transcribe client requiring
     * valid parameters region and credentials
     */
    public AmazonTranscribe getClient() {
        return AmazonTranscribeAsyncClient.builder()
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withRegion(DEFAULT_REGION_AmazonS3)
                .build();
    } 

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    //////// Transcription Of Single Specified File In Bucket
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Begins the transcription process for the audio file and
     * places transcription JSON into the 
     * 'disability-aid-transcription-us-west2' bucket
     * 
     * 
     * @param jobName the string name for the transcription job
     * @param bucketURI the string uri of the bucket's audio file
     * @spec.effects 
     * @exception AmazonTranscribeException if transcribe services fail
     * @exception S3Exception if S3 services fail
     * @return confirmation for transcription start; not guaranteed 
     * confirmation for successful transcription
     */
    public String startTranscription(String jobName, String bucketURI) {
        AmazonTranscribe client = getClient();

        try {
            //Set up media object
            Media media = new Media();
            media.setMediaFileUri(bucketURI);

            //Set up transcription job request
            StartTranscriptionJobRequest jobRequest = new StartTranscriptionJobRequest();
            jobRequest.setLanguageCode("en-US");
            jobRequest.setMedia(media);
            jobRequest.setTranscriptionJobName(jobName);
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

    /**
     * Gets the string representation of the transcript through
     * deserializing the JSON utilizing GSON.
     * 
     * @param jobName the string name for the transcription job
     * @param s3 the S3Client required to initialize input stream 
     * for POJO response of transcription JSON
     * @exception AmazonTranscribeException if transcribe services fail
     * @exception S3Exception if S3 services fail
     * @return string representation of only the transcript value
     */
    public String getTranscript(String jobName, S3Client s3) {
        AmazonTranscribe client = getClient();

        try {
            //Set up get request for transcript
            GetTranscriptionJobRequest transcriptRequest = new GetTranscriptionJobRequest();
            transcriptRequest.setTranscriptionJobName(jobName);

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

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    ////// JSON Jackson Format Object Mapper
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Deserialize the transcript JSON with a Jackson Format 
     * objectMapper
     *
     * GSON used currently over object mapper
     * 
     * @param uri the string URI for the 'disability-aid-us-west2' bucket
     * @exception IOException if object mapper input stream fails
     * @return JobItem that contains the JSON fields such as
     * jobName, results, accountID, etc.
     */
    private JobItem deserializeJSONWithObjectMapper(String bucketURI) {
        try {
            //Apache httpclient get json
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(bucketURI);
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

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    ////// Batch Transcription Of All Transcripts In Bucket
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Gets all stored transcripts JSON and deserializes into JobItem Object
     * befoe returning all JobItems in a list
     * 
     * @exception AmazonTranscribeException if transcribe services fail
     * @exception S3Exception if S3 services fail
     * @return List of JobItems representing deserialized Transcript JSON
     */
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
