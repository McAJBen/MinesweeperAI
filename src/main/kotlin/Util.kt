
fun <T> Array<Array<T>>.forAll(action: (T) -> Unit) {
	forEach { it.forEach(action) }
}
