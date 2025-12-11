package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotConvertUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@ToString
public class DomainInfo {

    @Getter
    private String domainName;
    private Long expiration;
    private Long registration;
    private GlueInfo glueInfo;
    @SerializedName("registrant_contactId")
    private int registrantContactId;
    @SerializedName("admin_contactId")
    private int adminContactId;
    @SerializedName("tech_contactId")
    private int techContactId;
    @SerializedName("billing_contactId")
    private int billingContactId;
    private String locked;
    private String disabled;
    private String udrpLocked;
    private String registrantUnverified;
    private String hold;
    private String privacy;
    @SerializedName("is_for_sale")
    private String isForSale;
    @SerializedName("renew_option")
    private String renewOption;
    @Getter
    private String note;
    @SerializedName("folder_id")
    private int folderId;
    @SerializedName("folder_name")
    private String folderName;
    private String status;

    public boolean isLocked() {
        return DynadotConvertUtils.asBool(locked);
    }

    public boolean isDisabled() {
        return DynadotConvertUtils.asBool(disabled);
    }

    public boolean isHold() {
        return DynadotConvertUtils.asBool(hold);
    }

    public boolean isRegistrantUnverified() {
        return DynadotConvertUtils.asBool(registrantUnverified);
    }

    public DomainStatus getStatus() {
        return DomainStatus.fromLabel(status);
    }

    public Date getExpirationDate() {
        return expiration != null ? new Date(expiration) : null;
    }

    public Date getRegistrationDate() {
        return registration != null ? new Date(registration) : null;
    }

    public DomainPrivacy getPrivacy() {
        return DomainPrivacy.fromInfo(privacy);
    }

    public DomainRenewOption getRenewOption() {
        return DomainRenewOption.fromInfo(renewOption);
    }

    @ToString
    public static class GlueInfo {
        @SerializedName("name_server_settings")
        private NameServerSettings nameServerSettings;
    }

    @ToString
    public static class NameServerSettings {
        private String type;
        @SerializedName("with_ads")
        private String withAds;
        @SerializedName("forward_to")
        private String forwardTo;
        @SerializedName("forward_type")
        private String forwardType;
        @SerializedName("website_title")
        private String websiteTitle;
        private String ttl;
        @SerializedName("email_forwarding")
        private EmailForwarding emailForwarding;
    }

    @ToString
    public static class EmailForwarding {
        private String type;
    }
}
