rHttpSampler <- function(n = 5,
                         baseUrl,
                         swaggerUrl) {

  # Initialize JVM
  .jinit()
  .jaddClassPath(cmd)

  # Create and run app
  app <- .jnew("org/evolightsampler/EvoLightSamplerApp")
  .jcall(app, "V", "run",
         baseUrl,
         swaggerUrl,
         as.integer(n))

  # Get results (List<Map<String,String>>)
  results <- .jcall(app, "Ljava/util/List;", "getResults")

  # Convert to R list
  len <- .jcall(results, "I", "size")
  r_results <- vector("list", len)

  for (i in 0:(len - 1)) {
    entry <- .jcall(results, "Ljava/lang/Object;", "get", as.integer(i))
    entry <- .jcast(entry, "java/util/Map")

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

  # Convert list of named lists into a data frame
  df <- do.call(rbind, lapply(r_results, as.data.frame))
  rownames(df) <- NULL

  return(df)

}


# Swagger pet store api
n <- 5
baseUrl <- "https://petstore.swagger.io/"
swaggerUrl <- "https://petstore.swagger.io/v2/swagger.json"
results_df<-rHttpSampler(n = n,
                         baseUrl = "http://localhost:8080",
                         swaggerUrl = "https://petstore.swagger.io/v2/swagger.json")

View(results_df)
