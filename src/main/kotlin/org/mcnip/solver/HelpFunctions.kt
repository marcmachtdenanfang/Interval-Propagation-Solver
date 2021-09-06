package org.mcnip.solver

import org.mcnip.solver.Context.*
import org.mcnip.solver.Contractors.BoundContractors.*
import org.mcnip.solver.Contractors.Contractor
import org.mcnip.solver.Model.*
import org.mcnip.solver.Model.Pair as Dyad

private infix fun List<String>.from(assignment: Map<String, Interval>) = filter { !it[0].isDigit() && it[0] != '-' }.map { it to assignment.getValue(it) }.toMap()

fun <K> Map<K, Interval>.containsEmptyInterval() = values.stream().anyMatch(Interval::isEmpty)

fun findUnits(clauses: List<Clause>, map: Map<String, Interval>): List<Constraint>? {
  val units = mutableListOf<Constraint>()
  var newUnit = clauses[0].constraints[0]
  clauses.forEach { clause ->
    when (clause.constraints.map { constraint ->
      val filteredMap = map.filter { it.key in constraint.variables }
      if (updateIntervals(filteredMap, constraint).map { it.value.isEmpty }.reduce { acc, bool -> acc || bool })
        true
      else
        false.also { newUnit = constraint }
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
    if (!(atom is Bound && atom.isInfinite)) atom.update(currentAssignment).let { newIntervals ->
      if (newIntervals.containsEmptyInterval())
        return null
      currentAssignment += newIntervals
      if (atom is Dyad || atom is Triplet)
        bounds += extractBounds(newIntervals)
    }
  }
  return currentAssignment to bounds
}

/**
 * Implements the update_rho function from the paper.
 * Consult the test cases in AppTest.java for more details.
 *
 * @param intervals A Map of variables and their associated Intervals.
 * @param constraint A Constraint (i.e. Bound or Pair or Triple).
 * @return Contracted intervals, find them with their name.
 */
fun updateIntervals(intervals: Map<String, Interval>, constraint: Constraint): MutableMap<String, Interval> =
  constraint.contractor.contract(intervals, constraint.variables)

fun Atom.update(currentAssignment: Map<String, Interval>): MutableMap<String, Interval> = updateIntervals(when (this) {
  is Bound -> listOf(varName, bound.varName) from currentAssignment
  is Dyad -> listOf(result.varName, origin.varName) from currentAssignment
  else -> listOf((this as Triplet).result.varName, leftArg.varName, rightArg.varName) from currentAssignment
}, this as Constraint)

fun Bound.invert() = Bound(varName, bound, contractor.invert())

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