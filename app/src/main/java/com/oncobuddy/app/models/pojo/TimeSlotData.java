package com.oncobuddy.app.models.pojo;

import java.io.Serializable;

public class TimeSlotData implements Serializable {
        private String start_time;
        private String end_time;
        private boolean is_available;

    public TimeSlotData(String start_time, String end_time, boolean is_available) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.is_available = is_available;
    }

    public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public boolean isIs_available() {
            return is_available;
        }

        public void setIs_available(boolean is_available) {
            this.is_available = is_available;
        }

        @Override
        public String toString() {
            return "TimeSlotData{" +
                    "start_time='" + start_time + '\'' +
                    ", end_time='" + end_time + '\'' +
                    ", is_available=" + is_available +
                    '}';
        }
    }