package com.ams.dtos.clientDto;

import java.time.LocalDateTime;

public class Document {
    private String name;
    private LocalDateTime uploadTime;
    private long size;

    public Document(String name, LocalDateTime uploadTime, long size) {
        this.name = name;
        this.uploadTime = uploadTime;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public long getSize() {
        return size;
    }
}
