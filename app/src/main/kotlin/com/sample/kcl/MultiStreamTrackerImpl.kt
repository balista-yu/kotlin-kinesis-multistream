package com.sample.kcl

import com.sample.kcl.aws.KinesisConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import software.amazon.awssdk.arns.Arn
import software.amazon.kinesis.common.InitialPositionInStream
import software.amazon.kinesis.common.InitialPositionInStreamExtended
import software.amazon.kinesis.common.StreamConfig
import software.amazon.kinesis.common.StreamIdentifier
import software.amazon.kinesis.processor.FormerStreamsLeasesDeletionStrategy
import software.amazon.kinesis.processor.MultiStreamTracker

@Component
class MultiStreamTrackerImpl @Autowired constructor(private val kinesisConfig: KinesisConfig): MultiStreamTracker {
    override fun streamConfigList(): List<StreamConfig?>? {
        return listOf(
            StreamConfig(
                StreamIdentifier.multiStreamInstance(
                    Arn.fromString(kinesisConfig.animalsStreamName),
                    1234567890L,
                ),
                InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.TRIM_HORIZON),
            ),
            StreamConfig(
                StreamIdentifier.multiStreamInstance(
                    Arn.fromString(kinesisConfig.foodsStreamName),
                    1234567890L,
                ),
                InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.TRIM_HORIZON),
            ),
        )
    }

    override fun formerStreamsLeasesDeletionStrategy(): FormerStreamsLeasesDeletionStrategy? =
        FormerStreamsLeasesDeletionStrategy.NoLeaseDeletionStrategy()
}
