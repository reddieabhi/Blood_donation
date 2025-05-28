import 'package:awesome_snackbar_content/awesome_snackbar_content.dart';
import 'package:findmyblood/HomeScreen/homeScreen.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

import '../Components/coordinatesService.dart';
import '../Components/snackBar.dart';
import 'loginScreen.dart';

class Registrationscreen extends StatefulWidget {
  const Registrationscreen({super.key});

  @override
  State<Registrationscreen> createState() => _RegistrationscreenState();
}

class _RegistrationscreenState extends State<Registrationscreen> {
  final userNameController = TextEditingController();
  final fullNameController = TextEditingController();
  final placeController = TextEditingController();
  final cityController = TextEditingController();
  final dobController = TextEditingController();
  final emailController = TextEditingController();
  final phoneController = TextEditingController();
  final passwordController = TextEditingController();
  final weightController = TextEditingController();
  final otpController = TextEditingController();
  final heightController = TextEditingController();
  String? selectedBloodGroup;
  bool isEmailVerified = false;
  String? generatedOtp;


  final List<String> bloodGroups = [
    'A+',
    'A-',
    'B+',
    'B-',
    'AB+',
    'AB-',
    'O+',
    'O-',
  ];

  @override
  void initState() {
    super.initState();
    emailController.addListener(_onEmailChanged);
  }

  @override
  void dispose() {
    emailController.removeListener(_onEmailChanged);
    super.dispose();
  }

  void _onEmailChanged() {
    // We'll trigger OTP generation when email field loses focus
  }

  Future<void> _selectDate(BuildContext context) async {
    DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime(2000),
      firstDate: DateTime(1950),
      lastDate: DateTime.now(),
      builder: (context, child) {
        return Theme(
          data: Theme.of(context).copyWith(
            colorScheme: ColorScheme.light(
              primary: Colors.red.shade900,
              onPrimary: Colors.white,
              onSurface: Colors.black,
            ),
          ),
          child: child!,
        );
      },
    );
    if (picked != null) {
      setState(() {
        dobController.text =
        "${picked.day.toString().padLeft(2, '0')}/${picked.month.toString().padLeft(2, '0')}/${picked.year}";
      });
    }
  }

  Future<void> _generateOtp(String email) async {
    try {
      final url = Uri.parse('https://javapaas-196791-0.cloudclusters.net/otp/generate?email=$email&find=false');
      print('Generating OTP for email: $email, URL: $url');
      final response = await http.post(url);

      print('Response status: ${response.statusCode}');
      print('Response body: ${response.body}');

      if (response.statusCode == 200) {
        final responseData = json.decode(response.body);
        print('OTP Generation Response data: $responseData');

        await _showOtpDialog(email);
      } else {
        print('Failed to generate OTP. Server responded with status: ${response.statusCode}');
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to generate OTP. Please try again later.')),
        );
      }
    } catch (e, stacktrace) {
      print('Exception while generating OTP: $e');
      print(stacktrace);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('An error occurred while generating OTP. Please try again.')),
      );
    }
  }

  Future<Map<String,String>?> _verifyOtp(String email, String otp) async {
    try {
      final url = Uri.parse('https://javapaas-196791-0.cloudclusters.net/otp/verify?email=$email&otp=$otp');
      final response = await http.post(url);

      if (response.statusCode == 200) {
        final responseData = json.decode(response.body);

        if (responseData['statusCode'] == 200 && responseData['payload'] == 'Success') {
          setState(() {
            isEmailVerified = true;
          });

          final jwtToken = responseData['jwtToken'];
          final userId = responseData['userId'];
          return {
            'token': jwtToken,
            'userId': userId,
          };

        }
      }
    } catch (e) {
      print('OTP verification exception: $e');
    }

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Invalid OTP or error occurred.')),
    );
    return null;
  }

  Future<void> _showOtpDialog(String email) async {
    return showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return StatefulBuilder(
          builder: (context, setState) {
            bool isLoading = false;
            String currentStep = "Verifying OTP...";

            void startVerificationFlow() async {
              setState(() => isLoading = true);
              final otp = otpController.text;

              if (otp.isNotEmpty) {
                setState(() => currentStep = "Verifying Email...");
                final result = await _verifyOtp(email, otp);

                if (result != null) {
                  final jwtToken = result['token']!;
                  final userId = result['userId']!;
                  setState(() => currentStep = "Completing Registration...");
                  await _completeRegistration(jwtToken,userId);

                  setState(() => currentStep = "Registration Successful!");
                  await Future.delayed(const Duration(seconds: 1));
                  Navigator.of(context).pop();
                } else {
                  setState(() => isLoading = false);
                }
              } else {
                setState(() => isLoading = false);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Please enter OTP')),
                );
              }
            }

            return AlertDialog(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              title: Row(
                children: [
                  Icon(Icons.verified_user, color: Colors.red[900]),
                  const SizedBox(width: 10),
                  Text('Verify Email', style: GoogleFonts.karla()),
                ],
              ),
              content: isLoading
                  ? Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  CircularProgressIndicator(color: Colors.red[900]),
                  const SizedBox(height: 16),
                  Text(currentStep, style: GoogleFonts.karla(fontWeight: FontWeight.w600)),
                ],
              )
                  : Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text('An OTP has been sent to $email'),
                  const SizedBox(height: 20),
                  TextField(
                    controller: otpController,
                    keyboardType: TextInputType.number,
                    decoration: InputDecoration(
                      labelText: 'Enter OTP',
                      border: const OutlineInputBorder(),
                      prefixIcon: const Icon(Icons.lock_clock_outlined),
                    ),
                  ),
                ],
              ),
              actions: isLoading
                  ? []
                  : [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(),
                  child: const Text('Cancel'),
                ),
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red[900],
                  ),
                  onPressed: startVerificationFlow,
                  child: const Text('Verify', style: TextStyle(color: Colors.white)),
                ),
              ],
            );
          },
        );
      },
    );
  }



  Future<void> _completeRegistration(String jwtToken,String userId) async {
    final locationService = LocationService();
    final location = await locationService.getCurrentLocation();

    final uri = Uri.parse('https://javapaas-196791-0.cloudclusters.net/users/register');

    final Map<String, dynamic> body = {
      "userName": userNameController.text,
      "password": passwordController.text,
      "name": fullNameController.text,
      "place": placeController.text,
      "DOB": _formatDateForApi(dobController.text),
      "email": emailController.text.trim(),
      "city": cityController.text,
      "weight": int.tryParse(weightController.text) ?? 0,
      "phoneNumber": phoneController.text,
      "bloodGroup": selectedBloodGroup ?? "",
      "latitude": location["latitude"],
      "longitude": location["longitude"],
      "pushToken": jwtToken,
    };

    try {
      final response = await http.post(
        uri,
        headers: {
          "Content-Type": "application/json",
          "Authorization": "Bearer $jwtToken",
        },
        body: json.encode(body),
      );

      print('Registration Response Status: ${response.statusCode}');
      print('Response Body: ${response.body}');

      if (response.statusCode == 200) {

        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('Token', jwtToken);
        await prefs.setString('userId', userId);

        showAwesomeSnackBar(context, 'Success', 'Registration successful!', ContentType.success);

        Navigator.pushReplacement(
          context,
          CupertinoPageRoute(builder: (_) => Homescreen()),
        );
      } else {

        showAwesomeSnackBar(context, 'Error', 'Registration failed. Please try again.', ContentType.failure);

      }
    } catch (e) {
      print('Exception during registration: $e');

      showAwesomeSnackBar(context, 'Error', 'Error occurred. Please try again later.', ContentType.failure);

    }
  }

  String _formatDateForApi(String date) {
    try {
      final parts = date.split('/');
      if (parts.length == 3) {
        final day = parts[0].padLeft(2, '0');
        final month = parts[1].padLeft(2, '0');
        final year = parts[2];
        return "$year-$month-$day";
      }
    } catch (_) {}
    return "";
  }



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              const Icon(Icons.bloodtype_rounded, size: 80, color: Colors.red),
              const SizedBox(height: 12),
              Text(
                "Create an Account",
                style: GoogleFonts.karla(
                  fontSize: 28,
                  fontWeight: FontWeight.w800,
                  color: Colors.black,
                ),
              ),
              const SizedBox(height: 32),

              // Personal Info
              _buildSectionTitle("Personal Info"),
              _buildCardField("Full Name", fullNameController, icon: Icons.person),
              _buildCardField("Full Name", userNameController, icon: Icons.person_2_outlined),
              _buildCardField("Place", placeController, icon: Icons.location_on),
              _buildCardField("City", cityController, icon: Icons.location_searching),
              _buildDateField(context),

              // Contact Info
              _buildSectionTitle("Contact Details"),
              _buildEmailField(),
              _buildCardField("Phone Number", phoneController, icon: Icons.phone),

              // Security
              _buildSectionTitle("Security"),
              _buildCardField("Password", passwordController, icon: Icons.lock, obscure: true),

              // Health
              _buildSectionTitle("Health Details"),
              _buildCardField("Weight (kg)", weightController, icon: Icons.monitor_weight),
              _buildCardField("Height (cm)", heightController, icon: Icons.height),
              _buildBloodGroupDropdown(),

              const SizedBox(height: 28),

              // Register Button
              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 0),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10),
                    ),
                    elevation: 0,
                    backgroundColor: Colors.transparent,
                    shadowColor: Colors.transparent,
                  ),
                  onPressed: () {
                    final email = emailController.text.trim();
                    if (email.isEmpty) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Please enter your email before registering')),
                      );
                      return;
                    }
                    if (!isEmailVerified) {
                      // Generate OTP and show dialog
                      _generateOtp(email);
                    } else {
                      // If email already verified, proceed to login screen (or next step)
                      Navigator.push(context, CupertinoPageRoute(builder: (context) => Loginscreen()));
                    }
                  },
                  child: Ink(
                    decoration: BoxDecoration(
                      gradient: const LinearGradient(
                        colors: [Color(0xFFE53935), Color(0xFFD32F2F)],
                        begin: Alignment.topLeft,
                        end: Alignment.bottomRight,
                      ),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Center(
                      child: Text(
                        'Register',
                        style: GoogleFonts.karla(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                      ),
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 20),

              // Login Prompt
              TextButton(
                onPressed: () {
                  Navigator.push(context, CupertinoPageRoute(builder: (context)=>Loginscreen()));
                },
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      "Already have an account?",
                      style: GoogleFonts.karla(
                        color: Colors.grey[800],
                        fontWeight: FontWeight.w600,
                        fontSize: 14,
                      ),
                    ),
                    const SizedBox(width: 8),
                    Text(
                      "Login",
                      style: GoogleFonts.karla(
                        color: Colors.red[900],
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12, top: 24),
      child: Align(
        alignment: Alignment.centerLeft,
        child: Text(
          title,
          style: GoogleFonts.karla(
            fontSize: 16,
            fontWeight: FontWeight.w700,
            color: Colors.grey[800],
          ),
        ),
      ),
    );
  }

  Widget _buildCardField(String hint, TextEditingController controller,
      {bool obscure = false, IconData? icon}) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 14),
      child: Card(
        color: Colors.white,
        elevation: 3,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: TextField(

            controller: controller,
            obscureText: obscure,
            style: const TextStyle(fontSize: 16),
            decoration: InputDecoration(
              hintText: hint,
              border: InputBorder.none,
              icon: icon != null ? Icon(icon, color: Colors.red[900]) : null,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildEmailField() {
    return Padding(
      padding: const EdgeInsets.only(bottom: 14),
      child: Card(
        color: Colors.white,
        elevation: 3,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: TextField(
            controller: emailController,
            keyboardType: TextInputType.emailAddress,
            style: const TextStyle(fontSize: 16),
            decoration: InputDecoration(
              hintText: "Email",
              border: InputBorder.none,
              icon: Icon(Icons.email, color: Colors.red[900]),
              suffixIcon: isEmailVerified
                  ? Icon(Icons.verified, color: Colors.green)
                  : null,
            ),
            onSubmitted: (value) {
              if (value.isNotEmpty && !isEmailVerified) {
                _generateOtp(value);
              }
            },
            onEditingComplete: () {
              if (emailController.text.isNotEmpty && !isEmailVerified) {
                _generateOtp(emailController.text);
              }
            },
          ),
        ),
      ),
    );
  }

  Widget _buildDateField(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 14),
      child: Card(
        elevation: 3,
        color: Colors.white,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: TextField(
            controller: dobController,
            readOnly: true,
            onTap: () => _selectDate(context),
            style: const TextStyle(fontSize: 16),
            decoration: InputDecoration(
              hintText: "Date of Birth",
              icon: Icon(Icons.calendar_month, color: Colors.red[900]),
              border: InputBorder.none,
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildBloodGroupDropdown() {
    return Padding(
      padding: const EdgeInsets.only(bottom: 10),
      child: Card(
        color: Colors.white,
        elevation: 3,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Theme(
            data: Theme.of(context).copyWith(
              canvasColor: Colors.white, // Background of the dropdown list
            ),
            child: DropdownButtonFormField<String>(
              decoration: InputDecoration(
                icon: Icon(Icons.bloodtype, color: Colors.red[900]),
                border: InputBorder.none,
              ),
              dropdownColor: Colors.white,
              value: selectedBloodGroup,
              hint: const Text("Select Blood Group"),
              style: GoogleFonts.karla(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: Colors.red[900],
              ),
              iconEnabledColor: Colors.red[900],
              items: bloodGroups
                  .map((bg) => DropdownMenuItem(
                value: bg,
                child: Text(
                  bg,
                  style: GoogleFonts.karla(
                    color: Colors.red[900],
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ))
                  .toList(),
              onChanged: (value) {
                setState(() {
                  selectedBloodGroup = value;
                });
              },
            ),
          ),
        ),
      ),
    );
  }

}
