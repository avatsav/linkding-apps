package dev.avatsav.linkding.di

object GraphHolder {

  val graphs = mutableSetOf<Any>()

  /**
   * Fetch a graph of type [T] that has been added to the holder, automatically casting it in the
   * return.
   */
  inline fun <reified T> graph(): T =
    graphs.filterIsInstance<T>().firstOrNull()
      ?: throw NoSuchElementException("No component found for '${T::class.qualifiedName}'")

  /** Update a component of the given type, [T], in the component holder */
  fun <T : Any> updateGraph(graph: T) {
    graphs.removeAll { it::class.isInstance(graph) }
    graphs += graph
  }
}
