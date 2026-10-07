import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';

class ExportScreen extends StatelessWidget {
  final VoidCallback onBack;

  const ExportScreen({Key? key, required this.onBack}) : super(key: key);

  void _shareCsv(String filename, String content) async {
    final uri = Uri.parse('https://api.whatsapp.com/send?text=${Uri.encodeComponent('$filename\n\n$content')}');
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
          onPressed: onBack,
          icon: const Icon(Icons.arrow_back, size: 16, color: Color(0xFF1E40AF)),
          label: const Text('Menu', style: TextStyle(color: Color(0xFF1E40AF), fontWeight: FontWeight.bold)),
        ),
        leadingWidth: 90,
        title: const Text('Backup & Excel Export', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 18, fontWeight: FontWeight.bold)),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
            color: const Color(0xFFEFF6FF),
            child: const Padding(
              padding: EdgeInsets.all(14),
              child: Text(
                '💾 تمام اسکول ڈیٹا ایکسل (.csv) شیٹ میں محفوظ کریں تاکہ کمپیوٹر میں ریکارڈ محفوظ رہے یا واٹس ایپ پر شیئر کیا جا سکے۔',
                style: TextStyle(fontSize: 12, color: Color(0xFF1E40AF)),
              ),
            ),
          ),
          const SizedBox(height: 14),
          _exportTile(
            title: 'Students Directory (طلبہ رجسٹر)',
            subtitle: 'تمام طلبہ کے نام، رول نمبر، کلاس اور والدین کا رابطہ نمبر۔',
            onTap: () {
              final sb = StringBuffer('RollNo,Name,FatherName,Class,Phone,FeeStatus\n');
              for (var s in provider.students) {
                sb.writeln('${s.rollNo},${s.name},${s.fatherName},${s.className},${s.parentPhone},${s.feeStatus}');
              }
              _shareCsv('PakSchool_Students.csv', sb.toString());
            },
          ),
          _exportTile(
            title: 'Fee Ledger (فیس ریکارڈ و لیجر)',
            subtitle: 'فیس وصولی، بقایا جات اور تاریخ ادائیگی کا رجسٹر۔',
            onTap: () {
              final sb = StringBuffer('StudentName,Class,Month,Amount,Discount,Method,ReceiptNo\n');
              for (var f in provider.feeRecords) {
                sb.writeln('${f.studentName},${f.className},${f.month},${f.amount},${f.discount},${f.paymentMethod},${f.receiptNo}');
              }
              _shareCsv('PakSchool_Fees.csv', sb.toString());
            },
          ),
          _exportTile(
            title: 'Faculty Register (اساتذہ ریکارڈ)',
            subtitle: 'اساتذہ کے نام، عہدہ، مضمون اور تنخواہ کا ریکارڈ۔',
            onTap: () {
              final sb = StringBuffer('Name,Designation,Subject,Phone,Salary\n');
              for (var st in provider.staff) {
                sb.writeln('${st.name},${st.designation},${st.subject},${st.phone},${st.monthlySalary}');
              }
              _shareCsv('PakSchool_Faculty.csv', sb.toString());
            },
          ),
        ],
      ),
    );
  }

  Widget _exportTile({required String title, required String subtitle, required VoidCallback onTap}) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
            const SizedBox(height: 4),
            Text(subtitle, style: const TextStyle(fontSize: 11, color: Color(0xFF64748B))),
            const SizedBox(height: 10),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                onPressed: onTap,
                icon: const Icon(Icons.download, color: Colors.white, size: 16),
                label: const Text('Export to Excel (.csv)', style: TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
