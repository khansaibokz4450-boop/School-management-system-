import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';
import '../models/student.dart';

class FeeScreen extends StatefulWidget {
  final VoidCallback onBack;

  const FeeScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<FeeScreen> createState() => _FeeScreenState();
}

class _FeeScreenState extends State<FeeScreen> {
  String _filter = 'ALL';

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
          const SnackBar(content: Text('Could not open WhatsApp')),
        );
      }
    } catch (_) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Could not open WhatsApp')),
      );
    }
  }

  void _sendUrduReceipt(Student student, int amount, String receiptNo, String date) {
    final text = '''
🎓 *پاک اسکول اینڈ کالج سسٹم*
───────────────────
🧾 *فیس وصولی کی تصدیقی رسید (Fee Receipt)*

محترم والدین،
السلام علیکم!
اطلاع دی جاتی ہے کہ آپ کے بچے کی فیس کامیابی سے موصول ہو گئی ہے۔

👤 *طالب علم کا نام:* ${student.name}
🔢 *رول نمبر:* ${student.rollNo}
🏫 *کلاس:* ${student.className}
📅 *مہینہ:* اکتوبر 2026
💰 *وصول شدہ رقم:* Rs. $amount
🔖 *رسید نمبر:* $receiptNo
🗓️ *تاریخ ادائیگی:* $date

بروقت ادائیگی اور آپ کے تعاون کا شکریہ!
───────────────────
دفتر اکاؤنٹس و انتظامیہ | پاک اسکول سسٹم
''';

    _sendWhatsApp(student.parentPhone, text);
  }

  void _sendUrduReminder(Student student) {
    final text = '''
📢 *پاک اسکول اینڈ کالج سسٹم*
───────────────────
💳 *ماہانہ فیس کی یاددہانی (Fee Reminder)*

محترم والدین،
السلام علیکم!
طالب علم *${student.name}* (${student.className}) کی ماہانہ فیس مبلغ *Rs. ${student.monthlyFee}* واجب الادا ہے۔

تعلیمی نظام کو بلاتعطل جاری رکھنے کے لیے برائے مہربانی مقررہ تاریخ سے قبل فیس جمع کرا دیں۔

ادائیگی کے ذرائع: کیش کاؤنٹر / JazzCash / EasyPaisa / بینک اکاؤنٹ
شکریہ!
───────────────────
دفتر اکاؤنٹس | پاک اسکول سسٹم
''';

    _sendWhatsApp(student.parentPhone, text);
  }

  void _showPaymentDialog(BuildContext context, SchoolProvider provider, Student student) {
    final amountController = TextEditingController(text: student.monthlyFee.toString());
    String method = 'Cash';

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setDialogState) {
          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            title: Text('Collect Fee: ${student.name}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text('${student.rollNo} • ${student.className}', style: const TextStyle(color: Color(0xFF64748B), fontSize: 12)),
                const SizedBox(height: 12),
                TextField(
                  controller: amountController,
                  decoration: const InputDecoration(labelText: 'Amount (PKR)', border: OutlineInputBorder()),
                  keyboardType: TextInputType.number,
                ),
                const SizedBox(height: 12),
                DropdownButtonFormField<String>(
                  value: method,
                  decoration: const InputDecoration(labelText: 'Payment Method', border: OutlineInputBorder()),
                  items: ['Cash', 'JazzCash', 'EasyPaisa', 'Bank Transfer']
                      .map((e) => DropdownMenuItem(value: e, child: Text(e)))
                      .toList(),
                  onChanged: (val) => setDialogState(() => method = val ?? 'Cash'),
                ),
                const SizedBox(height: 8),
                const Text(
                  '📱 وصولی کے بعد والدین کو خودکار اردو رسید واٹس ایپ پر بھیجی جائے گی۔',
                  style: TextStyle(fontSize: 11, color: Color(0xFF1E40AF)),
                ),
              ],
            ),
            actions: [
              TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
              ElevatedButton(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                onPressed: () async {
                  final amount = int.tryParse(amountController.text) ?? student.monthlyFee;
                  await provider.recordFeePayment(
                    student: student,
                    month: 'October 2026',
                    amount: amount,
                    discount: 0,
                    paymentMethod: method,
                  );
                  Navigator.pop(ctx);
                  final receiptNo = 'REC-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}';
                  _sendUrduReceipt(student, amount, receiptNo, provider.todayDate);
                },
                child: const Text('Confirm & Send Urdu Receipt', style: TextStyle(color: Colors.white)),
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

    final students = provider.students.where((s) {
      if (_filter == 'PAID') return s.feeStatus == 'PAID';
      if (_filter == 'PENDING') return s.feeStatus == 'PENDING';
      if (_filter == 'DEFAULTER') return s.feeStatus == 'DEFAULTER';
      return true;
    }).toList();

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
              'Fee Payment Tracker',
              style: TextStyle(color: Color(0xFF1E40AF), fontSize: 18, fontWeight: FontWeight.bold),
            ),
            Text(
              'فیس وصولی و رسیدات • Class: ${provider.selectedClass}',
              style: const TextStyle(color: Color(0xFF64748B), fontSize: 11),
            ),
          ],
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // KPI Summary Cards
            Row(
              children: [
                _kpiCard('Collected', 'Rs. ${provider.totalCollectedFee}', const Color(0xFF16A34A)),
                const SizedBox(width: 10),
                _kpiCard('Class Students', '${provider.totalStudents}', const Color(0xFF1E40AF)),
              ],
            ),

            const SizedBox(height: 14),

            // Filter Chips
            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(
                children: [
                  _filterChip('ALL', 'All (${provider.students.length})'),
                  const SizedBox(width: 8),
                  _filterChip('PAID', 'Paid (${provider.students.where((s) => s.feeStatus == 'PAID').length})'),
                  const SizedBox(width: 8),
                  _filterChip('PENDING', 'Pending (${provider.students.where((s) => s.feeStatus == 'PENDING').length})'),
                  const SizedBox(width: 8),
                  _filterChip('DEFAULTER', 'Defaulter (${provider.students.where((s) => s.feeStatus == 'DEFAULTER').length})'),
                ],
              ),
            ),

            const SizedBox(height: 14),

            // Students List
            if (students.isEmpty)
              const Center(
                child: Padding(
                  padding: EdgeInsets.all(32),
                  child: Text('No students found in this category'),
                ),
              )
            else
              ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: students.length,
                itemBuilder: (context, index) {
                  final student = students[index];
                  final isPaid = student.feeStatus == 'PAID';

                  return Card(
                    margin: const EdgeInsets.only(bottom: 10),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                    child: Padding(
                      padding: const EdgeInsets.all(12),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(student.name, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                              Text(
                                '${student.rollNo} • Rs. ${student.monthlyFee}/month',
                                style: const TextStyle(fontSize: 11, color: Color(0xFF64748B)),
                              ),
                            ],
                          ),
                          Row(
                            children: [
                              if (isPaid)
                                ElevatedButton.icon(
                                  style: ElevatedButton.styleFrom(
                                    backgroundColor: const Color(0xFF16A34A),
                                    foregroundColor: Colors.white,
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                                    minimumSize: const Size(60, 32),
                                  ),
                                  onPressed: () {
                                    final receiptNo = 'REC-${DateTime.now().millisecondsSinceEpoch.toString().substring(7)}';
                                    _sendUrduReceipt(student, student.monthlyFee, receiptNo, provider.todayDate);
                                  },
                                  icon: const Icon(Icons.receipt, size: 14),
                                  label: const Text('🧾 اردو رسید', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold)),
                                )
                              else ...[
                                ElevatedButton(
                                  style: ElevatedButton.styleFrom(
                                    backgroundColor: const Color(0xFF1E40AF),
                                    foregroundColor: Colors.white,
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                                    minimumSize: const Size(50, 32),
                                  ),
                                  onPressed: () => _showPaymentDialog(context, provider, student),
                                  child: const Text('Collect', style: TextStyle(fontSize: 11)),
                                ),
                                const SizedBox(width: 6),
                                ElevatedButton.icon(
                                  style: ElevatedButton.styleFrom(
                                    backgroundColor: const Color(0xFF25D366),
                                    foregroundColor: Colors.white,
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
                                    minimumSize: const Size(40, 32),
                                  ),
                                  onPressed: () => _sendUrduReminder(student),
                                  icon: const Icon(Icons.send, size: 12),
                                  label: const Text('یاددہانی', style: TextStyle(fontSize: 10)),
                                ),
                              ],
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

  Widget _filterChip(String key, String label) {
    final isSelected = _filter == key;
    return ChoiceChip(
      label: Text(label, style: TextStyle(fontSize: 11, color: isSelected ? Colors.white : Colors.black87)),
      selected: isSelected,
      selectedColor: key == 'DEFAULTER' ? const Color(0xFFDC2626) : const Color(0xFF1E40AF),
      onSelected: (val) => setState(() => _filter = key),
    );
  }

  Widget _kpiCard(String title, String value, Color color) {
    return Expanded(
      child: Card(
        elevation: 2,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        color: Colors.white,
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(title, style: const TextStyle(fontSize: 11, color: Color(0xFF64748B))),
              const SizedBox(height: 4),
              Text(
                value,
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: color),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
