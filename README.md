## 📦 Overview

**RestSampler** provides a lightweight interface for **black-box REST API testing and analysis** directly from R.
It integrates with a **Kotlin-based sampling engine** derived from the open-source [**EvoMaster**](https://github.com/EMResearch/EvoMaster) framework — a state-of-the-art automated test generation system for RESTful web services.

This package allows users to **generate, encode, and execute random HTTP requests** against any target API that exposes a valid **OpenAPI/Swagger** specification.
The resulting structured response data can then be analyzed in R for:

* API robustness testing
* Machine learning and anomaly detection
* Automated input–output modeling
* Statistical evaluation of service behavior

---

## 🧠 Key Features

* 🔹 **Black-box sampling** of REST APIs — no source code required
* 🔹 Seamless integration with EvoMaster’s REST schema builder and encoder
* 🔹 Automatic construction and execution of random HTTP requests
* 🔹 Encodes sampled requests into numerical feature vectors
* 🔹 Returns responses and metadata as tidy R data frames
* 🔹 Compatible with downstream ML workflows and visualization tools

---

## 🧰 System Requirements

* **R ≥ 4.0.0**
* **Java ≥ 11**
* **rJava** (interface between R and JVM)

---

## ⚙️ Installation

You can install the package directly from GitHub using the **devtools** package:

```r
# Install devtools if not already installed
install.packages("devtools")

# Install RestSampler
devtools::install_github("MohsenTaheriShalmani/RestSampler")

# Load the package
library(RestSampler)
```

---

## 🚀 Usage Example

```r
# Sample random HTTP requests from a REST API
n <- 5
baseUrl <- "https://petstore.swagger.io"
swaggerUrl <- "https://petstore.swagger.io/v2/swagger.json"

results_df <- rHttpSampler(
  n = n,
  baseUrl = baseUrl,
  swaggerUrl = swaggerUrl
)

# View the resulting structured data frame
View(results_df)
```

### Example Output

| iteration | endpoint               | expandedGenes                    | response |
| --------- | ---------------------- | -------------------------------- | -------- |
| 1         | /pet/findByStatus      | id:StringGene, status:StringGene | 200      |
| 2         | /store/order/{orderId} | orderId:IntGene                  | 404      |
| ...       | ...                    | ...                              | ...      |

Each row corresponds to one sampled REST action, including:

* the endpoint,
* the genes (parameter encodings),
* and the HTTP response code.

---

## 🧩 Function Reference

### `rHttpSampler()`

```r
rHttpSampler(n = 5, baseUrl, swaggerUrl)
```

| Parameter      | Description                                                  |
| -------------- | ------------------------------------------------------------ |
| **n**          | Number of random HTTP request samples to generate            |
| **baseUrl**    | Base URL of the target API (e.g., `"http://localhost:8080"`) |
| **swaggerUrl** | URL or file path to the OpenAPI/Swagger JSON specification   |

**Returns:**
A tidy `data.frame` containing sampled endpoints, expanded gene encodings, and HTTP responses.

---

## 🧬 How It Works

Internally, **RestSampler** calls a lightweight Kotlin module (`EvoLightSamplerApp.jar`) that wraps EvoMaster’s REST sampling and encoding APIs.

1. **OpenAPI Parsing** – Loads the target specification.
2. **Action Generation** – Builds REST call templates for available endpoints.
3. **Random Sampling** – Randomizes parameter values and HTTP calls.
4. **Encoding** – Converts sampled requests to numeric feature vectors.
5. **Result Return** – Sends structured results back to R.

---

## 📖 Citation

If you use this package in academic work, please cite:

> Arcuri, A., & Fraser, G. (2018). *EvoMaster: Evolutionary System Testing of RESTful Web Services.*
> In *Proceedings of the 40th International Conference on Software Engineering* (ICSE).
> DOI: [10.1145/3293455](https://doi.org/10.1145/3293455)

> Arcuri, A. (2020). *EvoMaster: Automatic System Test Generation for REST and GraphQL APIs.*
> *Journal of Open Source Software*, 5(54), 2153.
> DOI: [10.21105/joss.02153](https://doi.org/10.21105/joss.02153)

and

> M. Taheri Shalmani (2025). *RestSampler: Lightweight REST API Black-Box Sampler and Encoder via EvoMaster Integration.*
> [https://github.com/MohsenTaheriShalmani/RestSampler](https://github.com/MohsenTaheriShalmani/RestSampler)

---

## 📜 License

**MIT License** © 2025 Mohsen Taheri Shalmani

This package integrates selected components from the open-source EvoMaster framework, which is distributed under the **Apache 2.0 License**.

---

## 🔗 Links

* 📦 CRAN (pending): *to be submitted*
* 🧰 GitHub: [https://github.com/MohsenTaheriShalmani/RestSampler](https://github.com/MohsenTaheriShalmani/RestSampler)
* 🧪 EvoMaster Framework: [https://github.com/EMResearch/EvoMaster](https://github.com/EMResearch/EvoMaster)


