package com.mmoigo

import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()

  object MongoSettings {
    val db = config.getString("mongo.db")
  }

}