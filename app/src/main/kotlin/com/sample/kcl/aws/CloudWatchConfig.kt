package com.sample.kcl.aws

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cloudwatch")
data class CloudWatchConfig (
    val endpoint: String
)
