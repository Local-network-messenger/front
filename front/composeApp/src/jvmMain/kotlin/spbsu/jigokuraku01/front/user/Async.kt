package spbsu.jigokuraku01.front.user

sealed class Async<out T> {
    object Loading : Async<Nothing>()
    data class Success<T>(val data: T) : Async<T>()
    data class Error(val throwable: Throwable) : Async<Nothing>()
}
