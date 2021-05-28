package org.mcnip.solver

fun example() {
  println("This is Kotlin.")
}

fun main(args: Array<String>) {
  val parser = Parser(args[0])
  println("${parser.asCNF()}\n\n")
  println(parser)
}