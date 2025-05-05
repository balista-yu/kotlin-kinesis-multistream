package com.sample.kcl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import software.amazon.kinesis.exceptions.InvalidStateException
import software.amazon.kinesis.exceptions.KinesisClientLibRetryableException
import software.amazon.kinesis.exceptions.ShutdownException
import software.amazon.kinesis.lifecycle.events.InitializationInput
import software.amazon.kinesis.lifecycle.events.LeaseLostInput
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput
import software.amazon.kinesis.lifecycle.events.ShardEndedInput
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput
import software.amazon.kinesis.processor.ShardRecordProcessor
import org.springframework.stereotype.Component

@Component
class RecordProcessor : ShardRecordProcessor {

    private var shardId: String? = null

    override fun initialize(initializationInput: InitializationInput) {
        shardId = initializationInput.shardId()
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber())
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun processRecords(processRecordsInput: ProcessRecordsInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Processing {} record(s)", processRecordsInput.records().size)
            processRecordsInput.records().forEach { record ->
                log.info("Processing record pk: {} -- Seq: {}", record.partitionKey(), record.sequenceNumber())
            }
            processRecordsInput.checkpointer().checkpoint()
        } catch (e: KinesisClientLibRetryableException) {
            log.error("Caught throwable while processing records. Aborting.", e)
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun leaseLost(leaseLostInput: LeaseLostInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Lost lease, so terminating.")
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun shardEnded(shardEndedInput: ShardEndedInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Reached shard end checkpointing.")
            shardEndedInput.checkpointer().checkpoint()
        } catch (e: ShutdownException) {
            log.error("Exception while checkpointing at shard end. Giving up." ,e)
        } catch (e: InvalidStateException) {
            log.error("Exception while checkpointing at shard end. Giving up." ,e)
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    override fun shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId)
        try {
            log.info("Scheduler is shutting down, checkpointing.")
            shutdownRequestedInput.checkpointer().checkpoint()
        } catch (e: ShutdownException) {
            log.error("Exception while checkpointing at requested shutdown. Giving up." ,e)
        } catch (e: InvalidStateException) {
            log.error("Exception while checkpointing at requested shutdown. Giving up." ,e)
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY)
        }
    }

    companion object {
        private const val SHARD_ID_MDC_KEY = "ShardId"
        private val log: Logger = LoggerFactory.getLogger(RecordProcessor::class.java)
    }
}
