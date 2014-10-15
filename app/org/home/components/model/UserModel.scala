package org.home.components.model

case class UserModel(id: String, login: String, name: String, password: String, docType: String = "user")
