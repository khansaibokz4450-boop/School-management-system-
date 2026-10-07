import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';

class DeveloperLicenseScreen extends StatefulWidget {
  final VoidCallback onBack;

  const DeveloperLicenseScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<DeveloperLicenseScreen> createState() => _DeveloperLicenseScreenState();
}

class _DeveloperLicenseScreenState extends State<DeveloperLicenseScreen> {
  bool _isRoyaltyEnabled = true;
  final _phoneController = TextEditingController(text: '03001234567');
  final _nameController = TextEditingController(text: 'Ahmad ullah okz');
  final _feeController = TextEditingController(text: '100');

  void _sendInvoice(SchoolProvider provider) async {
    final fee = int.tryParse(_feeController.text) ?? 100;
    final total = provider.totalStudents;
    final due = total * fee;

    final msg = '''
🎓 *پاک اسکول مینجمنٹ سسٹم - ڈیولپر لائسنس بل*
───────────────────────
محترم پرنسپل صاحب!
السلام علیکم،

آپ کے اسکول میں کل *$total* طلبہ کے داخلے فعال ہیں۔
سافٹ ویئر معاہدے کے تحت فی طالب علم Rs. $fee کے حساب سے واجب الادا رقم:

💰 *کل واجب الادا رقم:* Rs. $due
📱 *ایزی پیسہ اکاؤنٹ نمبر:* ${_phoneController.text}
👤 *اکاؤنٹ ٹائٹل:* ${_nameController.text}

برائے مہربانی ایزی پیسہ کے ذریعے رقم منتقل فرما کر اسکرین شاٹ اسی واٹس ایپ نمبر پر ارسال فرمائیں۔
شکریہ!
───────────────────────
سافٹ ویئر ڈیولپر: Ahmad ullah okz | پاکستان 🇵🇰
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
    final fee = int.tryParse(_feeController.text) ?? 100;
    final totalDue = _isRoyaltyEnabled ? provider.totalStudents * fee : 0;

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
        title: const Text('EasyPaisa Royalty Portal', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 18, fontWeight: FontWeight.bold)),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              color: const Color(0xFF1E3A8A),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    const CircleAvatar(
                      radius: 24,
                      backgroundColor: Color(0xFF2563EB),
                      child: Text('AO', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                    ),
                    const SizedBox(width: 14),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: const [
                        Text('Ahmad ullah okz', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
                        Text('Master Software Owner 🇵🇰', style: TextStyle(color: Color(0xFF93C5FD), fontSize: 12)),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 14),
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              color: Colors.white,
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Charge Royalty for This School', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                        Switch(
                          value: _isRoyaltyEnabled,
                          onChanged: (val) => setState(() => _isRoyaltyEnabled = val),
                          activeColor: const Color(0xFF16A34A),
                        ),
                      ],
                    ),
                    const Divider(height: 24),
                    TextField(controller: _phoneController, decoration: const InputDecoration(labelText: 'EasyPaisa Mobile Number', border: OutlineInputBorder())),
                    const SizedBox(height: 10),
                    TextField(controller: _nameController, decoration: const InputDecoration(labelText: 'Account Title', border: OutlineInputBorder())),
                    const SizedBox(height: 10),
                    TextField(controller: _feeController, decoration: const InputDecoration(labelText: 'Fee per Student (PKR)', border: OutlineInputBorder()), keyboardType: TextInputType.number),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 14),
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              color: const Color(0xFFF0FDF4),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Total Students Enrolled:'),
                        Text('${provider.totalStudents}', style: const TextStyle(fontWeight: FontWeight.bold)),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('Total Royalty Payable:'),
                        Text('Rs. $totalDue PKR', style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF16A34A))),
                      ],
                    ),
                    const SizedBox(height: 14),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                        onPressed: _isRoyaltyEnabled ? () => _sendInvoice(provider) : null,
                        icon: const Icon(Icons.send, color: Colors.white, size: 16),
                        label: const Text('📲 Send EasyPaisa Invoice to Principal', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
