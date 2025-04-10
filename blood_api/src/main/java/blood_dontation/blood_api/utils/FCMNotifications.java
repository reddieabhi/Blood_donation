package blood_dontation.blood_api.utils;

import blood_dontation.blood_api.model.DTO.UserPushInfo;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FCMNotifications {

    public static boolean sendPushNotifications(List<UserPushInfo> users, String bloodGroup, String msg, String eventId) {
        if (users.isEmpty()) {
            System.out.println("No users found to send notifications.");
            return false;
        }

        List<String> registrationTokens = new ArrayList<>();
        for (UserPushInfo user : users) {
            if (user.getPushToken() != null && !user.getPushToken().isEmpty()) {
                registrationTokens.add(user.getPushToken());
            }
        }


        if (registrationTokens.isEmpty()) {
            System.out.println("No valid FCM tokens available.");
            return false;
        }

        // Create a push notification message

        MulticastMessage message = MulticastMessage.builder()
                .putData("bloodGroup", bloodGroup)
                .putData("message", msg)
                .putData("eventId", eventId)
                .addAllTokens(registrationTokens)
                .build();

        try {
            // Send the push notification
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            System.out.println(response.getSuccessCount() + " messages were sent successfully.");
            return  true;
        } catch (Exception e) {
            System.err.println("Error sending push notifications: " + e.getMessage());
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
