class FeeRecord {
  final int? id;
  final int studentId;
  final String studentName;
  final String className;
  final String month;
  final int amount;
  final int discount;
  final int netAmount;
  final String status; // PAID, PENDING, OVERDUE
  final String? paidDate;
  final String paymentMethod;
  final String receiptNo;

  FeeRecord({
    this.id,
    required this.studentId,
    required this.studentName,
    required this.className,
    required this.month,
    required this.amount,
    this.discount = 0,
    required this.netAmount,
    this.status = 'PENDING',
    this.paidDate,
    this.paymentMethod = 'Cash',
    this.receiptNo = 'PK-0000',
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'studentId': studentId,
      'studentName': studentName,
      'className': className,
      'month': month,
      'amount': amount,
      'discount': discount,
      'netAmount': netAmount,
      'status': status,
      'paidDate': paidDate,
      'paymentMethod': paymentMethod,
      'receiptNo': receiptNo,
    };
  }

  factory FeeRecord.fromMap(Map<String, dynamic> map) {
    return FeeRecord(
      id: map['id'] as int?,
      studentId: map['studentId'] as int,
      studentName: map['studentName'] ?? '',
      className: map['className'] ?? '',
      month: map['month'] ?? '',
      amount: map['amount'] ?? 0,
      discount: map['discount'] ?? 0,
      netAmount: map['netAmount'] ?? 0,
      status: map['status'] ?? 'PENDING',
      paidDate: map['paidDate'],
      paymentMethod: map['paymentMethod'] ?? 'Cash',
      receiptNo: map['receiptNo'] ?? 'PK-0000',
    );
  }
}
