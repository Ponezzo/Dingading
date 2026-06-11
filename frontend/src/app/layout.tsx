import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { ReactNode } from "react";
import QueryProvider from "@/components/providers/QueryProvider";
import "./globals.css";
import Navbar from "@/components/navbar";
import AuthInitializer from "@/components/authiintializer";
import MockProvider from "@/mocks/MockProvider";
// import { useState } from "react"; // The React hook only works in a client component. 

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "DINGADING",
  description: "let's ding ga ding !",
  icons : {
    icon : "/favicon.png" // favicon 경로 설정 
  }
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="en">
      <head>
        <link rel="icon" href="/favicon.png"/>
      </head>
      <body className={`${geistSans.variable} ${geistMono.variable} antialiased`}>
        <div className="layout-container">
          <MockProvider />
          <AuthInitializer /> 
          <Navbar /> 
          <div className="main-content">
            <QueryProvider>
              {children}
            </QueryProvider>
          </div>
        </div>
      </body>
    </html>
  );
}