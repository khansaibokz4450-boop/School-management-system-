import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/school_provider.dart';
import '../models/student.dart';
import '../models/staff.dart';

class DashboardScreen extends StatelessWidget {
  final Function(int) onNavigate;

  const DashboardScreen({Key? key, required this.onNavigate}) : super(key: key);

  void _showAddStudentDialog(BuildContext context, SchoolProvider provider) {
    final nameController = TextEditingController();
    int age = 6;
    String selectedClass = provider.selectedClass;

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) {
          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
            title: Row(
              children: const [
                Text('🎒 ', style: TextStyle(fontSize: 22)),
                Text('New Student Admission', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ],
            ),
            content: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Student Name (طالب علم کا نام):', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  TextField(
                    controller: nameController,
                    decoration: const InputDecoration(
                      hintText: 'e.g. Ayaan Khan / Zoya Fatima',
                      border: OutlineInputBorder(),
                    ),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text('Age (عمر):', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                      Chip(label: Text('⭐ $age Years Old', style: const TextStyle(fontSize: 11, color: Color(0xFF1E40AF)))),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      IconButton(
                        icon: const Icon(Icons.remove_circle, color: Color(0xFF1E40AF)),
                        onPressed: () => setDialogState(() { if (age > 3) age--; }),
                      ),
                      Text('$age', style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                      IconButton(
                        icon: const Icon(Icons.add_circle, color: Color(0xFF1E40AF)),
                        onPressed: () => setDialogState(() { if (age < 20) age++; }),
                      ),
                    ],
                  ),
                  const SizedBox(height: 10),
                  const Text('Select Class (کلاس):', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  DropdownButtonFormField<String>(
                    value: selectedClass,
                    items: pakClasses.map((c) => DropdownMenuItem(value: c, child: Text(c))).toList(),
                    onChanged: (val) => setDialogState(() => selectedClass = val ?? 'KG'),
                    decoration: const InputDecoration(border: OutlineInputBorder()),
                  ),
                ],
              ),
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
              ElevatedButton(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                onPressed: () async {
                  if (nameController.text.trim().isNotEmpty) {
                    final count = (provider.students.length + 1).toString().padLeft(2, '0');
                    final newStudent = Student(
                      rollNo: '${selectedClass.substring(0, 2).toUpperCase()}-$count',
                      name: nameController.text.trim(),
                      fatherName: 'Parent of ${nameController.text.trim()}',
                      className: selectedClass,
                      section: 'A',
                      parentPhone: '+923001234567',
                      monthlyFee: 3500,
                      feeStatus: 'PAID',
                      gender: 'Male',
                    );
                    await provider.addStudent(newStudent);
                    Navigator.pop(ctx);
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('🎉 Student Registered Successfully!')),
                    );
                  }
                },
                child: const Text('Register Student ✨', style: TextStyle(color: Colors.white)),
              ),
            ],
          );
        },
      ),
    );
  }

  void _showAddTeacherDialog(BuildContext context, SchoolProvider provider) {
    final nameController = TextEditingController();
    final desigController = TextEditingController(text: 'Senior Teacher');
    final qualController = TextEditingController(text: 'M.Sc / B.Ed');
    final subjController = TextEditingController(text: 'Mathematics');
    final phoneController = TextEditingController(text: '+923001234567');
    final salController = TextEditingController(text: '45000');

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: Row(
          children: const [
            Text('👨‍🏫 ', style: TextStyle(fontSize: 22)),
            Text('Register Faculty Member', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          ],
        ),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration: const InputDecoration(labelText: 'Teacher Name (استاد کا نام)', border: OutlineInputBorder()),
              ),
              const SizedBox(height: 10),
              TextField(
                controller: desigController,
                decoration: const InputDecoration(labelText: 'Designation (عہدہ)', border: OutlineInputBorder()),
              ),
              const SizedBox(height: 10),
              TextField(
                controller: subjController,
                decoration: const InputDecoration(labelText: 'Subject (مضمون)', border: OutlineInputBorder()),
              ),
              const SizedBox(height: 10),
              TextField(
                controller: qualController,
                decoration: const InputDecoration(labelText: 'Qualification (تعلیم)', border: OutlineInputBorder()),
              ),
              const SizedBox(height: 10),
              TextField(
                controller: phoneController,
                decoration: const InputDecoration(labelText: 'Phone Number (فون نمبر)', border: OutlineInputBorder()),
              ),
              const SizedBox(height: 10),
              TextField(
                controller: salController,
                decoration: const InputDecoration(labelText: 'Salary PKR (تنخواہ)', border: OutlineInputBorder()),
                keyboardType: TextInputType.number,
              ),
            ],
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFFD97706)),
            onPressed: () async {
              if (nameController.text.trim().isNotEmpty) {
                final teacher = StaffMember(
                  name: nameController.text.trim(),
                  designation: desigController.text.trim(),
                  qualification: qualController.text.trim(),
                  phone: phoneController.text.trim(),
                  subject: subjController.text.trim(),
                  monthlySalary: int.tryParse(salController.text) ?? 45000,
                  salaryStatus: 'PENDING',
                  attendanceToday: 'PRESENT',
                );
                await provider.addStaff(teacher);
                Navigator.pop(ctx);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('👨‍🏫 Teacher Registered Successfully!')),
                );
              }
            },
            child: const Text('Register Teacher', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<SchoolProvider>();

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // 1. TOP ROYAL BLUE HEADER BANNER (From video)
            Container(
              width: double.infinity,
              color: const Color(0xFF1E40AF),
              padding: const EdgeInsets.symmetric(vertical: 28, horizontal: 20),
              child: Column(
                children: const [
                  Text(
                    'School Management System',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  SizedBox(height: 4),
                  Text(
                    'Cross-Platform Control Panel & Management Portal',
                    style: TextStyle(
                      color: Color(0xFFDBEAFE),
                      fontSize: 12,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ],
              ),
            ),

            const SizedBox(height: 16),

            // 2. "Select Target Class:" PANEL with + Student & + Teacher buttons
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Card(
                elevation: 2,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                color: Colors.white,
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Text(
                            'Select Target Class:',
                            style: TextStyle(
                              fontSize: 15,
                              fontWeight: FontWeight.bold,
                              color: Color(0xFF1E293B),
                            ),
                          ),
                          Row(
                            children: [
                              InkWell(
                                onTap: () => _showAddStudentDialog(context, provider),
                                borderRadius: BorderRadius.circular(8),
                                child: Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                  decoration: BoxDecoration(
                                    color: const Color(0xFFDCFCE7),
                                    borderRadius: BorderRadius.circular(8),
                                  ),
                                  child: const Text('🎒 + Student', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF16A34A))),
                                ),
                              ),
                              const SizedBox(width: 6),
                              InkWell(
                                onTap: () => _showAddTeacherDialog(context, provider),
                                borderRadius: BorderRadius.circular(8),
                                child: Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                  decoration: BoxDecoration(
                                    color: const Color(0xFFFEF3C7),
                                    borderRadius: BorderRadius.circular(8),
                                  ),
                                  child: const Text('👨‍🏫 + Teacher', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFFD97706))),
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      Wrap(
                        spacing: 8,
                        runSpacing: 8,
                        children: pakClasses.map((cls) {
                          final isSelected = cls.toLowerCase() == provider.selectedClass.toLowerCase();
                          return InkWell(
                            onTap: () => provider.setSelectedClass(cls),
                            borderRadius: BorderRadius.circular(8),
                            child: AnimatedContainer(
                              duration: const Duration(milliseconds: 200),
                              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                              decoration: BoxDecoration(
                                color: isSelected ? const Color(0xFF1E40AF) : const Color(0xFFF1F5F9),
                                borderRadius: BorderRadius.circular(8),
                                border: Border.all(
                                  color: isSelected ? const Color(0xFF1E40AF) : const Color(0xFFCBD5E1),
                                ),
                              ),
                              child: Text(
                                cls,
                                style: TextStyle(
                                  color: isSelected ? Colors.white : const Color(0xFF334155),
                                  fontSize: 13,
                                  fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
                                ),
                              ),
                            ),
                          );
                        }).toList(),
                      ),
                    ],
                  ),
                ),
              ),
            ),

            const SizedBox(height: 12),

            // 3. "Currently Viewing Database For: [Class]" BANNER (From video)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                decoration: BoxDecoration(
                  color: const Color(0xFFEFF6FF),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: const Color(0xFFBFDBFE)),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Container(
                          width: 10,
                          height: 10,
                          decoration: const BoxDecoration(
                            color: Color(0xFF22C55E),
                            shape: BoxShape.circle,
                          ),
                        ),
                        const SizedBox(width: 8),
                        const Text(
                          'Currently Viewing Database For: ',
                          style: TextStyle(
                            color: Color(0xFF1E40AF),
                            fontSize: 13,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        Text(
                          provider.selectedClass,
                          style: const TextStyle(
                            color: Color(0xFF1E3A8A),
                            fontSize: 15,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        '${provider.totalStudents} Students',
                        style: const TextStyle(
                          color: Color(0xFF1E40AF),
                          fontSize: 11,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 16),

            // 4. THE 4 MAIN FEATURE CARDS (From video)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Column(
                children: [
                  _FeatureCard(
                    title: 'Attendance',
                    subtitle: 'Live Face Recognition & Attendance Logs',
                    icon: Icons.assignment_turned_in,
                    accentColor: const Color(0xFF2563EB),
                    badgeText: '${provider.presentCount}/${provider.totalStudents} Present',
                    onTap: () => onNavigate(1),
                  ),
                  const SizedBox(height: 12),
                  _FeatureCard(
                    title: 'Fee Management',
                    subtitle: 'Fee Collection, Receipts & Ledger Records',
                    icon: Icons.payments,
                    accentColor: const Color(0xFF16A34A),
                    badgeText: 'Rs. ${(provider.totalCollectedFee / 1000).toStringAsFixed(0)}k Collected',
                    onTap: () => onNavigate(2),
                  ),
                  const SizedBox(height: 12),
                  _FeatureCard(
                    title: 'Teacher Records',
                    subtitle: 'Staff Profiles, Attendance & Salary Logs',
                    icon: Icons.school,
                    accentColor: const Color(0xFFF59E0B),
                    badgeText: 'Faculty Directory',
                    onTap: () => onNavigate(3),
                  ),
                  const SizedBox(height: 12),
                  _FeatureCard(
                    title: 'WhatsApp & SMS',
                    subtitle: 'Send Instant Alerts, Absentee Reports & Notices',
                    icon: Icons.chat_bubble,
                    accentColor: const Color(0xFFEC4899),
                    badgeText: 'Broadcast Portal',
                    onTap: () => onNavigate(4),
                  ),

                  const SizedBox(height: 16),

                  // 6 NEW MODULAR ACADEMIC MODULES
                  const Align(
                    alignment: Alignment.centerLeft,
                    child: Text(
                      'ACADEMIC & SMART MODULES (اختیاری جدید اسکول فیچرز)',
                      style: TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF64748B),
                        letterSpacing: 0.5,
                      ),
                    ),
                  ),

                  const SizedBox(height: 10),

                  // 1. Exams & DMC
                  _FeatureCard(
                    title: 'Exams & DMC Result Cards',
                    subtitle: 'Student Marks, Percentage, Grading & WhatsApp DMC',
                    icon: Icons.assessment,
                    accentColor: const Color(0xFF2563EB),
                    badgeText: 'امتحانات و ڈی ایم سی',
                    onTap: () => onNavigate(5),
                  ),
                  const SizedBox(height: 12),

                  // 2. Daily Homework
                  _FeatureCard(
                    title: 'Daily Homework Diary',
                    subtitle: 'Subject Homework & Direct WhatsApp Broadcast',
                    icon: Icons.menu_book,
                    accentColor: const Color(0xFF0D9488),
                    badgeText: 'روزانہ ڈائری',
                    onTap: () => onNavigate(6),
                  ),
                  const SizedBox(height: 12),

                  // 3. Student ID Cards
                  _FeatureCard(
                    title: 'Student ID Card Maker',
                    subtitle: 'School Badges with QR Code & Principal Stamp',
                    icon: Icons.badge,
                    accentColor: const Color(0xFF7C3AED),
                    badgeText: 'شناختی کارڈ',
                    onTap: () => onNavigate(7),
                  ),
                  const SizedBox(height: 12),

                  // 4. Timetable
                  _FeatureCard(
                    title: 'Class Timetable & Schedule',
                    subtitle: 'Daily 7-Period Bell Timings, Teachers & Subjects',
                    icon: Icons.schedule,
                    accentColor: const Color(0xFFEA580C),
                    badgeText: 'ٹائم ٹیبل',
                    onTap: () => onNavigate(8),
                  ),
                  const SizedBox(height: 12),

                  // 5. Transport / Van
                  _FeatureCard(
                    title: 'Transport & School Van',
                    subtitle: 'Van Routes, Driver Contacts & Pick & Drop Logs',
                    icon: Icons.directions_bus,
                    accentColor: const Color(0xFFD97706),
                    badgeText: 'وین ٹرانسپورٹ',
                    onTap: () => onNavigate(9),
                  ),
                  const SizedBox(height: 12),

                  // 6. Backup & Excel Export
                  _FeatureCard(
                    title: 'Backup & Excel Export',
                    subtitle: 'Export Students, Fees & Attendance to CSV/Excel',
                    icon: Icons.download,
                    accentColor: const Color(0xFF16A34A),
                    badgeText: 'ایکسل ایکسپورٹ',
                    onTap: () => onNavigate(10),
                  ),

                  const SizedBox(height: 16),

                  // Developer Info Card
                  Card(
                    elevation: 2,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                    color: Colors.white,
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: const [
                              Text(
                                'SYSTEM & DEVELOPER INFO',
                                style: TextStyle(
                                  fontSize: 11,
                                  fontWeight: FontWeight.bold,
                                  color: Color(0xFF64748B),
                                  letterSpacing: 0.5,
                                ),
                              ),
                              Text(
                                'Admin',
                                style: TextStyle(
                                  fontSize: 11,
                                  fontWeight: FontWeight.bold,
                                  color: Color(0xFF1E40AF),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          Row(
                            children: [
                              CircleAvatar(
                                radius: 24,
                                backgroundColor: const Color(0xFF1E40AF),
                                child: const Text(
                                  'AO',
                                  style: TextStyle(
                                    color: Colors.white,
                                    fontWeight: FontWeight.bold,
                                    fontSize: 16,
                                  ),
                                ),
                              ),
                              const SizedBox(width: 14),
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: const [
                                  Text(
                                    'Ahmad ullah okz',
                                    style: TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.bold,
                                      color: Color(0xFF1E293B),
                                    ),
                                  ),
                                  SizedBox(height: 2),
                                  Text(
                                    'From Pakistan 🇵🇰',
                                    style: TextStyle(
                                      fontSize: 12,
                                      color: Color(0xFF16A34A),
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                          const Divider(height: 20),
                          Row(
                            children: const [
                              Icon(Icons.email, size: 16, color: Color(0xFF1E40AF)),
                              SizedBox(width: 8),
                              Text(
                                'khansaibokz4450@gmail.com',
                                style: TextStyle(
                                  fontSize: 13,
                                  color: Color(0xFF1E40AF),
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 12),
                          InkWell(
                            onTap: () => onNavigate(11),
                            borderRadius: BorderRadius.circular(10),
                            child: Container(
                              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                              decoration: BoxDecoration(
                                color: const Color(0xFFEFF6FF),
                                borderRadius: BorderRadius.circular(10),
                                border: Border.all(color: const Color(0xFFBFDBFE)),
                              ),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: const [
                                  Text(
                                    '💼 EasyPaisa Royalty & Licensing Portal',
                                    style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF1E40AF)),
                                  ),
                                  Icon(Icons.arrow_forward_ios, size: 12, color: Color(0xFF1E40AF)),
                                ],
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }
}

class _FeatureCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final IconData icon;
  final Color accentColor;
  final String badgeText;
  final VoidCallback onTap;

  const _FeatureCard({
    Key? key,
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.accentColor,
    required this.badgeText,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      color: Colors.white,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                width: 52,
                height: 52,
                decoration: BoxDecoration(
                  color: accentColor.withOpacity(0.12),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: accentColor.withOpacity(0.3)),
                ),
                child: Icon(icon, color: accentColor, size: 28),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: const TextStyle(
                        fontSize: 17,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF1E293B),
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      subtitle,
                      style: const TextStyle(
                        fontSize: 12,
                        color: Color(0xFF64748B),
                      ),
                    ),
                    const SizedBox(height: 6),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: accentColor.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(6),
                      ),
                      child: Text(
                        badgeText,
                        style: TextStyle(
                          color: accentColor,
                          fontSize: 11,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              const Icon(Icons.arrow_forward_ios, size: 16, color: Color(0xFF94A3B8)),
            ],
          ),
        ),
      ),
    );
  }
}
