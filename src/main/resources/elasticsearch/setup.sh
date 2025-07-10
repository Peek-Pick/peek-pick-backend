#!/bin/bash

echo "📦 Elasticsearch 인덱스 생성 및 매핑 적용 시작..."

auth="-u elastic:peekpickES"
base="http://localhost:9200"

# Elasticsearch가 살아날 때까지 최대 60초간 체크
max_retries=30
count=0
until curl $auth -s $base >/dev/null; do
  count=$((count + 1))
  if [ $count -ge $max_retries ]; then
    echo "❌ Elasticsearch가 60초 이내에 시작되지 않았습니다. 종료."
    exit 1
  fi
  echo "⏳ Elasticsearch 시작 대기 중... (${count}/${max_retries})"
  sleep 2
done

echo "✅ Elasticsearch 준비 완료, 인덱스 생성 시작."

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
