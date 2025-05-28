import 'package:dotted_line/dotted_line.dart';
import 'package:findmyblood/Donate%20blood/DonationScreen.dart';
import 'package:findmyblood/Donate%20blood/serviceClassForGettingNearbyRequests.dart';
import 'package:findmyblood/Personal%20profile/profileDetailsScreen.dart';
import 'package:findmyblood/Request%20blood/requestDialog.dart';
import 'package:findmyblood/onBoarding/loginScreen.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Homescreen extends StatefulWidget {
  const Homescreen({super.key});

  @override
  State<Homescreen> createState() => _HomescreenState();
}

class _HomescreenState extends State<Homescreen> {

  late Future<List<BloodRequest>> _bloodRequests;

  Position? _currentPosition;

  @override
  void initState() {
    super.initState();
    _initLocation();
    _bloodRequests = BloodRequestService().fetchNearbyRequests();
  }

  Future<void> _initLocation() async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      // Handle: Location service not enabled
      return;
    }

    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.deniedForever || permission == LocationPermission.denied) {
        // Handle: Permission denied
        return;
      }
    }

    _currentPosition = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    setState(() {});
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      body: Stack(
        children: [
          Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                colors: [Color(0xFFFFEBEE), Color(0xFFE57373)],
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
              ),
            ),
          ),
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 30),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Top bar
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Row(
                        children: [
                          Icon(Icons.bloodtype_rounded, size: 40, color: Colors.red[900]),
                          const SizedBox(width: 8),
                          Text(
                            "Blood Finders",
                            style: GoogleFonts.karla(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                              color: Colors.red[900],
                            ),
                          ),
                        ],
                      ),
                      Row(
                        children: [
                          IconButton(
                            icon: Icon(Icons.more_vert, color: Colors.red[900], size: 28),
                            onPressed: () => _showOptionsSheet(context),
                            tooltip: 'More Options',
                          ),
                          IconButton(
                            icon: Icon(Icons.logout, color: Colors.red[900], size: 28),
                            onPressed: () => _confirmLogout(context),
                            tooltip: 'Logout',
                          ),
                        ],
                      )
                    ],
                  ),
                  const SizedBox(height: 40),

                  Center(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        Expanded(
                          child: _buildHeroCard(
                            title: "Request\n Blood",
                            icon: Icons.search_rounded,
                            color: const Color(0xFFE53935),
                            onTap: (){
                              showBloodRequestDialog(context);
                            },
                          ),
                        ),
                        const SizedBox(width: 20),
                        Expanded(
                          child: _buildHeroCard(
                            title: "Donate\n Blood",
                            icon: Icons.volunteer_activism_rounded,
                            color: const Color(0xFF2E7D32),
                            onTap: () {
                              Navigator.push(context, CupertinoPageRoute(builder: ((context)=>NearbyRequestsScreen())));
                            },
                          ),
                        ),
                      ],
                    ),
                  ),
                  SizedBox(height: 15,),
                  DottedLine(
                    dashColor: Colors.red[600]!,
                    dashGapLength: 4,
                    dashLength: 4,
                    lineThickness: 1,
                  ),
                  SizedBox(height: 10,),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
                    child: Row(
                      children: [
                        Icon(Icons.near_me_rounded, size: 30, color: Colors.red[900]),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            "Nearby blood requests",
                            style: GoogleFonts.karla(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                              color: Colors.black87,
                            ),
                            overflow: TextOverflow.ellipsis,
                            maxLines: 1,
                          ),
                        ),

                      ],
                    ),
                  ),
                  SizedBox(height: 10,),
                  Expanded(
                    child: FutureBuilder<List<BloodRequest>>(
                      future: _bloodRequests,
                      builder: (context, snapshot) {
                        if (snapshot.connectionState == ConnectionState.waiting) {
                          return const Center(child: CircularProgressIndicator(color: Colors.red,));
                        } else if (snapshot.hasError) {
                          return Center(child: Text('Something went wrong 😢'));
                        } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                          return Center(child: Text('No nearby requests found.'));
                        }

                        final requests = snapshot.data!;
                        return ListView.separated(
                          itemCount: requests.length,
                          separatorBuilder: (_, __) => const SizedBox(height: 12),
                          padding: const EdgeInsets.only(bottom: 16),
                          itemBuilder: (context, index) {
                            final req = requests[index];
                            return _buildRequestTile(req);
                          },
                        );
                      },
                    ),
                  ),

                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRequestTile(BloodRequest request) {
    double? distanceKm;

    if (_currentPosition != null &&
        request.latitude != null &&
        request.longitude != null) {
      distanceKm = Geolocator.distanceBetween(
        _currentPosition!.latitude,
        _currentPosition!.longitude,
        request.latitude!,
        request.longitude!,
      ) /
          1000; // meters to km
    }

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black12,
            blurRadius: 6,
            offset: const Offset(0, 3),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(Icons.bloodtype, color: Colors.red[800]),
              const SizedBox(width: 8),
              Text(
                request.bloodGroup,
                style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              const Spacer(),
              if (distanceKm != null)
                Row(
                  children: [
                    const Icon(Icons.location_on, size: 18, color: Colors.black45),
                    const SizedBox(width: 4),
                    Text(
                      '${distanceKm.toStringAsFixed(2)} km',
                      style: const TextStyle(fontSize: 14, color: Colors.black87),
                    ),
                    const SizedBox(width: 8),
                  ],
                ),
              // Status box
              if (request.currentStatus != null)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                  decoration: BoxDecoration(
                    color: Colors.green[600],
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    request.currentStatus!,
                    style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                      color: Colors.white,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              const Icon(Icons.person, size: 20, color: Colors.black54),
              const SizedBox(width: 6),
              Text(request.userName, style: const TextStyle(fontSize: 16)),
            ],
          ),
          const SizedBox(height: 6),
          Row(
            children: [
              const Icon(Icons.pin_drop, size: 20, color: Colors.black54),
              const SizedBox(width: 6),
              Expanded(
                child: Text(
                  request.place ?? 'No location info',
                  style: const TextStyle(fontSize: 16),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }


  Widget _buildHeroCard({
    required String title,
    required IconData icon,
    required Color color,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        height: 120,
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: Colors.black12,
              blurRadius: 12,
              offset: const Offset(0, 6),
            ),
          ],
        ),
        child: Row(
          children: [
            Container(
              height: 60,
              width: 60,
              decoration: BoxDecoration(
                color: color.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
              child: Icon(icon, color: color, size: 28),
            ),
            const SizedBox(width: 16),
            Text(
              title,
              style: GoogleFonts.karla(
                fontSize: 18,
                fontWeight: FontWeight.w700,
                color: Colors.black87,
              ),
            ),
          ],
        ),
      ),
    );
  }


  void _showOptionsSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.white,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (_) {
        return SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 20),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Center(
                  child: Container(
                    height: 5,
                    width: 40,
                    margin: const EdgeInsets.only(bottom: 20),
                    decoration: BoxDecoration(
                      color: Colors.grey[300],
                      borderRadius: BorderRadius.circular(10),
                    ),
                  ),
                ),
                _buildOptionTile(
                  icon: Icons.person,
                  label: 'Profile Details',
                  onTap: () {
                    if (!mounted) return;

                    Navigator.push(
                      context,
                      CupertinoPageRoute(builder: (_) => const ProfileScreen()),
                    );
                  },
                ),
                _buildOptionTile(
                  icon: Icons.help_outline,
                  label: 'Help',
                  onTap: () {
                    Navigator.pop(context);
                    // Navigate to help
                  },
                ),
                _buildOptionTile(
                  icon: Icons.info_outline,
                  label: 'About Us',
                  onTap: () {
                    Navigator.pop(context);
                    // Navigate to about
                  },
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildOptionTile({
    required IconData icon,
    required String label,
    required VoidCallback onTap,
  }) {
    return ListTile(
      leading: Icon(icon, color: Colors.red[900]),
      title: Text(
        label,
        style: TextStyle(
          fontSize: 16,
          fontWeight: FontWeight.w600,
          color: Colors.black87,
        ),
      ),
      onTap: onTap,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      hoverColor: Colors.red[50],
    );
  }


  Future<void> _confirmLogout(BuildContext context) async {
    final bool? confirmed = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) {
        return Dialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
          backgroundColor: Colors.white,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 30),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(Icons.logout, color: Colors.red[700], size: 48),
                const SizedBox(height: 20),
                const Text(
                  'Logout Confirmation',
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: Colors.black87),
                ),
                const SizedBox(height: 12),
                const Text(
                  'Are you sure you want to logout?\nYou will need to log in again to continue.',
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 16, color: Colors.black54),
                ),
                const SizedBox(height: 30),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    OutlinedButton(
                      onPressed: () => Navigator.of(context).pop(false),
                      style: OutlinedButton.styleFrom(
                        foregroundColor: Colors.grey[700],
                        side: BorderSide(color: Colors.grey[400]!),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                      child: const Padding(
                        padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                        child: Text('Cancel'),
                      ),
                    ),
                    ElevatedButton(
                      onPressed: () => Navigator.of(context).pop(true),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.red[700],
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 10),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      ),
                      child: const Text('Logout'),
                    ),
                  ],
                )
              ],
            ),
          ),
        );
      },
    );

    if (confirmed == true) {
      await _logout(context);
    }
  }

  Future<void> _logout(BuildContext context) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    print("prefs are cleared");
    Navigator.pushReplacement(context, CupertinoPageRoute(builder: (_) => Loginscreen()));
  }
}
