package balbucio.dynadot4j.model.webhook;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum WebhookEventType {

    ORDER_COMPLETED("order_completed", OrderEventData.class),
    DOMAIN_TRANSFER_AWAY("domain_transfer_away", DomainTransferAwayData.class),
    DOMAIN_EXPIRING("domain_expiring", DomainExpiringData.class),
    ACCOUNT_BALANCE_REMINDER("account_balance_reminder", AccountBalanceReminderData.class),
    WHOIS_VERIFICATION_REQUIRED("whois_verification_required", WhoisVerificationRequiredData.class),
    WHOIS_VERIFICATION_NOTIFICATION("whois_verification_notification", WhoisVerificationNotificationData.class),
    ORDER_PAYMENT_REQUIRED("order_payment_required", OrderEventData.class),
    DOMAIN_STATUS_CHANGED("domain_status_changed", DomainStatusChangedData.class),
    DOMAIN_SUSPENSION_STATUS_CHANGED("domain_suspension_status_changed", DomainSuspensionStatusChangedData.class),
    MAINTENANCE_NOTICE("maintenance_notice", MaintenanceNoticeData.class);

    private final String label;
    private final Class<?> dataClass;

    public static WebhookEventType fromLabel(String label) {
        return Arrays.stream(values())
                .filter((value) -> value.getLabel().equals(label))
                .findFirst().orElse(null);
    }
}