package org.linphone.onu_legacy.MVP.Implementation.model.AudioDataClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AudioUploadResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("info")
    @Expose
    private String info;
    @SerializedName("file_name")
    @Expose
    private String fileName;
    @SerializedName("request_file_size")
    @Expose
    private Integer requestFileSize;
    @SerializedName("file_size")
    @Expose
    private Integer fileSize;
    @SerializedName("file_exists")
    @Expose
    private Boolean fileExists;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getRequestFileSize() {
        return requestFileSize;
    }

    public void setRequestFileSize(Integer requestFileSize) {
        this.requestFileSize = requestFileSize;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public Boolean getFileExists() {
        return fileExists;
    }

    public void setFileExists(Boolean fileExists) {
        this.fileExists = fileExists;
    }
}
