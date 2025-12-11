package balbucio.dynadot4j.action;

import balbucio.dynadot4j.model.DomainPrivacy;
import balbucio.dynadot4j.model.RegistrantContact;
import balbucio.dynadot4j.utils.DynadotConvertUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class DomainRegistration {

    private final String domainName;
    private int duration = 1;
    @SerializedName("auth_code")
    private String authCode = "";
    private int customerId = 0;
    @SerializedName("name_server_list")
    private List<String> nameserver = new ArrayList<>();
    private DomainPrivacy privacy = DomainPrivacy.FULL;
    private String currency = "USD";
    private boolean registerPremium = false;
    private String couponCode = "";
    @SerializedName("registrant_contact")
    private RegistrantContact registrant;
    @SerializedName("admin_contact")
    private RegistrantContact admin;
    @SerializedName("tech_contact")
    private RegistrantContact tech;
    @SerializedName("billing_contact")
    private RegistrantContact billing;

    public DomainRegistration(String domainName) {
        this.domainName = domainName;
    }

    public DomainRegistration withDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public DomainRegistration withAuthCode(String authCode) {
        this.authCode = authCode;
        return this;
    }

    public DomainRegistration withCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }

    public DomainRegistration withNS(List<String> ns) {
        this.nameserver = ns;
        return this;
    }

    public DomainRegistration addNS(String ns) {
        this.nameserver.add(ns);
        return this;
    }

    public DomainRegistration withPrivacy(DomainPrivacy privacy) {
        this.privacy = privacy;
        return this;
    }

    public DomainRegistration withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public DomainRegistration setPremium(boolean premium) {
        this.registerPremium = premium;
        return this;
    }

    public DomainRegistration withCouponCode(String couponCode) {
        this.couponCode = couponCode;
        return this;
    }

    public DomainRegistration withRegistrantContact(RegistrantContact contact) {
        this.registrant = contact;
        return this;
    }

    public DomainRegistration withAdminContact(RegistrantContact contact) {
        this.admin = contact;
        return this;
    }

    public DomainRegistration withTechContact(RegistrantContact contact) {
        this.tech = contact;
        return this;
    }

    public DomainRegistration withBillingContact(RegistrantContact contact) {
        this.billing = contact;
        return this;
    }

    public DomainRegistration withContact(RegistrantContact contact) {
        this.registrant = contact;
        this.admin = contact;
        this.tech = contact;
        this.billing = contact;
        return this;
    }

    public JSONObject toJSON() {
        JSONObject domain = new JSONObject();
        domain.put("duration", duration);
        domain.put("auth_code", authCode);
        domain.put("customer_id", customerId);
        domain.put("name_server_list", nameserver);
        domain.put("registrant_contact", registrant.toJSON());
        domain.put("admin_contact", admin.toJSON());
        domain.put("tech_contact", tech.toJSON());
        domain.put("billing_contact", billing.toJSON());
        domain.put("privacy", privacy.getLabel());

        JSONObject obj = new JSONObject();
        obj.put("domain", domain);
        obj.put("register_premium", registerPremium);
        obj.put("coupon_code", couponCode);
        obj.put("currency", currency);
        return obj;
    }

    public static DomainRegistration create(String domainName) {
        return new DomainRegistration(domainName);
    }
}
