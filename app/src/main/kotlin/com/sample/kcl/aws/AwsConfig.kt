package com.sample.kcl.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("aws")
data class AwsConfig (
    val accessKey: String,
    val secretKey: String,
    val region: String
)
