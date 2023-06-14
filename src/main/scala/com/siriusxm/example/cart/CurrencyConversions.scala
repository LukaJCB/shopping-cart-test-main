package com.siriusxm.example.cart

object CurrencyConversions {
  
  def toCents(decimalPrice: Double): Int =
    Math.ceil(decimalPrice * 100).toInt

  def toDecimal(centPrice: Int): Double =
    centPrice.toDouble / 100
}
