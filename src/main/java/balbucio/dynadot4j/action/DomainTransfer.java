package balbucio.dynadot4j.action;

import balbucio.dynadot4j.model.DomainPrivacy;
import balbucio.dynadot4j.model.RegistrantContact;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class DomainTransfer {

    private final String domainName;
    private String authCode;
    private int duration = 1;
    private String currency = "USD";
    private int customerId = 0;
    @SerializedName("name_server_list")
    private List<String> nameserver = new ArrayList<>();
    private DomainPrivacy privacy = DomainPrivacy.FULL;
    private boolean registerPremium = false;
    private String couponCode = "";
    @SerializedName("registrant_contact_id")
    private int registrantContactId;
    @SerializedName("admin_contact_id")
    private int adminContactId;
    @SerializedName("tech_contact_id")
    private int techContactId;
    @SerializedName("billing_contact_id")
    private int billingContactId;
    @SerializedName("registrant_contact")
    private RegistrantContact registrant;
    @SerializedName("admin_contact")
    private RegistrantContact admin;
    @SerializedName("tech_contact")
    private RegistrantContact tech;
    @SerializedName("billing_contact")
    private RegistrantContact billing;

    public DomainTransfer(String domainName) {
        this.domainName = domainName;
    }

    public static DomainTransfer create(String domainName) {
        return new DomainTransfer(domainName);
    }

    public DomainTransfer withAuthCode(String authCode) {
        this.authCode = authCode;
        return this;
    }

    public DomainTransfer withDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public DomainTransfer withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public DomainTransfer withCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }

    public DomainTransfer withNS(List<String> ns) {
        this.nameserver = ns;
        return this;
    }

    public DomainTransfer addNS(String ns) {
        this.nameserver.add(ns);
        return this;
    }

    public DomainTransfer withPrivacy(DomainPrivacy privacy) {
        this.privacy = privacy;
        return this;
    }

    public DomainTransfer setPremium(boolean premium) {
        this.registerPremium = premium;
        return this;
    }

    public DomainTransfer withCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    public DomainTransfer withRegistrantContact(RegistrantContact contact) {
        this.registrant = contact;
        return this;
    }

    public DomainTransfer withAdminContact(RegistrantContact contact) {
        this.admin = contact;
        return this;
    }

    public DomainTransfer withTechContact(RegistrantContact contact) {
        this.tech = contact;
        return this;
    }

    public DomainTransfer withBillingContact(RegistrantContact contact) {
        this.billing = contact;
        return this;
    }

    public DomainTransfer withContact(RegistrantContact contact) {
        this.registrant = contact;
        this.admin = contact;
        this.tech = contact;
        this.billing = contact;
        return this;
    }

    public DomainTransfer withRegistrantContactId(int contactId) {
        this.registrantContactId = contactId;
        return this;
    }

    public DomainTransfer withAdminContactId(int contactId) {
        this.adminContactId = contactId;
        return this;
    }

    public DomainTransfer withTechContactId(int contactId) {
        this.techContactId = contactId;
        return this;
    }

    public DomainTransfer withBillingContactId(int contactId) {
        this.billingContactId = contactId;
        return this;
    }

    public JSONObject toJSON() {
        JSONObject domain = new JSONObject();
        domain.put("auth_code", authCode);
        domain.put("duration", duration);
        domain.put("customer_id", customerId);
        if (!nameserver.isEmpty()) {
            domain.put("name_server_list", nameserver);
        }
        if (registrant != null) {
            domain.put("registrant_contact", registrant.toJSON());
        }
        if (admin != null) {
            domain.put("admin_contact", admin.toJSON());
        }
        if (tech != null) {
            domain.put("tech_contact", tech.toJSON());
        }
        if (billing != null) {
            domain.put("billing_contact", billing.toJSON());
        }
        if (registrantContactId > 0) {
            domain.put("registrant_contact_id", registrantContactId);
        }
        if (adminContactId > 0) {
            domain.put("admin_contact_id", adminContactId);
        }
        if (techContactId > 0) {
            domain.put("tech_contact_id", techContactId);
        }
        if (billingContactId > 0) {
            domain.put("billing_contact_id", billingContactId);
        }
        domain.put("privacy", privacy.getLabel());

        JSONObject obj = new JSONObject();
        obj.put("domain", domain);
        obj.put("register_premium", registerPremium);
        obj.put("coupon_code", couponCode);
        obj.put("currency", currency);
        return obj;
    }
}
