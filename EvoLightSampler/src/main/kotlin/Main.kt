package org.evolightsampler

fun main(args: Array<String>) {
    val baseUrl = if (args.isNotEmpty()) args[0] else "http://localhost:8080"
    val swaggerUrl = if (args.size > 1) args[1] else "http://localhost:8080/api/v3/openapi.json"
    val iterations = if (args.size > 2) args[2].toIntOrNull() ?: 100 else 100

    println("Running EvoLightApp with base URL: $baseUrl , swaggerUrl: $swaggerUrl and iterations: $iterations")
    val app = EvoLightSamplerApp()
    app.run(baseUrl, swaggerUrl, iterations)

//    val results = app.getResults()

//    for (r in results) {
//        println("Endpoint: ${r["endpoint"]}")
//        println("Expanded Genes: ${r["expandedGenes"]}")
//        println("Encoded Features: ${r["encoder"]}")
//        println("Response: ${r["response"]}")
//    }

}

