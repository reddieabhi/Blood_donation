import 'package:findmyblood/Personal%20profile/gettingProfileDetailsService.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  final ProfileService _profileService = ProfileService();
  UserProfile? _profile;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  Future<void> _loadProfile() async {
    final data = await _profileService.fetchProfileDetails();
    setState(() {
      _profile = data;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        title: Text(
          'My Profile',
          style: GoogleFonts.karla(fontWeight: FontWeight.bold),
        ),
        backgroundColor: Colors.red[900],
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator(color: Colors.red))
          : _profile == null
          ? const Center(child: Text("Failed to load profile"))
          : SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            CircleAvatar(
              radius: 48,
              backgroundColor: Colors.red[100],
              child: Icon(Icons.person, size: 48, color: Colors.red[900]),
            ),
            const SizedBox(height: 12),
            Text(
              _profile!.name,
              style: GoogleFonts.karla(
                fontSize: 22,
                fontWeight: FontWeight.bold,
                color: Colors.black87,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              _profile!.email,
              style: GoogleFonts.karla(
                fontSize: 14,
                color: Colors.grey[700],
              ),
            ),
            const SizedBox(height: 24),
            _buildInfoCard("Phone", _profile!.phoneNumber, Icons.phone),
            _buildInfoCard("Place", _profile!.place, Icons.location_on),
            _buildInfoCard("City", _profile!.city, Icons.location_city),
            _buildInfoCard("Blood Group", _profile!.bloodGroup, Icons.bloodtype),
            _buildInfoCard("Latitude", _profile!.latitude.toString(), Icons.my_location),
            _buildInfoCard("Longitude", _profile!.longitude.toString(), Icons.my_location_outlined),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoCard(String title, String value, IconData icon) {
    return Card(
      color: Colors.white,
      margin: const EdgeInsets.symmetric(vertical: 8),
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: ListTile(
        leading: Icon(icon, color: Colors.red[900]),
        title: Text(
          title,
          style: GoogleFonts.karla(
            fontSize: 14,
            fontWeight: FontWeight.w600,
            color: Colors.grey[700],
          ),
        ),
        subtitle: Text(
          value,
          style: GoogleFonts.karla(
            fontSize: 16,
            fontWeight: FontWeight.bold,
            color: Colors.black87,
          ),
        ),
      ),
    );
  }
}
