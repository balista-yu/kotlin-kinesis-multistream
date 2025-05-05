# Name

kotlin-kinesis-multistream

## Overview
This is a Kotlin application that demonstrates consuming multiple Kinesis streams using the Amazon Kinesis Client Library (KCL) with support for DynamoDB and LocalStack for local development.

## Getting Start

1. Clone the repository

```
$ git clone https://github.com/balista-yu/kotlin-kinesis-multistream.git
```

2. Run docker compose
```
$ cd kotlin-kinesis-multistream
$ task up
```

3. DynamoDB Insert Record with localstack
```
# awslocal dynamodb put-item --table-name animals --item '{"name": {"S": "dog"}}'
# awslocal dynamodb put-item --table-name foods --item '{"name": {"S": "apple"}}'
```
