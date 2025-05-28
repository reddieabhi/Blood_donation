import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../Components/coordinatesService.dart';
import 'package:awesome_snackbar_content/awesome_snackbar_content.dart';
import '../Components/snackBar.dart';

Future<void> showBloodRequestDialog(BuildContext context) async {
  final TextEditingController nameController = TextEditingController();
  final TextEditingController placeController = TextEditingController();
  String? selectedBloodGroup;

  final List<String> bloodGroups = [
    'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-',
  ];

  showDialog(
    context: context,
    barrierDismissible: false,
    builder: (context) {
      return StatefulBuilder(
        builder: (context, setState) {
          return AlertDialog(
            backgroundColor: Colors.white,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            title: Row(
              children: [
                Icon(Icons.bloodtype_rounded, color: Colors.red[900]),
                const SizedBox(width: 8),
                Text(
                  'Request Blood',
                  style: GoogleFonts.karla(
                    fontWeight: FontWeight.bold,
                    color: Colors.black87,
                  ),
                ),
              ],
            ),
            content: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  _buildInputField("Full Name", nameController, Icons.person),
                  const SizedBox(height: 14),
                  _buildInputField("Place", placeController, Icons.location_on),
                  const SizedBox(height: 14),
                  DropdownButtonFormField<String>(
                    value: selectedBloodGroup,
                    decoration: InputDecoration(
                      icon: Icon(Icons.bloodtype, color: Colors.red[900]),
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
                      labelText: 'Select Blood Group',
                    ),
                    items: bloodGroups.map((bg) {
                      return DropdownMenuItem(
                        value: bg,
                        child: Text(
                          bg,
                          style: GoogleFonts.karla(fontWeight: FontWeight.bold),
                        ),
                      );
                    }).toList(),
                    onChanged: (value) {
                      setState(() {
                        selectedBloodGroup = value;
                      });
                    },
                  ),
                ],
              ),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.of(context).pop(),
                child: const Text('Cancel'),
              ),
              ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red[900],
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                onPressed: () async {
                  try {
                    if (nameController.text.isEmpty ||
                        placeController.text.isEmpty ||
                        selectedBloodGroup == null) {
                      showAwesomeSnackBar(
                        context,
                        'Missing Info',
                        'Please fill all fields.',
                        ContentType.warning,
                      );
                      return;
                    }

                    final prefs = await SharedPreferences.getInstance();
                    final token = prefs.getString('Token');
                    final userId = prefs.getString('userId');

                    debugPrint("Token: $token");
                    debugPrint("UserID: $userId");

                    if (token == null || userId == null) {
                      showAwesomeSnackBar(
                        context,
                        'Error',
                        'User info missing. Please login again.',
                        ContentType.failure,
                      );
                      return;
                    }

                    final locationService = LocationService();
                    final location = await locationService.getCurrentLocation();
                    debugPrint("Location: $location");

                    final uri = Uri.parse('https://javapaas-196791-0.cloudclusters.net/blood/request-new-event');

                    final body = {
                      "bloodGroup": selectedBloodGroup!,
                      "userName": nameController.text.trim(),
                      "place": placeController.text.trim(),
                      "latitude": location["latitude"],
                      "longitude": location["longitude"],
                      "userId": userId,
                      "currentStatus": "ACTIVE",
                    };

                    debugPrint("Request Body: ${jsonEncode(body)}");

                    final response = await http.post(
                      uri,
                      headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer $token",
                      },
                      body: json.encode(body),
                    );

                    debugPrint("Response status: ${response.statusCode}");
                    debugPrint("Response body: ${response.body}");

                    if (response.statusCode == 200) {
                      Navigator.of(context).pop(); // dismiss dialog
                      showAwesomeSnackBar(
                        context,
                        'Success',
                        'Blood request submitted!',
                        ContentType.success,
                      );
                    } else {
                      showAwesomeSnackBar(
                        context,
                        'Failed',
                        'Could not submit request: ${response.body}',
                        ContentType.failure,
                      );
                    }
                  } catch (e, stack) {
                    debugPrint("Exception occurred: $e");
                    debugPrint(stack.toString());

                    Navigator.of(context).pop(); // avoid black screen
                    showAwesomeSnackBar(
                      context,
                      'Error',
                      'An unexpected error occurred. Check logs.',
                      ContentType.failure,
                    );
                  }
                },
                child: const Text('Submit', style: TextStyle(color: Colors.white)),
              ),
            ],
          );
        },
      );
    },
  );
}

Widget _buildInputField(String label, TextEditingController controller, IconData icon) {
  return TextField(
    controller: controller,
    decoration: InputDecoration(
      labelText: label,
      prefixIcon: Icon(icon, color: Colors.red[900]),
      border: OutlineInputBorder(borderRadius: BorderRadius.circular(10)),
    ),
  );
}

