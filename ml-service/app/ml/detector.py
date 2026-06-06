import re
import numpy as np
from typing import List, Dict, Tuple
from app.schemas import AppScanRequest, ReviewInput, FraudDetails, MalwareDetails, SentimentSummary

class RiskDetector:
    def __init__(self):
        # High risk permissions in Android environment
        self.dangerous_permissions_list = {
            "android.permission.SEND_SMS",
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_PHONE_STATE",
            "android.permission.READ_SMS",
            "android.permission.WRITE_SMS",
            "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.CAMERA",
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS",
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.RECEIVE_BOOT_COMPLETED"
        }
        
        # NLP keywords associated with scam/fake reviews
        self.scam_keywords = [
            r"\bbest app\b", r"\bawesome app\b", r"\blove it\b", r"\bwow\b", 
            r"\brefuse to pay\b", r"\bscam\b", r"\bfake\b", r"\bwaste of time\b",
            r"\bstole my money\b", r"\bworst app\b", r"\bspam\b", r"\bvirus\b"
        ]
        self.toxic_keywords = [
            r"\bshit\b", r"\bfuck\b", r"\bbitch\b", r"\bass\b", r"\bsuck\b",
            r"\bstupid\b", r"\bidiot\b", r"\bgarbage\b", r"\btrash\b"
        ]

    def analyze_sentiment(self, text: str) -> Tuple[str, bool, bool]:
        """
        Simple Rule-Based NLP Classifier for review sentiment, spam, and toxicity.
        Returns: (sentiment, is_fake, is_toxic)
        """
        text_lower = text.lower()
        
        # Toxicty check
        is_toxic = any(re.search(kw, text_lower) for kw in self.toxic_keywords)
        
        # Fake / spam check (highly repetitive, keyword dense, or suspicious combinations)
        has_scam_keyword = any(re.search(kw, text_lower) for kw in self.scam_keywords)
        is_fake = has_scam_keyword and (len(text.split()) < 4 or is_toxic)
        
        # Simple sentiment logic
        pos_words = ["good", "great", "excellent", "best", "love", "amazing", "wonderful", "nice", "perfect", "cool"]
        neg_words = ["bad", "worst", "waste", "scam", "slow", "crash", "horrible", "error", "useless", "hate"]
        
        pos_score = sum(1 for w in pos_words if w in text_lower)
        neg_score = sum(1 for w in neg_words if w in text_lower)
        
        if pos_score > neg_score:
            sentiment = "positive"
        elif neg_score > pos_score:
            sentiment = "negative"
        else:
            sentiment = "neutral"
            
        return sentiment, is_fake, is_toxic

    def detect(self, app: AppScanRequest) -> Tuple[float, float, float, str, FraudDetails, MalwareDetails, SentimentSummary]:
        # 1. NLP and Sentiment Analysis of reviews
        pos_count = 0
        neg_count = 0
        neu_count = 0
        toxic_count = 0
        fake_count = 0
        
        for review in app.reviews:
            sent, fake, toxic = self.analyze_sentiment(review.text)
            if sent == "positive":
                pos_count += 1
            elif sent == "negative":
                neg_count += 1
            else:
                neu_count += 1
                
            if fake:
                fake_count += 1
            if toxic:
                toxic_count += 1

        total_reviews = len(app.reviews) if app.reviews else 1
        fake_ratio = fake_count / total_reviews
        toxic_ratio = toxic_count / total_reviews
        
        # 2. Fraud Rank detection indicators
        # High ratio of rating count to total downloads
        rating_to_download_ratio = (app.rating_count / app.downloads) if app.downloads > 0 else 0
        download_spike_flag = app.downloads > 500000 and rating_to_download_ratio < 0.001
        
        # Abnormal rating distributions vs reviews
        rating_anomaly = False
        if app.rating > 4.8 and neg_count > (pos_count + neu_count) * 1.5:
            rating_anomaly = True # Rating is high but reviews are heavily negative (fake rating injection)

        review_sentiment_anomaly = fake_ratio > 0.35
        
        # High rating count with virtually no app detail description/ranking logic (mock simulation)
        rank_manipulation_flag = rating_to_download_ratio > 0.4 and app.downloads > 1000
        
        # Compute fraud probability based on heuristic features (stands in for Random Forest/Isolation Forest)
        fraud_features = [
            float(download_spike_flag),
            float(rating_anomaly),
            float(review_sentiment_anomaly),
            float(rank_manipulation_flag),
            min(1.0, fake_ratio * 2.0)
        ]
        fraud_prob = float(np.mean(fraud_features))
        
        # 3. Malware risk details
        detected_dangerous_perms = [p for p in app.permissions if p in self.dangerous_permissions_list]
        suspicious_behaviors = []
        
        # If accessing storage fine location AND boot completed, mark suspicious background activity
        if "android.permission.ACCESS_FINE_LOCATION" in app.permissions and "android.permission.RECEIVE_BOOT_COMPLETED" in app.permissions:
            suspicious_behaviors.append("Background Location Monitoring on startup")
        
        # SMS permissions can execute premium SMS fraud
        if "android.permission.SEND_SMS" in app.permissions or "android.permission.RECEIVE_SMS" in app.permissions:
            suspicious_behaviors.append("SMS monitoring or dispatch capabilities")
            
        # Record audio and camera
        if "android.permission.RECORD_AUDIO" in app.permissions and "android.permission.CAMERA" in app.permissions:
            suspicious_behaviors.append("Audio & video background recording potential")
            
        if len(detected_dangerous_perms) >= 5:
            suspicious_behaviors.append("Excessive high-severity capabilities requested")

        # Compute malware probability
        max_possible_dangerous = len(self.dangerous_permissions_list)
        perm_ratio = len(detected_dangerous_perms) / max_possible_dangerous if max_possible_dangerous > 0 else 0
        malware_features = [
            perm_ratio * 1.5,
            len(suspicious_behaviors) * 0.25,
            1.0 if "android.permission.SYSTEM_ALERT_WINDOW" in app.permissions else 0.0 # Screen overlay risk
        ]
        malware_prob = min(1.0, float(sum(malware_features)))

        # 4. Overall risk calculation
        overall_risk_score = (fraud_prob + malware_prob) / 2.0
        
        if overall_risk_score < 0.35:
            risk_level = "SAFE"
        elif overall_risk_score < 0.70:
            risk_level = "MEDIUM_RISK"
        else:
            risk_level = "HIGH_RISK"
            
        fraud_details = FraudDetails(
            rating_anomaly=rating_anomaly,
            review_sentiment_anomaly=review_sentiment_anomaly,
            rank_manipulation_flag=rank_manipulation_flag,
            download_spike_flag=download_spike_flag,
            fraud_probability=round(fraud_prob, 3)
        )
        
        malware_details = MalwareDetails(
            dangerous_permissions=detected_dangerous_perms,
            suspicious_behaviors=suspicious_behaviors,
            malware_probability=round(malware_prob, 3)
        )
        
        sentiment_summary = SentimentSummary(
            positive_count=pos_count,
            negative_count=neg_count,
            neutral_count=neu_count,
            toxic_review_count=toxic_count,
            fake_review_count=fake_count
        )
        
        return (
            round(fraud_prob, 3),
            round(malware_prob, 3),
            round(overall_risk_score, 3),
            risk_level,
            fraud_details,
            malware_details,
            sentiment_summary
        )
