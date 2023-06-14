package com.siriusxm.example.cart

import cats.effect.IO
import cats.syntax.all._

class CartService(pricingService: PricingDataService) {
  def getSubtotal(cart: Cart): IO[Int] =
    cart.items.toList.foldMapA {
      case (item, quantity) => pricingService.fetch(item).flatMap {
        case None => IO.raiseError(new Exception(s"Pricing not available for item: $item"))
        case Some(pricing) => IO.pure(CurrencyConversions.toCents(pricing.price) * quantity)
      }
    }

  def getTaxes(subtotal: Int): Int =
    Math.ceil(subtotal.toDouble * 0.125).toInt
}
