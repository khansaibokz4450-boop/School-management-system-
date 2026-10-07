import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';
import '../models/student.dart';

class IdCardScreen extends StatefulWidget {
  final VoidCallback onBack;

  const IdCardScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<IdCardScreen> createState() => _IdCardScreenState();
}

class _IdCardScreenState extends State<IdCardScreen> {
  int _selectedStudentIndex = 0;

  void _sendWhatsAppCard(Student student) async {
    final msg = '''
🪪 *پاک اسکول اینڈ کالج سسٹم*
───────────────────
🎓 *طالب علم اسکول شناختی کارڈ (Digital ID Card)*

محترم والدین!
طالب علم *${student.name}* کا اسکول کارڈ تفصیلات درج ذیل ہیں:

👤 *نام:* ${student.name}
🔢 *رول نمبر:* ${student.rollNo}
🏫 *کلاس:* ${student.className}
👨 *والد کا نام:* ${student.fatherName}
📞 *ایمرجنسی رابطہ:* ${student.parentPhone}
🩸 *بلڈ گروپ:* B+
🗓️ *سیشن:* 2026 - 2027

برائے مہربانی اسکول آتے وقت بچے کو کارڈ ہمراہ رکھنے کی تاکید فرمائیں۔
───────────────────
دفتر انتظامیہ | پاک اسکول
''';

    var clean = student.parentPhone.replaceAll(RegExp(r'[^0-9+]'), '');
    if (clean.startsWith('+')) clean = clean.substring(1);
    if (clean.startsWith('03')) clean = '92${clean.substring(1)}';
    if (!clean.startsWith('92') && clean.length == 10) clean = '92$clean';

    final uri = Uri.parse('https://api.whatsapp.com/send?phone=$clean&text=${Uri.encodeComponent(msg)}');
    try {
      if (await canLaunchUrl(uri)) {
        await launchUrl(uri, mode: LaunchMode.externalApplication);
      }
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<SchoolProvider>();
    final students = provider.students;
    final currentStudent = students.isNotEmpty ? students[_selectedStudentIndex.clamp(0, students.length - 1)] : null;

    return Scaffold(
      backgroundColor: const Color(0xFFF8FAFC),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 1,
        leading: TextButton.icon(
          onPressed: widget.onBack,
          icon: const Icon(Icons.arrow_back, size: 16, color: Color(0xFF1E40AF)),
          label: const Text('Menu', style: TextStyle(color: Color(0xFF1E40AF), fontWeight: FontWeight.bold)),
        ),
        leadingWidth: 90,
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Student ID Card Maker', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 18, fontWeight: FontWeight.bold)),
            Text('اسکول شناختی کارڈ • Class: ${provider.selectedClass}', style: const TextStyle(color: Color(0xFF64748B), fontSize: 11)),
          ],
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (currentStudent != null) ...[
              // The Printable ID Card Widget
              Card(
                elevation: 4,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                color: Colors.white,
                child: Column(
                  children: [
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      decoration: const BoxDecoration(
                        color: Color(0xFF1E3A8A),
                        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
                      ),
                      child: Column(
                        children: const [
                          Text('PAK SCHOOL & COLLEGE SYSTEM', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 13)),
                          Text('STUDENT IDENTITY CARD • SESSION 2026-27', style: TextStyle(color: Color(0xFFFDE047), fontSize: 9, fontWeight: FontWeight.bold)),
                        ],
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          Column(
                            children: [
                              Container(
                                width: 68,
                                height: 68,
                                decoration: BoxDecoration(
                                  color: const Color(0xFFEFF6FF),
                                  shape: BoxShape.circle,
                                  border: Border.all(color: const Color(0xFF16A34A), width: 2),
                                ),
                                child: const Center(child: Text('🎓', style: TextStyle(fontSize: 32))),
                              ),
                              const SizedBox(height: 4),
                              const Text('Blood: B+', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Colors.red)),
                            ],
                          ),
                          const SizedBox(width: 14),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(currentStudent.name, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                                Text('S/O: ${currentStudent.fatherName}', style: const TextStyle(fontSize: 12, color: Color(0xFF475569))),
                                Text('Roll: ${currentStudent.rollNo} • Class: ${currentStudent.className}', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF1E40AF))),
                                Text('Contact: ${currentStudent.parentPhone}', style: const TextStyle(fontSize: 11, color: Color(0xFF64748B))),
                              ],
                            ),
                          ),
                          const Icon(Icons.qr_code_2, size: 52, color: Color(0xFF1E293B)),
                        ],
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      decoration: const BoxDecoration(
                        color: Color(0xFFF8FAFC),
                        borderRadius: BorderRadius.vertical(bottom: Radius.circular(20)),
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: const [
                          Text('Main Campus, Pakistan', style: TextStyle(fontSize: 10, color: Color(0xFF64748B))),
                          Text('Principal Signature ✍️', style: TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Color(0xFF1E3A8A))),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 12),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                  onPressed: () => _sendWhatsAppCard(currentStudent),
                  icon: const Icon(Icons.send, color: Colors.white, size: 16),
                  label: const Text('📲 Send Card via WhatsApp (کارڈ واٹس ایپ کریں)', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                ),
              ),
            ],

            const SizedBox(height: 18),
            const Text('SELECT STUDENT:', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Color(0xFF64748B))),
            const SizedBox(height: 8),

            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: students.length,
              itemBuilder: (context, index) {
                final s = students[index];
                final isSelected = index == _selectedStudentIndex;

                return Card(
                  color: isSelected ? const Color(0xFFEFF6FF) : Colors.white,
                  child: ListTile(
                    leading: const Icon(Icons.badge, color: Color(0xFF1E40AF)),
                    title: Text(s.name, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text('${s.rollNo} • S/O: ${s.fatherName}'),
                    trailing: Text(isSelected ? '● ACTIVE' : 'View', style: TextStyle(color: isSelected ? const Color(0xFF1E40AF) : Colors.grey, fontWeight: FontWeight.bold)),
                    onTap: () => setState(() => _selectedStudentIndex = index),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
