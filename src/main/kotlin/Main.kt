package online.aruka

import online.aruka.backend.SimpleBackend
import online.aruka.backend.server.SimpleServer
import online.aruka.frontend.SimpleFrontend
import online.aruka.info.Hardware

fun main() {
    //debug
    val baseAddress = "http://data-plane-test:5555"
    val apiVersion = "v3"
    val credential = "admin" to "adminpwd"

    println(Hardware.get(baseAddress, apiVersion, credential))
    println(SimpleBackend.get(baseAddress, apiVersion, credential))
    println(SimpleFrontend.get(baseAddress, apiVersion, credential))
    println(SimpleServer.get(baseAddress, "minecraft_server", apiVersion, credential))
}