class StaffMember {
  final int? id;
  final String name;
  final String designation;
  final String qualification;
  final String phone;
  final String subject;
  final int monthlySalary;
  final String salaryStatus; // PAID, PARTIAL, PENDING, UNPAID
  final String attendanceToday; // PRESENT, ABSENT, LEAVE
  final int paidAmount;
  final int remainingBalance;

  StaffMember({
    this.id,
    required this.name,
    required this.designation,
    required this.qualification,
    required this.phone,
    required this.subject,
    required this.monthlySalary,
    this.salaryStatus = 'PAID',
    this.attendanceToday = 'PRESENT',
    this.paidAmount = 0,
    this.remainingBalance = 0,
  });

  int get effectiveRemaining => paidAmount > 0 ? (monthlySalary - paidAmount).clamp(0, monthlySalary) : (salaryStatus == 'PAID' ? 0 : monthlySalary);

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'name': name,
      'designation': designation,
      'qualification': qualification,
      'phone': phone,
      'subject': subject,
      'monthlySalary': monthlySalary,
      'salaryStatus': salaryStatus,
      'attendanceToday': attendanceToday,
      'paidAmount': paidAmount,
      'remainingBalance': remainingBalance,
    };
  }

  factory StaffMember.fromMap(Map<String, dynamic> map) {
    final sal = map['monthlySalary'] ?? 0;
    final paid = map['paidAmount'] ?? (map['salaryStatus'] == 'PAID' ? sal : 0);
    return StaffMember(
      id: map['id'] as int?,
      name: map['name'] ?? '',
      designation: map['designation'] ?? '',
      qualification: map['qualification'] ?? '',
      phone: map['phone'] ?? '',
      subject: map['subject'] ?? '',
      monthlySalary: sal,
      salaryStatus: map['salaryStatus'] ?? 'PAID',
      attendanceToday: map['attendanceToday'] ?? 'PRESENT',
      paidAmount: paid,
      remainingBalance: map['remainingBalance'] ?? (sal - paid),
    );
  }

  StaffMember copyWith({
    int? id,
    String? name,
    String? designation,
    String? qualification,
    String? phone,
    String? subject,
    int? monthlySalary,
    String? salaryStatus,
    String? attendanceToday,
    int? paidAmount,
    int? remainingBalance,
  }) {
    return StaffMember(
      id: id ?? this.id,
      name: name ?? this.name,
      designation: designation ?? this.designation,
      qualification: qualification ?? this.qualification,
      phone: phone ?? this.phone,
      subject: subject ?? this.subject,
      monthlySalary: monthlySalary ?? this.monthlySalary,
      salaryStatus: salaryStatus ?? this.salaryStatus,
      attendanceToday: attendanceToday ?? this.attendanceToday,
      paidAmount: paidAmount ?? this.paidAmount,
      remainingBalance: remainingBalance ?? this.remainingBalance,
    );
  }
}
