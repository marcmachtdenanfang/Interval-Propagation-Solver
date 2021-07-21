package org.mcnip.solver

import org.mcnip.solver.Context.*
import org.mcnip.solver.Contractors.BoundContractor.*
import org.mcnip.solver.Contractors.Contractor
import org.mcnip.solver.Model.*
import kotlin.Pair

private fun <K> Map<K, Interval>.containsEmptyInterval() = values.fold(false) { acc, interval -> acc || interval.isEmpty }

private infix fun List<String>.from(assignment: Map<String, Interval>) = filter { !it[0].isDigit() && it[0] != '-' }.map { it to assignment.getValue(it) }.toMap()

fun findUnits(clauses: List<Clause>, map: Map<String, Interval>, assertedAtoms: List<Atom>): List<Constraint>? {
  val units = mutableListOf<Constraint>()
  var newUnit = clauses[0].constraints[0]
  clauses.forEach { clause ->
    when (clause.constraints.map { constraint ->
      if(constraint is Bool)
        (assertedAtoms.find { (it is Bool) && it.name == constraint.name } as Bool?)?.run { isPolarity xor constraint.isPolarity }?:false
      else {
        val filteredMap = map.filter { it.key in constraint.variables }
        if (/*constraint.variables.map { it in filteredMap }.reduce { acc, bool -> acc && bool } && */updateIntervals(filteredMap, constraint).map { it.value.isEmpty }.reduce { acc, bool -> acc || bool })
          true
        else
          false.also { newUnit = constraint }
      }
    }.filter { !it }.size) {
      0 -> return null
      1 -> units += newUnit
    }
  }
  return units
}

fun List<Atom>.narrowContractors(currentAssignment: MutableMap<String, Interval>): Pair<MutableMap<String, Interval>, MutableList<Bound>>? {
  val bounds = mutableListOf<Bound>()
  forEach { atom ->
    if (atom !is Bool && !(atom is Bound && atom.isInfinite)) atom.update(currentAssignment).let { newIntervals ->
      if (newIntervals.containsEmptyInterval())
        return null
      currentAssignment += newIntervals
      if (atom is org.mcnip.solver.Model.Pair || atom is Triplet)
        bounds += extractBounds(newIntervals)
    }
  }
  return currentAssignment to bounds
}

fun Atom.update(currentAssignment: Map<String, Interval>): MutableMap<String, Interval> = updateIntervals(when (this) {
  is Bound -> listOf(varName, bound.varName) from currentAssignment
  is org.mcnip.solver.Model.Pair -> listOf(result.varName, origin.varName) from currentAssignment
  else -> listOf((this as Triplet).result.varName, leftArg.varName, rightArg.varName) from currentAssignment
}, this as Constraint)

fun Atom.invert() = if (this is Bool) Bool(name, !isPolarity) else Bound((this as Bound).varName, bound, contractor.invert())

fun Contractor.invert() = when (this) {
  is EqualsContractor -> NotEqualsContractor()
  is GreaterContractor -> LessEqualsContractor()
  is GreaterEqualsContractor -> LessContractor()
  is LessContractor -> GreaterEqualsContractor()
  is LessEqualsContractor -> GreaterContractor()
  is NotEqualsContractor -> EqualsContractor()
  else -> this
}

fun <I> filteredMapOf(vararg pairs: Pair<String, I>) = pairs.filter { !it.first[0].isDigit() && it.first[0] != '-' }.toMap()