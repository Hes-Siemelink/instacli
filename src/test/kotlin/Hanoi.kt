fun hanoi(n: Int, from_rod: String, to_rod: String, aux_rod: String) {
    if (n == 0) {
        return
    }
    hanoi(n - 1, from_rod, aux_rod, to_rod)
    println("Move disk $n from rod $from_rod to $to_rod")
    hanoi(n - 1, aux_rod, to_rod, from_rod)
}

fun main() {
    hanoi(3, "A", "B", "C")
}