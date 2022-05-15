package com.oncobuddy.app.models.pojo.video_calling;

import java.io.Serializable;
import java.util.List;

public class FCMResponse implements Serializable {
    private long multicast_id;
    private long success;
    private long failure;
    private long canonical_ids;
    private List<ResultFCMOutput> results;

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public long getSuccess() {
        return success;
    }

    public void setSuccess(long success) {
        this.success = success;
    }

    public long getFailure() {
        return failure;
    }

    public void setFailure(long failure) {
        this.failure = failure;
    }

    public long getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(long canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<ResultFCMOutput> getResults() {
        return results;
    }

    public void setResults(List<ResultFCMOutput> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "FCMResponse{" +
                "multicast_id=" + multicast_id +
                ", success=" + success +
                ", failure=" + failure +
                ", canonical_ids=" + canonical_ids +
                ", results=" + results +
                '}';
    }

    public class ResultFCMOutput implements Serializable{
        private String message_id;

        public String getMessage_id() {
            return message_id;
        }

        public void setMessage_id(String message_id) {
            this.message_id = message_id;
        }

        @Override
        public String toString() {
            return "ResultFCMOutput{" +
                    "message_id='" + message_id + '\'' +
                    '}';
        }
    }
}