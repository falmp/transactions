#!/bin/bash

RESULTS=${1:-60}
WINDOW=${2:-60}

NOW=$(date +"%s")
BASE_TIMESTAMP=$(($NOW - $WINDOW))

for i in $(seq $RESULTS); do
    RANDOM_NOISE=$(($RANDOM % $WINDOW))
    RANDOM_TIMESTAMP=$(($BASE_TIMESTAMP + $RANDOM_NOISE))000
    RANDOM_AMOUNT=$(($RANDOM % 100)).0

    echo 'http://localhost:8080/transactions POST {"amount":'$RANDOM_AMOUNT',"timestamp":'$RANDOM_TIMESTAMP'}'
done
