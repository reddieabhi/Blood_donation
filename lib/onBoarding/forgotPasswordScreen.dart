import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:http/http.dart' as http;

class PasswordReset extends StatefulWidget {
  const PasswordReset({super.key});

  @override
  State<PasswordReset> createState() => _PasswordResetState();
}

class _PasswordResetState extends State<PasswordReset> {
  final otpController = TextEditingController();
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  bool isEmailVerified = false;
  String? jwtToken;


  Future<void> _generateOtp(String email) async {
    try {
      final url = Uri.parse('https://javapaas-196791-0.cloudclusters.net/otp/generate?email=$email&find=true');
      final response = await http.post(url);

      if (response.statusCode == 200) {
        print(response.body);
        await _showOtpDialog(email);
      } else {
        print(response.statusCode);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to generate OTP. Please try again later.')),
        );
      }
    } catch (_) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('An error occurred while generating OTP. Please try again.')),
      );
    }
  }

  Future<bool> _verifyOtp(String email, String otp) async {
    try {
      final url = Uri.parse('https://javapaas-196791-0.cloudclusters.net/otp/verify?email=$email&otp=$otp');
      final response = await http.post(url);

      if (response.statusCode == 200) {
        final responseData = json.decode(response.body);
        print(responseData);
        if (responseData['statusCode'] == 200 && responseData['payload'] == 'Success') {
          setState(() {
            isEmailVerified = true;
            jwtToken = responseData['jwtToken'];
          });
          return true;
        }
      }
    } catch (_) {}
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Invalid OTP or error occurred.')),
    );
    return false;
  }

  Future<void> _resetPassword(String email, String password) async {
    print("$email and $password");
    try {
      final url = Uri.parse('https://javapaas-196791-0.cloudclusters.net/user/reset-password');
      final response = await http.post(
        url,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $jwtToken',
        },
        body: jsonEncode({'email': email, 'password': password}),
      );

      print(response);

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Password changed successfully!')),
        );
        Navigator.of(context).pop();
        Navigator.of(context).pop();
      } else {
        print(response.statusCode);
        print(response.body);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to reset password.')),
        );
      }
    } catch (e) {
      print(e);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('An error occurred while resetting password.')),
      );
    }
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
                final verified = await _verifyOtp(email, otp);
                if (verified) {
                  Navigator.of(context).pop(); // Close OTP dialog
                  await _showPasswordResetDialog(email);
                } else {
                  setState(() => isLoading = false);
                }
              } else {
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
                  SizedBox(width: 10),
                  Text('Verify Email', style: GoogleFonts.karla()),
                ],
              ),
              content: isLoading
                  ? Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  CircularProgressIndicator(color: Colors.red[900]),
                  SizedBox(height: 16),
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
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.lock_clock_outlined),
                    ),
                  ),
                ],
              ),
              actions: isLoading
                  ? []
                  : [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(),
                  child: Text('Cancel'),
                ),
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red[900],
                  ),
                  onPressed: startVerificationFlow,
                  child: Text('Verify', style: TextStyle(color: Colors.white)),
                ),
              ],
            );
          },
        );
      },
    );
  }

  Future<void> _showPasswordResetDialog(String email) async {
    final newPassController = TextEditingController();

    return showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text('Reset Password', style: GoogleFonts.karla(fontWeight: FontWeight.bold)),
          content: TextField(
            controller: newPassController,
            obscureText: true,
            decoration: const InputDecoration(
              labelText: 'New Password',
              border: OutlineInputBorder(),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () {
                if (newPassController.text.isNotEmpty) {
                  _resetPassword(email, newPassController.text);
                } else {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Please enter a new password.')),
                  );
                }
              },
              style: ElevatedButton.styleFrom(backgroundColor: Colors.red[900]),
              child: const Text('Change Password', style: TextStyle(color: Colors.white)),
            )
          ],
        );
      },
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
              suffixIcon: isEmailVerified ? Icon(Icons.verified, color: Colors.green) : null,
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: SafeArea(
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ShaderMask(
                  shaderCallback: (Rect bounds) {
                    return const LinearGradient(
                      colors: [Color(0xFFE53935), Color(0xFFD32F2F)],
                    ).createShader(bounds);
                  },
                  child: Text(
                    'Reset your password',
                    style: GoogleFonts.karla(
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                ),
                SizedBox(height: 40,),
                _buildEmailField(),
                SizedBox(height: 25,),
                SizedBox(
                  width: double.infinity,
                  height: 50,
                  child: InkWell(
                    onTap: (){
                      final email = emailController.text;
                      if (email.isNotEmpty && !isEmailVerified) {
                        _generateOtp(email);
                      }
                    },
                    borderRadius: BorderRadius.circular(10),
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
                          'Verify Email',
                          style: GoogleFonts.karla(
                            fontSize: 22,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,

                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
