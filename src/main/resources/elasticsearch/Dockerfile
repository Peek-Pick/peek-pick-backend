FROM docker.elastic.co/elasticsearch/elasticsearch:9.0.2

# 플러그인 설치
RUN elasticsearch-plugin install --batch analysis-nori && \
    elasticsearch-plugin install --batch analysis-kuromoji
