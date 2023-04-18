/*
 * Created by William Anwara on 2023.3.29
 * Copyright © 2023 William Anwara. All rights reserved.
 */

package objects



import provenance.util.Provenance
abstract class TaintAny[T <: Any](val value: T, p: Provenance) extends TaintBase(p) {
  def toTaintString: TaintString = {
    TaintString(value.toString, getProvenance())
  }

  def getCallSite() : Provenance = {
    val loc = Thread.currentThread.getStackTrace()
      .dropWhile{s =>
        (s.getClassName.startsWith("symbolicprimitives") ||
          s.getClassName.startsWith("provenance") ||
          s.getClassName.startsWith("java.lang"))
      }(0)
      .getLineNumber
    val provCreatorFn = Provenance.createFn()
    val prov = provCreatorFn(Seq(loc))
    newProvenance(prov)
  }

  override def hashCode(): Int = value.hashCode()

  override def equals(obj: scala.Any): Boolean =
    obj match {
      case x: TaintAny[_] => value.equals(x.value)
      case _ => value.equals(obj)
    }

  override def toString: String = s"${this.getClass.getSimpleName}($value, ${getProvenance()})"


  // jteoh Disabled: These could actually be implicits in the symbase class too, but I'm hesitant
  // to do so because of scoping and incomplete knowledge on implicits.
  //  /** Creates new TaintInt with current object provenance */
  //  def sym(v: Int): TaintInt = TaintInt(v, p)
  //
  //  // TODO: support Long type
  //  /** Creates new TaintLong with current object provenance */
  //  def sym(v: Long): TaintLong = new TaintLong(v.toInt, p)
  //
  //  /** Creates new TaintDouble with current object provenance */
  //  def sym(v: Double): TaintDouble = TaintDouble(v, p)
  //
  //  /** Creates new TaintFloat with current object provenance */
  //  def sym(v: Float): TaintFloat = TaintFloat(v, p)
  //
  //  /** Creates new TaintString with current object provenance */
  //  def sym(v: String): TaintString = TaintString(v, p)
}
object TaintAny {
}