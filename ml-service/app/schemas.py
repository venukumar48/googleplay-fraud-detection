from pydantic import BaseModel, Field
from typing import List, Dict, Optional

class ReviewInput(BaseModel):
    author: str
    text: str
    rating: int

class AppScanRequest(BaseModel):
    package_name: str
    title: str
    downloads: int
    rating: float
    rating_count: int
    price: float
    permissions: List[str] = []
    reviews: List[ReviewInput] = []

class FraudDetails(BaseModel):
    rating_anomaly: bool
    review_sentiment_anomaly: bool
    rank_manipulation_flag: bool
    download_spike_flag: bool
    fraud_probability: float

class MalwareDetails(BaseModel):
    dangerous_permissions: List[str]
    suspicious_behaviors: List[str]
    malware_probability: float

class SentimentSummary(BaseModel):
    positive_count: int
    negative_count: int
    neutral_count: int
    toxic_review_count: int
    fake_review_count: int

class AppScanResponse(BaseModel):
    package_name: str
    fraud_score: float
    malware_score: float
    overall_risk_score: float
    risk_level: str  # SAFE, MEDIUM_RISK, HIGH_RISK
    fraud_details: FraudDetails
    malware_details: MalwareDetails
    sentiment_analysis: SentimentSummary
