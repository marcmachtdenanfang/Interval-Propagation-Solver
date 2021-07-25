package org.mcnip.solver

import org.mcnip.solver.Contractors.BinContractor.*
import org.mcnip.solver.Contractors.BoundContractor.*
import org.mcnip.solver.Contractors.Contractor
import org.mcnip.solver.Contractors.UnContractor.*
import org.mcnip.solver.Model.*
import kotlin.Pair

typealias UnaryOperations = MutableList<String>
typealias BinaryOperations = MutableList<Pair<String, String>>
typealias BoundTriple = Triple<String, String, String>

class Parser(filePath: String) : IParser {

  /* private for Formula is necessary, due to: see below getFormula implementation. */
  private val formula: Formula 
  private val constants = mutableMapOf<String, String>()
  private val variables = mutableMapOf<String, Pair<String, String>>()
  private val booleans = mutableListOf<String>()
  private val clauseList = mutableListOf<List<String>>()
  private val boundList = mutableListOf<BoundTriple>()
  private val brackets = mutableListOf<String>()
  private val negations: UnaryOperations = mutableListOf()
  private val multiplications: BinaryOperations = mutableListOf()
  private val divisions: BinaryOperations = mutableListOf()
  private val additions: BinaryOperations = mutableListOf()
  private val subtractions: BinaryOperations = mutableListOf()
  private val absolutes: UnaryOperations = mutableListOf()
  private val minimums: BinaryOperations = mutableListOf()
  private val maximums: BinaryOperations = mutableListOf()
  private val exponents: UnaryOperations = mutableListOf()
  private val sines: UnaryOperations = mutableListOf()
  private val cosines: UnaryOperations = mutableListOf()
  private val powers: BinaryOperations = mutableListOf()
  private val roots: BinaryOperations = mutableListOf()
  private val intervals = mutableMapOf<String, Interval>()
  private val bounds = mutableListOf<Bound>()
  private val clauses = mutableListOf<Clause>()
  private val unBraOps = listOf("neg", "abs", "exp", "sin", "cos")
  private val biBraOps = listOf("min", "max", "pow", "nrt")
  private val braOps = unBraOps + biBraOps
  private val relOps = listOf(">=", "<=", "!=", "=", ">", "<")
  private val opMap = mapOf('^' to "pow", '*' to "mul", '/' to "div", '+' to "add", '~' to "sub")
  private val intReg = Regex("\\d+(\\b|$)")
  private val floatReg = Regex("\\d+[.]\\d+(\\b|$)")
  private val varReg = Regex("[_a-zA-Z]+\\w*(\\b|$)")
  private val atomReg = Regex("((!$varReg)|($varReg)|(-$floatReg)|($floatReg)|(-$intReg)|($intReg))")
  private val atomEndReg = Regex("$atomReg\\s*$")
  private val subReg = Regex("$atomReg\\s*-")
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

  init {
    constants.forEach {
      intervals[it.key] = DotInterval(it.key, it.value)
    }
    variables.forEach {
      intervals[it.key] = Interval(it.key, it.value.first, it.value.second)
    }
    boundList.forEach { (fst, snd, trd) ->
      bounds += (
          if (snd.isNumber())
            Triple(fst.replace('<', '_').replace('>', '<').replace('_', '>'), trd, snd)
          else
            Triple(fst, snd, trd)
          ).let { (relType, leftArg, rightArg) ->
            Bound(leftArg, getInterval(rightArg), when (relType) {
              relOps[0] -> GreaterEqualsContractor()
              relOps[1] -> LessEqualsContractor()
              relOps[2] -> NotEqualsContractor()
              relOps[3] -> EqualsContractor()
              relOps[4] -> GreaterContractor()
              else -> LessContractor()
            })
          }
    }
    clauseList.forEach { clause ->
      val variables = mutableSetOf<String>()
      clause.map { lit ->
        var str = when {
          lit.startsWith("_bra") -> brackets[lit.drop(4).toInt()]
          lit.startsWith("!_bra") -> "!${brackets[lit.drop(5).toInt()]}".removePrefix("!!")
          else -> lit
        }
        if (str.startsWith("_bnd")) {
          bounds[str.drop(4).toInt()]
        }
        else {
          (if (str[0] != '!') {
            // Bool(str)
            Bound(str, DotInterval("1", 1), GreaterEqualsContractor())
          }
          else {
            str = str.drop(1)
            //Bool(str, false)
            Bound(str, DotInterval("0", 0), LessEqualsContractor())
          })}.also { variables += str }
      }.let { clauses += Clause(variables, it) }
    }
    negations.operateUn("neg", NegContractor())
    multiplications.operateBi("mul", MulContractor())
    divisions.operateBi("div", DivContractor())
    additions.operateBi("add", AddContractor())
    subtractions.operateBi("sub", SubContractor())
    absolutes.operateUn("abs", AbsContractor())
    minimums.operateBi("min", MinContractor())
    maximums.operateBi("max", MaxContractor())
    exponents.operateUn("exp", ExpContractor())
    sines.operateUn("sin", SinContractor())
    cosines.operateUn("cos", CosContractor())
    powers.operateBi("pow", PowContractor())
    roots.operateBi("nrt", NrtContractor())
    typecasting()
    formula = Formula(clauses)
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

  //private fun String.boolVariables() = .let {}

  private fun declare(declarations: MutableList<String>) {
    declarations.forEach { decl ->
      when {
        decl[0] == 'd' ->
          decl.drop(7).splitTrim('=').toPair().run {
            constants[first] = second
          }
        decl[0] == 'b' -> {
          //booleans += decl.drop(5)
          val boolNames = decl.drop(5).splitTrim(',')
          for(bool in boolNames) {
            variables[bool] = Pair("0","1")
          }
        }
        else ->
          decl.removePrefix("fl").drop(5).boundVariables().map { (variable, bound) ->
            variables[variable] = bound.toPair()
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
            val inner = expr.substring(braOpen + 1, braClose)
            if (" or " in inner || " and " in inner)
              expr = "${expr.substring(0, braOpen)}$inner${expr.substring(braClose + 1)}"
            else {
              expressions += ".bra $inner"
              expr = "${expr.substring(0, braOpen)}_bra${bracketCount++}${expr.substring(braClose + 1)}"
            }
          }
        }
      }

      subReg.findAll(expr).forEach { match ->
        expr = expr.replaceRange(match.range.last, match.range.last + 1, "~")
      }

      expr = expr.replace("! ", "!").replace("not ", "!")

      expr = powers.addAllFrom(expr, '^')

      expr = multiplications.addAllFrom(expr, '*')

      expr = divisions.addAllFrom(expr, '/')

      expr = additions.addAllFrom(expr, '+')

      expr = subtractions.addAllFrom(expr, '~')

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
      expr = "${expr.substring(0, fstMatch.range.first)}_bnd${boundList.size}${expr.substring(sndMatch.range.last + 1)}"
      val fstVal = fstMatch.value.trimEnd()
      boundList += BoundTriple(relMatch.value, fstVal, sndMatch.value)
    }
    return expr
  }

  private fun cleanUp(expr: String) {
    if (expr[0] != '.')
      expr.split(" and ").forEach {
        clauseList += it.split(" or ")
      }
    else {
      val args = expr.substring(5).splitTrim(',')
      when (expr.substring(1, 4)) {
        "bra" -> brackets += args[0]
        "neg" -> negations += args[0]
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
  }

  private fun BinaryOperations.operateBi(str: String, contractor: Contractor) = forEachIndexed { idx, (left, right) ->
    val leftArg = getInterval(left)
    val rightArg = getInterval(right)
    val result = Interval("_$str$idx", leftArg.type?:rightArg.type)
    addIntervalAndClause(result, setOf(result.varName, leftArg.varName, rightArg.varName), Triplet(result, leftArg, rightArg, contractor))
  }

  private fun UnaryOperations.operateUn(str: String, contractor: Contractor) = forEachIndexed { idx, name ->
    val arg = getInterval(name)
    val result = Interval("_$str$idx", arg.type)
    addIntervalAndClause(result, setOf(result.varName, arg.varName), Pair(result, arg, contractor))
  }

  private fun addIntervalAndClause(result: Interval, set: Set<String>, constraint: Constraint) {
    intervals[result.varName] = result
    clauses += Clause(set, listOf(constraint))
  }

  private fun getInterval(str: String) =
      if (str.isNumber())
        DotInterval(str, str)
      else
        intervals.getOrPut(str, { Interval(str, null) })

  private fun String.isNumber() = removePrefix("-")[0].isDigit()

  private fun typecasting() {
    bounds.forEach {
      if (it.bound.type == null)
        it.bound.type = intervals.getValue(it.bound.varName).type
    }
    clauses.forEach { clause ->
      clause.constraints.forEach { constraint ->
        when (constraint) {
          is Triplet -> {
            val mainType = constraint.result.type?:constraint.leftArg.type?:constraint.rightArg.type?:intervals[constraint.result.varName]?.type?:intervals[constraint.leftArg.varName]?.type?:intervals[constraint.rightArg.varName]?.type
            constraint.result.type = mainType
            constraint.leftArg.type = mainType
            constraint.rightArg.type = mainType
          }
          is org.mcnip.solver.Model.Pair -> {
            val mainType = constraint.result.type?:constraint.origin.type?:intervals[constraint.result.varName]?.type?:intervals[constraint.origin.varName]?.type
            constraint.result.type = mainType
            constraint.origin.type = mainType
          }
          is Bound ->
            if (constraint.bound.type == null)
              constraint.bound.type = intervals.getValue(constraint.bound.varName).type
        }
      }
    }
  }

  override fun toString() = "constants:\n$constants\n\nvariables:\n$variables\n\nbooleans:\n$booleans\n\nclauses:\n$clauseList\n\nbounds:\n$boundList\n\nbrackets:\n$brackets\n\nnegations:\n$negations\n\nmultiplications:\n$multiplications\n\ndivisions:\n$divisions\n\nadditions:\n$additions\n\nsubtractions:\n$subtractions\n\nabsolutes:\n$absolutes\n\nminimums:\n$minimums\n\nmaximums:\n$maximums\n\nexponents:\n$exponents\n\nsines:\n$sines\n\ncosines:\n$cosines\n\npowers:\n$powers\n\nroots:\n$roots"

  fun asCNF(): String = ("\u001B[32m-- CNF:\u001B[0m\n" +
      constants.toList().joinToString(separator = "") { "${it.first} = ${it.second} and " } +
      variables.toList().joinToString(separator = "") { "${it.first} >= ${it.second.first} and ${it.first} <= ${it.second.second} and " } +
      clauseList.joinToString(separator = "") { "(${it.joinToString(" or ")}) and ".run {
        var str = this
        while ("_b" in str) {
          while ("_bra" in str) {
            val match = Regex("_bra$intReg").find(str)!!
            str = "${str.substring(0, match.range.first)}(${brackets[match.value.drop(4).toInt()]})${str.substring(match.range.last + 1)}"
          }
          while ("_bnd" in str) {
            val match = Regex("_bnd$intReg").find(str)!!
            str = "${str.substring(0, match.range.first)}${boundList[match.value.drop(4).toInt()].run { "$second $first $third" }}${str.substring(match.range.last + 1)}"
          }
        }
        str
      } } + negations.mapIndexed { idx, it -> "_neg$idx = -$it and " }.joinToString("") +
      multiplications.mapIndexed { idx, it -> "_mul$idx = ${it.first} * ${it.second} and " }.joinToString("") +
      divisions.mapIndexed { idx, it -> "_div$idx = ${it.first} / ${it.second} and " }.joinToString("") +
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

  /**
   * For implementing the IParser interface, since it is written in Java.
   * Necessary due to: https://youtrack.jetbrains.com/issue/KT-6653
   */
  override fun getFormula() = formula
  override fun getIntervals() = intervals

}