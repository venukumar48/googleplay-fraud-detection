-- Google Play Search Fraud and Malware Detection System SQL Schema

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL
);

-- User Roles Table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- Apps Table
CREATE TABLE IF NOT EXISTS apps (
    id BIGSERIAL PRIMARY KEY,
    package_name VARCHAR(255) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    downloads INT DEFAULT 0,
    rating DOUBLE PRECISION DEFAULT 0.0,
    rating_count INT DEFAULT 0,
    price DOUBLE PRECISION DEFAULT 0.0,
    overall_risk_score DOUBLE PRECISION DEFAULT 0.0,
    risk_level VARCHAR(50) DEFAULT 'SAFE'
);

-- App Permissions Table
CREATE TABLE IF NOT EXISTS app_permissions (
    app_id BIGINT REFERENCES apps(id) ON DELETE CASCADE,
    permission VARCHAR(255) NOT NULL,
    PRIMARY KEY (app_id, permission)
);

-- Reviews Table
CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    app_id BIGINT REFERENCES apps(id) ON DELETE CASCADE,
    author VARCHAR(255),
    text TEXT,
    rating INT
);

-- Fraud Results Table
CREATE TABLE IF NOT EXISTS fraud_results (
    id BIGSERIAL PRIMARY KEY,
    app_id BIGINT UNIQUE REFERENCES apps(id) ON DELETE CASCADE,
    rating_anomaly BOOLEAN DEFAULT FALSE,
    review_sentiment_anomaly BOOLEAN DEFAULT FALSE,
    rank_manipulation_flag BOOLEAN DEFAULT FALSE,
    download_spike_flag BOOLEAN DEFAULT FALSE,
    fraud_probability DOUBLE PRECISION DEFAULT 0.0
);

-- Malware Results Table
CREATE TABLE IF NOT EXISTS malware_results (
    id BIGSERIAL PRIMARY KEY,
    app_id BIGINT UNIQUE REFERENCES apps(id) ON DELETE CASCADE,
    malware_probability DOUBLE PRECISION DEFAULT 0.0
);

-- Malware Flagged Permissions Table
CREATE TABLE IF NOT EXISTS malware_flagged_permissions (
    malware_result_id BIGINT REFERENCES malware_results(id) ON DELETE CASCADE,
    flagged_permission VARCHAR(255) NOT NULL
);

-- Malware Suspicious Behaviors Table
CREATE TABLE IF NOT EXISTS malware_suspicious_behaviors (
    malware_result_id BIGINT REFERENCES malware_results(id) ON DELETE CASCADE,
    behavior VARCHAR(255) NOT NULL
);

-- Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100),
    action VARCHAR(100),
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
