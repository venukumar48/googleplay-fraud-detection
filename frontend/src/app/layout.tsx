"use client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { usePathname } from "next/navigation";
import Sidebar from "@/components/Sidebar";
import { useState } from "react";
import "../app/globals.css"; // Next.js global styles import

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [queryClient] = useState(() => new QueryClient());
  const pathname = usePathname();
  const isLoginPage = pathname === "/login" || pathname === "/";

  return (
    <html lang="en">
      <body className="bg-[#050814] text-slate-100 min-h-screen font-sans antialiased selection:bg-cyan-500 selection:text-black">
        <QueryClientProvider client={queryClient}>
          <div className="flex h-screen overflow-hidden">
            {!isLoginPage && <Sidebar />}
            <main className="flex-1 flex flex-col h-full overflow-y-auto bg-[#050814]">
              {children}
            </main>
          </div>
        </QueryClientProvider>
      </body>
    </html>
  );
}
