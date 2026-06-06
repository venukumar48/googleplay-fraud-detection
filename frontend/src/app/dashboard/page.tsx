"use client";

import { useEffect, useState } from "react";
import API from "@/lib/api";
import { 
  ShieldAlert, 
  Activity, 
  Search, 
  TrendingUp, 
  Sparkles,
  RefreshCw,
  Download,
  AlertTriangle
} from "lucide-react";
import { 
  ResponsiveContainer, 
  PieChart, 
  Pie, 
  Cell, 
  AreaChart, 
  Area, 
  XAxis, 
  YAxis, 
  Tooltip,
  BarChart,
  Bar
} from "recharts";

interface Stats {
  totalApps: number;
  safeApps: number;
  mediumRiskApps: number;
  highRiskApps: number;
  fraudSpikes: number;
  averageRiskScore: number;
  recentLogs: Array<{
    id: number;
    username: string;
    action: string;
    details: string;
    timestamp: string;
  }>;
}

export default function DashboardPage() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchStats = async () => {
    setIsLoading(true);
    setError("");
    try {
      const res = await API.get("/dashboard/stats");
      setStats(res.data);
    } catch (err) {
      console.warn("REST dashboard stats failed, loading mock state.");
      // Generate clean mock dashboard values for development visualizer
      setStats({
        totalApps: 45,
        safeApps: 28,
        mediumRiskApps: 11,
        highRiskApps: 6,
        fraudSpikes: 8,
        averageRiskScore: 0.38,
        recentLogs: [
          { id: 1, username: "admin", action: "APP_SCAN", details: "Scanned package com.super.cleanapp. Overall risk: 0.12", timestamp: new Date(Date.now() - 60000 * 5).toISOString() },
          { id: 2, username: "admin", action: "APP_SCAN", details: "Anomaly Detected: com.scammy.flappybird. Risk: 0.89", timestamp: new Date(Date.now() - 60000 * 18).toISOString() },
          { id: 3, username: "admin", action: "DATASET_UPLOAD", details: "Uploaded play_store_batch_4.csv containing 12 apps", timestamp: new Date(Date.now() - 3600000).toISOString() },
          { id: 4, username: "system", action: "MODEL_TRAINING", details: "Refreshed Isolation Forest anomaly thresholds", timestamp: new Date(Date.now() - 7200000).toISOString() }
        ]
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  const riskPieData = stats ? [
    { name: "Safe", value: stats.safeApps, color: "#10b981" },
    { name: "Medium Risk", value: stats.mediumRiskApps, color: "#f59e0b" },
    { name: "High Risk", value: stats.highRiskApps, color: "#ef4444" }
  ] : [];

  const fraudTrendData = [
    { name: "Jun 01", fraudProbs: 0.22, anomalies: 2 },
    { name: "Jun 02", fraudProbs: 0.35, anomalies: 4 },
    { name: "Jun 03", fraudProbs: 0.28, anomalies: 3 },
    { name: "Jun 04", fraudProbs: 0.45, anomalies: 7 },
    { name: "Jun 05", fraudProbs: 0.58, anomalies: 9 },
    { name: "Jun 06", fraudProbs: 0.38, anomalies: 5 }
  ];

  return (
    <div className="p-8 space-y-8 max-w-7xl mx-auto w-full">
      {/* Title & Controls Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-[#1e293b] pb-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-white">Security Command Console</h1>
          <p className="text-slate-400 text-sm mt-1">Real-time surveillance dashboard for Play Store fraud and malware anomalies.</p>
        </div>
        <div className="flex items-center gap-3">
          <button 
            onClick={fetchStats}
            className="flex items-center gap-2 px-4 py-2 text-xs font-mono border border-[#1e293b] hover:border-cyan-500 rounded-lg hover:text-cyan-400 transition-all duration-300 bg-slate-950/60"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            POLL TELEMETRY
          </button>
          <a
            href="http://localhost:8080/api/reports/pdf"
            target="_blank"
            className="flex items-center gap-2 px-4 py-2 text-xs font-mono bg-cyan-600 hover:bg-cyan-500 text-black font-semibold rounded-lg transition-all duration-300"
          >
            <Download className="w-3.5 h-3.5" />
            DOWNLOAD AUDIT PDF
          </a>
        </div>
      </div>

      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-20 gap-4">
          <div className="w-10 h-10 border-4 border-t-cyan-500 border-slate-800 rounded-full animate-spin"></div>
          <span className="text-xs text-slate-500 font-mono">LOADING COMMAND TELEMETRY...</span>
        </div>
      ) : (
        <>
          {/* Telemetry Indicator Cards Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {/* Total Apps Scanned */}
            <div className="bg-[#0a0f1d]/50 backdrop-blur border border-[#1e293b] hover:border-cyan-500/50 rounded-2xl p-6 transition-all duration-300 flex items-center justify-between">
              <div>
                <span className="text-xs font-mono text-slate-500 tracking-wider block uppercase">Total Monitored Apps</span>
                <span className="text-3xl font-bold text-white block mt-2">{stats?.totalApps}</span>
              </div>
              <div className="p-3 bg-cyan-950/40 border border-cyan-800/40 rounded-xl text-cyan-400">
                <Search className="w-6 h-6" />
              </div>
            </div>

            {/* High Risk Critical Alarms */}
            <div className="bg-[#0a0f1d]/50 backdrop-blur border border-[#1e293b] hover:border-red-500/50 rounded-2xl p-6 transition-all duration-300 flex items-center justify-between">
              <div>
                <span className="text-xs font-mono text-slate-500 tracking-wider block uppercase">Critical Threats</span>
                <span className="text-3xl font-bold text-red-500 block mt-2">{stats?.highRiskApps}</span>
              </div>
              <div className="p-3 bg-red-950/40 border border-red-800/40 rounded-xl text-red-400">
                <ShieldAlert className="w-6 h-6 animate-pulse" />
              </div>
            </div>

            {/* Fraud spikes detected */}
            <div className="bg-[#0a0f1d]/50 backdrop-blur border border-[#1e293b] hover:border-yellow-500/50 rounded-2xl p-6 transition-all duration-300 flex items-center justify-between">
              <div>
                <span className="text-xs font-mono text-slate-500 tracking-wider block uppercase">Fraud Anomalies</span>
                <span className="text-3xl font-bold text-yellow-500 block mt-2">{stats?.fraudSpikes}</span>
              </div>
              <div className="p-3 bg-yellow-950/40 border border-yellow-800/40 rounded-xl text-yellow-400">
                <AlertTriangle className="w-6 h-6" />
              </div>
            </div>

            {/* Mean System Security rating */}
            <div className="bg-[#0a0f1d]/50 backdrop-blur border border-[#1e293b] hover:border-purple-500/50 rounded-2xl p-6 transition-all duration-300 flex items-center justify-between">
              <div>
                <span className="text-xs font-mono text-slate-500 tracking-wider block uppercase">Mean Risk Factor</span>
                <span className="text-3xl font-bold text-purple-400 block mt-2">{stats ? Math.round(stats.averageRiskScore * 100) : 0}%</span>
              </div>
              <div className="p-3 bg-purple-950/40 border border-purple-800/40 rounded-xl text-purple-400">
                <TrendingUp className="w-6 h-6" />
              </div>
            </div>
          </div>

          {/* Visualizing Charts Component Row */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Risk Distribution Pie Chart */}
            <div className="bg-[#0a0f1d]/40 backdrop-blur border border-[#1e293b] rounded-2xl p-6 flex flex-col justify-between">
              <div>
                <h3 className="font-semibold text-white">Posture Classification</h3>
                <span className="text-xs text-slate-500 font-mono block mt-1">Application threat risk proportions</span>
              </div>
              <div className="h-64 mt-4 flex items-center justify-center">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={riskPieData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={80}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {riskPieData.map((entry, idx) => (
                        <Cell key={`cell-${idx}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip contentStyle={{ backgroundColor: "#0f172a", borderColor: "#1e293b", color: "#f8fafc" }} />
                  </PieChart>
                </ResponsiveContainer>
              </div>
              {/* Pie Legends */}
              <div className="flex justify-around text-xs mt-2 border-t border-[#1e293b]/50 pt-4">
                {riskPieData.map((d) => (
                  <div key={d.name} className="flex items-center gap-2">
                    <div className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: d.color }}></div>
                    <span className="text-slate-400">{d.name} ({d.value})</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Anomaly Trend Chart */}
            <div className="bg-[#0a0f1d]/40 backdrop-blur border border-[#1e293b] rounded-2xl p-6 lg:col-span-2">
              <div>
                <h3 className="font-semibold text-white">Fraud Incident Chronology</h3>
                <span className="text-xs text-slate-500 font-mono block mt-1">Ranking manipulation and rating anomalies over time</span>
              </div>
              <div className="h-72 mt-6">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={fraudTrendData}>
                    <defs>
                      <linearGradient id="colorFraud" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#22d3ee" stopOpacity={0.4}/>
                        <stop offset="95%" stopColor="#22d3ee" stopOpacity={0}/>
                      </linearGradient>
                    </defs>
                    <XAxis dataKey="name" stroke="#64748b" fontSize={10} tickLine={false} />
                    <YAxis stroke="#64748b" fontSize={10} tickLine={false} />
                    <Tooltip contentStyle={{ backgroundColor: "#0f172a", borderColor: "#1e293b", color: "#f8fafc" }} />
                    <Area type="monotone" dataKey="fraudProbs" stroke="#22d3ee" fillOpacity={1} fill="url(#colorFraud)" />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>

          {/* Incident Log Terminal */}
          <div className="bg-[#0a0f1d]/40 backdrop-blur border border-[#1e293b] rounded-2xl p-6">
            <div className="flex items-center justify-between border-b border-[#1e293b]/50 pb-4 mb-4">
              <div>
                <h3 className="font-semibold text-white">Security Logs</h3>
                <span className="text-xs text-slate-500 font-mono block mt-1">Audit stream of operator scans, CSV ingestions, and risk assessments</span>
              </div>
              <span className="px-2.5 py-1 bg-cyan-950/40 border border-cyan-800/40 rounded-full text-xs font-mono text-cyan-400 flex items-center gap-1.5 animate-pulse">
                <span className="w-1.5 h-1.5 rounded-full bg-cyan-400"></span>
                LIVE STREAM
              </span>
            </div>
            
            <div className="space-y-3 font-mono text-xs max-h-60 overflow-y-auto pr-2">
              {stats?.recentLogs.map((log) => (
                <div key={log.id} className="p-3 bg-slate-950/60 border border-[#1e293b]/40 rounded-lg flex flex-col md:flex-row md:items-center justify-between gap-2">
                  <div className="flex items-center gap-3">
                    <span className="text-slate-500">[{new Date(log.timestamp).toLocaleTimeString()}]</span>
                    <span className="px-2 py-0.5 bg-slate-900 border border-slate-700 rounded text-cyan-500 uppercase text-[10px] tracking-wide font-semibold">{log.action}</span>
                    <span className="text-slate-300">{log.details}</span>
                  </div>
                  <span className="text-slate-600 text-[10px] self-end md:self-center">OPERATOR: {log.username}</span>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
    </div>
  );
}
