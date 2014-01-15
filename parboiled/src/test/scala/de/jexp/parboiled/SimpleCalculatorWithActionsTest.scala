package de.jexp.parboiled

import org.parboiled.scala.parserunners.ReportingParseRunner

object SimpleCalculatorWithActionsTest extends App {
  val input = "1:2"
  val parser = new SimpleCalculatorWithActions { override val buildParseTree = true }
  val result = ReportingParseRunner(parser.Expression).run(input)
  val parseTreePrintOut = org.parboiled.support.ParseTreeUtils.printNodeTree(result)
  println("parse tree "+parseTreePrintOut)
  println("result value "+result.resultValue)
  println("matched "+result.matched)
  println("has errors "+result.hasErrors)
  println("errors"+result.parseErrors)
}