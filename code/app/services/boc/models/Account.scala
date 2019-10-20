package services.boc.models

import play.api.libs.json.Json

object Account {
  implicit val jsonFormat = Json.format[Account]
}

case class Account(
                    bankId: String,
                    accountId: String,
                    accountAlias: String,
                    accountType: String,
                    accountName: String,
                    IBAN: String,
                    currency: String,
                    infoTimeStamp: String,
                    interestRate: Double,
                    maturityDate: String,
                    lastPaymentDate: String,
                    nextPaymentDate: String,
                    remainingInstallments: Double,
                    balances: Seq[Balance]
                  )
