import 'package:findmyblood/Donate%20blood/serviceClassForGettingNearbyRequests.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class NearbyRequestsScreen extends StatelessWidget {
  const NearbyRequestsScreen({super.key});

  void _fetchRequests(BuildContext context) async {
    final service = BloodRequestService();
    await service.fetchNearbyRequests();

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Request sent. Check console for response.')),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        title: Text(
          'Nearby Blood Requests',
          style: GoogleFonts.karla(fontWeight: FontWeight.bold),
        ),
        backgroundColor: Colors.red[900],
        foregroundColor: Colors.white,
      ),
      body: Center(
        child: ElevatedButton.icon(
          onPressed: () => _fetchRequests(context),
          icon: const Icon(Icons.cloud_download),
          label: const Text("Fetch Requests"),
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.red[900],
            foregroundColor: Colors.white,
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          ),
        ),
      ),
    );
  }
}
