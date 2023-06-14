package com.siriusxm.example.cart

import cats.effect.IO
import org.http4s.client.Client
import org.http4s.{Uri, Request, Method}

case class Pricing(title: String, price: Double)

trait PricingDataService {
  def fetch(name: String): IO[Option[Pricing]]
}

class LivePricingDataService(client: Client[IO], baseUri: Uri) extends PricingDataService {

  import org.http4s.circe.CirceEntityDecoder._
  import io.circe.generic.auto._

  def fetch(name: String): IO[Option[Pricing]] = 
    client.expectOption[Pricing](Request(Method.GET, baseUri / s"$name.json"))
}