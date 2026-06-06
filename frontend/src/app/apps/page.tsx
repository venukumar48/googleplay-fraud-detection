"use client";

import { useEffect, useState } from "react";
import API from "@/lib/api";
import { 
  Search, 
  ShieldAlert, 
  ShieldCheck, 
  AlertTriangle,
  Info,
  ChevronRight,
  TrendingDown,
  Sparkles
} from "lucide-react";

interface AppData {
  id: number;
  packageName: string;
  title: string;
  downloads: number;
  rating: number;
  ratingCount: number;
  price: number;
  overallRiskScore: number;
  riskLevel: string;
  permissions: string[];
  reviews: Array<{ author: string; text: string; rating: number }>;
  fraudResult?: {
    ratingAnomaly: boolean;
    reviewSentimentAnomaly: boolean;
    rankManipulationFlag: boolean;
    downloadSpikeFlag: boolean;
    fraudProbability: number;
  };
  malwareResult?: {
    flaggedPermissions: string[];
    suspiciousBehaviors: string[];
    malwareProbability: number;
  };
}

export default function AppsPage() {
  const [apps, setApps] = useState<AppData[]>([]);
  const [search, setSearch] = useState("");
  const [selectedApp, setSelectedApp] = useState<AppData | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchApps = async () => {
    setIsLoading(true);
    try {
      const res = await API.get("/apps");
      setApps(res.data);
    } catch (err) {
      console.warn("Rest fetch apps failed. Loading mock data.");
      // Standard demo mock dataset matching the CSV file for development
      setApps([
        {
          id: 1,
          packageName: "com.scammy.flappybird3d",
          title: "Flappy Bird 3D Clone",
          downloads: 800000,
          rating: 4.9,
          ratingCount: 250000,
          price: 0.0,
          overallRiskScore: 0.88,
          riskLevel: "HIGH_RISK",
          permissions: ["android.permission.SEND_SMS", "android.permission.RECEIVE_SMS", "android.permission.SYSTEM_ALERT_WINDOW", "android.permission.ACCESS_FINE_LOCATION"],
          reviews: [{ author: "BotsReviews", text: "Super fast app amazing design wow best app love it", rating: 5 }],
          fraudResult: {
            ratingAnomaly: true,
            reviewSentimentAnomaly: true,
            rankManipulationFlag: true,
            downloadSpikeFlag: false,
            fraudProbability: 0.92
          },
          malwareResult: {
            flaggedPermissions: ["android.permission.SEND_SMS", "android.permission.RECEIVE_SMS", "android.permission.SYSTEM_ALERT_WINDOW", "android.permission.ACCESS_FINE_LOCATION"],
            suspiciousBehaviors: ["SMS monitoring or dispatch capabilities", "Overlay hijacking potential"],
            malwareProbability: 0.84
          }
        },
        {
          id: 2,
          packageName: "com.secure.authenticator",
          title: "Security Authenticator Shield",
          downloads: 500,
          rating: 4.6,
          ratingCount: 12,
          price: 0.0,
          overallRiskScore: 0.12,
          riskLevel: "SAFE",
          permissions: ["android.permission.CAMERA"],
          reviews: [{ author: "RealUser101", text: "A decent authentication helper app works fine", rating: 4 }],
          fraudResult: {
            ratingAnomaly: false,
            reviewSentimentAnomaly: false,
            rankManipulationFlag: false,
            downloadSpikeFlag: false,
            fraudProbability: 0.08
          },
          malwareResult: {
            flaggedPermissions: [],
            suspiciousBehaviors: [],
            malwareProbability: 0.15
          }
        },
        {
          id: 3,
          packageName: "com.mining.freebtc",
          title: "Free Bitcoin Cloud Miner",
          downloads: 2000,
          rating: 4.8,
          ratingCount: 950,
          price: 0.0,
          overallRiskScore: 0.76,
          riskLevel: "HIGH_RISK",
          permissions: ["android.permission.RECORD_AUDIO", "android.permission.CAMERA", "android.permission.RECEIVE_BOOT_COMPLETED", "android.permission.SYSTEM_ALERT_WINDOW"],
          reviews: [{ author: "VictimUser", text: "Waste of time refuses to pay scam took my money", rating: 1 }],
          fraudResult: {
            ratingAnomaly: true,
            reviewSentimentAnomaly: true,
            rankManipulationFlag: true,
            downloadSpikeFlag: false,
            fraudProbability: 0.82
          },
          malwareResult: {
            flaggedPermissions: ["android.permission.RECORD_AUDIO", "android.permission.CAMERA", "android.permission.RECEIVE_BOOT_COMPLETED", "android.permission.SYSTEM_ALERT_WINDOW"],
            suspiciousBehaviors: ["Overlay hijacking potential", "Audio & video background recording potential"],
            malwareProbability: 0.70
          }
        }
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchApps();
  }, []);

  const filteredApps = apps.filter((app) => 
    app.title.toLowerCase().includes(search.toLowerCase()) ||
    app.packageName.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="p-8 max-w-7xl mx-auto w-full flex flex-col lg:flex-row gap-8">
      {/* Left List Section */}
      <div className="flex-1 space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-white">App Security Directory</h1>
          <p className="text-slate-400 text-sm mt-1">Audit profile log listing analyzed applications with active scores.</p>
        </div>

        {/* Search Input */}
        <div className="relative max-w-md">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
          <input
            type="text"
            placeholder="Search by app title or package name..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-[#0a0f1d]/60 border border-[#1e293b] rounded-xl py-2.5 pl-10 pr-4 text-slate-200 text-sm outline-none focus:border-cyan-500/80 transition-all duration-300"
          />
        </div>

        {isLoading ? (
          <div className="py-20 flex justify-center">
            <div className="w-8 h-8 border-4 border-t-cyan-500 border-slate-800 rounded-full animate-spin"></div>
          </div>
        ) : (
          <div className="bg-[#0a0f1d]/30 border border-[#1e293b] rounded-2xl overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs border-collapse">
                <thead>
                  <tr className="border-b border-[#1e293b] bg-[#0a0f1d]/80 text-slate-400 font-mono uppercase">
                    <th className="p-4">App details</th>
                    <th className="p-4">Downloads</th>
                    <th className="p-4">Rating</th>
                    <th className="p-4">Threat Level</th>
                    <th className="p-4">Risk Index</th>
                    <th className="p-4"></th>
                  </tr>
                </thead>
                <tbody>
                  {filteredApps.map((app) => (
                    <tr 
                      key={app.id} 
                      onClick={() => setSelectedApp(app)}
                      className={`border-b border-[#1e293b]/40 hover:bg-slate-900/40 cursor-pointer transition-all duration-300 ${
                        selectedApp?.id === app.id ? "bg-slate-900/60" : ""
                      }`}
                    >
                      <td className="p-4">
                        <div className="font-semibold text-white text-sm">{app.title}</div>
                        <div className="text-slate-500 font-mono mt-0.5 text-[11px]">{app.packageName}</div>
                      </td>
                      <td className="p-4 text-slate-300 font-mono">{app.downloads.toLocaleString()}</td>
                      <td className="p-4 text-slate-300 font-mono">{app.rating} ★</td>
                      <td className="p-4">
                        <span className={`px-2.5 py-1 rounded-full text-[10px] font-semibold border ${
                          app.riskLevel === "HIGH_RISK" 
                            ? "bg-red-950/40 border-red-500/40 text-red-400"
                            : app.riskLevel === "MEDIUM_RISK"
                            ? "bg-yellow-950/40 border-yellow-500/40 text-yellow-400"
                            : "bg-emerald-950/40 border-emerald-500/40 text-emerald-400"
                        }`}>
                          {app.riskLevel.replace("_", " ")}
                        </span>
                      </td>
                      <td className="p-4 text-slate-300 font-mono">{Math.round(app.overallRiskScore * 100)}%</td>
                      <td className="p-4 text-slate-500">
                        <ChevronRight className="w-4 h-4" />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      {/* Right Drawer Panel */}
      {selectedApp && (
        <div className="w-full lg:w-96 bg-[#0a0f1d]/90 border border-[#1e293b] rounded-2xl p-6 self-start space-y-6">
          <div className="flex items-center justify-between border-b border-[#1e293b]/50 pb-4">
            <h3 className="font-semibold text-white text-base">Security Posture Details</h3>
            <button 
              onClick={() => setSelectedApp(null)}
              className="text-xs text-slate-500 hover:text-slate-300 font-mono"
            >
              CLOSE
            </button>
          </div>

          <div>
            <h4 className="font-bold text-white text-lg">{selectedApp.title}</h4>
            <span className="text-xs text-slate-500 font-mono block mt-0.5">{selectedApp.packageName}</span>
          </div>

          {/* Indicators cards */}
          <div className="grid grid-cols-2 gap-4">
            <div className="p-4 bg-slate-950/40 border border-[#1e293b] rounded-xl text-center">
              <span className="text-[10px] text-slate-500 font-mono block uppercase">Fraud Factor</span>
              <span className="text-xl font-bold text-yellow-500 mt-1 block">
                {selectedApp.fraudResult ? Math.round(selectedApp.fraudResult.fraudProbability * 100) : 0}%
              </span>
            </div>
            <div className="p-4 bg-slate-950/40 border border-[#1e293b] rounded-xl text-center">
              <span className="text-[10px] text-slate-500 font-mono block uppercase">Malware Factor</span>
              <span className="text-xl font-bold text-red-500 mt-1 block">
                {selectedApp.malwareResult ? Math.round(selectedApp.malwareResult.malwareProbability * 100) : 0}%
              </span>
            </div>
          </div>

          {/* Fraud Flags */}
          <div className="space-y-3">
            <h5 className="text-xs font-mono text-slate-400 uppercase tracking-wider">Fraud Diagnostics</h5>
            <div className="space-y-2 text-xs">
              <div className="flex justify-between p-2 bg-slate-950/20 border border-[#1e293b]/40 rounded-lg">
                <span className="text-slate-400">Abnormal Ratings Spikes</span>
                <span className={selectedApp.fraudResult?.ratingAnomaly ? "text-yellow-500 font-semibold" : "text-slate-500"}>
                  {selectedApp.fraudResult?.ratingAnomaly ? "FLAGGED" : "CLEAN"}
                </span>
              </div>
              <div className="flex justify-between p-2 bg-slate-950/20 border border-[#1e293b]/40 rounded-lg">
                <span className="text-slate-400">Review Sentiment Discrepancy</span>
                <span className={selectedApp.fraudResult?.reviewSentimentAnomaly ? "text-yellow-500 font-semibold" : "text-slate-500"}>
                  {selectedApp.fraudResult?.reviewSentimentAnomaly ? "FLAGGED" : "CLEAN"}
                </span>
              </div>
              <div className="flex justify-between p-2 bg-slate-950/20 border border-[#1e293b]/40 rounded-lg">
                <span className="text-slate-400">Ranking Manipulation Indicator</span>
                <span className={selectedApp.fraudResult?.rankManipulationFlag ? "text-yellow-500 font-semibold" : "text-slate-500"}>
                  {selectedApp.fraudResult?.rankManipulationFlag ? "FLAGGED" : "CLEAN"}
                </span>
              </div>
            </div>
          </div>

          {/* Malware flags */}
          <div className="space-y-3">
            <h5 className="text-xs font-mono text-slate-400 uppercase tracking-wider">Vulnerability Flags</h5>
            {selectedApp.malwareResult && selectedApp.malwareResult.suspiciousBehaviors.length > 0 ? (
              <div className="space-y-2">
                {selectedApp.malwareResult.suspiciousBehaviors.map((behavior, idx) => (
                  <div key={idx} className="p-2.5 bg-red-950/20 border border-red-500/20 rounded-lg text-red-300 text-[11px] flex gap-2">
                    <ShieldAlert className="w-4 h-4 shrink-0 text-red-400" />
                    <span>{behavior}</span>
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-3 bg-emerald-950/20 border border-emerald-500/20 rounded-lg text-emerald-300 text-xs flex gap-2">
                <ShieldCheck className="w-4 h-4 text-emerald-400" />
                <span>No behavioral threat triggers recorded.</span>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
