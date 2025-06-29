### McpServer Command

The `McpServer` command launches a long-running Model Context Protocol (MCP) server in a background thread. This allowed the instacli script to complete while the server is running. The instacli command line tool will keep running until the server is stopped.

### Key Implementation Details:

*   **Non-Daemon Thread:** The server is started in a non-daemon thread using Kotlin's `thread()` convenience function. This is crucial because non-daemon threads prevent the JVM from exiting, allowing the server to continue running even after the main `instacli` script has completed.
*   **Structured Concurrency:** The server's lifecycle is managed using structured concurrency principles. The `server.connect(transport)` call is blocking and is wrapped in a `runBlocking` block. An `onClose` handler is used to signal a `Job` when the server is closed, which allows the thread to complete and the JVM to exit gracefully.
*   **Server Management:** A map of running servers is maintained, allowing for multiple named servers to be active simultaneously. The `stopServer` function provides a mechanism to gracefully shut down a running server by name.

This approach ensures that the MCP server can be launched as a background process from the command line, and that it can be managed and shut down cleanly, which is essential for both interactive use and automated testing.
