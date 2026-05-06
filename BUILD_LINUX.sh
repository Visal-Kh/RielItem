#!/bin/bash
echo "================================"
echo "  Building RielItem Plugin"
echo "  LoyaltyMC - /loyalty command"
echo "================================"
mvn clean package -q && \
echo "" && \
echo "SUCCESS! JAR is at: target/RielItem_v1_1_loyalty.jar" || \
echo "BUILD FAILED!"
