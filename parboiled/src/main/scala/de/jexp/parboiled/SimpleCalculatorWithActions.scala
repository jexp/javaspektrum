package de.jexp.parboiled

import org.parboiled.scala._
import org.parboiled.scala.rules.Rule1

class SimpleCalculatorWithActions extends Parser {

  // Parser-Aktion addidert, subtrahiert Terme
  def Expression: Rule1[Int] = rule {
    Term ~ zeroOrMore(
      "+" ~ Term ~~> ((a:Int, b) => a + b)
        | "-" ~ Term ~~> ((a:Int, b) => a - b)
    )
  }

  // Parser-Aktion multipliziert / dividiert Terme
  def Term = rule {
    Factor ~ zeroOrMore(
      "*" ~ Factor ~~> ((a:Int, b) => a * b)
        | "/" ~ Factor ~~> ((a:Int, b) => a / b)
    )
  }

  def Factor = rule { Number | Parens }

  def Parens = rule { "(" ~ Expression ~ ")" }

  // Parser-Aktion wandelt String in Integer
  def Number = rule { oneOrMore("0" - "9") ~> (_.toInt) }
}
