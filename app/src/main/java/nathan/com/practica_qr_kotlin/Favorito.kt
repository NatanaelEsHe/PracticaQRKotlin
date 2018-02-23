package nathan.com.practica_qr_kotlin

import java.io.Serializable

class Favorito : Serializable {

    var url: String? = null
    var fecha: String? = null
    var key: String? = null

    constructor() {
        url = ""
        fecha = ""
        key = ""
    }

    constructor(url: String, fecha: String) {
        this.url = url
        this.fecha = fecha
    }

    constructor(url: String, fecha: String, key: String) {
        this.url = url
        this.fecha = fecha
        this.key = key
    }
}
