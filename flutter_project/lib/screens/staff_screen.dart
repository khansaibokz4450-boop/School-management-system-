import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';
import '../models/staff.dart';

class StaffScreen extends StatefulWidget {
  final VoidCallback onBack;

  const StaffScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<StaffScreen> createState() => _StaffScreenState();
}

class _StaffScreenState extends State<StaffScreen> {
  // Local state for salary tracking and monthly attendance simulation
  final Map<int, int> _paidAmounts = {};
  final Map<int, Map<int, String>> _monthlyAttendance = {};

  void _showMonthlyRegisterDialog(StaffMember staff) {
    final staffId = staff.id ?? 0;
    if (!_monthlyAttendance.containsKey(staffId)) {
      _monthlyAttendance[staffId] = {
        for (int i = 1; i <= 30; i++)
          i: (i % 7 == 0) ? 'OFF' : (i == 14 ? 'ABSENT' : (i == 21 ? 'LEAVE' : 'PRESENT'))
      };
    }
    final days = _monthlyAttendance[staffId]!;

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) {
          final presentCount = days.values.where((v) => v == 'PRESENT').length;
          final absentCount = days.values.where((v) => v == 'ABSENT').length;
          final leaveCount = days.values.where((v) => v == 'LEAVE').length;

          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            title: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Monthly Attendance Register', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Color(0xFF1E40AF))),
                Text('${staff.name} • October 2026', style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
              ],
            ),
            content: SizedBox(
              width: double.maxFinite,
              height: 380,
              child: Column(
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Container(padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4), decoration: BoxDecoration(color: const Color(0xFFDCFCE7), borderRadius: BorderRadius.circular(6)), child: Text('Present: $presentCount', style: const TextStyle(color: Color(0xFF166534), fontWeight: FontWeight.bold, fontSize: 11))),
                      Container(padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4), decoration: BoxDecoration(color: const Color(0xFFFEE2E2), borderRadius: BorderRadius.circular(6)), child: Text('Absent: $absentCount', style: const TextStyle(color: Colors.red, fontWeight: FontWeight.bold, fontSize: 11))),
                      Container(padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4), decoration: BoxDecoration(color: const Color(0xFFFEF3C7), borderRadius: BorderRadius.circular(6)), child: Text('Leave: $leaveCount', style: const TextStyle(color: Color(0xFFB45309), fontWeight: FontWeight.bold, fontSize: 11))),
                    ],
                  ),
                  const SizedBox(height: 12),
                  const Text('Click any day to toggle (Present / Absent / Leave):', style: TextStyle(fontSize: 11, color: Color(0xFF475569))),
                  const SizedBox(height: 8),
                  Expanded(
                    child: GridView.builder(
                      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 6,
                        crossAxisSpacing: 6,
                        mainAxisSpacing: 6,
                      ),
                      itemCount: 30,
                      itemBuilder: (ctx, i) {
                        final day = i + 1;
                        final status = days[day] ?? 'PRESENT';
                        final color = status == 'PRESENT'
                            ? const Color(0xFFDCFCE7)
                            : (status == 'ABSENT' ? const Color(0xFFFEE2E2) : const Color(0xFFFEF3C7));
                        final textColor = status == 'PRESENT'
                            ? const Color(0xFF166534)
                            : (status == 'ABSENT' ? Colors.red : const Color(0xFFB45309));
                        final label = status == 'PRESENT' ? 'P' : (status == 'ABSENT' ? 'A' : (status == 'LEAVE' ? 'Lv' : 'Sun'));

                        return InkWell(
                          onTap: () {
                            setDialogState(() {
                              days[day] = status == 'PRESENT' ? 'ABSENT' : (status == 'ABSENT' ? 'LEAVE' : 'PRESENT');
                            });
                          },
                          child: Container(
                            decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(8)),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Text('$day', style: const TextStyle(fontSize: 9, color: Color(0xFF64748B))),
                                Text(label, style: TextStyle(fontWeight: FontWeight.bold, fontSize: 11, color: textColor)),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                ],
              ),
            ),
            actions: [
              ElevatedButton(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                onPressed: () => Navigator.pop(ctx),
                child: const Text('Done (مکمل)', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
              ),
            ],
          );
        },
      ),
    );
  }

  void _showPaySalaryDialog(StaffMember staff) {
    final staffId = staff.id ?? 0;
    int currentPaid = _paidAmounts[staffId] ?? staff.paidAmount;
    final ctrl = TextEditingController(text: currentPaid > 0 ? currentPaid.toString() : (staff.monthlySalary ~/ 2).toString());

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) {
          final entered = int.tryParse(ctrl.text) ?? 0;
          final balance = (staff.monthlySalary - entered).clamp(0, staff.monthlySalary);

          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            title: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Pay Faculty Salary', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                Text('${staff.name} • Monthly: Rs. ${staff.monthlySalary}', style: const TextStyle(fontSize: 12, color: Color(0xFF64748B))),
              ],
            ),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('کتنی رقم ادا کی گئی ہے؟ (Enter Paid Amount):', style: TextStyle(fontSize: 12, color: Color(0xFF475569))),
                const SizedBox(height: 8),
                TextField(
                  controller: ctrl,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(labelText: 'Paid Amount (PKR)', border: OutlineInputBorder()),
                  onChanged: (val) => setDialogState(() {}),
                ),
                const SizedBox(height: 10),
                Row(
                  children: [
                    InkWell(
                      onTap: () {
                        ctrl.text = (staff.monthlySalary ~/ 2).toString();
                        setDialogState(() {});
                      },
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(color: const Color(0xFFEFF6FF), borderRadius: BorderRadius.circular(6)),
                        child: const Text('آدھی تنخواہ (50% Half)', style: TextStyle(fontSize: 11, color: Color(0xFF1E40AF), fontWeight: FontWeight.bold)),
                      ),
                    ),
                    const SizedBox(width: 8),
                    InkWell(
                      onTap: () {
                        ctrl.text = staff.monthlySalary.toString();
                        setDialogState(() {});
                      },
                      child: Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(color: const Color(0xFFDCFCE7), borderRadius: BorderRadius.circular(6)),
                        child: const Text('مکمل تنخواہ (100% Full)', style: TextStyle(fontSize: 11, color: Color(0xFF166534), fontWeight: FontWeight.bold)),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                Container(
                  padding: const EdgeInsets.all(10),
                  decoration: BoxDecoration(color: balance > 0 ? const Color(0xFFFEF3C7) : const Color(0xFFDCFCE7), borderRadius: BorderRadius.circular(8)),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('بقایا رقم (Remaining Due):', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: balance > 0 ? const Color(0xFF92400E) : const Color(0xFF166534))),
                      Text('Rs. $balance', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: balance > 0 ? Colors.red : const Color(0xFF166534))),
                    ],
                  ),
                ),
              ],
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
              ElevatedButton(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                onPressed: () {
                  setState(() {
                    _paidAmounts[staffId] = int.tryParse(ctrl.text) ?? currentPaid;
                  });
                  Navigator.pop(ctx);
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Payment of Rs. ${ctrl.text} recorded for ${staff.name}')),
                  );
                },
                child: const Text('Save Payment', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
              ),
            ],
          );
        },
      ),
    );
  }

  void _sendWhatsAppSalarySlip(StaffMember staff, int paid, int remaining) async {
    final status = paid >= staff.monthlySalary ? 'مکمل ادا شدہ (PAID)' : (paid > 0 ? 'آدھی ادا شدہ (PARTIAL 50%)' : 'غیر ادا شدہ (UNPAID)');
    final msg = '''
🏫 *پاک اسکول اینڈ کالج سسٹم*
───────────────────
💼 *ماہانہ تنخواہ سلپ (Faculty Salary Slip)*

👤 *استاد کا نام:* ${staff.name}
💼 *عہدہ:* ${staff.designation} (${staff.subject})
🗓️ *مہینہ:* October 2026

💰 *ماہانہ مقررہ تنخواہ:* Rs. ${staff.monthlySalary}
💵 *ادا شدہ رقم:* Rs. $paid
⚠️ *بقایا تنخواہ (Remaining Due):* Rs. $remaining

📊 *اسٹیٹس:* $status
───────────────────
اکاؤنٹس ڈیپارٹمنٹ و پرنسپل | پاک اسکول
''';

    var clean = staff.phone.replaceAll(RegExp(r'[^0-9+]'), '');
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

    int totalBudget = provider.staff.fold(0, (sum, s) => sum + s.monthlySalary);
    int totalPaid = provider.staff.fold(0, (sum, s) => sum + (_paidAmounts[s.id ?? 0] ?? (s.salaryStatus == 'PAID' ? s.monthlySalary : 0)));
    int totalRemaining = (totalBudget - totalPaid).clamp(0, totalBudget);

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
        title: const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Staff & Faculty Payroll', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 17, fontWeight: FontWeight.bold)),
            Text('اساتذہ کی ماہانہ حاضری، آدھی/مکمل تنخواہ و بقایا', style: TextStyle(color: Color(0xFF64748B), fontSize: 11)),
          ],
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Top Summary Card
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              color: Colors.white,
              child: Padding(
                padding: const EdgeInsets.all(14),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('MONTHLY PAYROLL & FACULTY SUMMARY', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF64748B))),
                    const SizedBox(height: 10),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Total Budget', style: TextStyle(fontSize: 11, color: Color(0xFF64748B))),
                            Text('Rs. ${totalBudget ~/ 1000}k', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Color(0xFF0F172A))),
                          ],
                        ),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Paid (ادا شدہ)', style: TextStyle(fontSize: 11, color: Color(0xFF16A34A), fontWeight: FontWeight.bold)),
                            Text('Rs. ${totalPaid ~/ 1000}k', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Color(0xFF16A34A))),
                          ],
                        ),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Due (بقایا واجب)', style: TextStyle(fontSize: 11, color: Colors.red, fontWeight: FontWeight.bold)),
                            Text('Rs. ${totalRemaining ~/ 1000}k', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.red)),
                          ],
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 14),

            const Text('FACULTY MEMBERS:', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 11, color: Color(0xFF64748B))),
            const SizedBox(height: 8),

            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: provider.staff.length,
              itemBuilder: (context, index) {
                final st = provider.staff[index];
                final paid = _paidAmounts[st.id ?? 0] ?? (st.salaryStatus == 'PAID' ? st.monthlySalary : 0);
                final remaining = (st.monthlySalary - paid).clamp(0, st.monthlySalary);
                final isPaid = paid >= st.monthlySalary;
                final isPartial = paid > 0 && !isPaid;

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
                            Row(
                              children: [
                                CircleAvatar(
                                  backgroundColor: const Color(0xFFEFF6FF),
                                  radius: 20,
                                  child: Text(st.name.substring(0, 2).toUpperCase(), style: const TextStyle(color: Color(0xFF1E40AF), fontWeight: FontWeight.bold)),
                                ),
                                const SizedBox(width: 10),
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(st.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15, color: Color(0xFF0F172A))),
                                    Text('${st.designation} • ${st.subject}', style: const TextStyle(fontSize: 11, color: Color(0xFF64748B))),
                                  ],
                                ),
                              ],
                            ),
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                              decoration: BoxDecoration(
                                color: isPaid ? const Color(0xFFDCFCE7) : (isPartial ? const Color(0xFFFEF3C7) : const Color(0xFFFEE2E2)),
                                borderRadius: BorderRadius.circular(6),
                              ),
                              child: Text(
                                isPaid ? 'PAID (مکمل ادا)' : (isPartial ? 'PARTIAL (آدھی ادا)' : 'UNPAID (بقایا)'),
                                style: TextStyle(
                                  fontSize: 10,
                                  fontWeight: FontWeight.bold,
                                  color: isPaid ? const Color(0xFF166534) : (isPartial ? const Color(0xFFB45309) : Colors.red),
                                ),
                              ),
                            ),
                          ],
                        ),

                        const SizedBox(height: 10),

                        // Salary Breakdown Container
                        Container(
                          padding: const EdgeInsets.all(10),
                          decoration: BoxDecoration(color: const Color(0xFFF8FAFC), borderRadius: BorderRadius.circular(8)),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text('مقررہ تنخواہ', style: TextStyle(fontSize: 10, color: Color(0xFF64748B))),
                                  Text('Rs. ${st.monthlySalary}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF1E293B))),
                                ],
                              ),
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text('ادا شدہ رقم', style: TextStyle(fontSize: 10, color: Color(0xFF16A34A), fontWeight: FontWeight.bold)),
                                  Text('Rs. $paid', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Color(0xFF16A34A))),
                                ],
                              ),
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  const Text('بقایا رقم', style: TextStyle(fontSize: 10, color: Colors.red, fontWeight: FontWeight.bold)),
                                  Text('Rs. $remaining', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Colors.red)),
                                ],
                              ),
                              ElevatedButton(
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: isPaid ? const Color(0xFF16A34A) : const Color(0xFF2563EB),
                                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                                  minimumSize: Size.zero,
                                ),
                                onPressed: () => _showPaySalaryDialog(st),
                                child: Text(isPaid ? 'اپڈیٹ' : 'تنخواہ دیں', style: const TextStyle(fontSize: 10, color: Colors.white, fontWeight: FontWeight.bold)),
                              ),
                            ],
                          ),
                        ),

                        const SizedBox(height: 10),

                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            OutlinedButton.icon(
                              onPressed: () => _showMonthlyRegisterDialog(st),
                              icon: const Icon(Icons.calendar_month, size: 14, color: Color(0xFF1E40AF)),
                              label: const Text('ماہانہ رجسٹر', style: TextStyle(fontSize: 11, color: Color(0xFF1E40AF), fontWeight: FontWeight.bold)),
                            ),
                            OutlinedButton.icon(
                              onPressed: () => _sendWhatsAppSalarySlip(st, paid, remaining),
                              icon: const Icon(Icons.receipt_long, size: 14, color: Color(0xFF16A34A)),
                              label: const Text('تنخواہ سلپ', style: TextStyle(fontSize: 11, color: Color(0xFF16A34A), fontWeight: FontWeight.bold)),
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
