import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from app.schemas import AppScanRequest, AppScanResponse
from app.ml.detector import RiskDetector

app = FastAPI(
    title="Google Play Fraud Rank and Malware Detection ML Service",
    description="Python microservice powered by NLP and Anomaly Detection Classifiers.",
    version="1.0.0"
)

# Enable CORS for internal microservice communications
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Instantiate the risk detector pipeline
detector = RiskDetector()

@app.get("/health")
def health_check():
    return {"status": "UP", "message": "ML Microservice is running and ready to score."}

@app.post("/api/ml/scan", response_model=AppScanResponse)
def scan_app(request: AppScanRequest):
    try:
        fraud_prob, malware_prob, risk_score, risk_lvl, fraud_det, malware_det, sent_sum = detector.detect(request)
        return AppScanResponse(
            package_name=request.package_name,
            fraud_score=fraud_prob,
            malware_score=malware_prob,
            overall_risk_score=risk_score,
            risk_level=risk_lvl,
            fraud_details=fraud_det,
            malware_details=malware_det,
            sentiment_analysis=sent_sum
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"ML Scan Execution Error: {str(e)}")

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
