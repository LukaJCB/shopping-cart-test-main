package com.siriusxm.example.cart

import scala.collection.immutable.Map

case class Cart(items: Map[String, Int]) {
  def add(item: String, amount: Int): Cart = 
    Cart(items.updatedWith(item)(quantity => Some(quantity.getOrElse(0) + amount)))
}

object Cart {
  val empty = Cart(Map.empty)
}