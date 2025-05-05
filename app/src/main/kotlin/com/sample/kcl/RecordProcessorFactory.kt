package com.sample.kcl

import org.springframework.stereotype.Component
import software.amazon.kinesis.processor.ShardRecordProcessor
import software.amazon.kinesis.processor.ShardRecordProcessorFactory

@Component
class RecordProcessorFactory (private val recordProcessor: RecordProcessor) : ShardRecordProcessorFactory {
    override fun shardRecordProcessor(): ShardRecordProcessor = recordProcessor
}
