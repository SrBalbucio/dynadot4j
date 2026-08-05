package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MaintenanceNoticeData {

    @SerializedName("start_time")
    private long startTime;
    @SerializedName("end_time")
    private long endTime;
    @SerializedName("available_services")
    private String availableServices;
    @SerializedName("unavailable_services")
    private String unavailableServices;
    @SerializedName("registry_name")
    private String registryName;
    @SerializedName("affected_tlds")
    private String affectedTlds;
}