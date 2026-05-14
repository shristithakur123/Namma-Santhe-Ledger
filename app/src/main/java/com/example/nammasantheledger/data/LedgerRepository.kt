package com.example.nammasantheledger.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class LedgerRepository(private val db: AppDatabase) {

    private val customerDao     = db.customerDao()
    private val transactionDao  = db.transactionDao()
    private val userDao         = db.userDao()

    // ─── Customers ────────────────────────────────────────────────────────────
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()

    suspend fun addCustomer(name: String, phone: String = "") {
        customerDao.insertCustomer(CustomerEntity(name = name, phone = phone))
    }

    suspend fun deleteCustomer(customer: CustomerEntity) {
        customerDao.deleteCustomer(customer)
    }

    fun searchCustomers(query: String): Flow<List<CustomerEntity>> {
        return customerDao.searchCustomers(query)
    }

    // ─── Transactions ─────────────────────────────────────────────────────────
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getTransactionsByCustomer(name: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCustomer(name)
    }

    suspend fun addTransaction(
        customerName : String,
        type         : String,
        amount       : Double,
        notes        : String,
        isCredit     : Boolean
    ) {
        // Also auto-add customer if not exists
        val existing = customerDao.getCustomerByName(customerName)
        if (existing == null) {
            customerDao.insertCustomer(CustomerEntity(name = customerName))
        }

        transactionDao.insertTransaction(
            TransactionEntity(
                customerName = customerName,
                type         = type,
                amount       = amount,
                notes        = notes,
                isCredit     = isCredit
            )
        )
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun getTotalCredit(): Double = transactionDao.getTotalCredit() ?: 0.0
    suspend fun getTotalPayment(): Double = transactionDao.getTotalPayment() ?: 0.0

    suspend fun getCustomerCredit(name: String): Double =
        transactionDao.getTotalCreditByCustomer(name) ?: 0.0

    suspend fun getCustomerPayment(name: String): Double =
        transactionDao.getTotalPaymentByCustomer(name) ?: 0.0

    // ─── Users ────────────────────────────────────────────────────────────────
    suspend fun registerUser(fullName: String, mobile: String, password: String) {
        userDao.insertUser(
            UserEntity(
                fullName     = fullName,
                mobileNumber = mobile,
                password     = password
            )
        )
    }

    suspend fun loginUser(mobile: String, password: String): UserEntity? {
        return userDao.login(mobile, password)
    }

    suspend fun getUserByMobile(mobile: String): UserEntity? {
        return userDao.getUserByMobile(mobile)
    }

    // ─── Helper: format date ──────────────────────────────────────────────────
    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMM yyyy · hh:mm a", Locale.getDefault())
            .format(Date(timestamp))
    }
}