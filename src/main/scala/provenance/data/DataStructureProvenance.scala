/*
 * Created by William Anwara on 2023.3.29
 * Copyright © 2023 William Anwara. All rights reserved.
 */

package provenance.data

abstract class DataStructureProvenance(private var data: Any) extends LazyCloneableProvenance {
  override def hashCode(): Int = data.hashCode()

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: DataStructureProvenance =>
        data.equals(other.data)
      case _ => false
    }
  }
}
