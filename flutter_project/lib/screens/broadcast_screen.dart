import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';
import '../providers/school_provider.dart';

class BroadcastScreen extends StatefulWidget {
  final VoidCallback onBack;

  const BroadcastScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  State<BroadcastScreen> createState() => _BroadcastScreenState();
}

class _BroadcastScreenState extends State<BroadcastScreen> {
  final _titleController = TextEditingController(text: 'Urgent Announcement');
  final _msgController = TextEditingController(
    text: 'Respected Parents, please note that school timings tomorrow will be 08:30 AM to 01:30 PM due to weather conditions.',
  );

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
        title: const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('WhatsApp & SMS', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 18, fontWeight: FontWeight.bold)),
            Text('Class-wise Alerts & Announcements', style: TextStyle(color: Color(0xFF64748B), fontSize: 11)),
          ],
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Quick Templates Card
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
              child: Padding(
                padding: const EdgeInsets.all(14),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('QUICK TEMPLATES (ONE-CLICK SELECT)', style: TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Color(0xFF16A34A))),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      children: [
                        ActionChip(
                          label: const Text('Absentee Alert', style: TextStyle(fontSize: 11)),
                          backgroundColor: const Color(0xFFFEE2E2),
                          onPressed: () {
                            setState(() {
                              _titleController.text = 'Student Absence Alert';
                              _msgController.text = 'Respected Parent, your child was marked ABSENT today. Please contact school administration to confirm.';
                            });
                          },
                        ),
                        ActionChip(
                          label: const Text('Fee Reminder', style: TextStyle(fontSize: 11)),
                          backgroundColor: const Color(0xFFFEF3C7),
                          onPressed: () {
                            setState(() {
                              _titleController.text = 'Monthly Fee Reminder';
                              _msgController.text = 'Dear Parent, school tuition fee is due. Kindly deposit dues before the due date to avoid late surcharge.';
                            });
                          },
                        ),
                        ActionChip(
                          label: const Text('Holiday Notice', style: TextStyle(fontSize: 11)),
                          backgroundColor: const Color(0xFFCCFBF1),
                          onPressed: () {
                            setState(() {
                              _titleController.text = 'Notification: Holiday / Timings';
                              _msgController.text = 'School will remain closed tomorrow as per official Government notification. Study homework is on the portal.';
                            });
                          },
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 16),

            // Composer Card
            Card(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('COMPOSE MESSAGE', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF64748B))),
                    const SizedBox(height: 12),
                    TextField(
                      controller: _titleController,
                      decoration: const InputDecoration(labelText: 'Announcement Title', border: OutlineInputBorder()),
                    ),
                    const SizedBox(height: 12),
                    TextField(
                      controller: _msgController,
                      decoration: const InputDecoration(labelText: 'Broadcast Message', border: OutlineInputBorder()),
                      maxLines: 4,
                    ),
                    const SizedBox(height: 14),
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton.icon(
                        onPressed: () async {
                          final text = '*${_titleController.text}*\n\n${_msgController.text}\n\n— PakSchool SMS Administration';
                          await provider.sendBroadcast(
                            title: _titleController.text,
                            targetClass: provider.selectedClass,
                            message: _msgController.text,
                          );
                          final uri = Uri.parse('whatsapp://send?text=${Uri.encodeComponent(text)}');
                          if (await canLaunchUrl(uri)) {
                            await launchUrl(uri);
                          }
                        },
                        icon: const Icon(Icons.send),
                        label: const Text('Send WhatsApp Broadcast'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF25D366),
                          foregroundColor: Colors.white,
                          padding: const EdgeInsets.symmetric(vertical: 12),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 16),
            const Text('BROADCAST HISTORY', style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF64748B))),
            const SizedBox(height: 8),

            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: provider.broadcasts.length,
              itemBuilder: (context, index) {
                final b = provider.broadcasts[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 8),
                  child: ListTile(
                    leading: const CircleAvatar(backgroundColor: Color(0xFFDCFCE7), child: Icon(Icons.campaign, color: Color(0xFF16A34A))),
                    title: Text(b.title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                    subtitle: Text(b.message, maxLines: 2, overflow: TextOverflow.ellipsis, style: const TextStyle(fontSize: 11)),
                    trailing: const Icon(Icons.check_circle, color: Color(0xFF16A34A), size: 16),
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
