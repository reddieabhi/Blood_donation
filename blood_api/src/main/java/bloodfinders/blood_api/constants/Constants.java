package bloodfinders.blood_api.constants;

public class Constants {

    //events
    public static final String EVENT_STATUS_CREATED = "Created";

    // emails
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final int SMTP_PORT = 587;
    public static final String OTP_EMAIL_SUBJECT = "Your One-Time Password (OTP)";
    public static final String OTP_EMAIL_BODY_PREFIX = "Your OTP is: ";

    // OTP
    public static final int OTP_EXPIRY_MINUTES = 5;
    public static final int OTP_LENGTH = 6;

    //Status codes
    public static final int STATUS_OK = 200;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
}
