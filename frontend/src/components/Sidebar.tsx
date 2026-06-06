"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { 
  Shield, 
  LayoutDashboard, 
  Upload, 
  ListFilter, 
  Skull, 
  LogOut, 
  FileText 
} from "lucide-react";

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();

  const menuItems = [
    { name: "Dashboard", href: "/dashboard", icon: LayoutDashboard },
    { name: "Upload Dataset", href: "/upload", icon: Upload },
    { name: "App Directory", href: "/apps", icon: ListFilter },
    { name: "Malware Lab", href: "/malware", icon: Skull },
  ];

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    router.push("/login");
  };

  return (
    <aside className="w-64 bg-[#0a0f1d] border-r border-[#1e293b] flex flex-col h-screen text-slate-300">
      {/* Brand Logo */}
      <div className="p-6 border-b border-[#1e293b] flex items-center gap-3">
        <div className="p-2 bg-cyan-950 border border-cyan-500 rounded-lg text-cyan-400">
          <Shield className="w-6 h-6 animate-pulse" />
        </div>
        <div>
          <h1 className="font-bold text-sm text-white tracking-wider">PLAYGUARD</h1>
          <span className="text-xs text-slate-500 font-mono">SECURE AUDIT v1.0</span>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4 space-y-2">
        {menuItems.map((item) => {
          const isActive = pathname === item.href;
          const Icon = item.icon;
          return (
            <Link
              key={item.name}
              href={item.href}
              className={`flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-all duration-300 ${
                isActive
                  ? "bg-gradient-to-r from-cyan-950/60 to-purple-950/20 text-cyan-400 border-l-2 border-cyan-500 shadow-lg shadow-cyan-950/20"
                  : "hover:bg-slate-900/60 hover:text-white"
              }`}
            >
              <Icon className={`w-5 h-5 ${isActive ? "text-cyan-400" : "text-slate-400"}`} />
              {item.name}
            </Link>
          );
        })}
      </nav>

      {/* Footer / Logout */}
      <div className="p-4 border-t border-[#1e293b]">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 w-full px-4 py-3 rounded-lg text-sm font-medium text-slate-400 hover:bg-red-950/30 hover:text-red-400 transition-all duration-300"
        >
          <LogOut className="w-5 h-5" />
          Terminate Session
        </button>
      </div>
    </aside>
  );
}
