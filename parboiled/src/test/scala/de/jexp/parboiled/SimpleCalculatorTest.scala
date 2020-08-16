package de.jexp.parboiled

import org.parboiled.scala.parserunners.ReportingParseRunner

object SimpleCalculatorTest extends App {
  val input = "1+2"
  val parser = new SimpleCalculator { override val buildParseTree = true }
  val result = ReportingParseRunner(parser.Expression).run(input)
  val parseTreePrintOut = org.parboiled.support.ParseTreeUtils.printNodeTree(result)
  println(parseTreePrintOut)
}