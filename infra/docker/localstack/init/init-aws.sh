#!/bin/bash

# create dynamodb
awslocal dynamodb create-table \
    --table-name animals \
    --attribute-definitions \
        AttributeName=name,AttributeType=S \
    --key-schema \
        AttributeName=name,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --no-deletion-protection-enabled \
    --stream-specification StreamEnabled=true,StreamViewType=NEW_IMAGE

awslocal dynamodb create-table \
    --table-name foods \
    --attribute-definitions \
        AttributeName=name,AttributeType=S \
    --key-schema \
        AttributeName=name,KeyType=HASH \
    --billing-mode=PAY_PER_REQUEST \
    --no-deletion-protection-enabled \
    --stream-specification StreamEnabled=true,StreamViewType=NEW_IMAGE

# create kinesis stream
awslocal kinesis create-stream \
    --stream-name animals-stream \
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

awslocal kinesis create-stream \
    --stream-name foods-stream \
    --stream-mode-details '{"StreamMode": "ON_DEMAND"}'

# activate dynamodb kinesis data stream
awslocal dynamodb enable-kinesis-streaming-destination \
    --table-name animals \
    --stream-arn arn:aws:kinesis:ap-northeast-1:000000000000:stream/animals-stream

awslocal dynamodb enable-kinesis-streaming-destination \
    --table-name foods \
    --stream-arn arn:aws:kinesis:ap-northeast-1:000000000000:stream/foods-stream
