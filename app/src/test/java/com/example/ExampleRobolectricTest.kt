package com.example

import com.example.data.entity.Member
import com.example.data.entity.TransactionRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testMemberModelCreation() {
    val member = Member(
      id = 1L,
      memberId = "FZ-101",
      name = "Abdul Karim",
      mobileNumber = "01712345678",
      address = "Kalir Bazar",
      nidNumber = "19851234567890",
      nomineeName = "Fatema Begum",
      nomineeAddress = "Kalir Bazar",
      nomineeNidNumber = "19901234567890",
      loginPin = "1234"
    )

    assertEquals("FZ-101", member.memberId)
    assertEquals("Abdul Karim", member.name)
    assertEquals("1234", member.loginPin)
    assertEquals("ACTIVE", member.status)
  }

  @Test
  fun testTransactionRecordCalculation() {
    val deposit = TransactionRecord(
      id = 1L,
      memberId = "FZ-101",
      amount = 1500.0,
      type = "MONTHLY_DEPOSIT",
      month = "November",
      year = 2026,
      date = "2026-11-07"
    )

    val withdrawal = TransactionRecord(
      id = 2L,
      memberId = "FZ-101",
      amount = 500.0,
      type = "WITHDRAWAL",
      month = "November",
      year = 2026,
      date = "2026-11-07"
    )

    assertTrue(deposit.isDeposit)
    assertTrue(!withdrawal.isDeposit)
  }
}
