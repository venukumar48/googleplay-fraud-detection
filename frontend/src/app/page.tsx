"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function RootPage() {
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      router.push("/dashboard");
    } else {
      router.push("/login");
    }
  }, [router]);

  return (
    <div className="flex items-center justify-center h-screen bg-[#050814]">
      <div className="flex flex-col items-center gap-3">
        <div className="w-12 h-12 border-4 border-t-cyan-500 border-r-transparent border-slate-800 rounded-full animate-spin"></div>
        <p className="text-slate-400 font-mono text-sm tracking-wider">SECURE SHIELD SYSTEM HANDSHAKE...</p>
      </div>
    </div>
  );
}
