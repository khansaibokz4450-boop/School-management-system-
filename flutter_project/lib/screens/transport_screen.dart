import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class TransportScreen extends StatelessWidget {
  final VoidCallback onBack;

  const TransportScreen({Key? key, required this.onBack}) : super(key: key);

  final List<Map<String, dynamic>> _routes = const [
    {'route': 'Main City & Colony (روٹ 1: مین سٹی)', 'driver': 'Chaudhry Riaz', 'phone': '+923012345671', 'veh': 'ICT-LE-4580 (Hiace)', 'fee': 2500},
    {'route': 'West Canal & Satellite Town (روٹ 2: نہر پل)', 'driver': 'Ustad Gulzar', 'phone': '+923023456782', 'veh': 'LHR-MN-9921 (Coaster)', 'fee': 2800},
    {'route': 'East Bypass & Cantt Road (روٹ 3: کینٹ)', 'driver': 'Malik Aslam', 'phone': '+923034567893', 'veh': 'RWP-AB-3342 (Bolan)', 'fee': 2200},
    {'route': 'Airport Road & Garden Town (روٹ 4: ایئرپورٹ)', 'driver': 'Mama Zahoor', 'phone': '+923045678904', 'veh': 'PES-CD-1120 (Hiace AC)', 'fee': 3000},
  ];

  @override
  Widget build(BuildContext context) {
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
        title: const Text('Transport & School Van', style: TextStyle(color: Color(0xFF1E40AF), fontSize: 18, fontWeight: FontWeight.bold)),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _routes.length,
        itemBuilder: (context, index) {
          final r = _routes[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
            child: Padding(
              padding: const EdgeInsets.all(14),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(r['route'], style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14)),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                        decoration: BoxDecoration(color: const Color(0xFFDCFCE7), borderRadius: BorderRadius.circular(6)),
                        child: Text('Rs. ${r['fee']}/mo', style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF16A34A), fontSize: 11)),
                      ),
                    ],
                  ),
                  const SizedBox(height: 6),
                  Text('Vehicle: ${r['veh']}', style: const TextStyle(fontSize: 12, color: Color(0xFF475569))),
                  Text('Driver: ${r['driver']} (${r['phone']})', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Color(0xFF1E40AF))),
                  const SizedBox(height: 10),
                  Row(
                    children: [
                      Expanded(
                        child: OutlinedButton.icon(
                          onPressed: () => launchUrl(Uri.parse('tel:${r['phone']}')),
                          icon: const Icon(Icons.call, size: 14),
                          label: const Text('Call Driver', style: TextStyle(fontSize: 11)),
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: ElevatedButton.icon(
                          style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF16A34A)),
                          onPressed: () {
                            final msg = '🚌 اسکول وین الرٹ: ${r['route']} کی وین روانہ ہو چکی ہے۔ ڈرائیور: ${r['driver']} (${r['phone']})';
                            launchUrl(Uri.parse('https://api.whatsapp.com/send?text=${Uri.encodeComponent(msg)}'), mode: LaunchMode.externalApplication);
                          },
                          icon: const Icon(Icons.send, size: 14, color: Colors.white),
                          label: const Text('وین الرٹ', style: TextStyle(fontSize: 11, color: Colors.white, fontWeight: FontWeight.bold)),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
