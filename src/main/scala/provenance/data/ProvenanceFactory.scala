/*
 * Created by William Anwara on 2023.4.4
 * Copyright © 2023 William Anwara. All rights reserved.
 */

package provenance.data
trait ProvenanceFactory {
  def create(ids: Long*): Provenance
}
