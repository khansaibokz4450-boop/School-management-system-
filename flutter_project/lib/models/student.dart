const List<String> pakClasses = [
  'KG',
  'Nursery',
  'Prep',
  'Class 1',
  'Class 2',
  'Class 3',
  'Class 4',
  'Class 5',
  'Class 6',
  'Class 7',
  'Class 8',
  'Class 9',
  'Class 10',
  '1st Year',
  '2nd Year',
];

class Student {
  final int? id;
  final String rollNo;
  final String name;
  final String fatherName;
  final String className;
  final String section;
  final String parentPhone;
  final int monthlyFee;
  final String feeStatus; // PAID, PENDING, DEFAULTER
  final String gender;
  final String bFormOrCnic;
  final String admissionDate;

  Student({
    this.id,
    required this.rollNo,
    required this.name,
    required this.fatherName,
    required this.className,
    this.section = 'A',
    required this.parentPhone,
    this.monthlyFee = 3500,
    this.feeStatus = 'PAID',
    this.gender = 'Male',
    this.bFormOrCnic = '',
    this.admissionDate = '2026-08-15',
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'rollNo': rollNo,
      'name': name,
      'fatherName': fatherName,
      'className': className,
      'section': section,
      'parentPhone': parentPhone,
      'monthlyFee': monthlyFee,
      'feeStatus': feeStatus,
      'gender': gender,
      'bFormOrCnic': bFormOrCnic,
      'admissionDate': admissionDate,
    };
  }

  factory Student.fromMap(Map<String, dynamic> map) {
    return Student(
      id: map['id'] as int?,
      rollNo: map['rollNo'] ?? '',
      name: map['name'] ?? '',
      fatherName: map['fatherName'] ?? '',
      className: map['className'] ?? '',
      section: map['section'] ?? 'A',
      parentPhone: map['parentPhone'] ?? '',
      monthlyFee: map['monthlyFee'] ?? 3500,
      feeStatus: map['feeStatus'] ?? 'PAID',
      gender: map['gender'] ?? 'Male',
      bFormOrCnic: map['bFormOrCnic'] ?? '',
      admissionDate: map['admissionDate'] ?? '',
    );
  }

  Student copyWith({
    int? id,
    String? rollNo,
    String? name,
    String? fatherName,
    String? className,
    String? section,
    String? parentPhone,
    int? monthlyFee,
    String? feeStatus,
    String? gender,
    String? bFormOrCnic,
    String? admissionDate,
  }) {
    return Student(
      id: id ?? this.id,
      rollNo: rollNo ?? this.rollNo,
      name: name ?? this.name,
      fatherName: fatherName ?? this.fatherName,
      className: className ?? this.className,
      section: section ?? this.section,
      parentPhone: parentPhone ?? this.parentPhone,
      monthlyFee: monthlyFee ?? this.monthlyFee,
      feeStatus: feeStatus ?? this.feeStatus,
      gender: gender ?? this.gender,
      bFormOrCnic: bFormOrCnic ?? this.bFormOrCnic,
      admissionDate: admissionDate ?? this.admissionDate,
    );
  }
}
