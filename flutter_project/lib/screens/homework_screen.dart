import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';

class HomeworkScreen extends StatefulWidget {
  final VoidCallback onBack;

  const HomeworkScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<HomeworkScreen> createState() => _HomeworkScreenState();
}

class _HomeworkScreenState extends State<HomeworkScreen> {
  final _engController = TextEditingController(text: 'Unit 3 Reading + Write Questions 1 & 2 on notebook.');
  final _urduController = TextEditingController(text: 'سبق نمبر 4 کے الفاظ معنی یاد کریں اور خوشخطی لکھیں۔');
  final _mathController = TextEditingController(text: 'Exercise 2.4 Questions 1 to 6 practice on rough register.');
  final _sciController = TextEditingController(text: 'Chapter 3: Learn Solar System diagram and key terms.');
  final _islController = TextEditingController(text: 'سورۃ الفلق زبانی ترجمے کے ساتھ یاد کریں۔');

  void _sendWhatsAppDiary(SchoolProvider provider) async {
    final msg = '''
🏫 *پاک اسکول اینڈ کالج سسٹم*
───────────────────
📖 *روزانہ ہوم ورک و اسکول ڈائری (Daily Diary)*

📅 *تاریخ:* ${provider.todayDate}
🏫 *کلاس:* ${provider.selectedClass}

📚 *آج کا ہوم ورک:*
• 🇬🇧 *انگریزی (English):* ${_engController.text}
• 🇵🇰 *اردو (Urdu):* ${_urduController.text}
• 📐 *ریاضی (Mathematics):* ${_mathController.text}
• 🔬 *سائنس (Science):* ${_sciController.text}
• 📖 *اسلامیات / ناظرہ:* ${_islController.text}

⚠️ *محترم والدین!* برائے مہربانی شام کو بچے کا ہوم ورک چیک فرما کر ڈائری پر دستخط کریں۔
───────────────────
کلاس انچارج و پرنسپل | پاک اسکول
''';

    final uri = Uri.parse('https://api.whatsapp.com/send?text=${Uri.encodeComponent(msg)}');
    try {
      if (await canLaunchUrl(uri)) {
        await launchUrl(uri, mode: LaunchMode.externalApplication);
      }
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<SchoolProvider>();

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
            const Text('Daily Homework Diary', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 18, fontWeight: FontWeight.bold)),
            Text('روزانہ ڈائری • Class: ${provider.selectedClass}', style: const TextStyle(color: Color(0xFF64748B), fontSize: 11)),
          ],
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              color: Colors.white,
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Subjects Homework Inputs', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
                    const SizedBox(height: 12),
                    TextField(controller: _engController, decoration: const InputDecoration(labelText: '🇬🇧 English', border: OutlineInputBorder())),
                    const SizedBox(height: 10),
                    TextField(controller: _urduController, decoration: const InputDecoration(labelText: '🇵🇰 Urdu', border: OutlineInputBorder())),
                    const SizedBox(height: 10),
                    TextField(controller: _mathController, decoration: const InputDecoration(labelText: '📐 Mathematics', border: OutlineInputBorder())),
                    const SizedBox(height: 10),
                    TextField(controller: _sciController, decoration: const InputDecoration(labelText: '🔬 General Science', border: OutlineInputBorder())),
                    const SizedBox(height: 10),
                    TextField(controller: _islController, decoration: const InputDecoration(labelText: '📖 Islamiat & Quran', border: OutlineInputBorder())),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              height: 48,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF16A34A),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
                onPressed: () => _sendWhatsAppDiary(provider),
                icon: const Icon(Icons.send, color: Colors.white),
                label: const Text('📲 Send Diary via WhatsApp (ڈائری ارسال کریں)', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
