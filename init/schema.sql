

--  https://docs.spring.io/spring-ai/reference/1.0/api/vectordbs/pgvector.html

-- pgvector 확장 설치 (벡터 DB 기능 활성화 저장/검색용)
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS tbl_admin (
                                         id SERIAL PRIMARY KEY,
                                         account_id VARCHAR(100) NOT NULL,
                                         password TEXT NOT NULL,
                                         reg_date TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tbl_chatbot_faq (
                                               id SERIAL PRIMARY KEY,
                                               question TEXT NOT NULL,
                                               answer TEXT NOT NULL,
                                               category VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS tbl_point_store (
                                               id SERIAL PRIMARY KEY,
                                               item VARCHAR(100) NOT NULL,
                                               price INTEGER NOT NULL,
                                               description TEXT,
                                               product_type VARCHAR(50),
                                               img_url TEXT,
                                               is_hidden BOOLEAN DEFAULT FALSE,
                                               reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                               mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tbl_product_base (
                                                product_id BIGINT PRIMARY KEY,
                                                barcode VARCHAR(50) UNIQUE NOT NULL,
                                                img_url TEXT,
                                                img_thumb_url TEXT,
                                                like_count INTEGER DEFAULT 0,
                                                review_count INTEGER DEFAULT 0,
                                                score NUMERIC(3, 2) DEFAULT 0.0,
                                                is_delete BOOLEAN DEFAULT FALSE,
                                                main_tag VARCHAR(100),
                                                reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tbl_product_en (
                                              product_id BIGINT PRIMARY KEY REFERENCES tbl_product_base(product_id),
                                              allergens TEXT,
                                              category VARCHAR(100),
                                              description TEXT,
                                              ingredients TEXT,
                                              "name" VARCHAR(255),
                                              nutrition TEXT,
                                              volume VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tbl_product_ja (
                                              product_id BIGINT PRIMARY KEY REFERENCES tbl_product_base(product_id),
                                              "name" VARCHAR(255),
                                              description TEXT,
                                              category VARCHAR(100),
                                              volume VARCHAR(100),
                                              ingredients TEXT,
                                              allergens TEXT,
                                              nutrition TEXT
);

CREATE TABLE IF NOT EXISTS tbl_product_ko (
                                              product_id BIGINT PRIMARY KEY REFERENCES tbl_product_base(product_id),
                                              "name" VARCHAR(255),
                                              description TEXT,
                                              category VARCHAR(100),
                                              ingredients TEXT,
                                              allergens TEXT,
                                              nutrition TEXT,
                                              volume VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS tbl_tag (
                                       id SERIAL PRIMARY KEY,
                                       tag_name VARCHAR(100) NOT NULL UNIQUE,
                                       category VARCHAR(100)
);




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

