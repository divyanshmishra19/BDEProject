/*
 * Created by William Anwara on 2023.3.29
 * Copyright © 2023 William Anwara. All rights reserved.
 */

package provenance.data

import java.io.{IOException, ObjectInputStream, ObjectOutputStream}

import org.roaringbitmap.RoaringBitmap

class RoaringBitmapProvenance(var bitmap: RoaringBitmap) extends DataStructureProvenance(bitmap) {
  override def _cloneProvenance(): RoaringBitmapProvenance = new RoaringBitmapProvenance(bitmap.clone())

  override def _merge(other: Provenance): this.type = {
    other match {
      case rbp: RoaringBitmapProvenance =>
        bitmap.or(rbp.bitmap)
      // Optional (but potentially unsafe/incorrect?) operation to pre-emptively free memory
      //rbp.bitmap.clear()
      case other => throw new NotImplementedError(s"Unsupported RoaringBitmap merge provenance " +
        s"type! $other")
    }
    this
  }

  /** Returns number of provenance IDs. */
  override def count: Int = bitmap.getCardinality

  /** Returns estimate in serialization size, experimental. */
  override def estimateSize: Long = bitmap.getLongSizeInBytes

  private var serializationCount: Int = 0
  private var deserializationCount: Int = 0
  @throws(classOf[IOException])
  private def writeObject(out: ObjectOutputStream): Unit = {
    serializationCount += 1
    //if (serializationCount > 1) println(s"TADA!: I've been serialized $serializationCount times
    // " + s"and have $count items!")
    //println(SizeEstimator.estimate(this))
    out.writeObject(bitmap)
  }

  @throws(classOf[IOException])
  private def readObject(in: ObjectInputStream): Unit = {
    deserializationCount += 1
    bitmap = in.readObject().asInstanceOf[RoaringBitmap]
  }

  override def toString: String = {
    var count = 0
    val printLimit = 10
    val iter = this.bitmap.iterator()
    var buf = new StringBuilder("[")
    while (count < printLimit && iter.hasNext()) {
      buf ++= iter.next().toString
      if (iter.hasNext) buf += ','
      count += 1
    }
    if (iter.hasNext) buf ++= s" ...(${this.bitmap.getCardinality - printLimit} more)"
    buf += ']'
    s"${this.getClass.getSimpleName}: ${buf.toString()}"
  }

  override def containsAll(other: Provenance): Boolean = {
    other match {
      case rbp: RoaringBitmapProvenance =>
        bitmap.contains(rbp.bitmap)
      case dummy: DummyProvenance => true
      case other => throw new NotImplementedError(s"Unsupported RoaringBitmap containsAll check: $other")
    }
  }
}

object RoaringBitmapProvenance extends ProvenanceFactory {
  override def create(ids: Long*): RoaringBitmapProvenance = {
    if (ids.exists(_ > Int.MaxValue))
    // jteoh: Roaring64NavigableBitmap should be an option if this is required.
      throw new UnsupportedOperationException(
        s"At least one offset is greater than Int.Max which is not supported yet: $ids")
    val bitmap = new RoaringBitmap()
    bitmap.add(ids.map(_.toInt): _*)
    new RoaringBitmapProvenance(bitmap)
  }
}
