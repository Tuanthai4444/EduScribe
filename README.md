# EduScribe

### What Does EduScribe Do?

EduScribe is an application geared towards creating speech to text 
transcriptions from audio files or through microphone stream input. 
The main goal is to create an easy to use application that can be used 
to help students with disabilities or schedule conflictions. 
We also want to design EduScribe in a unique and customizable manner, 
such that any student can use the application to fit their needs and 
circumstances.

### How Does EduScribe Do This?

In order to reach this goal, EduScribe utilizes multiple ASR APIs from
major companies and cross references them for maximum accuracy. 
To aid in customizability, EduScribe allows for the creation
of customizable dictionaries that will help recognize less common words.
EduScribe will also provide applications to help create these dictionaries. 
Furthermore EduScribe will allow for setting a number of speakers
to accomodate more than one lecturer as well as different microphone configurations.

### EduScribe RoadMap

Code will not always reflect the full status of the application as much of the
application is currently on my computer undergoing experimentation and
code reviews/testing.

**AWS Transcribe**
- Uploading Files To S3 Bucket :heavy_check_mark:
- Transcribing Files :heavy_check_mark:
- Asynchronous Streaming Data Input :heavy_check_mark:
- Synchronous Thread Pool Usage :heavy_exclamation_mark: *Bugs*
- Transcription Data Storage :heavy_check_mark:
- Token System :heavy_exclamation_mark: *Need to change to MF validation or Federation Token W/ MF authentification third party*
- Lambda Triggers :heavy_check_mark:
- CloudWatch Rule Implementations :heavy_check_mark:
- Report System :heavy_exclamation_mark: *Bugs*
- Multiple Speaker Configuration :heavy_exclamation_mark: *Bugs*
- Different Microphone Configuration :heavy_exclamation_mark: *Redoing microphones*
- Customizable Dictionaries :heavy_exclamation_mark: *Bugs*
- Output Microphone Stream Input As Sound File :x:
- Allow For More Audio File Acceptance :x:
- Chunk Transcription Only :x:

**Google Speech**
- Uploading Files To Cloud Storage Bucket :heavy_check_mark:
- Transcribing Files :heavy_check_mark:
- Transcription Data Storage :x:
- Asynchronous Streaming Data Input :x:
- Synchronous Thread Pool Usage :x:

**Front End**
- CSS :heavy_exclamation_mark: *Unintuitive And Ugly*
- HTML :heavy_exclamation_mark: *Unintuitive And Ugly*
  - *Optional Usage Of Template..*
- JavaScript :x:

**Long Term Goals**
- Swap To A Different Storage System To Consolidate Multiple APIs :x:
- Add More APIs :x:
- Look For A Way To Get Away From In-Code Lambdas/Rule Connections :x:
- Move Into Mobile App Conversion :x:
