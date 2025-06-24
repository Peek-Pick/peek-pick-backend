

--  https://docs.spring.io/spring-ai/reference/1.0/api/vectordbs/pgvector.html

-- pgvector 확장 설치 (벡터 DB 기능 활성화 저장/검색용)
CREATE EXTENSION IF NOT EXISTS vector;


-- hstore, uuid 사용을 위한 확장
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 벡터 저장용 테이블
CREATE TABLE IF NOT EXISTS tbl_vector_store (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY, -- 각 벡터 항목의 고유 식별자 (자동 생성 UUID)
    content text,                  -- 벡터화된 원본 텍스트 (예: "상품명: 홍삼정\n설명: 면역력 향상에 도움을 줍니다" 등)
    metadata json,                 -- 원본 데이터에 대한 부가 정보 (예: {"productId": 123, "category": "건강식품", "mainTag": "면역력"}) -> 검색 결과를 원본 db와 연결하거나, 카테고리/태그 필터링 시 활용
    embedding vector(1536)
);

-- 벡터 검색 인덱스 (HNSW, 코사인 유사도)
CREATE INDEX ON tbl_vector_store USING HNSW (embedding vector_cosine_ops);


