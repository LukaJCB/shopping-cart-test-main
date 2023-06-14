package com.siriusxm.example.cart

import munit.CatsEffectSuite
import cats.effect.IO

class CartTests extends CatsEffectSuite {

  val testPricingService: PricingDataService = new PricingDataService {
    def fetch(name: String): IO[Option[Pricing]] = name match {
      case "cornflakes" => IO.pure(Some(Pricing("cornflakes", 4.0)))
      case "cheerios" => IO.pure(Some(Pricing("cheerios", 3.15)))
      case _ => IO.pure(None)
    }
  }

  val cartService = new CartService(testPricingService)

  test("Empty cart should be 0 for subtotal") {
    cartService.getSubtotal(Cart.empty).map(result => assertEquals(result, 0))
  }

  test("Multiple items should arrive at the correct subtotal") {
    val cart = Cart.empty.add("cornflakes", 2).add("cheerios", 1)
    
    cartService.getSubtotal(cart).map(result => assertEquals(result, 1115))
  }

  test("Adding an item twice should be the same as adding two of it") {
    val cart1 = Cart.empty.add("cornflakes", 2)
    val cart2 = Cart.empty.add("cornflakes", 1).add("cornflakes", 1)
    
    for {
      result1 <- cartService.getSubtotal(cart1)
      result2 <- cartService.getSubtotal(cart2)
    } yield assertEquals(result1, result2)
  }

  test("Taxes should be rounded up") {
    val result = cartService.getTaxes(100)
    assertEquals(result, 13)
  }

  test("Value in decimal should be able to be converted into cents and back") {
    val decimal = 42.56
    val result = CurrencyConversions.toDecimal(CurrencyConversions.toCents(decimal))
    assertEquals(result, decimal)
  }

  test("Value in cents should be able to be converted into decimal and back") {
    val cents = 39999
    val result = CurrencyConversions.toCents(CurrencyConversions.toDecimal(cents))
    assertEquals(result, cents)
  }

  test("Calculation should fail if a price can't be found") {
    cartService.getSubtotal(Cart.empty.add("nothing", 1)).attempt.map(result => assert(result.isLeft))
  }


}
