public class JobItem {

    private String jobName;
    private String accountId;
    private Result results;
    private String status;

    public class Result {
        Transcript[] transcripts;
        Item[] items;

        public Item[] getItems() {
            return this.items;
        }

        public Transcript[] getTranscripts() {
            return this.transcripts;
        }

        public class Transcript {
            String transcript;

            public String getTranscript() {
                return transcript;
            }
        }

        public class Item {
            String start_time;
            String end_time;
            Alternative[] alternatives;

            public Alternative[] getAlternatives() {
                return this.alternatives;
            }

            public String getEnd_time() {
                return this.end_time;
            }

            public String getStart_time() {
                return this.start_time;
            }

            public class Alternative {
                String word;
                Float confidence;

                public Float getConfidence() {
                    return this.confidence;
                }

                public String getWord() {
                    return this.word;
                }
            }
        }
    }

    public JobItem(String jobName, String accountId, Result results, String status) {
        this.jobName = jobName;
        this.accountId = accountId;
        this.results = results;
        this.status = status;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getJobName() {
        return this.jobName;
    }

    public Result getResults() {
        return this.results;
    }

    public String getStatus() {
        return this.status;
    }
}
