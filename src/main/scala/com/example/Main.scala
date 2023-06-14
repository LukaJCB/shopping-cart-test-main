package com.example

import cats.syntax.all._
import cats.effect.{IOApp, IO}
import com.siriusxm.example.cart._
import org.http4s.client.Client
import org.http4s.jdkhttpclient.JdkHttpClient
import org.http4s.implicits._


object Main extends IOApp.Simple {

  def run: IO[Unit] =
    for {
      client <- JdkHttpClient.simple[IO]
      pricingService = new CartService(
        new LivePricingDataService(
          client, 
          uri"https://raw.githubusercontent.com/mattjanks16/shopping-cart-test-data/main/"
        )
      )
      cart = Cart.empty.add("cornflakes", 2).add("weetabix", 1)
      subtotal <- pricingService.getSubtotal(cart)
      _ <- IO.println(s"Subtotal = ${CurrencyConversions.toDecimal(subtotal)}")
      taxes = pricingService.getTaxes(subtotal)
      _ <- IO.println(s"Tax = ${CurrencyConversions.toDecimal(taxes)}")
      total = subtotal + taxes
      _ <- IO.println(s"Total = ${CurrencyConversions.toDecimal(total)}")
    } yield ()

}
