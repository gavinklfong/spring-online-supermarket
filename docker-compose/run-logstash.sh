docker run --rm -it \
-v ${PWD}/logstash-pipeline/:/usr/share/logstash/pipeline/ \
-v ${PWD}/logstash-data/products.json:/usr/share/logstash/data/products.json \
--network docker-compose_default \
logstash:7.14.2

