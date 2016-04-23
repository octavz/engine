package org.home.models

/**
  * Created by octav on 21.04.2016.
  */
case class ResState(id: String
                    , name: String
                    , itemType: Int
                    , props: Map[String, String]) extends GenericState {}
