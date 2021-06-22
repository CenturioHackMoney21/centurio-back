window.swaggerSpec={
  "swagger" : "2.0",
  "info" : {
    "description" : "",
    "version" : "1.0.0",
    "title" : "Centurio",
    "contact" : {
      "email" : "ooctogene@gmail.com"
    }
  },
  "host" : "centurio-hm21.herokuapp.com",
  "basePath" : "/",
  "tags" : [ {
    "name" : "cover",
    "description" : "Everything about covers"
  } ],
  "schemes" : [ "https", "http" ],
  "paths" : {
    "/cover" : {
      "get" : {
        "tags" : [ "cover" ],
        "summary" : "Get all covers from Nexus Mutual",
        "description" : "",
        "produces" : [ "application/json" ],
        "responses" : {
          "200" : {
            "description" : "Successful operation",
            "schema" : {
              "$ref" : "#/definitions/Cover"
            }
          }
        }
      }
    },
    "/cover/recommend/{address}" : {
      "get" : {
        "tags" : [ "cover" ],
        "summary" : "Get recommended covers for a given wallet address",
        "produces" : [ "application/json" ],
        "parameters" : [ {
          "name" : "address",
          "in" : "path",
          "description" : "Address of a wallet",
          "required" : true,
          "type" : "string"
        } ],
        "responses" : {
          "200" : {
            "description" : "Successful operation",
            "schema" : {
              "$ref" : "#/definitions/Cover"
            }
          },
          "400" : {
            "description" : "Invalid Address supplied"
          }
        }
      }
    }
  },
  "definitions" : {
    "Cover" : {
      "type" : "object",
      "properties" : {
        "name" : {
          "type" : "string",
          "description" : "contract's name"
        },
        "address" : {
          "type" : "string",
          "description" : "contract's address"
        },
        "type" : {
          "type" : "string",
          "description" : "cover type",
          "enum" : [ "protocol", "custodian", "yield" ]
        },
        "supportedChains" : {
          "type" : "string",
          "description" : "contract's chains supported",
          "enum" : [ "ethereum", "bsc", "fantom", "polygon", "starkware", "xdai", "terra", "thorchain" ]
        },
        "logo" : {
          "type" : "string",
          "description" : "logo filename"
        }
      }
    }
  }
}