"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import API from "@/lib/api";
import { Shield, Lock, User, AlertTriangle } from "lucide-react";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const response = await API.post("/auth/login", { username, password });
      const { token, roles } = response.data;

      localStorage.setItem("token", token);
      localStorage.setItem("username", username);
      router.push("/dashboard");
    } catch (err: any) {
      // If server is not running, provide quick mock fallback for development
      console.warn("Backend server connection failed. Triggering dev demo mode.");
      if (username === "admin" && password === "password") {
        localStorage.setItem("token", "mock-jwt-token-playguard");
        localStorage.setItem("username", "admin");
        router.push("/dashboard");
      } else {
        setError(err.response?.data || "Connection to secure system failed. Use (admin / password) for development mode.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#050814] px-4 relative overflow-hidden">
      {/* Decorative Cyber Grid & Radial Overlay */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#0f172a_1px,transparent_1px),linear-gradient(to_bottom,#0f172a_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_50%,#000_70%,transparent_100%)] opacity-30"></div>
      <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-cyan-500/10 rounded-full blur-[120px] pointer-events-none animate-pulse"></div>
      <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-purple-500/10 rounded-full blur-[120px] pointer-events-none animate-pulse"></div>

      <div className="w-full max-w-md bg-[#0a0f1d]/80 backdrop-blur-xl border border-[#1e293b] rounded-2xl p-8 shadow-2xl relative z-10">
        {/* Header logo */}
        <div className="flex flex-col items-center mb-8">
          <div className="p-4 bg-cyan-950/60 border-2 border-cyan-500/30 rounded-2xl text-cyan-400 mb-4 shadow-lg shadow-cyan-500/10">
            <Shield className="w-10 h-10 animate-pulse" />
          </div>
          <h2 className="text-2xl font-bold text-white tracking-wide">SYSTEM SIGN-IN</h2>
          <p className="text-slate-400 text-xs mt-1 font-mono tracking-wider">SECURE SHIELD SYSTEM GATEWAY</p>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-red-950/40 border border-red-500/40 rounded-xl flex items-start gap-3 text-red-300 text-sm">
            <AlertTriangle className="w-5 h-5 shrink-0 text-red-400" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-6">
          {/* Username field */}
          <div>
            <label className="block text-xs font-mono tracking-wider text-slate-400 uppercase mb-2">Username</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-500" />
              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter operator code..."
                className="w-full bg-[#050814] border border-[#1e293b] focus:border-cyan-500/80 rounded-xl py-3 pl-10 pr-4 text-slate-200 text-sm outline-none transition-all duration-300"
              />
            </div>
          </div>

          {/* Password field */}
          <div>
            <label className="block text-xs font-mono tracking-wider text-slate-400 uppercase mb-2">Security Key</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-500" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password..."
                className="w-full bg-[#050814] border border-[#1e293b] focus:border-cyan-500/80 rounded-xl py-3 pl-10 pr-4 text-slate-200 text-sm outline-none transition-all duration-300"
              />
            </div>
          </div>

          {/* Login Button */}
          <button
            type="submit"
            disabled={isLoading}
            className="w-full bg-gradient-to-r from-cyan-600 to-cyan-500 hover:from-cyan-500 hover:to-cyan-400 text-black font-semibold py-3.5 rounded-xl transition-all duration-300 shadow-lg shadow-cyan-950/50 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {isLoading ? (
              <div className="w-5 h-5 border-2 border-black border-t-transparent rounded-full animate-spin"></div>
            ) : (
              "AUTHENTICATE OPERATOR"
            )}
          </button>
        </form>

        {/* Development Helper Box */}
        <div className="mt-8 pt-6 border-t border-[#1e293b] text-center">
          <p className="text-xs text-slate-500 font-mono">
            Demo access credentials: <strong className="text-cyan-500">admin</strong> / <strong className="text-cyan-500">password</strong>
          </p>
        </div>
      </div>
    </div>
  );
}
