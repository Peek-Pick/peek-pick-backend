FROM docker.elastic.co/elasticsearch/elasticsearch:9.0.2

# 플러그인 설치
RUN elasticsearch-plugin install --batch analysis-nori && \
    elasticsearch-plugin install --batch analysis-kuromoji

# 루트 권한 상승
USER root

# 파일 복사 및 권한 설정
COPY setup.sh /usr/share/elasticsearch/setup.sh
RUN chmod +x /usr/share/elasticsearch/setup.sh

COPY products-settings-*.json /usr/share/elasticsearch/
COPY products-mappings-*.json /usr/share/elasticsearch/

# 기본 사용자로 복귀
USER elasticsearch

# Elasticsearch 기동 후 index 생성 및 매핑 적용
CMD ["/bin/bash", "-c", "/usr/local/bin/docker-entrypoint.sh & sleep 20 && /usr/share/elasticsearch/setup.sh && wait"]
