import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../database/db_helper.dart';
import '../models/student.dart';
import '../models/attendance.dart';
import '../models/fee_record.dart';
import '../models/staff.dart';
import '../models/broadcast.dart';

class SchoolProvider extends ChangeNotifier {
  final DBHelper _db = DBHelper.instance;

  String _selectedClass = 'KG';
  String get selectedClass => _selectedClass;

  List<Student> _students = [];
  List<Student> get students => _students;

  Map<int, AttendanceRecord> _todayAttendance = {};
  Map<int, AttendanceRecord> get todayAttendance => _todayAttendance;

  List<FeeRecord> _feeRecords = [];
  List<FeeRecord> get feeRecords => _feeRecords;

  List<StaffMember> _staff = [];
  List<StaffMember> get staff => _staff;

  List<BroadcastAlert> _broadcasts = [];
  List<BroadcastAlert> get broadcasts => _broadcasts;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String get todayDate => DateFormat('yyyy-MM-dd').format(DateTime.now());

  SchoolProvider() {
    loadAllData();
  }

  void setSelectedClass(String className) {
    _selectedClass = className;
    loadStudents();
    notifyListeners();
  }

  Future<void> loadAllData() async {
    _isLoading = true;
    notifyListeners();

    await loadStudents();
    await loadTodayAttendance();
    await loadFeeRecords();
    await loadStaff();
    await loadBroadcasts();

    _isLoading = false;
    notifyListeners();
  }

  Future<void> loadStudents() async {
    _students = await _db.getStudentsByClass(_selectedClass);
    notifyListeners();
  }

  Future<void> addStudent(Student newStudent) async {
    await _db.insertStudent(newStudent);
    await loadStudents();
  }

  Future<void> loadTodayAttendance() async {
    final list = await _db.getAttendanceForDate(todayDate);
    _todayAttendance = {for (var item in list) item.studentId: item};
    notifyListeners();
  }

  Future<void> markAttendance(int studentId, String status) async {
    await _db.markAttendance(studentId, todayDate, status);
    await loadTodayAttendance();
  }

  Future<void> markAllPresent() async {
    for (var s in _students) {
      if (s.id != null) {
        await _db.markAttendance(s.id!, todayDate, 'PRESENT');
      }
    }
    await loadTodayAttendance();
  }

  Future<void> loadFeeRecords() async {
    _feeRecords = await _db.getFeeRecords();
    notifyListeners();
  }

  Future<void> recordFeePayment({
    required Student student,
    required String month,
    required int amount,
    required int discount,
    required String paymentMethod,
  }) async {
    final net = amount - discount;
    final record = FeeRecord(
      studentId: student.id!,
      studentName: student.name,
      className: student.className,
      month: month,
      amount: amount,
      discount: discount,
      netAmount: net,
      status: 'PAID',
      paidDate: DateFormat('dd MMM yyyy').format(DateTime.now()),
      paymentMethod: paymentMethod,
      receiptNo: 'PK-${1000 + _feeRecords.length + 1}',
    );

    await _db.insertFeeRecord(record);
    await _db.updateStudentFeeStatus(student.id!, 'PAID');
    await loadStudents();
    await loadFeeRecords();
  }

  Future<void> loadStaff() async {
    _staff = await _db.getAllStaff();
    notifyListeners();
  }

  Future<void> addStaff(StaffMember newStaff) async {
    await _db.insertStaff(newStaff);
    await loadStaff();
  }

  Future<void> loadBroadcasts() async {
    _broadcasts = await _db.getAllBroadcasts();
    notifyListeners();
  }

  Future<void> sendBroadcast({
    required String title,
    required String targetClass,
    required String message,
  }) async {
    final alert = BroadcastAlert(
      title: title,
      targetClass: targetClass,
      message: message,
      timestamp: DateTime.now().millisecondsSinceEpoch,
    );
    await _db.insertBroadcast(alert);
    await loadBroadcasts();
  }

  // Summary Metrics
  int get totalStudents => _students.length;
  int get presentCount => _students.where((s) => _todayAttendance[s.id]?.status == 'PRESENT').length;
  int get totalCollectedFee => _feeRecords.where((f) => f.status == 'PAID').fold(0, (sum, item) => sum + item.netAmount);
}
