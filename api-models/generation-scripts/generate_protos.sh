#!/bin/bash

# Path to the directory containing the .proto files
PROTO_DIR="../../api-models"

# Path to the output directory for generated Java code
JAVA_OUTPUT_DIR="../../flightvisualizer-backend/src/main/java"

# Output directory for generated TypeScript code
TS_OUTPUT_DIR="../../flightvisualizer-frontend/src/app/protos"

# Path to the protoc-gen-ts_proto.cmd (npm package)
PROTOC_GEN_TS_PATH=".\\..\\..\\flightvisualizer-frontend\\node_modules\\.bin\\protoc-gen-ts_proto.cmd"

# Generate Java definitions
protoc -I="$PROTO_DIR" --java_out="$JAVA_OUTPUT_DIR" "$PROTO_DIR/*.proto"

# Generate TypeScript definitions
protoc -I="$PROTO_DIR" --plugin=protoc-gen-ts_proto=$PROTOC_GEN_TS_PATH --ts_proto_out=$TS_OUTPUT_DIR "$PROTO_DIR/*.proto"