class BroadcastAlert {
  final int? id;
  final String title;
  final String targetClass;
  final String message;
  final int timestamp;
  final String category;
  final String status;

  BroadcastAlert({
    this.id,
    required this.title,
    required this.targetClass,
    required this.message,
    required this.timestamp,
    this.category = 'ANNOUNCEMENT',
    this.status = 'SENT',
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'title': title,
      'targetClass': targetClass,
      'message': message,
      'timestamp': timestamp,
      'category': category,
      'status': status,
    };
  }

  factory BroadcastAlert.fromMap(Map<String, dynamic> map) {
    return BroadcastAlert(
      id: map['id'] as int?,
      title: map['title'] ?? '',
      targetClass: map['targetClass'] ?? '',
      message: map['message'] ?? '',
      timestamp: map['timestamp'] ?? 0,
      category: map['category'] ?? 'ANNOUNCEMENT',
      status: map['status'] ?? 'SENT',
    );
  }
}
