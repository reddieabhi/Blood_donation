import 'package:geolocator/geolocator.dart';

class LocationService {
  Future<Map<String, double>> getCurrentLocation() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return {"latitude": 17.38405, "longitude": 78.45636};
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return {"latitude": 17.38405, "longitude": 78.45636};
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return {"latitude": 17.38405, "longitude": 78.45636};
    }

    Position position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high);
    return {
      "latitude": position.latitude,
      "longitude": position.longitude,
    };
  }
}