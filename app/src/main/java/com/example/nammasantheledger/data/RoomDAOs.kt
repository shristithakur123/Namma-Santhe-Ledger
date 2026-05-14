package com.example.nammasantheledger.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ─── Customer DAO ─────────────────────────────────────────────────────────────
@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE name = :name LIMIT 1")
    suspend fun getCustomerByName(name: String): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>
}

// ─── Transaction DAO ──────────────────────────────────────────────────────────
@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE customerName = :name ORDER BY createdAt DESC")
    fun getTransactionsByCustomer(name: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE customerName LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT SUM(amount) FROM transactions WHERE customerName = :name AND isCredit = 1")
    suspend fun getTotalCreditByCustomer(name: String): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE customerName = :name AND isCredit = 0")
    suspend fun getTotalPaymentByCustomer(name: String): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE isCredit = 1")
    suspend fun getTotalCredit(): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE isCredit = 0")
    suspend fun getTotalPayment(): Double?
}

// ─── User DAO ─────────────────────────────────────────────────────────────────
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE mobileNumber = :mobile AND password = :password LIMIT 1")
    suspend fun login(mobile: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE mobileNumber = :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): UserEntity?
}