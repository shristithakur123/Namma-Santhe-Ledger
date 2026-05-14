package com.example.nammasantheledger.data


import androidx.room.Entity
import androidx.room.PrimaryKey

// ─── Customer Table ───────────────────────────────────────────────────────────
@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id       : Int    = 0,
    val name     : String,
    val phone    : String = "",
    val createdAt: Long   = System.currentTimeMillis()
)

// ─── Transaction Table ────────────────────────────────────────────────────────
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id           : Int     = 0,
    val customerName : String,
    val type         : String,  // "Credit (Udari)" or "Payment"
    val amount       : Double,
    val notes        : String  = "",
    val isCredit     : Boolean,
    val createdAt    : Long    = System.currentTimeMillis()
)

// ─── User Table (for login/register) ─────────────────────────────────────────
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id          : Int    = 0,
    val fullName    : String,
    val mobileNumber: String,
    val password    : String,
    val createdAt   : Long   = System.currentTimeMillis()
)