/*
 * Created by William Anwara on 2023.3.29
 * Copyright © 2023 William Anwara. All rights reserved.
 */

package objects

/**
 * Created by malig on 4/25/19.
 */

import provenance.util.{Provenance}


case class TaintInt(override val value: Int, p : Provenance) extends TaintAny(value, p) {

  setProvenance(getCallSite())
  def this(value: Int) = {
    this(value, p)
  }

  /**
   * Overloading operators from here onwards
   */
  def +(x: Int): TaintInt = {
    val mprov = getCallSite()
    val d = value + x
    TaintInt(d, newProvenance(mprov.cloneProvenance()))
  }

  def -(x: Int): TaintInt = {
    val mprov = getCallSite()
    val d = value - x
    TaintInt(d, newProvenance(mprov.cloneProvenance()))
  }

  def *(x: Int): TaintInt = {
    val mprov = getCallSite()
    val d = value * x
    TaintInt(d, newProvenance(mprov.cloneProvenance()))
  }

//  def *(x: Float): TaintFloat = {
//    val mprov = getCallSite()
//    val d = value * x
//    TaintFloat(d, newProvenance(mprov.cloneProvenance()))
//  }
//
//
//  def /(x: Int): TaintDouble= {
//    val mprov = getCallSite()
//    val d = value / x
//    TaintDouble(d, newProvenance(mprov.cloneProvenance()) )
//  }
//
//  def /(x: Long): TaintDouble= {
//    val mprov = getCallSite()
//    val d = value / x
//    TaintDouble(d, newProvenance(mprov.cloneProvenance()))
//  }

  def +(x: TaintInt): TaintInt = {
    val mprov = getCallSite()
    TaintInt(value + x.value, newProvenance(x.getProvenance(), mprov.cloneProvenance()))
  }

  def -(x: TaintInt): TaintInt = {
    val mprov = getCallSite()
    TaintInt(value - x.value, newProvenance(x.getProvenance(), mprov.cloneProvenance()))
  }

  def *(x: TaintInt): TaintInt = {
    val mprov = getCallSite()
    TaintInt(value * x.value, newProvenance(x.getProvenance(), mprov.cloneProvenance()))
  }

  def /(x: TaintInt): TaintInt = {
    val mprov = getCallSite()
    TaintInt(value / x.value, newProvenance(x.getProvenance(), mprov.cloneProvenance()))
  }

  def %(x: Int): TaintInt = {
    val mprov = getCallSite()
    TaintInt(value % x, newProvenance(mprov.cloneProvenance()))
  }

//  // Implementing on a need-to-use basis
//  def toInt: TaintInt = this
//  def toDouble: TaintDouble = { val mprov = getCallSite()
//    TaintDouble(value.toDouble, newProvenance(mprov.cloneProvenance()))}

  /**
   * Operators not supported yet
   */

  def ==(x: Int): Boolean = value == x

  def toByte: Byte = value.toByte

  def toShort: Short = value.toShort

  def toChar: Char = value.toChar



  def toLong: Long = value.toLong

  def toFloat: Float = value.toFloat

  //def toDouble: Double = value.toDouble

  def unary_~ : Int = value.unary_~

  def unary_+ : Int = value.unary_+

  def unary_- : Int = value.unary_-

  def +(x: String): String = value + x

  def <<(x: Int): Int = value << x

  def <<(x: Long): Int = value << x

  def >>>(x: Int): Int = value >>> x

  def >>>(x: Long): Int = value >>> x

  def >>(x: Int): Int = value >> x

  def >>(x: Long): Int = value >> x

  def ==(x: Byte): Boolean = value == x

  def ==(x: Short): Boolean = value == x

  def ==(x: Char): Boolean = value == x

  def ==(x: Long): Boolean = value == x

  def ==(x: Float): Boolean = value == x

  def ==(x: Double): Boolean = value == x

  def !=(x: Byte): Boolean = value != x

  def !=(x: Short): Boolean = value != x

  def !=(x: Char): Boolean = value != x

  def !=(x: Int): Boolean = value != x

  def !=(x: Long): Boolean = value != x

  def !=(x: Float): Boolean = value != x

  def !=(x: Double): Boolean = value != x

  def <(x: Byte): Boolean = value < x

  def <(x: Short): Boolean = value < x

  def <(x: Char): Boolean = value < x

  def <(x: Int): Boolean = value < x

  def <(x: Long): Boolean = value < x

  def <(x: Float): Boolean = value < x

  def <(x: Double): Boolean = value < x

  def <=(x: Byte): Boolean = value <= x

  def <=(x: Short): Boolean = value <= x

  def <=(x: Char): Boolean = value <= x

  def <=(x: Int): Boolean = value <= x

  def <=(x: Long): Boolean = value <= x

  def <=(x: Float): Boolean = value <= x

  def <=(x: Double): Boolean = value <= x

  def >(x: Byte): Boolean = value > x

  def >(x: Short): Boolean = value > x

  def >(x: Char): Boolean = value > x

  def >(x: Int): Boolean = value > x
  def >(x: TaintInt): Boolean = value > x.value

  def >(x: Long): Boolean = value > x

  def >(x: Float): Boolean = value > x

  def >(x: Double): Boolean = value > x

  def >=(x: Byte): Boolean = value >= x

  def >=(x: Short): Boolean = value >= x

  def >=(x: Char): Boolean = value >= x

  def >=(x: Int): Boolean = value >= x

  def >=(x: Long): Boolean = value >= x

  def >=(x: Float): Boolean = value >= x

  def >=(x: Double): Boolean = value >= x

  def |(x: Byte): Int = value | x

  def |(x: Short): Int = value | x

  def |(x: Char): Int = value | x

  def |(x: Int): Int = value | x

  def |(x: Long): Long = value | x

  def &(x: Byte): Int = value & x

  def &(x: Short): Int = value & x

  def &(x: Char): Int = value & x

  def &(x: Int): Int = value & x

  def &(x: Long): Long = value & x

  def ^(x: Byte): Int = value ^ x

  def ^(x: Short): Int = value ^ x

  def ^(x: Char): Int = value ^ x

  def ^(x: Int): Int = value ^ x

  def ^(x: Long): Long = value ^ x

  def +(x: Byte): Int = value + x

  def +(x: Short): Int = value + x

  def +(x: Char): Int = value + x

  def +(x: Long): Long = value + x

  def +(x: Float): Float = value + x

  def +(x: Double): Double = value + x

  def -(x: Byte): Int = value - x

  def -(x: Short): Int = value - x

  def -(x: Char): Int = value - x

  def -(x: Long): Long = value - x

  def -(x: Float): Float = value - x

  def -(x: Double): Double = value - x

  def *(x: Byte): Int = value * x

  def *(x: Short): Int = value * x

  def *(x: Char): Int = value * x

  def *(x: Long): Long = value * x

  def *(x: Double): Double = value * x

  def /(x: Byte): Int = value / x

  def /(x: Short): Int = value / x

  def /(x: Char): Int = value / x

  def /(x: Float): Float = value / x

  def /(x: Double): Double = value / x

  def %(x: Byte): Int = value % x

  def %(x: Short): Int = value % x

  def %(x: Char): Int = value % x

  def %(x: Long): Long = value % x

  def %(x: Float): Float = value % x

  def %(x: Double): Double = value % x

}

object TaintInt {
  implicit def ordering: Ordering[TaintInt] = Ordering.by(_.value)
}
