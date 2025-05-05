package com.sample.kcl

import com.sample.kcl.aws.KinesisClientFactory
import com.sample.kcl.aws.DynamoDbClientFactory
import com.sample.kcl.aws.CloudWatchClientFactory
import com.sample.kcl.aws.KinesisConfig
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Autowired
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler
import java.util.UUID
import org.springframework.stereotype.Component
import software.amazon.kinesis.leases.LeaseManagementConfig
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class Consumer @Autowired constructor(
    private val kinesisClientFactory: KinesisClientFactory,
    private val dynamoDbClientFactory: DynamoDbClientFactory,
    private val cloudWatchClientFactory: CloudWatchClientFactory,
    private val kinesisConfig: KinesisConfig,
    private val recordProcessorFactory: RecordProcessorFactory
) {
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private lateinit var scheduler: Scheduler

    init {
        start()
    }

    private fun start() {
        val kinesisAsyncClient: KinesisAsyncClient = kinesisClientFactory.create()
        val dynamoDbAsyncClient: DynamoDbAsyncClient = dynamoDbClientFactory.create()
        val cloudWatchAsyncClient: CloudWatchAsyncClient = cloudWatchClientFactory.create()
        val workerIdentifier = UUID.randomUUID().toString()
        val streamTracker = MultiStreamTrackerImpl(kinesisConfig)

        val configsBuilder = ConfigsBuilder(
            streamTracker,
            APPLICATION_NAME,
            kinesisAsyncClient,
            dynamoDbAsyncClient,
            cloudWatchAsyncClient,
            workerIdentifier,
            recordProcessorFactory
        )

        val leaseManagementConfig = LeaseManagementConfig(
            LEASE_TABLE_NAME,
            APPLICATION_NAME,
            dynamoDbAsyncClient,
            kinesisAsyncClient,
            workerIdentifier
        ).workerUtilizationAwareAssignmentConfig(
            LeaseManagementConfig.WorkerUtilizationAwareAssignmentConfig()
                .disableWorkerMetrics(true).workerMetricsTableConfig(
                    LeaseManagementConfig.WorkerMetricsTableConfig(APPLICATION_NAME)
                ))

        scheduler = Scheduler(
            configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(),
            leaseManagementConfig,
            configsBuilder.lifecycleConfig(),
            configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(),
            configsBuilder.retrievalConfig()
        )

        executorService.submit(scheduler)
    }

    @PreDestroy
    fun shutdown() {
        if (this::scheduler.isInitialized) {
            scheduler.shutdown()
        }
        executorService.shutdown()
    }

    companion object {
        const val APPLICATION_NAME = "sample-kcl-app"
        const val LEASE_TABLE_NAME = "lease_table"
    }
}

