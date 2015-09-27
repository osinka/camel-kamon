package com.osinka.camel.kamon

import scala.util.matching.Regex

object KamonCamel {
  val TRACE_PROPERTY = "KamonTraceContext"

  /*
   * Kamon prefers small letters everywhere and hyphenation.
   */
  def hyphenateName(id: String) = {
    val fn = Function.chain(Seq(camelCaseToHyphen _, replaceUnderscores _, dedupHyphens _, trimHyphens _))
    removeScalaParts(id) map fn mkString "."
  }

  /*
   * Kamon prefers small letters everywhere and hyphenation. But one could still want to
   * build a name from full class path. This function leaves the last fragment only.
   */
  def camelCaseName(id: String) =
    removeScalaParts(id).last

  private def removeScalaParts(s: String) = s.
    replaceAllLiterally("$$anonfun", ".").
    replaceAllLiterally("$apply", ".").
    replaceAllLiterally(".package", "").
    replaceAll("""\$\d*""", ".").
    split('.')

  private val CamelCaseRE = """[\p{Upper}\d]*[\p{Lower}\d]+[\d_]*""".r

  private def camelCaseToHyphen(s: String) = {
    val fragments = CamelCaseRE.findAllMatchIn(s).map{ case Regex.Match(f) => f.toLowerCase }
    if (fragments.isEmpty) s
    else fragments mkString "-"
  }

  private def replaceUnderscores(s: String) = s.replaceAllLiterally("_", "-")

  private def dedupHyphens(s: String) = s.replaceAll("""-{2,}""", "-")

  private def trimHyphens(s: String) = s.replaceAll("""^-""", "").replaceAll("""-$""", "")
}
