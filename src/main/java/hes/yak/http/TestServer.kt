package hes.yak.http

import io.javalin.Javalin

class TestServer {

}

fun startServer(port: Int): Javalin {
    return Javalin.create(/*config*/)
        .get("/") { ctx -> ctx.result("Hello World") }
        .start(port)!!
}

fun main() {
    startServer(25125)
}