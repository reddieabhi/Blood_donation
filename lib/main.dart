import 'dart:io';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'HomeScreen/homeScreen.dart';
import 'onBoarding/loginScreen.dart';

// local notifications
final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
    FlutterLocalNotificationsPlugin();

Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
  print('📩 [BG] Message ID: ${message.messageId}');
  print('📩 [BG] Data: ${message.data}');
}

final FlutterSecureStorage secureStorage = FlutterSecureStorage();
FirebaseMessaging messaging = FirebaseMessaging.instance;

Future<void> initFcmAndHandleToken() async {
  NotificationSettings settings = await messaging.requestPermission(
    alert: true,
    badge: true,
    sound: true,
  );

  if (Platform.isAndroid) {
    if (await Permission.notification.isDenied) {
      await Permission.notification.request();
    }
  }

  if (settings.authorizationStatus == AuthorizationStatus.authorized) {
    await saveFcmToken(); // Merged into one

    FirebaseMessaging.onMessage.listen((message) {
      print('📥 [FOREGROUND] Message: ${message.notification?.title}');
      print('📦 [NOTIFICATION] Full message: ${message.toMap()}');
      _showLocalNotification(message);
    });

    print('✅ [FCM] onMessage listener ATTACHED because authorization was granted.');

    FirebaseMessaging.onMessageOpenedApp.listen((message) {
      print('📲 [OPENED] App opened via notification: ${message.data}');
      print('📲 [OPENED] Notification tapped');
      print('📦 [NOTIFICATION] Full message: ${message.toMap()}');
    });
  } else {
    print('🚫 [FCM] Permission denied');
  }
}

Future<void> saveFcmToken() async {
  final fcmToken = await messaging.getToken();
  print('🔑 [FCM] Token: $fcmToken');

  if (fcmToken == null) return;

  final storedToken = await secureStorage.read(key: 'fcmToken');

  if (storedToken != fcmToken) {
    await secureStorage.write(key: 'fcmToken', value: fcmToken);
    print('🔄 [FCM] Token updated in secure storage');
  } else {
    print('✅ [FCM] Token already up-to-date');
  }
}

Future<void> _showLocalNotification(RemoteMessage message) async {
  print("⚡ Notification function called!");
  print("🔔 Title: ${message.notification?.title}");
  print("📜 Body: ${message.notification?.body}");
  print("🚀 Showing notification...");
  const androidDetails = AndroidNotificationDetails(
    'default_channel_id',
    'Default Channel',
    channelDescription: 'For general notifications',
    importance: Importance.max,
    priority: Priority.high,
  );

  const notificationDetails = NotificationDetails(android: androidDetails);

  await flutterLocalNotificationsPlugin.show(
    message.notification.hashCode,
    message.notification?.title ?? 'No Title',
    message.notification?.body ?? 'No Body',
    notificationDetails,
  );

}

Future<void> main() async{
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp();
  FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);

  const androidInit = AndroidInitializationSettings('@mipmap/ic_launcher');
  const initSettings = InitializationSettings(android: androidInit);
  await flutterLocalNotificationsPlugin.initialize(initSettings);

  await flutterLocalNotificationsPlugin.initialize(
    initSettings,
    onDidReceiveNotificationResponse: (NotificationResponse response) async {
      print("🔍 Notification tapped! Payload: ${response.payload}");
    },
  );

  await initFcmAndHandleToken();

  runApp(const MyApp());
}



class MyApp extends StatelessWidget {
  const MyApp({super.key});

  Future<Widget> _getInitialScreen() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('Token');
    if (token != null && token.isNotEmpty) {
      // Token exists, navigate to Home
      return Homescreen();
    }
    // No token, navigate to Login
    return Loginscreen();
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: 'Find my blood',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
          useMaterial3: true,
        ),
      home: FutureBuilder<Widget>(
        future: _getInitialScreen(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Scaffold(
              body: Center(child: CircularProgressIndicator(color: Colors.red,)),
            );
          } else if (snapshot.hasData) {
            return snapshot.data!;
          } else {
            return Loginscreen();
          }
        },
      ),
    );
  }
}

