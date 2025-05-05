package com.sample.kcl.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("dynamodb")
data class DynamoDbConfig (
    val endpoint: String
)
