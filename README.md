# EduScribe

### Problem ###
According to the DO-IT center, approximately 840,000+ students have disabilities 
capable of impeding their capabilities to learn. With the current trajectory of modern education, 
these numbers are expected to continuously rise. 

### Solution ###
As per this problem, I intended to create an application that could 
act as a remedy to this educational disparity. 
This application named EduScribe allows for students to create transcripts 
from real time streaming input or through audio file submissions. 
EduScribe creates a distinction from other products through allowing 
the application to be custom tailored by the student according to 
their requirements and classes, whilst maintaining accurate transcription. 

### Forecasting ###
EduScribe will tackle the problem of accuracy through means such as cross-referencing multiple ASR APIs,
as well as implementing a human review feedback loop. 
EduScribe will also provide automated dictionary creation open to 
client interaction in order to give a unique user experience that is intuitive yet customizable. 
Speed will be sacrificed for accuracy (cross referencing) however EduScribe 
will neutralize this through synchronous processing of audio file chunks.
Furthermore EduScribe will allow for setting a number of speakers
to accomodate more than one lecturer as well as different microphone configurations.


### EduScribe RoadMap ###

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
- Human Review Feedback Loop :heavy_exclamation_mark:
- Report System :heavy_exclamation_mark: *Bugs*
- Multiple Speaker Configuration :heavy_exclamation_mark: *Bugs*
- Different Microphone Configuration :heavy_exclamation_mark: *Redoing microphones*
- Customizable Dictionaries With Word Weight Options :heavy_exclamation_mark: *Bugs*
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
- Compilation Into Larger Portfolio :x:

**Long Term Goals**
- Swap To A Different Storage System To Consolidate Multiple APIs :x:
- Add More APIs :x:
- Look For A Way To Get Away From In-Code Lambdas/Rule Connections :x:
- Move Into Mobile App Conversion :x:
- Look Into Spark and Scala
