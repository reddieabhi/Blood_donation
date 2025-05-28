import 'dart:convert';
import 'package:findmyblood/onBoarding/forgotPasswordScreen.dart';
import 'package:http/http.dart' as http;
import 'package:awesome_snackbar_content/awesome_snackbar_content.dart';
import 'package:findmyblood/onBoarding/registrationScreen.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../Components/myTextField.dart';
import '../Components/snackBar.dart';
import '../HomeScreen/homeScreen.dart';

class Loginscreen extends StatefulWidget {
  const Loginscreen({super.key});

  @override
  State<Loginscreen> createState() => _LoginscreenState();
}

class _LoginscreenState extends State<Loginscreen> {
  final emailController = TextEditingController();
  final passwordController = TextEditingController();

  Future<void> _handleLogin() async {
    final email = emailController.text.trim();
    final password = passwordController.text.trim();

    print("[LOGIN] Attempting login with email: $email");

    if (email.isEmpty || password.isEmpty) {
      print("[LOGIN] Missing email or password");
      showAwesomeSnackBar(context, "Missing Fields", "Please enter email and password", ContentType.warning);
      return;
    }

    final uri = Uri.parse('https://javapaas-196791-0.cloudclusters.net/user/login');
    final body = {"email": email, "password": password};

    try {
      print("[LOGIN] Sending POST to $uri with body: $body");

      final response = await http.post(
        uri,
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(body),
      );

      print("[LOGIN] Response Status: ${response.statusCode}");
      print("[LOGIN] Response Body: ${response.body}");

      if (response.statusCode == 200) {
        final responseData = json.decode(response.body);

        final jwtToken = responseData['jwtToken'];
        final userId = responseData['uuid'];
        print("[LOGIN] Extracted jwtToken: $jwtToken");

        if (jwtToken != null && jwtToken.isNotEmpty && userId != null && userId.isNotEmpty) {
          final prefs = await SharedPreferences.getInstance();
          await prefs.setString('Token', jwtToken);
          await prefs.setString('userId', userId);

          print("Token and userId saved successfully");

          showAwesomeSnackBar(context, "Login Successful", "Welcome back!", ContentType.success);

          await Future.delayed(const Duration(seconds: 2));
          Navigator.pushReplacement(
            context,
            CupertinoPageRoute(builder: (_) => Homescreen()),
          );
        } else {
          print("[LOGIN] jwtToken is null or empty");
          showAwesomeSnackBar(context, "Login Failed", "Invalid credentials or missing token", ContentType.failure);
        }
      } else if (response.statusCode == 401) {
        print("[LOGIN] Unauthorized (401) - Invalid credentials");
        showAwesomeSnackBar(context, "Login Failed", "Invalid email or password", ContentType.failure);
      } else {
        print("[LOGIN] Unexpected status code: ${response.statusCode}");
        showAwesomeSnackBar(context, "Error", "Login failed. Please try again.", ContentType.failure);
      }
    } catch (e) {
      print("[LOGIN] Exception: $e");
      showAwesomeSnackBar(context, "Network Error", "Could not connect to the server. Check your internet connection.", ContentType.failure);
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 40),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Logo icon
              Icon(
                Icons.bloodtype_rounded,
                size: 90,
                color: Colors.red[900],
              ),
              const SizedBox(height: 10),
              // App name
              Text(
                "Blood Finders",
                style: GoogleFonts.karla(
                  fontSize: 36,
                  fontWeight: FontWeight.bold,
                  color: Colors.black,
                ),
              ),
              const SizedBox(height: 40),

              // Email TextField
              Card(
                color: Colors.white,
                elevation: 4,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10),
                ),
                child: MyTextField(
                  hintText: "Email ID",
                  obscureText: false,
                  controller: emailController,
                ),
              ),
              const SizedBox(height: 20),

              // Password TextField
              Card(
                color: Colors.white,
                elevation: 4,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10),
                ),
                child: MyTextField(
                  hintText: "Password",
                  obscureText: true,
                  controller: passwordController,
                ),
              ),
              const SizedBox(height: 30),

              // Gradient Login Button
              SizedBox(
                width: double.infinity,
                height: 50,
                child: InkWell(
                  onTap: _handleLogin,
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
                        'Login',
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

              const SizedBox(height: 20),

              // Register button
              TextButton(
                onPressed: () {
                  Navigator.push(context, CupertinoPageRoute(builder: (context)=>Registrationscreen()));
                },
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      "Don't have an account?",
                      style: GoogleFonts.karla(
                        color: Colors.grey[800],
                        fontWeight: FontWeight.w700,
                        fontSize: 14,
                      ),
                    ),
                    SizedBox(width: 10,),
                    Text(
                      "Register here",
                      style: GoogleFonts.karla(
                        color: Colors.red[900],
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
              SizedBox(height: 10,),
              TextButton(
                onPressed: () {
                 Navigator.push(context, CupertinoPageRoute(builder: (context)=>PasswordReset()));
                },
                child:Text(
                  "Forgot Password?",
                  style: GoogleFonts.karla(
                    color: Colors.blue[800],
                    fontWeight: FontWeight.w700,
                    fontSize: 14,
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
