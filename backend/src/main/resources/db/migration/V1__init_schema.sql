CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::varchar,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'DEVELOPER',
    ai_credits INTEGER NOT NULL DEFAULT 1000,
    total_features_generated INTEGER NOT NULL DEFAULT 0,
    total_bugs_predicted INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ai_feature_requests (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::varchar,
    user_id VARCHAR(255) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    intent TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    generated_code TEXT,
    tokens_used INTEGER,
    processing_time_ms BIGINT,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE generated_files (
    request_id VARCHAR(255) NOT NULL REFERENCES ai_feature_requests(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL
);

CREATE TABLE bug_predictions (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::varchar,
    user_id VARCHAR(255) REFERENCES users(id) ON DELETE SET NULL,
    file_path VARCHAR(500) NOT NULL,
    file_content TEXT,
    total_bugs_found INTEGER NOT NULL DEFAULT 0,
    critical_bugs INTEGER NOT NULL DEFAULT 0,
    analyzed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE bug_prediction_items (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::varchar,
    prediction_id VARCHAR(255) NOT NULL REFERENCES bug_predictions(id) ON DELETE CASCADE,
    line_number INTEGER,
    bug_type VARCHAR(100),
    confidence_score DOUBLE PRECISION,
    description TEXT,
    suggested_fix TEXT,
    severity VARCHAR(20)
);

CREATE TABLE code_decisions (
    id VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid()::varchar,
    user_id VARCHAR(255) REFERENCES users(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    context TEXT,
    decision TEXT,
    rationale TEXT,
    vector_id VARCHAR(255),
    tags VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_features_user_id ON ai_feature_requests(user_id);
CREATE INDEX idx_features_status ON ai_feature_requests(status);
CREATE INDEX idx_bugs_user_id ON bug_predictions(user_id);
CREATE INDEX idx_decisions_user_id ON code_decisions(user_id);
CREATE INDEX idx_decisions_tags ON code_decisions(tags);
