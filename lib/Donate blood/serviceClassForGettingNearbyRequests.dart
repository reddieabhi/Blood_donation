import 'dart:convert';
import 'package:findmyblood/Components/coordinatesService.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';


class BloodRequestService {
  final String _url = 'https://javapaas-196791-0.cloudclusters.net/blood/get-near-requests';

  Future<List<BloodRequest>> fetchNearbyRequests() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('Token');

      if (token == null) {
        print('⚠️ [Auth] Token not found in SharedPreferences.');
        return [];
      }

      final location = await LocationService().getCurrentLocation();
      print('📍 [Location] $location');

      final request = http.Request('GET', Uri.parse(_url))
        ..headers.addAll({
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        })
        ..body = jsonEncode({
          'latitude': location['latitude'],
          'longitude': location['longitude'],
        });

      final streamedResponse = await request.send();
      final response = await http.Response.fromStream(streamedResponse);

      print('🌐 [HTTP] Status: ${response.statusCode}');
      print('📝 [HTTP] Response: ${response.body}');

      if (response.statusCode == 200) {
        final decoded = json.decode(response.body);
        if (decoded is List) {
          final requests = decoded.map((e) => BloodRequest.fromJson(e)).toList();
          print('✅ [Parsed] ${requests.length} requests fetched.');
          return List<BloodRequest>.from(requests);
        } else {
          print('❗ [Parse Error] Unexpected response format.');
          return [];
        }
      } else {
        print('❌ [Error] HTTP ${response.statusCode}: ${response.reasonPhrase}');
        return [];
      }
    } catch (e, stackTrace) {
      print('💥 [Exception] $e');
      print(stackTrace);
      return [];
    }
  }
}


class BloodRequest {
  final String eventID;
  final String bloodGroup;
  final String userName;
  final String? place;
  final double latitude;
  final double longitude;
  final String userId;
  final String currentStatus;

  BloodRequest({
    required this.eventID,
    required this.bloodGroup,
    required this.userName,
    this.place,
    required this.latitude,
    required this.longitude,
    required this.userId,
    required this.currentStatus,
  });

  factory BloodRequest.fromJson(Map<String, dynamic> json) {
    return BloodRequest(
      eventID: json['eventID'],
      bloodGroup: json['bloodGroup'],
      userName: json['userName'],
      place: json['place'],
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      userId: json['userId'],
      currentStatus: json['currentStatus'],
    );
  }
}
