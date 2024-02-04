package instacli.util

/**
 * Only adds element if it's not null.
 */
fun <E> MutableList<E>.addNotNull(element: E?) {
    if (element != null) {
        add(element)
    }
}
