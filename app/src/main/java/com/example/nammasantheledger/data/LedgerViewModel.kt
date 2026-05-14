package com.example.nammasantheledger.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LedgerRepository(
        AppDatabase.getInstance(application)
    )

    // ─── Live Data Streams ────────────────────────────────────────────────────

    // All customers as StateFlow — UI observes this
    val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
        .stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )

    // All transactions as StateFlow — UI observes this
    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )

    // ─── Login State ──────────────────────────────────────────────────────────
    var loggedInUser: UserEntity? = null
        private set

    // ─── Customer Actions ─────────────────────────────────────────────────────
    fun addCustomer(name: String, phone: String = "") {
        viewModelScope.launch {
            repository.addCustomer(name, phone)
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    // ─── Transaction Actions ──────────────────────────────────────────────────
    fun addTransaction(
        customerName : String,
        type         : String,
        amount       : Double,
        notes        : String,
        isCredit     : Boolean
    ) {
        viewModelScope.launch {
            repository.addTransaction(customerName, type, amount, notes, isCredit)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // ─── Get transactions for one customer ────────────────────────────────────
    fun getCustomerTransactions(name: String): StateFlow<List<TransactionEntity>> {
        return repository.getTransactionsByCustomer(name)
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // ─── Computed totals ──────────────────────────────────────────────────────
    fun getTotals(
        txList: List<TransactionEntity>
    ): Triple<Double, Double, Double> {
        val credit  = txList.filter { it.isCredit }.sumOf { it.amount }
        val payment = txList.filter { !it.isCredit }.sumOf { it.amount }
        val net     = credit - payment
        return Triple(credit, payment, net)
    }

    fun formatAmount(value: Double): String = "₹ ${"%.0f".format(value)}"

    fun formatDate(timestamp: Long): String = repository.formatDate(timestamp)

    // ─── Auth Actions ─────────────────────────────────────────────────────────
    fun registerUser(
        fullName : String,
        mobile   : String,
        password : String,
        onSuccess: () -> Unit,
        onError  : (String) -> Unit
    ) {
        viewModelScope.launch {
            val existing = repository.getUserByMobile(mobile)
            if (existing != null) {
                onError("Mobile number already registered")
            } else {
                repository.registerUser(fullName, mobile, password)
                onSuccess()
            }
        }
    }

    fun loginUser(
        mobile   : String,
        password : String,
        onSuccess: () -> Unit,
        onError  : (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = repository.loginUser(mobile, password)
            if (user != null) {
                loggedInUser = user
                onSuccess()
            } else {
                onError("Invalid mobile number or password")
            }
        }
    }
}