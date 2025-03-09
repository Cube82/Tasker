package pl.cube.pp_client

class WasmPlatform {
    val name: String = "Web with Kotlin/Wasm"
}

fun getPlatform() = WasmPlatform()