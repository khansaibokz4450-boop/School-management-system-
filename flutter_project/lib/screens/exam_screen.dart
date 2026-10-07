import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';
import '../models/student.dart';

const List<Map<String, String>> comprehensiveFlutterSubjects = [
  {'key': 'bio', 'name': 'Biology', 'urdu': 'بیالوجی'},
  {'key': 'phy', 'name': 'Physics', 'urdu': 'فزکس'},
  {'key': 'chem', 'name': 'Chemistry', 'urdu': 'کیمسٹری'},
  {'key': 'cs', 'name': 'Computer Science', 'urdu': 'کمپیوٹر سائنس'},
  {'key': 'math', 'name': 'Mathematics', 'urdu': 'ریاضی'},
  {'key': 'eng', 'name': 'English', 'urdu': 'انگریزی'},
  {'key': 'urdu', 'name': 'Urdu', 'urdu': 'اردو'},
  {'key': 'pashto', 'name': 'Pashto', 'urdu': 'پشتو'},
  {'key': 'isl', 'name': 'Islamiat', 'urdu': 'اسلامیات'},
  {'key': 'quran', 'name': 'Mutalia Quran', 'urdu': 'مطالعہ قرآن'},
  {'key': 'ps', 'name': 'Pakistan Studies', 'urdu': 'مطالعہ پاکستان'},
  {'key': 'gsci', 'name': 'General Science', 'urdu': 'جنرل سائنس'},
  {'key': 'civics', 'name': 'Civics', 'urdu': 'شہریت'},
  {'key': 'eco', 'name': 'Economics', 'urdu': 'معاشیات'},
];

class ExamScreen extends StatefulWidget {
  final VoidCallback onBack;

  const ExamScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<ExamScreen> createState() => _ExamScreenState();
}

class _ExamScreenState extends State<ExamScreen> {
  String _selectedTerm = 'Midterm Exam 2026';
  final Map<int, Map<String, int>> _studentMarks = {};

  void _sendWhatsAppDmc(Student student, Map<String, int> marks) async {
    final buffer = StringBuffer();
    int total = 0;
    int maxTotal = marks.length * 100;
    if (maxTotal == 0) maxTotal = 100;

    buffer.writeln('🎓 *پاک اسکول اینڈ کالج سسٹم*');
    buffer.writeln('───────────────────');
    buffer.writeln('📊 *امتحانی نتیجہ و رزلٹ کارڈ (Student DMC)*\n');
    buffer.writeln('محترم والدین، السلام علیکم!');
    buffer.writeln('طالب علم *${student.name}* کا *$_selectedTerm* کا رزلٹ کارڈ درج ذیل ہے:\n');
    buffer.writeln('🔢 *رول نمبر:* ${student.rollNo}');
    buffer.writeln('🏫 *کلاس:* ${student.className}');
    buffer.writeln('📝 *امتحان:* $_selectedTerm\n');
    buffer.writeln('📚 *حاصل کردہ نمبرات (منتخب مضامین):*');

    marks.forEach((key, score) {
      final subInfo = comprehensiveFlutterSubjects.firstWhere(
        (s) => s['key'] == key,
        orElse: () => {'name': key, 'urdu': key},
      );
      total += score;
      buffer.writeln('• ${subInfo['name']} (${subInfo['urdu']}): $score / 100');
    });

    final pct = (total * 100) ~/ maxTotal;
    final grade = pct >= 80 ? 'A+' : (pct >= 70 ? 'A' : (pct >= 60 ? 'B' : (pct >= 50 ? 'C' : 'D')));

    buffer.writeln('\n📈 *کل نمبر:* $total / $maxTotal ($pct%)');
    buffer.writeln('🏆 *گریڈ:* $grade (${pct >= 40 ? 'کامیاب PASSED' : 'ناکام FAILED'})');
    buffer.writeln('───────────────────');
    buffer.writeln('پرنسپل و امتحان برانچ | پاک اسکول');

    var clean = student.parentPhone.replaceAll(RegExp(r'[^0-9+]'), '');
    if (clean.startsWith('+')) clean = clean.substring(1);
    if (clean.startsWith('03')) clean = '92${clean.substring(1)}';
    if (!clean.startsWith('92') && clean.length == 10) clean = '92$clean';

    final uri = Uri.parse('https://api.whatsapp.com/send?phone=$clean&text=${Uri.encodeComponent(buffer.toString())}');
    try {
      if (await canLaunchUrl(uri)) {
        await launchUrl(uri, mode: LaunchMode.externalApplication);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Could not open WhatsApp')));
      }
    } catch (_) {}
  }

  void _showEditMarksDialog(Student student, Map<String, int> currentMarks) {
    final tempMarks = Map<String, int>.from(currentMarks);

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) {
          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            title: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Select Subjects & Marks', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                Text('${student.name} • ${student.className}', style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
              ],
            ),
            content: SizedBox(
              width: double.maxFinite,
              height: 400,
              child: ListView(
                children: [
                  const Text('مضامین منتخب کریں اور نمبر درج کریں:', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Color(0xFF1E40AF))),
                  const SizedBox(height: 8),
                  ...comprehensiveFlutterSubjects.map((sub) {
                    final key = sub['key']!;
                    final isEnrolled = tempMarks.containsKey(key);
                    final score = tempMarks[key] ?? 75;

                    return Padding(
                      padding: const EdgeInsets.symmetric(vertical: 4),
                      child: Row(
                        children: [
                          Checkbox(
                            value: isEnrolled,
                            onChanged: (val) {
                              setDialogState(() {
                                if (val == true) {
                                  tempMarks[key] = 80;
                                } else {
                                  tempMarks.remove(key);
                                }
                              });
                            },
                          ),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(sub['name']!, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                                Text(sub['urdu']!, style: const TextStyle(fontSize: 11, color: Color(0xFF64748B))),
                              ],
                            ),
                          ),
                          if (isEnrolled)
                            SizedBox(
                              width: 60,
                              height: 36,
                              child: TextFormField(
                                initialValue: score.toString(),
                                keyboardType: TextInputType.number,
                                decoration: const InputDecoration(
                                  contentPadding: EdgeInsets.symmetric(horizontal: 6, vertical: 8),
                                  border: OutlineInputBorder(),
                                ),
                                onChanged: (val) {
                                  tempMarks[key] = int.tryParse(val) ?? score;
                                },
                              ),
                            )
                          else
                            const Text('Excluded', style: TextStyle(fontSize: 11, color: Colors.grey)),
                        ],
                      ),
                    );
                  }).toList(),
                ],
              ),
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
              ElevatedButton(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                onPressed: () {
                  setState(() {
                    _studentMarks[student.id ?? 0] = tempMarks;
                  });
                  Navigator.pop(ctx);
                },
                child: const Text('Save Marks', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
              ),
            ],
          );
        },
      ),
    );
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
            const Text('Exams & Result Cards (DMC)', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 17, fontWeight: FontWeight.bold)),
            Text('سائنس و آرٹس مضامین • Class: ${provider.selectedClass}', style: const TextStyle(color: Color(0xFF64748B), fontSize: 11)),
          ],
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Exam Session Selector
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              color: Colors.white,
              child: Padding(
                padding: const EdgeInsets.all(12),
                child: Row(
                  children: [
                    const Text('Exam: ', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF0F172A))),
                    const SizedBox(width: 8),
                    DropdownButton<String>(
                      value: _selectedTerm,
                      items: ['Monthly Test', 'Midterm Exam 2026', 'Final Term 2026']
                          .map((e) => DropdownMenuItem(value: e, child: Text(e, style: const TextStyle(fontSize: 13, color: Color(0xFF0F172A)))))
                          .toList(),
                      onChanged: (val) => setState(() => _selectedTerm = val ?? _selectedTerm),
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 12),

            // Students List
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: provider.students.length,
              itemBuilder: (context, index) {
                final student = provider.students[index];
                final marks = _studentMarks[student.id ?? index] ?? {
                  'bio': 85,
                  'phy': 80,
                  'chem': 82,
                  'math': 90,
                  'eng': 78,
                  'urdu': 85,
                  'isl': 92,
                  'quran': 95
                };
                final total = marks.values.isNotEmpty ? marks.values.reduce((a, b) => a + b) : 0;
                final maxTotal = (marks.length * 100).clamp(100, 2000);
                final pct = (total * 100) ~/ maxTotal;
                final grade = pct >= 80 ? 'A+' : (pct >= 70 ? 'A' : (pct >= 60 ? 'B' : 'C'));

                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                  color: Colors.white,
                  child: Padding(
                    padding: const EdgeInsets.all(14),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(student.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15, color: Color(0xFF0F172A))),
                                Text('Roll #: ${student.rollNo} • $_selectedTerm (${marks.length} Subjects)', style: const TextStyle(fontSize: 11, color: Color(0xFF64748B))),
                              ],
                            ),
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                              decoration: BoxDecoration(
                                color: pct >= 60 ? const Color(0xFFDCFCE7) : const Color(0xFFFEF3C7),
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: Text('$pct% ($grade)', style: TextStyle(color: pct >= 60 ? const Color(0xFF166534) : const Color(0xFFB45309), fontWeight: FontWeight.bold, fontSize: 11)),
                            ),
                          ],
                        ),
                        const SizedBox(height: 10),
                        Wrap(
                          spacing: 6,
                          runSpacing: 4,
                          children: marks.entries.map((e) {
                            return Container(
                              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                              decoration: BoxDecoration(color: const Color(0xFFF1F5F9), borderRadius: BorderRadius.circular(6)),
                              child: Text('${e.key.toUpperCase()}: ${e.value}', style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF1E293B))),
                            );
                          }).toList(),
                        ),
                        const SizedBox(height: 10),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text('Total: $total / $maxTotal', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF0F172A))),
                            Row(
                              children: [
                                OutlinedButton(
                                  onPressed: () => _showEditMarksDialog(student, marks),
                                  child: const Text('مضامین و نمبر', style: TextStyle(fontSize: 11, color: Color(0xFF1E40AF), fontWeight: FontWeight.bold)),
                                ),
                                const SizedBox(width: 6),
                                ElevatedButton.icon(
                                  style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                                  onPressed: () => _sendWhatsAppDmc(student, marks),
                                  icon: const Icon(Icons.send, size: 14, color: Colors.white),
                                  label: const Text('واٹس ایپ رزلٹ', style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold)),
                                ),
                              ],
                            ),
                          ],
                        ),
                      ],
                    ),
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
