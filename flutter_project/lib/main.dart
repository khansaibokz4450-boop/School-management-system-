import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'providers/school_provider.dart';
import 'screens/dashboard_screen.dart';
import 'screens/attendance_screen.dart';
import 'screens/fee_screen.dart';
import 'screens/staff_screen.dart';
import 'screens/broadcast_screen.dart';
import 'screens/exam_screen.dart';
import 'screens/homework_screen.dart';
import 'screens/id_card_screen.dart';
import 'screens/timetable_screen.dart';
import 'screens/transport_screen.dart';
import 'screens/export_screen.dart';
import 'screens/developer_license_screen.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => SchoolProvider()),
      ],
      child: const PakSchoolApp(),
    ),
  );
}

class PakSchoolApp extends StatelessWidget {
  const PakSchoolApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'PakSchool SMS',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        brightness: Brightness.light,
        scaffoldBackgroundColor: const Color(0xFFF8FAFC),
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF1E40AF),
          brightness: Brightness.light,
          primary: const Color(0xFF1E40AF),
          secondary: const Color(0xFF16A34A),
          surface: Colors.white,
          onSurface: const Color(0xFF0F172A),
        ),
        textTheme: const TextTheme(
          bodyLarge: TextStyle(color: Color(0xFF0F172A)),
          bodyMedium: TextStyle(color: Color(0xFF1E293B)),
          titleMedium: TextStyle(color: Color(0xFF0F172A), fontWeight: FontWeight.bold),
        ),
      ),
      home: const MainNavigationScreen(),
    );
  }
}

class MainNavigationScreen extends StatefulWidget {
  const MainNavigationScreen({Key? key}) : super(key: key);

  @override
  State<MainNavigationScreen> createState() => _MainNavigationScreenState();
}

class _MainNavigationScreenState extends State<MainNavigationScreen> {
  int _currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    final screens = [
      DashboardScreen(onNavigate: (index) => setState(() => _currentIndex = index)),
      AttendanceScreen(onBack: () => setState(() => _currentIndex = 0)),
      FeeScreen(onBack: () => setState(() => _currentIndex = 0)),
      StaffScreen(onBack: () => setState(() => _currentIndex = 0)),
      BroadcastScreen(onBack: () => setState(() => _currentIndex = 0)),
      ExamScreen(onBack: () => setState(() => _currentIndex = 0)),
      HomeworkScreen(onBack: () => setState(() => _currentIndex = 0)),
      IdCardScreen(onBack: () => setState(() => _currentIndex = 0)),
      TimetableScreen(onBack: () => setState(() => _currentIndex = 0)),
      TransportScreen(onBack: () => setState(() => _currentIndex = 0)),
      ExportScreen(onBack: () => setState(() => _currentIndex = 0)),
      DeveloperLicenseScreen(onBack: () => setState(() => _currentIndex = 0)),
    ];

    return Scaffold(
      body: screens[_currentIndex],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex.clamp(0, 4),
        onDestinationSelected: (idx) => setState(() => _currentIndex = idx),
        destinations: const [
          NavigationDestination(icon: Icon(Icons.dashboard_outlined), selectedIcon: Icon(Icons.dashboard), label: 'Dashboard'),
          NavigationDestination(icon: Icon(Icons.fingerprint_outlined), selectedIcon: Icon(Icons.fingerprint), label: 'Attendance'),
          NavigationDestination(icon: Icon(Icons.payments_outlined), selectedIcon: Icon(Icons.payments), label: 'Fees'),
          NavigationDestination(icon: Icon(Icons.groups_outlined), selectedIcon: Icon(Icons.groups), label: 'Faculty'),
          NavigationDestination(icon: Icon(Icons.campaign_outlined), selectedIcon: Icon(Icons.campaign), label: 'Broadcast'),
        ],
      ),
    );
  }
}
