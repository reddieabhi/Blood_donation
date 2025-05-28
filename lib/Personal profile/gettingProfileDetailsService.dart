import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class ProfileService {
  Future<UserProfile?> fetchProfileDetails() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('Token');
      final userId = prefs.getString('userId');

      if (token == null || userId == null) {
        print('⚠️ Token or userId not found in SharedPreferences.');
        return null;
      }

      final url = 'https://javapaas-196791-0.cloudclusters.net/users/$userId';

      final response = await http.get(
        Uri.parse(url),
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $token",
        },
      );

      print('📡 Request URL: $url');
      print('📡 Status Code: ${response.statusCode}');

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        print('✅ Parsed Profile Data: $data');
        return UserProfile.fromJson(data);
      } else {
        print('❌ Failed to fetch profile: ${response.reasonPhrase}');
        return null;
      }
    } catch (e, stack) {
      print('❌ Exception occurred while fetching profile: $e');
      print(stack);
      return null;
    }
  }
}

class UserProfile {
  final String name;
  final String phoneNumber;
  final String place;
  final String city;
  final String bloodGroup;
  final double latitude;
  final double longitude;
  final String email;

  UserProfile({
    required this.name,
    required this.phoneNumber,
    required this.place,
    required this.city,
    required this.bloodGroup,
    required this.latitude,
    required this.longitude,
    required this.email,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      name: json['name'] ?? '',
      phoneNumber: json['phoneNumber'] ?? '',
      place: json['place'] ?? '',
      city: json['city'] ?? '',
      bloodGroup: json['bloodGroup'] ?? '',
      latitude: (json['latitude'] ?? 0).toDouble(),
      longitude: (json['longitude'] ?? 0).toDouble(),
      email: json['email'] ?? '',
    );
  }
}
