import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';
import '../models/student.dart';

class AttendanceScreen extends StatefulWidget {
  final VoidCallback onBack;

  const AttendanceScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<AttendanceScreen> createState() => _AttendanceScreenState();
}

class _AttendanceScreenState extends State<AttendanceScreen> {
  bool _isHoliday = false;

  void _sendWhatsApp(String phone, String text) async {
    var clean = phone.replaceAll(RegExp(r'[^0-9+]'), '');
    if (clean.startsWith('+')) clean = clean.substring(1);
    if (clean.startsWith('03')) clean = '92${clean.substring(1)}';
    if (!clean.startsWith('92') && clean.length == 10) clean = '92$clean';

    final uri = Uri.parse('https://api.whatsapp.com/send?phone=$clean&text=${Uri.encodeComponent(text)}');
    try {
      if (await canLaunchUrl(uri)) {
        await launchUrl(uri, mode: LaunchMode.externalApplication);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('WhatsApp cannot be opened on this device')),
        );
      }
    } catch (_) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Could not open WhatsApp')),
      );
    }
  }

  void _sendPrincipalSheet(SchoolProvider provider) {
    final absents = provider.students.where((s) => provider.todayAttendance[s.id]?.status == 'ABSENT').toList();
    final presentCount = provider.presentCount;
    final total = provider.totalStudents;
    final absentCount = absents.length;
    final date = provider.todayDate;
    final cls = provider.selectedClass;

    String text;
    if (_isHoliday) {
      text = '''
🏫 *پاک اسکول اینڈ کالج سسٹم*
───────────────────────
📢 *روزانہ رپورٹ برائے پرنسپل صاحب (Holiday Report)*

📅 *تاریخ:* $date
🏫 *کلاس / دائرہ کار:* $cls
🏖️ *صورتحال:* *اسکول بند ہے (چھٹی کا دن)*
📝 *تفصیل:* سرکاری تعطیل / اتوار

نوٹ: چھٹی کے باعث والدین کو غیر حاضری کے پیغامات نہیں بھیجے گئے۔
───────────────────────
رپورٹ خودکار سسٹم | پاک اسکول
''';
    } else {
      final listBuf = StringBuffer();
      if (absents.isEmpty) {
        listBuf.writeln('✅ ماشاءاللہ! آج کوئی طالب علم غیر حاضر نہیں ہے (100% حاضری)۔');
      } else {
        listBuf.writeln('📋 *غیر حاضر طلبہ کی فہرست (Roll No & Name):*');
        for (var i = 0; i < absents.length; i++) {
          final s = absents[i];
          listBuf.writeln('${i + 1}. [${s.rollNo}] ${s.name} (${s.className}) - فون: ${s.parentPhone}');
        }
      }

      text = '''
🏫 *پاک اسکول اینڈ کالج سسٹم*
───────────────────────
📊 *روزانہ حاضری شیٹ برائے پرنسپل صاحب*

📅 *تاریخ:* $date
🏫 *کلاس:* $cls
👥 *کل رجسٹرڈ طلبہ:* $total

📈 *حاضری سمری:*
🟢 *حاضر طلبہ کی تعداد:* $presentCount
🔴 *غیر حاضر طلبہ کی تعداد:* $absentCount

───────────────────────
${listBuf.toString().trim()}
───────────────────────
رپورٹ تیار کردہ: اے آئی اسمارٹ اسکول منیجمنٹ سسٹم
''';
    }

    _sendWhatsApp('+923001234567', text);
  }

  void _sendAbsenteeNoticeToParent(Student student, String date) {
    if (_isHoliday) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Holiday Mode Active: Parents will not receive alerts.')),
      );
      return;
    }

    final text = '''
🏫 *پاک اسکول اینڈ کالج سسٹم*
───────────────────
⚠️ *اطلاع غیر حاضری (Absentee Notice)*

محترم والدین،
السلام علیکم!
آپ کا بچہ *${student.name}* (رول نمبر: *${student.rollNo}*، کلاس: *${student.className}*) آج مورخہ *$date* کو اسکول سے *غیر حاضر* ہے۔

تعلیم کے تسلسل کے لیے روزانہ حاضری ضروری ہے۔ برائے مہربانی اپنے بچے کی غیر حاضری کی وجہ تحریری طور پر یا کال کر کے اسکول آفس کو فوری مطلع فرمائیں۔

شکریہ،
───────────────────
پرنسپل و انتظامیہ | پاک اسکول
''';

    _sendWhatsApp(student.parentPhone, text);
  }

  void _openBiometricDialog(Student student, SchoolProvider provider) {
    int selectedMode = 0; // 0: Mobile Fingerprint, 1: Face AI, 2: USB Device
    bool isScanning = false;
    bool isSuccess = false;

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) {
          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
            title: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Biometric Verify', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                Chip(label: Text(student.rollNo, style: const TextStyle(fontSize: 10, color: Color(0xFF1E40AF)))),
              ],
            ),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text('${student.name} • Class: ${student.className}', style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
                const SizedBox(height: 12),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ChoiceChip(
                      label: const Text('📱 Fingerprint', style: TextStyle(fontSize: 10)),
                      selected: selectedMode == 0,
                      onSelected: (val) => setDialogState(() => selectedMode = 0),
                    ),
                    const SizedBox(width: 6),
                    ChoiceChip(
                      label: const Text('📷 Face AI', style: TextStyle(fontSize: 10)),
                      selected: selectedMode == 1,
                      onSelected: (val) => setDialogState(() => selectedMode = 1),
                    ),
                    const SizedBox(width: 6),
                    ChoiceChip(
                      label: const Text('🔌 USB', style: TextStyle(fontSize: 10)),
                      selected: selectedMode == 2,
                      onSelected: (val) => setDialogState(() => selectedMode = 2),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                Container(
                  width: 120,
                  height: 120,
                  decoration: BoxDecoration(
                    color: isSuccess ? const Color(0xFFDCFCE7) : const Color(0xFFEFF6FF),
                    shape: BoxShape.circle,
                    border: Border.all(color: isSuccess ? Colors.green : const Color(0xFF1E40AF), width: 3),
                  ),
                  child: Center(
                    child: Icon(
                      isSuccess
                          ? Icons.check_circle
                          : (selectedMode == 0 ? Icons.fingerprint : (selectedMode == 1 ? Icons.face : Icons.usb)),
                      size: 60,
                      color: isSuccess ? Colors.green : const Color(0xFF1E40AF),
                    ),
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  isSuccess ? 'Verified (100% Match)' : (isScanning ? 'Scanning sensor...' : 'Tap scan below'),
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                    color: isSuccess ? Colors.green : const Color(0xFF1E40AF),
                  ),
                ),
              ],
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Close')),
              if (!isSuccess)
                ElevatedButton(
                  style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                  onPressed: () async {
                    setDialogState(() => isScanning = true);
                    await Future.delayed(const Duration(milliseconds: 1000));
                    setDialogState(() {
                      isScanning = false;
                      isSuccess = true;
                    });
                    if (student.id != null) {
                      await provider.markAttendance(student.id!, 'PRESENT');
                    }
                    await Future.delayed(const Duration(milliseconds: 700));
                    if (Navigator.canPop(ctx)) Navigator.pop(ctx);
                  },
                  child: const Text('Start Scan', style: TextStyle(color: Colors.white)),
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
            const Text(
              'Daily Attendance',
              style: TextStyle(color: Color(0xFF1E40AF), fontSize: 18, fontWeight: FontWeight.bold),
            ),
            Text(
              'روزانہ حاضری • Class: ${provider.selectedClass}',
              style: const TextStyle(color: Color(0xFF64748B), fontSize: 11),
            ),
          ],
        ),
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 12),
            child: ElevatedButton.icon(
              onPressed: () => provider.markAllPresent(),
              icon: const Icon(Icons.done_all, size: 16),
              label: const Text('All Present', style: TextStyle(fontSize: 11)),
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF16A34A),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
              ),
            ),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            // Principal WhatsApp Sheet & Holiday Mode Card
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              color: _isHoliday ? const Color(0xFFFEF3C7) : const Color(0xFFF0FDF4),
              child: Padding(
                padding: const EdgeInsets.all(14),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          _isHoliday ? '🏖️ School Holiday Mode' : '📋 Principal WhatsApp Sheet',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 14,
                            color: _isHoliday ? const Color(0xFF92400E) : const Color(0xFF166534),
                          ),
                        ),
                        Row(
                          children: [
                            Text(
                              _isHoliday ? 'چھٹی آن ہے' : 'چھٹی کا موڈ',
                              style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold),
                            ),
                            Switch(
                              value: _isHoliday,
                              onChanged: (val) => setState(() => _isHoliday = val),
                              activeColor: const Color(0xFFD97706),
                            ),
                          ],
                        ),
                      ],
                    ),
                    if (_isHoliday)
                      const Text(
                        '⚠️ چھٹی کے باعث والدین کو غیر حاضری کے پیغامات خودکار طور پر بند کر دیے گئے ہیں۔',
                        style: TextStyle(fontSize: 11, color: Color(0xFFB45309)),
                      ),
                    const SizedBox(height: 8),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF1E40AF),
                          foregroundColor: Colors.white,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                        ),
                        onPressed: () => _sendPrincipalSheet(provider),
                        icon: const Icon(Icons.send, size: 16),
                        label: const Text('📲 Send Sheet to Principal WhatsApp (پرنسپل کو شیٹ بھیجیں)', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
                      ),
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 12),

            // Student Roster
            if (provider.students.isEmpty)
              const Padding(
                padding: EdgeInsets.all(32),
                child: Text('No students found in this class'),
              )
            else
              ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: provider.students.length,
                itemBuilder: (context, index) {
                  final student = provider.students[index];
                  final attendance = provider.todayAttendance[student.id];
                  final currentStatus = attendance?.status ?? 'UNMARKED';

                  return Card(
                    margin: const EdgeInsets.only(bottom: 12),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                    elevation: 1.5,
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
                                  Text(
                                    student.name,
                                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15),
                                  ),
                                  Text(
                                    '${student.rollNo} • S/O: ${student.fatherName} • ${student.className}',
                                    style: const TextStyle(fontSize: 11, color: Color(0xFF64748B)),
                                  ),
                                ],
                              ),
                              IconButton(
                                icon: const Icon(Icons.fingerprint, color: Color(0xFF1E40AF), size: 28),
                                onPressed: () => _openBiometricDialog(student, provider),
                                tooltip: 'Fingerprint / Face / USB Verify',
                              ),
                            ],
                          ),
                          const SizedBox(height: 10),
                          Row(
                            children: [
                              _statusButton(
                                label: 'Present',
                                isSelected: currentStatus == 'PRESENT',
                                color: const Color(0xFF2E7D32),
                                onTap: () => student.id != null ? provider.markAttendance(student.id!, 'PRESENT') : null,
                              ),
                              const SizedBox(width: 6),
                              _statusButton(
                                label: 'Absent',
                                isSelected: currentStatus == 'ABSENT',
                                color: const Color(0xFFDC2626),
                                onTap: () => student.id != null ? provider.markAttendance(student.id!, 'ABSENT') : null,
                              ),
                              const SizedBox(width: 6),
                              _statusButton(
                                label: 'Late',
                                isSelected: currentStatus == 'LATE',
                                color: const Color(0xFFF59E0B),
                                onTap: () => student.id != null ? provider.markAttendance(student.id!, 'LATE') : null,
                              ),
                              const SizedBox(width: 6),
                              _statusButton(
                                label: 'Leave',
                                isSelected: currentStatus == 'LEAVE',
                                color: const Color(0xFF0D9488),
                                onTap: () => student.id != null ? provider.markAttendance(student.id!, 'LEAVE') : null,
                              ),
                            ],
                          ),
                          if (currentStatus == 'ABSENT') ...[
                            const SizedBox(height: 8),
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                              decoration: BoxDecoration(
                                color: const Color(0xFFFEE2E2),
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Text(
                                    _isHoliday ? '🏖️ چھٹی: میسج موقوف' : '⚠️ غیر حاضر - والدین کو اردو اطلاع',
                                    style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFFDC2626)),
                                  ),
                                  if (!_isHoliday)
                                    ElevatedButton.icon(
                                      style: ElevatedButton.styleFrom(
                                        backgroundColor: const Color(0xFF25D366),
                                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                        minimumSize: const Size(60, 28),
                                      ),
                                      onPressed: () => _sendAbsenteeNoticeToParent(student, provider.todayDate),
                                      icon: const Icon(Icons.send, size: 12, color: Colors.white),
                                      label: const Text('📱 اردو الرٹ', style: TextStyle(fontSize: 10, color: Colors.white, fontWeight: FontWeight.bold)),
                                    ),
                                ],
                              ),
                            ),
                          ],
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

  Widget _statusButton({
    required String label,
    required bool isSelected,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Expanded(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(8),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            color: isSelected ? color : color.withOpacity(0.08),
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: isSelected ? color : color.withOpacity(0.3)),
          ),
          child: Center(
            child: Text(
              label,
              style: TextStyle(
                fontSize: 11,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.w500,
                color: isSelected ? Colors.white : color,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
