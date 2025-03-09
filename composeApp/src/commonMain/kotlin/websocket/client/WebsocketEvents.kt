package websocket.client

interface WebsocketEvents {
    fun <T> onReceive(data: T)
    fun onConnected()
    fun onDisconnected(reason: String)
    fun log(message: String)
}