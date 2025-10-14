#' Sample Random HTTP Requests from a REST API
#'
#' The `rHttpSampler()` function provides a lightweight interface for
#' black-box REST API testing using a Kotlin-based sampling engine derived
#' from the EvoMaster framework. It executes random HTTP requests defined
#' in an OpenAPI specification and returns structured response data as a
#' data frame.
#'
#' @param n Integer. Number of sampling iterations (default: 5).
#' @param baseUrl Character. Base URL of the target REST API service.
#' @param swaggerUrl Character. URL of the OpenAPI/Swagger JSON specification.
#'
#' @return A data frame containing information about sampled requests
#' and responses.
#' @references
#' Arcuri, Andrea. "RESTful API automated test case generation with EvoMaster." ACM Transactions on Software Engineering and Methodology (TOSEM) 28.1 (2019): 1-37.
#' \doi{doi:10.1145/3293455}
#'
#' Arcuri, Andrea, et al. "Evomaster: A search-based system test generation tool." (2021).
#' \doi{doi:10.21105/joss.02153}
#'
#' @examples
#' \dontrun{
#' results_df <- rHttpSampler(
#'   n = 5,
#'   baseUrl = "https://petstore.swagger.io",
#'   swaggerUrl = "https://petstore.swagger.io/v2/swagger.json"
#' )
#' View(results_df)
#' }
#'
#' @importFrom rJava .jinit .jaddClassPath .jnew .jcall .jcast .jstrVal
#' @export
rHttpSampler <- function(n = 5,
                         baseUrl,
                         swaggerUrl) {

  # --- Initialize Java Virtual Machine ---------------------------------------
  # Ensures JVM is ready to run the Kotlin-based EvoLightSamplerApp
  .jinit()
  .jaddClassPath("inst/java/EvoLightSamplerApp-jar-with-dependencies.jar")

  # --- Create and Run the Sampler Application --------------------------------
  # Instantiate EvoLightSamplerApp and call its `run` method
  app <- .jnew("org/evolightsampler/EvoLightSamplerApp")
  .jcall(app, "V", "run",
         baseUrl,
         swaggerUrl,
         as.integer(n))

  # --- Retrieve Results from Java --------------------------------------------
  # Obtain the List<Map<String,String>> object from Kotlin via getResults()
  results <- .jcall(app, "Ljava/util/List;", "getResults")

  # Convert Java List into an R list of named lists
  len <- .jcall(results, "I", "size")
  r_results <- vector("list", len)

  for (i in 0:(len - 1)) {
    # Get each Map<String,String> entry
    entry <- .jcall(results, "Ljava/lang/Object;", "get", as.integer(i))
    entry <- .jcast(entry, "java/util/Map")

    # Extract key-value pairs from Map
    keys <- .jcall(entry, "Ljava/util/Set;", "keySet")
    keys_iter <- .jcall(keys, "Ljava/util/Iterator;", "iterator")

    result_item <- list()

    while (.jcall(keys_iter, "Z", "hasNext")) {
      key_obj <- .jcall(keys_iter, "Ljava/lang/Object;", "next")
      val_obj <- .jcall(entry, "Ljava/lang/Object;", "get", key_obj)

      key <- .jstrVal(key_obj)
      value <- .jstrVal(val_obj)
      result_item[[key]] <- value
    }

    r_results[[i + 1]] <- result_item
  }

  # --- Convert to Data Frame -------------------------------------------------
  # Combine the list of named lists into a tidy data frame
  df <- do.call(rbind, lapply(r_results, as.data.frame))
  rownames(df) <- NULL

  return(df)
}
