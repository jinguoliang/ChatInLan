package com.jone.lanchat.network

interface Scanner {
    fun scan(onResult: (address: String) -> Unit)
}