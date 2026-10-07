import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';
import '../models/student.dart';
import '../models/attendance.dart';
import '../models/fee_record.dart';
import '../models/staff.dart';
import '../models/broadcast.dart';

class DBHelper {
  static final DBHelper instance = DBHelper._init();
  static Database? _database;

  DBHelper._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB('pakschool.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(
      path,
      version: 1,
      onCreate: _createDB,
    );
  }

  Future _createDB(Database db, int version) async {
    // 1. Students Table
    await db.execute('''
      CREATE TABLE students (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        rollNo TEXT NOT NULL,
        name TEXT NOT NULL,
        fatherName TEXT NOT NULL,
        className TEXT NOT NULL,
        section TEXT NOT NULL,
        parentPhone TEXT NOT NULL,
        monthlyFee INTEGER NOT NULL,
        feeStatus TEXT NOT NULL,
        gender TEXT NOT NULL,
        bFormOrCnic TEXT,
        admissionDate TEXT
      )
    ''');

    // 2. Attendance Table
    await db.execute('''
      CREATE TABLE attendance (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        studentId INTEGER NOT NULL,
        date TEXT NOT NULL,
        status TEXT NOT NULL,
        time TEXT NOT NULL,
        method TEXT NOT NULL,
        UNIQUE(studentId, date)
      )
    ''');

    // 3. Fee Records Table
    await db.execute('''
      CREATE TABLE fees (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        studentId INTEGER NOT NULL,
        studentName TEXT NOT NULL,
        className TEXT NOT NULL,
        month TEXT NOT NULL,
        amount INTEGER NOT NULL,
        discount INTEGER NOT NULL,
        netAmount INTEGER NOT NULL,
        status TEXT NOT NULL,
        paidDate TEXT,
        paymentMethod TEXT NOT NULL,
        receiptNo TEXT NOT NULL
      )
    ''');

    // 4. Staff Table
    await db.execute('''
      CREATE TABLE staff (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        designation TEXT NOT NULL,
        qualification TEXT NOT NULL,
        phone TEXT NOT NULL,
        subject TEXT NOT NULL,
        monthlySalary INTEGER NOT NULL,
        salaryStatus TEXT NOT NULL,
        attendanceToday TEXT NOT NULL
      )
    ''');

    // 5. Broadcasts Table
    await db.execute('''
      CREATE TABLE broadcasts (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        title TEXT NOT NULL,
        targetClass TEXT NOT NULL,
        message TEXT NOT NULL,
        timestamp INTEGER NOT NULL,
        category TEXT NOT NULL,
        status TEXT NOT NULL
      )
    ''');

    // Seed Initial Data
    await _seedData(db);
  }

  Future<void> _seedData(Database db) async {
    final students = [
      Student(rollNo: "KG-01", name: "Ayaan Ali", fatherName: "Muhammad Ali", className: "KG", section: "A", parentPhone: "+923001234501", monthlyFee: 2500, feeStatus: "PAID", gender: "Male"),
      Student(rollNo: "KG-02", name: "Zoya Fatima", fatherName: "Tariq Mehmood", className: "KG", section: "A", parentPhone: "+923214567802", monthlyFee: 2500, feeStatus: "PENDING", gender: "Female"),
      Student(rollNo: "KG-03", name: "Rayyan Ahmed", fatherName: "Ahmed Raza", className: "KG", section: "A", parentPhone: "+923335551203", monthlyFee: 2500, feeStatus: "PAID", gender: "Male"),
      Student(rollNo: "NUR-01", name: "Muhammad Musa", fatherName: "Kashif Khan", className: "Nursery", section: "Rose", parentPhone: "+923459876504", monthlyFee: 2800, feeStatus: "PAID", gender: "Male"),
      Student(rollNo: "NUR-02", name: "Anaya Zahra", fatherName: "Syed Imran", className: "Nursery", section: "Rose", parentPhone: "+923017778805", monthlyFee: 2800, feeStatus: "DEFAULTER", gender: "Female"),
      Student(rollNo: "PRP-01", name: "Hassan Bilal", fatherName: "Bilal Farooq", className: "Prep", section: "Tulip", parentPhone: "+923124443306", monthlyFee: 3000, feeStatus: "PAID", gender: "Male"),
      Student(rollNo: "PRP-02", name: "Meerab Tariq", fatherName: "Tariq Jamil", className: "Prep", section: "Tulip", parentPhone: "+923361112207", monthlyFee: 3000, feeStatus: "PENDING", gender: "Female"),
      Student(rollNo: "C01-01", name: "Hamza Shah", fatherName: "Shahid Hussain", className: "Class 1", section: "A", parentPhone: "+923023334408", monthlyFee: 3200, feeStatus: "PAID", gender: "Male"),
      Student(rollNo: "C01-02", name: "Dua Fatima", fatherName: "Munir Ahmed", className: "Class 1", section: "A", parentPhone: "+923246667709", monthlyFee: 3200, feeStatus: "PAID", gender: "Female"),
      Student(rollNo: "C10-01", name: "Muhammad Ali", fatherName: "Liaqat Ali", className: "Class 10", section: "A-Science", parentPhone: "+923001122334", monthlyFee: 5000, feeStatus: "PAID", gender: "Male"),
      Student(rollNo: "C10-02", name: "Fatima Zahra", fatherName: "Syed Zafar", className: "Class 10", section: "A-Science", parentPhone: "+923214455667", monthlyFee: 5000, feeStatus: "PAID", gender: "Female"),
      Student(rollNo: "C10-03", name: "Bilal Siddiqui", fatherName: "Rashid Siddiqui", className: "Class 10", section: "B-Arts", parentPhone: "+923337788990", monthlyFee: 4800, feeStatus: "DEFAULTER", gender: "Male"),
      Student(rollNo: "FY-01", name: "Daniyal Baig", fatherName: "Mirza Baig", className: "1st Year", section: "FSc Pre-Med", parentPhone: "+923001239916", monthlyFee: 7000, feeStatus: "PAID", gender: "Male"),
      Student(rollNo: "SY-01", name: "Mustafa Hashmi", fatherName: "Hashmi Saeed", className: "2nd Year", section: "FSc Pre-Med", parentPhone: "+923019991119", monthlyFee: 7500, feeStatus: "PAID", gender: "Male"),
    ];

    for (var s in students) {
      await db.insert('students', s.toMap());
    }

    final staff = [
      StaffMember(name: "Sir Tariq Mahmood", designation: "Senior Principal", qualification: "M.Sc Physics (QAU)", phone: "+923009988771", subject: "Physics & Administration", monthlySalary: 85000),
      StaffMember(name: "Ma'am Sadia Noor", designation: "Vice Principal", qualification: "M.A English (PU)", phone: "+923214433221", subject: "English Literature", monthlySalary: 65000),
      StaffMember(name: "Sir Asif Javed", designation: "Senior Math Teacher", qualification: "M.Sc Mathematics", phone: "+923337766551", subject: "Mathematics", monthlySalary: 55000),
      StaffMember(name: "Sir Farhan Qureshi", designation: "IT & Computer Incharge", qualification: "BS CS (FAST)", phone: "+923129988001", subject: "Computer Science", monthlySalary: 52000),
    ];

    for (var st in staff) {
      await db.insert('staff', st.toMap());
    }

    final broadcasts = [
      BroadcastAlert(
        title: "Daily Absentee Alert",
        targetClass: "All Classes",
        message: "Respected Parents, your child was marked ABSENT today. Please contact the school office to confirm reason of absence.",
        timestamp: DateTime.now().millisecondsSinceEpoch,
        category: "ABSENTEE",
      ),
      BroadcastAlert(
        title: "Monthly Fee Reminder",
        targetClass: "All Classes",
        message: "Dear Parents, monthly tuition fee is due. Please submit dues on time to avoid late surcharge.",
        timestamp: DateTime.now().millisecondsSinceEpoch - 3600000,
        category: "FEE_REMINDER",
      ),
    ];

    for (var b in broadcasts) {
      await db.insert('broadcasts', b.toMap());
    }
  }

  // --- Student Operations ---
  Future<List<Student>> getStudentsByClass(String className) async {
    final db = await database;
    final List<Map<String, dynamic>> maps = (className == 'All Classes')
        ? await db.query('students', orderBy: 'rollNo ASC')
        : await db.query('students', where: 'className = ?', whereArgs: [className], orderBy: 'rollNo ASC');
    return maps.map((e) => Student.fromMap(e)).toList();
  }

  Future<int> insertStudent(Student student) async {
    final db = await database;
    return await db.insert('students', student.toMap());
  }

  Future<int> updateStudentFeeStatus(int studentId, String status) async {
    final db = await database;
    return await db.update(
      'students',
      {'feeStatus': status},
      where: 'id = ?',
      whereArgs: [studentId],
    );
  }

  // --- Attendance Operations ---
  Future<List<AttendanceRecord>> getAttendanceForDate(String date) async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query('attendance', where: 'date = ?', whereArgs: [date]);
    return maps.map((e) => AttendanceRecord.fromMap(e)).toList();
  }

  Future<void> markAttendance(int studentId, String date, String status, {String method = 'MANUAL'}) async {
    final db = await database;
    await db.insert(
      'attendance',
      {
        'studentId': studentId,
        'date': date,
        'status': status,
        'time': '08:00 AM',
        'method': method,
      },
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
  }

  // --- Fee Operations ---
  Future<List<FeeRecord>> getFeeRecords() async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query('fees', orderBy: 'id DESC');
    return maps.map((e) => FeeRecord.fromMap(e)).toList();
  }

  Future<int> insertFeeRecord(FeeRecord record) async {
    final db = await database;
    return await db.insert('fees', record.toMap());
  }

  // --- Staff Operations ---
  Future<List<StaffMember>> getAllStaff() async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query('staff', orderBy: 'name ASC');
    return maps.map((e) => StaffMember.fromMap(e)).toList();
  }

  Future<int> insertStaff(StaffMember staff) async {
    final db = await database;
    return await db.insert('staff', staff.toMap());
  }

  // --- Broadcast Operations ---
  Future<List<BroadcastAlert>> getAllBroadcasts() async {
    final db = await database;
    final List<Map<String, dynamic>> maps = await db.query('broadcasts', orderBy: 'timestamp DESC');
    return maps.map((e) => BroadcastAlert.fromMap(e)).toList();
  }

  Future<int> insertBroadcast(BroadcastAlert alert) async {
    final db = await database;
    return await db.insert('broadcasts', alert.toMap());
  }
}
