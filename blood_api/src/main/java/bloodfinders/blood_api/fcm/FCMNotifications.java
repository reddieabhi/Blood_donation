package bloodfinders.blood_api.fcm;

import bloodfinders.blood_api.controller.RequestBloodController;
import bloodfinders.blood_api.model.DTO.UserPushTokenDTO;
//import com.google.api.services.storage.model.Notification;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FCMNotifications {

    private static final Logger logger = LoggerFactory.getLogger(FCMNotifications.class);
    public static boolean sendPushNotifications(List<UserPushTokenDTO> users, String bloodGroup, String msg, String eventId) {
        logger.debug("IN FCM Notification class to send push notifications");
        if (users.isEmpty()) {
            logger.warn("No users found to send notifications.");
            return false;
        }

        List<String> registrationTokens = new ArrayList<>();
        for (UserPushTokenDTO user : users) {
            if (user.getPushToken() != null && !user.getPushToken().isEmpty()) {
                registrationTokens.add(user.getPushToken());
            }
        }
        logger.info("FCM Registrations tokens received for push {}", registrationTokens);

        if (registrationTokens.isEmpty()) {
            logger.warn("No valid FCM tokens available.");
            return false;
        }

        // Create a push notification message
//
//        MulticastMessage message = MulticastMessage.builder()
//                .putData("bloodGroup", bloodGroup)
//                .putData("message", msg)
//                .putData("eventId", eventId)
//                .addAllTokens(registrationTokens)
//                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle("Alert")
                        .setBody("From backend")
                        .build())
                .putData("bloodGroup", bloodGroup)
                .putData("message", msg)
                .putData("eventId", eventId)
                .addAllTokens(registrationTokens)
                .build();


        try {
            // Send the push notification
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            logger.info("Number of push notifications sent {}", response.getSuccessCount());
            return  true;
        } catch (Exception e) {
            logger.error("Error sending push notifications: {}", e.getMessage());
            e.printStackTrace();
            return  false;
        }
    }

    public static boolean sendPushNotificationssingle(String pushToken, String acceptorId, String msg) {
        if (pushToken == null || pushToken.isEmpty()){
            return false;
        }
        Message message = Message.builder()
                .putData("acceptor", acceptorId)
                .putData("message", msg)
                .setToken(pushToken)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification sent successfully: " + response);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
            return false;
        }


    }
}
