package org.mcnip.solver

typealias UnaryOperations = MutableList<String>
typealias BinaryOperations = MutableList<Pair<String, String>>
typealias Bound = Triple<String, String, String>

class Parser(filePath: String) {

  val constants = mutableMapOf<String, Number>()
  val variables = mutableMapOf<String, Pair<Number, Number>>()
  val booleans = mutableListOf<String>()
  val clauses = mutableListOf<List<String>>()
  val bounds = mutableListOf<Bound>()
  val brackets = mutableListOf<String>()
  val multiplications: BinaryOperations = mutableListOf()
  val additions: BinaryOperations = mutableListOf()
  val subtractions: BinaryOperations = mutableListOf()
  val absolutes: UnaryOperations = mutableListOf()
  val minimums: BinaryOperations = mutableListOf()
  val maximums: BinaryOperations = mutableListOf()
  val exponents: UnaryOperations = mutableListOf()
  val sines: UnaryOperations = mutableListOf()
  val cosines: UnaryOperations = mutableListOf()
  val powers: BinaryOperations = mutableListOf()
  val roots: BinaryOperations = mutableListOf()
  private val unBraOps = listOf("abs", "exp", "sin", "cos")
  private val biBraOps = listOf("min", "max", "pow", "nrt")
  private val braOps = unBraOps + biBraOps
  private val relOps = listOf(">=", "<=", "!=", "=", ">", "<")
  private val opMap = mapOf('^' to "pow", '*' to "mul", '+' to "add", '-' to "sub")
  private val intReg = Regex("\\d+(\\b|$)")
  private val floatReg = Regex("\\d+[.]\\d+(\\b|$)")
  private val varReg = Regex("[_a-zA-Z]+\\w*(\\b|$)")
  private val atomReg = Regex("((!$varReg)|(-$varReg)|($varReg)|(-$floatReg)|($floatReg)|(-$intReg)|($intReg))")
  private val atomEndReg = Regex("$atomReg\\s*$")
  private val relReg = Regex("(${relOps.joinToString("|")})")
  private fun String.splitTrim(c: Char) = split(c).map(String::trim)
  private fun <T> Iterable<T>.toPair() = first() to last()

  init {
    val declarations = mutableListOf<String>()
    val expressions = mutableListOf<String>()
    var readDecl = false
    var readExpr = false
    java.io.File(filePath).inputStream().bufferedReader().forEachLine { line ->
      line.split("--")[0].trim().removeSuffix(";").takeUnless { it.isEmpty() }?.let { importantLine ->
        when {
          importantLine == "DECL" -> readDecl = true
          importantLine == "EXPR" -> readExpr = true
          readExpr -> expressions.add(importantLine)
          readDecl -> declarations.add(importantLine)
        }
      }
    }
    declare(declarations)
    express(expressions)
  }

  private fun String.boundVariables() = this.split("] ").map { it.splitTrim(',') }.toPair().run {
    first.map { bound ->
      bound.removePrefix("-").run {
        if (first().isDigit())
          bound
        else
          (if (length < bound.length) "-" else "") + constants[this]
      }
    }.let { (lower, upper) ->
      second.map { name ->
        name to listOf(lower, upper)
      }
    }
  }

  private fun declare(declarations: MutableList<String>) {
    declarations.forEach { decl ->
      when {
        decl[0] == 'd' ->
          decl.drop(7).splitTrim('=').toPair().run {
            constants[first] = second.run { if (contains('.')) toFloat() else toBigInteger() }
          }
        decl[0] == 'b' ->
          booleans += decl.drop(6)
        else ->
          decl.removePrefix("fl").drop(5).boundVariables().map { (variable, bound) ->
            variables[variable] = bound.map(if (decl[0] == 'f') String::toFloat else String::toBigInteger).toPair()
          }
      }
    }
  }

  private fun String.innerBrackets(): Pair<Int, Int> {
    var braOpen = 0
    forEachIndexed { idx, c ->
      when (c) {
        '(' -> braOpen = idx
        ')' -> return braOpen to idx
      }
    }
    return -1 to -1
  }

  private fun express(expressions: MutableList<String>) {
    var i = 0
    var bracketCount = 0
    val bracketOpsCount = braOps.associateWith { 0 } as MutableMap<String, Int>
    while (i < expressions.size) {
      var expr = expressions[i].replace('{', '(').replace('[', '(').replace(']', ')').replace('}', ')')

      while ('(' in expr) {
        expr.innerBrackets().let { (braOpen, braClose) ->
          if (braOpen > 2 && expr.substring(braOpen - 3, braOpen) in braOps) {
            val braOp = expr.substring(braOpen - 3, braOpen)
            val idx = bracketOpsCount.getValue(braOp)
            expressions += ".$braOp ${expr.substring(braOpen + 1, braClose)}"
            expr = "${expr.substring(0, braOpen - 3)}_$braOp$idx${expr.substring(braClose + 1)}"
            bracketOpsCount[braOp] = idx + 1
          }
          else {
            expressions += ".bra ${expr.substring(braOpen + 1, braClose)}"
            expr = "${expr.substring(0, braOpen)}_bra${bracketCount++}${expr.substring(braClose + 1)}"
          }
        }
      }

      expr = expr.replace("! ", "!").replace("not ", "!")

      expr = powers.addAllFrom(expr, '^')

      expr = multiplications.addAllFrom(expr, '*')

      expr = additions.addAllFrom(expr, '+')

      //TODO expr = subtractions.addAllFrom(expr, '-')

      expr = bound(expr)

      cleanUp(expr)

      i++
    }
  }

  private fun BinaryOperations.addAllFrom(str: String, op: Char): String {
    var expr = str
    while (op in expr) {
      val idx = expr.indexOf(op)
      val opName = opMap.getValue(op)
      val fstMatch = atomEndReg.find(expr.substring(0, idx))!!
      val sndMatch = atomReg.find(expr, idx)!!
      expr = "${expr.substring(0, fstMatch.range.first)}_$opName${size}${expr.substring(sndMatch.range.last + 1)}"
      this += Pair(fstMatch.value.trimEnd(), sndMatch.value)
    }
    return expr
  }

  private fun bound(str: String): String {
    var expr = str
    while (relOps.fold(false) { acc, it -> acc || (it in expr)}) {
      val relMatch = relReg.find(expr)!!
      val fstMatch = atomEndReg.find(expr.substring(0, relMatch.range.first))!!
      val sndMatch = atomReg.find(expr, relMatch.range.last)!!
      expr = "${expr.substring(0, fstMatch.range.first)}_bnd${bounds.size}${expr.substring(sndMatch.range.last + 1)}"
      bounds += Bound(relMatch.value, fstMatch.value.trimEnd(), sndMatch.value)
    }
    return expr
  }

  private fun cleanUp(expr: String) {
    if (expr[0] == '.') {
      val args = expr.substring(5).splitTrim(',')
      when (expr.substring(1, 4)) {
        "bra" -> brackets += args[0]
        "abs" -> absolutes += args[0]
        "min" -> minimums += args[0] to args[1]
        "max" -> maximums += args[0] to args[1]
        "exp" -> exponents += args[0]
        "sin" -> sines += args[0]
        "cos" -> cosines += args[0]
        "pow" -> powers += args[0] to args[1]
        "nrt" -> roots += args[0] to args[1]
      }
    }
    else
      expr.split(" and ").forEach {
        clauses += it.split(" or ")
      }
  }

  override fun toString() = "constants:\n$constants\n\nvariables:\n$variables\n\nbooleans:\n$booleans\n\nclauses:\n$clauses\n\nbounds:\n$bounds\n\nbrackets:\n$brackets\n\nmultiplications:\n$multiplications\n\nadditions:\n$additions\n\nsubtractions:\n$subtractions\n\nabsolutes:\n$absolutes\n\nminimums:\n$minimums\n\nmaximums:\n$maximums\n\nexponents:\n$exponents\n\nsines:\n$sines\n\ncosines:\n$cosines\n\npowers:\n$powers\n\nroots:\n$roots"

  fun asCNF(): String = ("CNF: " +
      constants.toList().joinToString(separator = "") { "${it.first} = ${it.second} and " } +
      variables.toList().joinToString(separator = "") { "${it.first} >= ${it.second.first} and ${it.first} <= ${it.second.second} and " } +
      clauses.joinToString(separator = "") { "(${it.joinToString(" or ")}) and ".run {
        var str = this
        while (str.contains("_b")) {
          while (str.contains("_bra")) {
            val match = Regex("_bra$intReg").find(str)!!
            str = "${str.substring(0, match.range.first)}(${brackets[match.value.drop(4).toInt()]})${str.substring(match.range.last + 1)}"
          }
          while (str.contains("_bnd")) {
            val match = Regex("_bnd$intReg").find(str)!!
            str = "${str.substring(0, match.range.first)}${bounds[match.value.drop(4).toInt()].run { "$second $first $third" }}${str.substring(match.range.last + 1)}"
          }
        }
        str
      } } +
      multiplications.mapIndexed { idx, it -> "_mul$idx = ${it.first} * ${it.second} and " }.joinToString("") +
      additions.mapIndexed { idx, it -> "_add$idx = ${it.first} + ${it.second} and " }.joinToString("") +
      subtractions.mapIndexed { idx, it -> "_sub$idx = ${it.first} - ${it.second} and " }.joinToString("") +
      absolutes.mapIndexed { idx, it -> "_abs$idx = abs($it) and " }.joinToString("") +
      minimums.mapIndexed { idx, it -> "_min$idx = min(${it.first}, ${it.second}) and " }.joinToString("") +
      maximums.mapIndexed { idx, it -> "_max$idx = max(${it.first}, ${it.second}) and " }.joinToString("") +
      exponents.mapIndexed { idx, it -> "_exp$idx = exp($it) and " }.joinToString("") +
      sines.mapIndexed { idx, it -> "_sin$idx = sin($it) and " }.joinToString("") +
      cosines.mapIndexed { idx, it -> "_cos$idx = cos($it) and " }.joinToString("") +
      powers.mapIndexed { idx, it -> "_pow$idx = pow(${it.first}, ${it.second}) and " }.joinToString("") +
      roots.mapIndexed { idx, it -> "_nrt$idx = nrt(${it.first}, ${it.second}) and " }.joinToString("")).dropLast(5)

}