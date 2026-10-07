class AttendanceRecord {
  final int? id;
  final int studentId;
  final String date; // YYYY-MM-DD
  final String status; // PRESENT, ABSENT, LATE, LEAVE
  final String time;
  final String method; // BIOMETRIC, MANUAL, NFC

  AttendanceRecord({
    this.id,
    required this.studentId,
    required this.date,
    required this.status,
    this.time = '08:00 AM',
    this.method = 'MANUAL',
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'studentId': studentId,
      'date': date,
      'status': status,
      'time': time,
      'method': method,
    };
  }

  factory AttendanceRecord.fromMap(Map<String, dynamic> map) {
    return AttendanceRecord(
      id: map['id'] as int?,
      studentId: map['studentId'] as int,
      date: map['date'] ?? '',
      status: map['status'] ?? 'PRESENT',
      time: map['time'] ?? '08:00 AM',
      method: map['method'] ?? 'MANUAL',
    );
  }

  AttendanceRecord copyWith({
    int? id,
    int? studentId,
    String? date,
    String? status,
    String? time,
    String? method,
  }) {
    return AttendanceRecord(
      id: id ?? this.id,
      studentId: studentId ?? this.studentId,
      date: date ?? this.date,
      status: status ?? this.status,
      time: time ?? this.time,
      method: method ?? this.method,
    );
  }
}
