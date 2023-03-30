/*
 * Created by William Anwara on 2023.3.29
 * Copyright © 2023 William Anwara. All rights reserved.
 */

package objects

import provenance.data.{DummyProvenance, Provenance}

/**
 * Created by malig on 4/29/19.
 */
case class TaintString(override val value: String, p: Provenance = DummyProvenance.create()) extends TaintAny(value, p) {

  setProvenance(getCallSite())

  def length: TaintInt = {
    val mprov =  getCallSite()
    TaintInt(value.length, newProvenance(mprov.cloneProvenance()))
  }

  def split(separator: Char): Array[TaintString] = {
    val mprov =  getCallSite()
    value
      .split(separator)
      .map(s =>
        TaintString(
          s, newProvenance(mprov.cloneProvenance())))
  }
  def split(regex: String): Array[TaintString] = {
    split(regex, 0)
  }

  def split(regex: String, limit: Int): Array[TaintString] = {
    val mprov =  getCallSite()
    value
      .split(regex, limit)
      .map(s =>
        TaintString(
          s, newProvenance(mprov.cloneProvenance())))
  }
  def split(separator: Array[Char]): Array[TaintString] = {
    val mprov =  getCallSite()
    value
      .split(separator)
      .map(s =>
        TaintString(
          s,newProvenance(mprov.cloneProvenance())
        ))
  }

  def substring(arg0: TaintInt): TaintString = {
    val mprov =  getCallSite()
    TaintString(value.substring(arg0.value), newProvenance(arg0.getProvenance(), mprov.cloneProvenance()))
  }

  def +(arg0: TaintString): TaintString = {
    val mprov =  getCallSite()
    TaintString(value + arg0.value, newProvenance(arg0.getProvenance(), mprov.cloneProvenance()))
  }

  def substring(arg0: Int, arg1: TaintInt): TaintString = {
    val mprov =  getCallSite()
    TaintString(value.substring(arg0, arg1.value), newProvenance(arg1.getProvenance(), mprov.cloneProvenance()))
  }
  def substring(arg0: TaintInt, arg1: TaintInt): TaintString = {
    val mprov =  getCallSite()
    TaintString(value.substring(arg0.value, arg1.value), newProvenance(arg0.getProvenance(), arg1.getProvenance() , mprov.cloneProvenance()))
  }

  def lastIndexOf(elem: Char): TaintInt = {
    val mprov =  getCallSite()
    TaintInt(value.lastIndexOf(elem), newProvenance(mprov.cloneProvenance()))
  }

  def trim(): TaintString = {
    val mprov =  getCallSite()
    TaintString(value.trim, newProvenance(mprov.cloneProvenance()))
  }


  def toInt: TaintInt ={
    val mprov =  getCallSite()
    TaintInt( value.toInt, newProvenance(mprov.cloneProvenance()))
  }

  def isEmpty: Boolean ={
    val mprov =  getCallSite()
    setProvenance(newProvenance(mprov.cloneProvenance()))
    value.isEmpty
  }

//  def toFloat: TaintFloat = {
//    val mprov = getCallSite()
//    TaintFloat(value.toFloat, newProvenance(mprov.cloneProvenance()))
//  }
//  def toDouble: TaintDouble ={
//    TaintDouble(value.toDouble , getProvenance())
//  }

  // TODO: add configuration to track equality checks, e.g. if used as a key in a map.
  def equals(obj: TaintString): Boolean = value.equals(obj.value)
  def eq(obj: TaintString): Boolean = value.eq(obj.value)

}
