import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class BloodRequestService {
  final String _url = 'https://javapaas-196791-0.cloudclusters.net/blood/get-near-requests';

  Future<void> fetchNearbyRequests() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('Token');

      if (token == null) {
        print('⚠️ Token not found in SharedPreferences.');
        return;
      }

      print(token);

      final response = await http.get(
        Uri.parse(_url),
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $token",
        },
      );

      print('📡 Request URL: $_url');
      print('📡 Status Code: ${response.statusCode}');
      print('📡 Response Body: ${response.body}');

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        print('✅ Parsed Data: $data');
      } else {
        print('❌ Failed to fetch data: ${response.reasonPhrase}');
      }
    } catch (e, stack) {
      print('❌ Exception occurred: $e');
      print(stack);
    }
  }
}
