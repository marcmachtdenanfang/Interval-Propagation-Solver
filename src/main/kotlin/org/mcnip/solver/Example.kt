package org.mcnip.solver

import org.mcnip.solver.Model.Bool
import org.mcnip.solver.Model.Constraint

fun example() {
  println("This is Kotlin.")
}

fun test() {
  val b: Bool = Bool("b")
  if(b is Constraint) {
    println("huhu")
    println(b)
  }
} 


fun main(args: Array<String>) {
  val parser = Parser(args[0])
  println("${parser.asCNF()}\n\n")
  println(parser)
  println(parser.formula)
}