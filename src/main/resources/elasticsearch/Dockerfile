FROM docker.elastic.co/elasticsearch/elasticsearch:9.0.2

# 플러그인 설치
RUN elasticsearch-plugin install --batch analysis-nori && \
    elasticsearch-plugin install --batch analysis-kuromoji

# 셋업 스크립트 복사
COPY setup.sh /usr/share/elasticsearch/setup.sh
COPY products-settings-*.json /usr/share/elasticsearch/
COPY products-mappings-*.json /usr/share/elasticsearch/

# 실행 권한 부여
RUN chmod +x /usr/share/elasticsearch/setup.sh

# elasticsearch 기동 이후 스크립트 실행
CMD ["/bin/bash", "-c", "/usr/local/bin/docker-entrypoint.sh & sleep 20 && /usr/share/elasticsearch/setup.sh && wait"]
