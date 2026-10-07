import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';

class TimetableScreen extends StatefulWidget {
  final VoidCallback onBack;

  const TimetableScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<TimetableScreen> createState() => _TimetableScreenState();
}

class _TimetableScreenState extends State<TimetableScreen> {
  String _selectedDay = 'Monday';
  final List<String> _days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

  final List<Map<String, dynamic>> _periods = [
    {'no': 1, 'time': '08:00 - 08:45 AM', 'subject': 'English Language (انگریزی)', 'teacher': 'Sir Tariq Mehmood', 'room': 'Room 101'},
    {'no': 2, 'time': '08:45 - 09:30 AM', 'subject': 'Mathematics (ریاضی)', 'teacher': "Ma'am Sadia Khan", 'room': 'Room 101'},
    {'no': 3, 'time': '09:30 - 10:15 AM', 'subject': 'Physics / Science (فزکس)', 'teacher': 'Sir Imran Ali', 'room': 'Physics Lab'},
    {'no': 4, 'time': '10:15 - 11:00 AM', 'subject': 'Chemistry (کیمسٹری)', 'teacher': 'Sir Bilal', 'room': 'Chemistry Lab'},
    {'no': 0, 'time': '11:00 - 11:30 AM', 'subject': '🥗 Lunch & Recess (تفریح و بریک)', 'teacher': 'Duty Staff', 'room': 'Ground', 'isBreak': true},
    {'no': 5, 'time': '11:30 - 12:15 PM', 'subject': 'Biology / Computer (کمپیوٹر/بیالوجی)', 'teacher': "Ma'am Ayesha", 'room': 'Lab 2'},
    {'no': 6, 'time': '12:15 - 01:00 PM', 'subject': 'Islamiat & Mutalia Quran (اسلامیات/قرآن)', 'teacher': 'Qari Saeed Ahmad', 'room': 'Room 101'},
    {'no': 7, 'time': '01:00 - 01:30 PM', 'subject': 'Urdu Literature / Pashto (اردو/پشتو)', 'teacher': 'Coach Farooq', 'room': 'Room 101'},
  ];

  void _showEditPeriodDialog({int? index}) {
    final isNew = index == null;
    final item = isNew
        ? {'no': _periods.length + 1, 'time': '01:30 - 02:15 PM', 'subject': 'Chemistry (کیمسٹری)', 'teacher': 'Sir Bilal', 'room': 'Room 101', 'isBreak': false}
        : _periods[index];

    final timeCtrl = TextEditingController(text: item['time'] ?? '');
    final subjCtrl = TextEditingController(text: item['subject'] ?? '');
    final teacherCtrl = TextEditingController(text: item['teacher'] ?? '');
    final roomCtrl = TextEditingController(text: item['room'] ?? '');
    bool isBreak = item['isBreak'] == true;

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) {
          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            title: Text(isNew ? 'Add Period Slot (نیا پیریڈ)' : 'Edit Period ${item['no']}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            content: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextField(controller: timeCtrl, decoration: const InputDecoration(labelText: 'Timing (وقت)')),
                  const SizedBox(height: 8),
                  TextField(controller: subjCtrl, decoration: const InputDecoration(labelText: 'Subject (مضمون)')),
                  const SizedBox(height: 8),
                  TextField(controller: teacherCtrl, decoration: const InputDecoration(labelText: 'Teacher Name (استاد کا نام)')),
                  const SizedBox(height: 8),
                  TextField(controller: roomCtrl, decoration: const InputDecoration(labelText: 'Room / Lab (کمرہ)')),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      Checkbox(value: isBreak, onChanged: (v) => setDialogState(() => isBreak = v ?? false)),
                      const Text('Is Lunch/Recess Break?', style: TextStyle(fontSize: 12)),
                    ],
                  ),
                ],
              ),
            ),
            actions: [
              if (!isNew)
                TextButton(
                  onPressed: () {
                    setState(() { _periods.removeAt(index); });
                    Navigator.pop(ctx);
                  },
                  child: const Text('Delete (ہٹائیں)', style: TextStyle(color: Colors.red)),
                ),
              TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
              ElevatedButton(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                onPressed: () {
                  setState(() {
                    final updated = {
                      'no': item['no'] ?? (_periods.length + 1),
                      'time': timeCtrl.text.trim(),
                      'subject': subjCtrl.text.trim(),
                      'teacher': teacherCtrl.text.trim(),
                      'room': roomCtrl.text.trim(),
                      'isBreak': isBreak,
                    };
                    if (isNew) {
                      _periods.add(updated);
                    } else {
                      _periods[index] = updated;
                    }
                  });
                  Navigator.pop(ctx);
                },
                child: const Text('Save Changes', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
              ),
            ],
          );
        },
      ),
    );
  }

  void _shareTimetableOnWhatsApp(String className) async {
    final sb = StringBuffer();
    sb.writeln('🏫 *پاک اسکول اینڈ کالج سسٹم*');
    sb.writeln('───────────────────');
    sb.writeln('⏰ *کلاس ٹائم ٹیبل و شیڈول ($_selectedDay)*\n');
    sb.writeln('🏫 *کلاس:* $className\n');

    for (var p in _periods) {
      if (p['isBreak'] == true) {
        sb.writeln('• 🥗 ${p['subject']} (${p['time']})');
      } else {
        sb.writeln('• پیریڈ ${p['no']} (${p['time']}): ${p['subject']} (استاد: ${p['teacher']} - ${p['room']})');
      }
    }
    sb.writeln('\n───────────────────');
    sb.writeln('ٹائم ٹیبل انچارج و پرنسپل | پاک اسکول');

    final uri = Uri.parse('https://api.whatsapp.com/send?text=${Uri.encodeComponent(sb.toString())}');
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
            const Text('Class Timetable & Periods', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 17, fontWeight: FontWeight.bold)),
            Text('پیریڈ و استاد ایڈٹ کنٹرول • Class: ${provider.selectedClass}', style: const TextStyle(color: Color(0xFF64748B), fontSize: 11)),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.add_circle, color: Color(0xFF16A34A)),
            tooltip: 'Add Period',
            onPressed: () => _showEditPeriodDialog(),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Day selector
            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: _days.map((day) {
                  final isSel = day == _selectedDay;
                  return Padding(
                    padding: const EdgeInsets.only(right: 8),
                    child: ChoiceChip(
                      label: Text(day, style: TextStyle(color: isSel ? Colors.white : Colors.black87, fontSize: 11, fontWeight: isSel ? FontWeight.bold : FontWeight.normal)),
                      selected: isSel,
                      selectedColor: const Color(0xFF1E40AF),
                      onSelected: (val) => setState(() => _selectedDay = day),
                    ),
                  );
                }).toList(),
              ),
            ),

            const SizedBox(height: 14),

            // Periods List
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: _periods.length,
              itemBuilder: (context, index) {
                final p = _periods[index];
                final isBreak = p['isBreak'] == true;

                if (isBreak) {
                  return Container(
                    margin: const EdgeInsets.only(bottom: 10),
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(color: const Color(0xFFFEF3C7), borderRadius: BorderRadius.circular(10)),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(p['subject'], style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF92400E))),
                        Row(
                          children: [
                            Text(p['time'], style: const TextStyle(fontSize: 11, color: Color(0xFFB45309))),
                            IconButton(
                              icon: const Icon(Icons.edit, size: 16, color: Color(0xFF92400E)),
                              onPressed: () => _showEditPeriodDialog(index: index),
                            ),
                          ],
                        ),
                      ],
                    ),
                  );
                }

                return Card(
                  margin: const EdgeInsets.only(bottom: 10),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  color: Colors.white,
                  child: Padding(
                    padding: const EdgeInsets.all(12),
                    child: Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: const Color(0xFFEFF6FF),
                          radius: 18,
                          child: Text('P${p['no']}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12, color: Color(0xFF1E40AF))),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(p['subject'], style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14, color: Color(0xFF0F172A))),
                              Text('${p['teacher']} • ${p['room']}', style: const TextStyle(fontSize: 11, color: Color(0xFF64748B))),
                              Text(p['time'], style: const TextStyle(fontSize: 10, fontWeight: FontWeight.bold, color: Color(0xFF1E40AF))),
                            ],
                          ),
                        ),
                        IconButton(
                          icon: const Icon(Icons.edit, size: 18, color: Color(0xFF2563EB)),
                          tooltip: 'Edit Period & Teacher',
                          onPressed: () => _showEditPeriodDialog(index: index),
                        ),
                        IconButton(
                          icon: const Icon(Icons.delete, size: 18, color: Colors.red),
                          tooltip: 'Delete Period',
                          onPressed: () {
                            setState(() { _periods.removeAt(index); });
                          },
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),

            const SizedBox(height: 12),

            ElevatedButton.icon(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF16A34A),
                minimumSize: const Size(double.infinity, 44),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              ),
              onPressed: () => _shareTimetableOnWhatsApp(provider.selectedClass),
              icon: const Icon(Icons.send, color: Colors.white, size: 16),
              label: const Text('📲 واٹس ایپ پر ٹائم ٹیبل شیئر کریں', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
            ),
          ],
        ),
      ),
    );
  }
}
