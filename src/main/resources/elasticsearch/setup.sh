#!/bin/bash

echo "📦 Elasticsearch 인덱스 생성 및 매핑 적용 시작..."

auth="-u elastic:peekpickES"
base="http://localhost:9200"

curl $auth -X DELETE "$base/products-ko"
curl $auth -X PUT "$base/products-ko" -H "Content-Type: application/json" --data-binary "@/usr/share/elasticsearch/products-settings-ko.json"

curl $auth -X DELETE "$base/products-en"
curl $auth -X PUT "$base/products-en" -H "Content-Type: application/json" --data-binary "@/usr/share/elasticsearch/products-settings-en.json"

curl $auth -X DELETE "$base/products-ja"
curl $auth -X PUT "$base/products-ja" -H "Content-Type: application/json" --data-binary "@/usr/share/elasticsearch/products-settings-ja.json"

curl $auth -X PUT "$base/products-ko/_mapping" -H "Content-Type: application/json" --data-binary "@/usr/share/elasticsearch/products-mappings-ko.json"
curl $auth -X PUT "$base/products-en/_mapping" -H "Content-Type: application/json" --data-binary "@/usr/share/elasticsearch/products-mappings-en.json"
curl $auth -X PUT "$base/products-ja/_mapping" -H "Content-Type: application/json" --data-binary "@/usr/share/elasticsearch/products-mappings-ja.json"

echo "✅ 인덱스 및 매핑 자동화 완료"
