package org.evolightsampler

import org.evomaster.core.EMConfig
import org.evomaster.core.problem.rest.builder.RestActionBuilderV3
import org.evomaster.core.problem.rest.classifier.probabilistic.InputEncoderUtilWrapper
import org.evomaster.core.problem.rest.data.RestCallAction
import org.evomaster.core.problem.rest.data.RestCallResult
import org.evomaster.core.problem.rest.schema.OpenApiAccess
import org.evomaster.core.problem.rest.schema.RestSchema
import org.evomaster.core.search.action.Action
import org.evomaster.core.search.service.Randomness
import java.net.HttpURLConnection
import java.net.URL
import javax.ws.rs.core.MediaType

fun executeRestCallAction(action: RestCallAction, baseUrl: String): RestCallResult {
    val fullUrl = "$baseUrl${action.resolvedPath()}"
//    println("Full Url Is: $fullUrl")

    if (!action.hasLocalId()) {
        action.setLocalId(java.util.UUID.randomUUID().toString())
    }

    val result = RestCallResult(action.getLocalId())
    val url = URL(fullUrl)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = action.verb.name
    connection.connectTimeout = 5000
    connection.readTimeout = 5000

    try {
        val status = connection.responseCode
        result.setStatusCode(status)
        val body = connection.inputStream.bufferedReader().use { it.readText() }
        result.setBody(body)
        result.setBodyType(MediaType.APPLICATION_JSON_TYPE)
    } catch (e: Exception) {
        result.setTimedout(true)
        result.setBody("ERROR: ${e.message}")
    } finally {
        connection.disconnect()
    }

    return result
}

class EvoLightSamplerApp {

    // Store results internally as a list of maps
    private val results: MutableList<Map<String, String>> = mutableListOf()

    fun run(baseUrl: String, swaggerUrl: String, iterations: Int) {

        val config = EMConfig()
        val randomness = Randomness()

        val schema = OpenApiAccess.getOpenAPIFromLocation(swaggerUrl)
        val restSchema = RestSchema(schema)

        val options = RestActionBuilderV3.Options(config)
        val actionCluster = mutableMapOf<String, Action>()
        RestActionBuilderV3.addActionsFromSwagger(restSchema, actionCluster, options = options)

        val actionList = actionCluster.values.filterIsInstance<RestCallAction>()
        println("Number of REST actions found from Swagger: ${actionList.size}")
        if (actionList.isEmpty()) {
            println("No REST actions found from Swagger at $swaggerUrl")
            return
        }

        for (i in 0 until iterations) {
            val template = randomness.choose(actionList)
            val sampledAction = template.copy() as RestCallAction
            sampledAction.doInitialize(randomness)

            val endpoint = sampledAction.endpoint

            val encoder = InputEncoderUtilWrapper(sampledAction, encoderType = config.aiEncoderType)
            val expandedGenes = encoder.endPointToGeneList()
                .joinToString(", ") { ng ->
                    "${ng.gene.name}:${ng.gene::class.simpleName ?: "Unknown"}"
                }

            val inputVector = encoder.encode()
            val encodedValues = inputVector.joinToString(", ") { it.toString() }

            val result = executeRestCallAction(sampledAction, baseUrl)
            val response = result.getStatusCode().toString()

            // Add to a result list as a map
            results.add(
                mapOf(
                    "iteration" to (i + 1).toString(),
                    "endpoint" to endpoint.toString(),
                    "expandedGenes" to expandedGenes,
                    "encoder" to encodedValues,
                    "response" to response
                )
            )
        }
    }

    // Getter to access results from R
    fun getResults(): List<Map<String, String>> {
        return results
    }
}
