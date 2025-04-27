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
    public static final String OTP_EXPIRED = "Otp Expired";
    public static final String INVALID_OTP = "Otp Invalid";
    public static final String OTP_VALID = "OTP verified successfully";

    //Status codes
    public static final int STATUS_OK = 200;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_INVALID = 400;

    //users
    public static final String USER_FOR_VERIFIED_OTP_JWT = "OTP_VERIFIED";

    //Login
    public static final String EMAIL_PASSWORD_EMPTY = "Email or password cannot be empty";
    public static final String USER_NOT_FOUND = "User not found with provided email";
    public static final String INVALID_PASSWORD = "Invalid Password";
    public static final String LOGIN_SUCCESS = "User Login success";

    //Expire timings
    public static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 365;
    public static final long SHORT_EXPIRE_TIME = 1000L * 60;


    //Passwords
    public static final String UNABLE_TO_CHANGE_PASSWORD = "Unable to change password";
    public static final String PASSWORD_CHANGE_SUCCESSFULL = "Changed Password Successfully";

}
