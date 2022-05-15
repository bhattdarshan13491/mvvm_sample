package com.oncobuddy.app.models.pojo.video_calling;

import java.io.Serializable;

public class FCMRequest implements Serializable {
    private String to;
    private DateInput data;
    private int priority;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public DateInput getData() {
        return data;
    }

    public void setData(DateInput data) {
        this.data = data;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "FCMRequest{" +
                "to='" + to + '\'' +
                ", data=" + data +
                ", priority=" + priority +
                '}';
    }

    public static class DateInput implements Serializable {
        private String body;
        private String title;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "DateInput{" +
                    "body='" + body + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
}